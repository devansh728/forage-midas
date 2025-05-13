package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.jpmc.midascore.service.TransactionService;

import org.springframework.beans.factory.annotation.Autowired;

@Component
public class kafkaConsumer {

    @Autowired
    private TransactionService transactionService;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void listen(Transaction transFull) {

        System.out.println("Transaction Received: " + transFull);

        // Process and validate the transaction
        boolean success = transactionService.processTransaction(transFull);

        if (success) {
            System.out.println("Transaction processed successfully: " + transFull);
        } else {
            System.out.println("Transaction validation failed: " + transFull);
        }

    }
}
