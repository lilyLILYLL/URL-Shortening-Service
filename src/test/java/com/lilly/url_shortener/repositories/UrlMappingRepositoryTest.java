package com.lilly.url_shortener.repositories;


import com.lilly.url_shortener.models.UrlMapping;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
public class UrlMappingRepositoryTest {
    @Autowired
    private UrlMappingRepository repository;

    @Test
    void test_findByCustomCode(){
        // Arrange
        UrlMapping mapping = new UrlMapping();
        mapping.setCustomCode(12345446);
        mapping.setLongUrl("https://google.com");
        repository.save(mapping);

        // Act
        Optional<UrlMapping> found = repository.findByCustomCode(12345446);

        // Assert
        assertTrue(found.isPresent());
        assertEquals("https://google.com", found.get().getLongUrl());
    }

    @Test
    void test_existsByCustomCode(){
        // Arrange
        UrlMapping mapping = new UrlMapping();
        mapping.setCustomCode(12345446);
        mapping.setLongUrl("https://google.com");
        repository.save(mapping);

        // Act
        boolean result = repository.existsByCustomCode(12345446);

        // Assert
        assertTrue(result);
    }

}
