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

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

}
