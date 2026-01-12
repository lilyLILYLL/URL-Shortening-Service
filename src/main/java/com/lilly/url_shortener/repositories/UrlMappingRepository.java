package com.lilly.url_shortener.repositories;

import com.lilly.url_shortener.models.UrlMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlMappingRepository extends MongoRepository<UrlMapping, String> {

    Optional<UrlMapping> findByCustomCode(long customCode);
    boolean existsByCustomCode(long customCode);
    void deleteByCustomCode(long customCode);

    // Finds by customCode and increments 'accessCount' by 1
    @org.springframework.data.mongodb.repository.Query(value = "{'customCode':?0}")
    @org.springframework.data.mongodb.repository.Update(value = "{'$inc':{accessCount:1}}")
    void incrementAccessCount(long customCode);

}
