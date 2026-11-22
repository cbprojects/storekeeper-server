package com.cafe.storekeeper.platform.identity;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("platform_accounts")
public record Account(@Id String id, @Indexed(unique = true) String email, String name,
        String passwordHash, boolean verified, long credentialVersion, Instant createdAt,
        @Indexed(unique=true, partialFilter="{'googleSubject':{'$type':'string'}}") String googleSubject) {}
