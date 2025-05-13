package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private static final String INCENTIVE_API_URL = "http://localhost:8080/incentive";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    public boolean processTransaction(Transaction transaction) {
        logger.info("Processing transaction: {}", transaction);

//        // Get sender and recipient
//        Optional<UserRecord> senderOpt = userRepository.findById(transaction.getSenderId());
//        Optional<UserRecord> recipientOpt = userRepository.findById(transaction.getRecipientId());
//
//        // Validate transaction
//        if (!senderOpt.isPresent()) {
//            logger.error("Invalid sender ID: {}", transaction.getSenderId());
//            return false;
//        }
//
//        if (!recipientOpt.isPresent()) {
//            logger.error("Invalid recipient ID: {}", transaction.getRecipientId());
//            return false;
//        }
//
//        UserRecord sender = senderOpt.get();
//        UserRecord recipient = recipientOpt.get();
        // Get sender and recipient
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        // Validate transaction
        if (sender == null) {
            logger.error("Invalid sender ID: {}", transaction.getSenderId());
            return false;
        }

        if (recipient == null) {
            logger.error("Invalid recipient ID: {}", transaction.getRecipientId());
            return false;
        }


//        UserRecord sender = senderOpt.get();
//        UserRecord recipient = recipientOpt.get();

        // Check if sender has sufficient balance
        if (sender.getBalance() < transaction.getAmount()) {
            logger.error("Insufficient balance for sender {}: {} < {}",
                    sender.getId(), sender.getBalance(), transaction.getAmount());
            return false;
        }

        // Call incentive API to get incentive amount
        Incentive incentive = restTemplate.postForObject(INCENTIVE_API_URL, transaction, Incentive.class);
        float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0;

        logger.info("Received incentive amount: {}", incentiveAmount);

        // Update balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

        // Save updated users
        userRepository.save(sender);
        userRepository.save(recipient);

        // Record transaction
        TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount());
        transactionRepository.save(record);

        logger.info("Transaction processed successfully. New sender balance: {}, New recipient balance: {}",
                sender.getBalance(), recipient.getBalance());

        return true;
    }
}