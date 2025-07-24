package com.jpmc.midascore.Listner;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionListener.class);

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public TransactionListener(UserRepository userRepository,
                               TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core")
    public void listen(Transaction transaction) {
        LOG.info("Received transaction: {}", transaction);

        long senderId = transaction.getSenderId();
        long recipientId = transaction.getRecipientId();

        float amount = transaction.getAmount();

        UserRecord sender = userRepository.findById(senderId);
        UserRecord recipient = userRepository.findById(recipientId);

        if (sender == null || recipient == null) {
            LOG.warn("Invalid user(s). Transaction ignored.");
            return;
        }

        if (sender.getBalance() < amount) {
            LOG.warn("Insufficient balance for sender ID {}. Transaction ignored.", senderId);
            return;
        }

        // Valid transaction
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        userRepository.save(sender);
        userRepository.save(recipient);

        TransactionRecord record = new TransactionRecord(sender, recipient, amount);
        transactionRecordRepository.save(record);

        LOG.info("Transaction recorded successfully.");
    }
}
