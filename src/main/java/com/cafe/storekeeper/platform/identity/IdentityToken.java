package com.cafe.storekeeper.platform.identity;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("platform_identity_tokens")
public record IdentityToken(@Id String hash, String accountId, String purpose,
        @Indexed(expireAfter = "0s") Instant expiresAt, Instant issuedAt, long credentialVersion) {}
