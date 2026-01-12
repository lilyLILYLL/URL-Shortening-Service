package com.lilly.url_shortener;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlMappingRepository extends MongoRepository<UrlMapping, String> {

    Optional<UrlMapping> findByCustomCode(long customCode);
    boolean existsByCustomCode(long customCode);
    long deleteByCustomCode(long customCode);

}
