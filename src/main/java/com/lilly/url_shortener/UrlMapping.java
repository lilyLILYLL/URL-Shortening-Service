package com.lilly.url_shortener;

import com.lilly.url_shortener.utils.Base62;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "UrlMapping")
public class UrlMapping {
    @Id
    private String id;

    @Indexed(unique = true)
    private long customCode;// "Business" Primary Key

    private String longUrl;
    private String shortCode;
    private long accessCount;
    private LocalDateTime createdAt;

    public UrlMapping(){
        this.createdAt = LocalDateTime.now();

    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
        this.customCode = Base62.decode(shortCode);
    }
}
