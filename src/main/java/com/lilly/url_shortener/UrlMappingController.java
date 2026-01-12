package com.lilly.url_shortener;

import com.lilly.url_shortener.dtos.UrlDto;
import com.lilly.url_shortener.services.UrlMappingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UrlMappingController {
    UrlMappingService service;
    public UrlMappingController(UrlMappingService service){
        this.service = service;
    }
    // Shorten URL
    @PostMapping("/api/shorten")
    public UrlDto.Response shortenUrl(@RequestBody  UrlDto.Request request){
        String originalUrl = request.longUrl();
        UrlMapping mapping = service.shortenUrl(originalUrl);

        return new UrlDto.Response(mapping.getShortCode(),originalUrl, mapping.getCreatedAt(), mapping.getAccessCount());

    }

    // Retrieve Original Url
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> retrieveOriginalUrl(@PathVariable String shortCode){
        String originalUrl =   service.getOriginalUrl(shortCode);

        // 2. Perform the redirect (302 Found)
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();

    }

}
