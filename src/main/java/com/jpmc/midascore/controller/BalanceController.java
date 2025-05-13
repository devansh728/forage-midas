//package com.jpmc.midascore.controller;
//
//import com.jpmc.midascore.entity.UserRecord;
//import com.jpmc.midascore.foundation.Balance;
//import com.jpmc.midascore.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Optional;
//
//@RestController
//public class BalanceController {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @GetMapping("/balance")
//    public Balance getBalance(@RequestParam("userId") Long userId) {
//        Optional<UserRecord> userOpt = userRepository.findById(userId);
//
//        // If user doesn't exist, return balance of 0
//        return userOpt.map(userRecord -> new Balance(userRecord.getBalance())).orElseGet(() -> new Balance(0));
//
//        // Return the user's balance
//    }
//}
package com.jpmc.midascore.controller;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {
    private static final Logger logger = LoggerFactory.getLogger(BalanceController.class);

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam("userId") Long userId) {
        logger.info("Received balance request for userId: {}", userId);

        // Use the primitive long version of findById from your custom method
        UserRecord user = userRepository.findById(userId.longValue());

        // If user doesn't exist, return balance of 0
        if (user == null) {
            logger.info("User not found, returning balance of 0");
            return new Balance(0);
        }

        // Return the user's balance
        logger.info("Found user, returning balance: {}", user.getBalance());
        return new Balance(user.getBalance());
    }
}