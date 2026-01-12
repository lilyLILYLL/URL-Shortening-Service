package com.lilly.url_shortener.services;

import com.lilly.url_shortener.models.DatabaseSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SequenceGeneratorService {
    @Autowired private MongoOperations mongoOperations;
    public long generateSequence(String seqName){
        if (!mongoOperations.exists(Query.query(Criteria.where("_id").is(seqName)), DatabaseSequence.class)) {
            // 2. Create it with starting value - 1 (e.g., 9999)
            DatabaseSequence seq = new DatabaseSequence();
            seq.setId(seqName);
            seq.setSeq(9999999);
            mongoOperations.save(seq);
        }

        // 3. Now run the standard increment
        DatabaseSequence counter = mongoOperations.findAndModify(
                Query.query(Criteria.where("_id").is(seqName)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                DatabaseSequence.class
        );
        return counter.getSeq();
    }
}
