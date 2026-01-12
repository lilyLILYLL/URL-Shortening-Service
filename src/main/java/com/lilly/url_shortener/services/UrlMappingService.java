package com.lilly.url_shortener.services;

import com.lilly.url_shortener.UrlMapping;
import com.lilly.url_shortener.UrlMappingRepository;
import com.lilly.url_shortener.dtos.UrlDto;
import com.lilly.url_shortener.utils.Base62;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UrlMappingService {
    UrlMappingRepository repository;

    public UrlMappingService(UrlMappingRepository repository){
        this.repository = repository;
    }

    // shorten url service
    public UrlMapping shortenUrl(String longUrl){
        UrlMapping mapping = new UrlMapping();
        mapping.setLongUrl(longUrl);
        mapping.setShortCode(Base62.createRandomShortCode());
        mapping.setAccessCount(0);
        return repository.save(mapping);
    }
    public String getOriginalUrl(String shortCode){
        long key = Base62.decode(shortCode);
        UrlMapping mapping =  repository.findByCustomCode(key).orElseThrow(() -> new NoSuchElementException("URL not found for code " + shortCode));
        return mapping.getLongUrl();
    }

    public boolean deleteShortUrl(String shortCode){
        long key = Base62.decode(shortCode);
        if(repository.existsByCustomCode(key)){
            repository.deleteByCustomCode(key);
            return true;
        }
        return false;
    }


}
