package com.cafe.storekeeper.platform.identity;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/** Shared atomic fixed-window counter; never stores a raw email or address. */
@Service
public class AttemptLimiter {
    private final MongoTemplate mongo;
    public AttemptLimiter(MongoTemplate mongo) { this.mongo = mongo; }

    public void check(String key, int limit) {
        long window = Instant.now().getEpochSecond() / 900;
        String id = IdentityService.hash(key + ":" + window);
        Counter counter = mongo.findAndModify(Query.query(Criteria.where("_id").is(id)),
                new Update().inc("attempts", 1).setOnInsert("expiresAt", Instant.ofEpochSecond((window + 2) * 900)),
                FindAndModifyOptions.options().upsert(true).returnNew(true), Counter.class);
        if (counter == null || counter.attempts() > limit) throw new IdentityProblem(429, "Demasiados intentos; intenta más tarde");
    }

    @Document("platform_attempts")
    public record Counter(@Id String id, long attempts, @Indexed(expireAfter = "0s") Instant expiresAt) {}
}
