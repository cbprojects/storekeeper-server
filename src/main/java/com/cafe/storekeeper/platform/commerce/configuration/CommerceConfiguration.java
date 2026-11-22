package com.cafe.storekeeper.platform.commerce.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class CommerceConfiguration {
    @Bean TransactionTemplate commerceTransactions(MongoDatabaseFactory factory) {
        return new TransactionTemplate(new MongoTransactionManager(factory));
    }
}
