package com.lilly.url_shortener.controllers;

import com.lilly.url_shortener.dtos.UrlDto;
import com.lilly.url_shortener.models.UrlMapping;
import com.lilly.url_shortener.services.UrlMappingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/url-mapping")
public class UrlMappingController {
    UrlMappingService service;
    public UrlMappingController(UrlMappingService service){
        this.service = service;
    }
    // Shorten URL
    @PostMapping
    public ResponseEntity<UrlDto.Response> shortenUrl(@RequestBody UrlDto.CreateRequest request){
        String originalUrl = request.longUrl();
        UrlMapping mapping = service.shortenUrl(originalUrl);

        UrlDto.Response responseDto =  new UrlDto.Response(mapping.getShortCode(),originalUrl, mapping.getCreatedAt(), mapping.getAccessCount());

        // Return 201 Created with the body
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

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
    //Delete Short URL
    // The endpoint should return a 204 No Content status code if the short URL was successfully deleted or a 404 Not Found status code if the short URL was not found.
    @DeleteMapping("/{shortCode}")
    public ResponseEntity<String> deleteShortCode(@PathVariable String shortCode){
        boolean deleted = service.deleteShortUrl(shortCode);

        if(deleted){
            return ResponseEntity.ok("Deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found"); // 404 Not Found
    }

    //Update Short URL
    @PutMapping("/{shortCode}")
    public ResponseEntity<UrlDto.Response> updateShortCode(@PathVariable String shortCode,@RequestBody UrlDto.UpdateRequest request){
        // 1. Validate input
        if(request.newLongUrl() == null || request.newLongUrl().isBlank()){
            return ResponseEntity.badRequest().build();
        }
        // 2. Call  service
        UrlMapping updatedMapping = service.updateShortUrl( shortCode, request.newLongUrl());

        return ResponseEntity.ok( new UrlDto.Response(
                    updatedMapping.getShortCode(),
                    updatedMapping.getLongUrl(),
                    updatedMapping.getCreatedAt(),
                    updatedMapping.getAccessCount()));

    }

    //Get Access Stats
    @GetMapping("/stats/{shortCode}")
    public ResponseEntity<UrlDto.AccessStatsResponse> getAccessStats(@PathVariable String shortCode){
        long accessCount = service.getAccessStats(shortCode);
        return  ResponseEntity.ok(new UrlDto.AccessStatsResponse(accessCount));
    }

}
