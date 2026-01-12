package com.lilly.url_shortener.dtos;
import java.time.LocalDateTime;

public class UrlDto{
    // Request DTO
    public record CreateRequest(String longUrl){}

    // Update Request DTO
    public record UpdateRequest(String newLongUrl){}

    // Response DTO
    public record Response(String shortCode,String longUrl, LocalDateTime expiryDate, long accessCount){

    }
    public record AccessStatsResponse(long accessCount){}

    // Error DTO (for exceptions)
    public record Error(String message, int status) {}
}
