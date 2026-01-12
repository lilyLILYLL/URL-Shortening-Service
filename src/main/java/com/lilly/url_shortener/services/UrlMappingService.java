package com.lilly.url_shortener.services;

import com.lilly.url_shortener.models.UrlMapping;
import com.lilly.url_shortener.UrlMappingRepository;
import com.lilly.url_shortener.utils.Base62;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UrlMappingService {
    private final UrlMappingRepository repository;
    private final SequenceGeneratorService sequenceGenerator;

    public UrlMappingService(UrlMappingRepository repository,SequenceGeneratorService sequenceGenerator){
        this.repository = repository;
        this.sequenceGenerator = sequenceGenerator;
    }

    // shorten url
    public UrlMapping shortenUrl(String longUrl){
        // 1. Get the next unique number (e.g., 10001)
        long seq = sequenceGenerator.generateSequence("short_code_seq");

        // 2. Convert to Base62 string (e.g., "abc")
        String shortCode = Base62.encode(seq);

        // 3. Save
        UrlMapping mapping = new UrlMapping();
        mapping.setLongUrl(longUrl);
        mapping.setShortCode(shortCode);
        mapping.setCustomCode(seq);
        mapping.setAccessCount(0);
        return repository.save(mapping);
    }

    // get original url
    public String getOriginalUrl(String shortCode){
        long key = Base62.decode(shortCode);
        UrlMapping mapping =  repository.findByCustomCode(key).orElseThrow(() -> new NoSuchElementException("URL not found for code " + shortCode));
        return mapping.getLongUrl();
    }

    // delete short url
    public boolean deleteShortUrl(String shortCode){
        long key = Base62.decode(shortCode);
        if(repository.existsByCustomCode(key)){
            repository.deleteByCustomCode(key);
            return true;
        }
        return false;
    }

    // update a short url
    public UrlMapping updateShortUrl(String shortCode, String newLongUrl){
        // 1. Find the existing record by custom code
        long key = Base62.decode(shortCode);
        UrlMapping mapping = repository.findByCustomCode(key).orElseThrow(() -> new NoSuchElementException("Short URL not found: " + shortCode));

        // 2. update the long Url
        mapping.setLongUrl(newLongUrl);

        // 3. Save to the database
        return  repository.save(mapping);

    }


}
