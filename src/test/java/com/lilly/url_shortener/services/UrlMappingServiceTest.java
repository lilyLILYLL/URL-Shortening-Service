package com.lilly.url_shortener.services;

import com.lilly.url_shortener.models.UrlMapping;
import com.lilly.url_shortener.repositories.UrlMappingRepository;
import com.lilly.url_shortener.utils.Base62;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlMappingServiceTest {
    @Mock
    private  UrlMappingRepository repository;

    @Mock
    private  SequenceGeneratorService sequenceGenerator;

    MockedStatic<Base62> mockedBase62 = mockStatic(Base62.class);

    @InjectMocks
    private UrlMappingService service;

    @Test
    void test_shortenUrl(){
        // Arrange
        String longUrl = "https://google.com";
        long mockSequenceId =10000014;
        String expectedShortCode = "fxSY";

        // 1. Mock the sequence generator to return a fixed ID
        when(sequenceGenerator.generateSequence(anyString())).thenReturn(mockSequenceId);
        //2. Mock repository.save() to return the object passed to it
        when(repository.save(ArgumentMatchers.any(UrlMapping.class))).thenAnswer((i -> i.getArguments()[0]));
        // Static mock
        mockedBase62.when(() -> Base62.encode(mockSequenceId)).thenReturn(expectedShortCode);

        // Act
        UrlMapping result  = service.shortenUrl(longUrl);

        // Assert
        assertNotNull(result);
        assertEquals(longUrl, result.getLongUrl());
        assertEquals(expectedShortCode, result.getShortCode());
        assertEquals(mockSequenceId, result.getCustomCode());

        // verify dependencies were called
        verify(sequenceGenerator, times(1)).generateSequence(anyString());
        verify(repository, times(1)).save(ArgumentMatchers.any(UrlMapping.class));
        mockedBase62.verify(()  -> Base62.encode(mockSequenceId));
    }

    @Test
    void test_getOriginalUrl(){
        // Arrange
        String shortCode = "fxSY";
        long mockCustomCode =10000014;
        String expectedOriginalUrl = "https://google.com";

        UrlMapping mockMapping = new UrlMapping();
        mockMapping.setLongUrl(expectedOriginalUrl);
        mockMapping.setCustomCode(mockCustomCode);

        mockedBase62.when(() -> Base62.decode(shortCode)).thenReturn(mockCustomCode);
        when(repository.findByCustomCode(mockCustomCode)).thenReturn(Optional.of(mockMapping));

        // Act
        String result  = service.getOriginalUrl(shortCode);

        // Assert
        assertEquals(expectedOriginalUrl, result);

        // Verify Side Effect: Ensure the count was actually incremented
        verify(repository, times(1)).findByCustomCode(mockCustomCode);
        verify(repository, times(1)).incrementAccessCount(mockCustomCode);
        mockedBase62.verify(() -> Base62.decode(shortCode));
    }
    @Test
    void test_getOriginalUrl_urlMappingNotFound(){
        // Arrange
        String shortCode = "invalid";
        long decodedId = 123456;
        mockedBase62.when(() -> Base62.decode(shortCode)).thenReturn(decodedId);
        when(repository.findByCustomCode(decodedId)).thenReturn(Optional.empty()); // Mock Repository to return Empty

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> service.getOriginalUrl(shortCode));

        // verify
        verify(repository, never()).incrementAccessCount(anyLong());

    }
    @Test
    void test_deleteShortUrl(){
        // Arrange
        String shortCode = "abc123";
        long decodedId = 12345678;
        boolean expectedResult =true;

        mockedBase62.when(() -> Base62.decode(shortCode)).thenReturn(decodedId);
        when(repository.existsByCustomCode(decodedId)).thenReturn(true);

        // Act
        boolean result = service.deleteShortUrl(shortCode);

        // Assert
        assertTrue(result,"Should return true when deletion is successful");

        // Verify: Ensure the delete method was actually called
        verify(repository, times(1)).deleteByCustomCode(decodedId);
        mockedBase62.verify(() -> Base62.decode(shortCode));

    }
    @Test
    void test_deleteShortUrl_shouldReturnFalse() {
        // Arrange
        String shortCode = "invalid";
        long decodedId = 999L;


        mockedBase62.when(() -> Base62.decode(shortCode)).thenReturn(decodedId);

        // Mock Repository: "No, this record does not exist"
        when(repository.existsByCustomCode(decodedId)).thenReturn(false);

        // Act
        boolean result = service.deleteShortUrl(shortCode);

        // Assert
        assertFalse(result, "Should return false when record is not found");

        // Verify: Ensure we NEVER tried to delete anything
        verify(repository, never()).deleteByCustomCode(anyLong());

    }
    @Test
    void test_updateShortUrl_whenFound(){
        // Arrange
        String shortCode = "abc";
        long decodedId = 100L;
        String oldUrl = "https://old-site.com";
        String newUrl = "https://new-site.com";

        // Create existing mapping
        UrlMapping existingMapping = new UrlMapping();
        existingMapping.setCustomCode(decodedId);
        existingMapping.setShortCode(shortCode);
        existingMapping.setLongUrl(oldUrl);

        mockedBase62.when(() -> Base62.decode(shortCode)).thenReturn(decodedId);
        when(repository.findByCustomCode(decodedId)).thenReturn(Optional.of(existingMapping));
        //2. Mock repository.save() to return the object passed to it
        when(repository.save(ArgumentMatchers.any(UrlMapping.class))).thenAnswer((i -> i.getArguments()[0]));

        // Act
        UrlMapping result = service.updateShortUrl(shortCode, newUrl);

        // Assert
        assertNotNull(result);
        assertEquals(newUrl, result.getLongUrl(), "The URL should be updated to the new value");
        assertEquals(decodedId, result.getCustomCode(), "The ID should remain unchanged");

        // Verify side effects
        verify(repository, times(1)).findByCustomCode(decodedId);
        verify(repository, times(1)).save(existingMapping); // Verify we saved the exact same object

    }
    @Test
    void test_updateShortUrl_whenNotFound(){
        // Arrange
        String shortCode = "invalid";
        long decodedId = 1233333;
        String newUrl = "https://new-site.com";

        // Mock repository returning empty
        mockedBase62.when(() -> Base62.decode(shortCode)).thenReturn(decodedId);
        when(repository.findByCustomCode(decodedId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class,
                () -> service.updateShortUrl(shortCode, newUrl));

        // Verify we never tried to save
        verify(repository, never()).save(ArgumentMatchers.any());

    }

    @Test
    void test_getAccessStats(){
        String shortCode = "abc123";
        long decodedId = 1233333;
        long expectedCount = 12;

        UrlMapping mockedMapping  = new UrlMapping();
        mockedMapping.setCustomCode(decodedId);
        mockedMapping.setShortCode(shortCode);
        mockedMapping.setAccessCount(expectedCount);


        mockedBase62.when(() -> Base62.decode(shortCode)).thenReturn(decodedId);
        when(repository.findByCustomCode(decodedId)).thenReturn(Optional.of(mockedMapping));

        // Act
        long result = service.getAccessStats(shortCode);

        // Assert
        assertEquals(expectedCount, result);

        // Verify lookup happened
        verify(repository, times(1)).findByCustomCode(decodedId);
        mockedBase62.verify(() -> Base62.decode(shortCode));





    }

    @Test
    void test_getAccessStats_WhenNotFound(){
        // Arrange
        String shortCode = "missing";
        long decodedId = 999L;

        mockedBase62.when(() -> Base62.decode(shortCode)).thenReturn(decodedId);

        // Mock repository returning empty
        when(repository.findByCustomCode(decodedId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class,
                () -> service.getAccessStats(shortCode));

    }
}
