package com.jpmc.midascore.Controller;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository; // Inject UserRepository (or UserService if you created one)

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @GetMapping("/balance")
    public ResponseEntity<Balance> getBalance(@RequestParam long userId) {
        LOG.info("Received request to get balance for userId: {}", userId);

        Optional<UserRecord> userRecordOptional = Optional.ofNullable(userRepository.findById(userId));

        if (userRecordOptional.isPresent()) {
            UserRecord user = userRecordOptional.get();
            LOG.info("User found. Returning balance: {}", user.getBalance());
            return ResponseEntity.ok(new Balance(user.getBalance()));
        } else {
            // User not found, return Balance with amount 0 as per requirements
            LOG.warn("User with ID {} not found. Returning balance 0.", userId);
            return ResponseEntity.ok(new Balance(0.0f));
        }
    }
}