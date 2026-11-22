package com.cafe.storekeeper.platform.identity;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountRepository extends MongoRepository<Account, String> {
    Optional<Account> findByEmail(String email);
    Optional<Account> findByGoogleSubject(String subject);
}
