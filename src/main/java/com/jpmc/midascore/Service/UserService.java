package com.jpmc.midascore.Service;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<Balance> getUserBalance(long userId){
        Optional<UserRecord> userRecordOptional = Optional.ofNullable(userRepository.findById(userId));
        if (userRecordOptional.isPresent()) {
            UserRecord userRecord = userRecordOptional.get();
            return Optional.of(new Balance(userRecord.getBalance()));
        } else {
            return Optional.empty(); // User not found
        }

    }
}
