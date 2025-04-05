package com.example.urlshortener.service;

import com.example.urlshortener.exception.InvalidUrlException;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepository;
import com.example.urlshortener.util.Base62Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UrlShortenerService {

    @Autowired
    private UrlMappingRepository urlRepo;

    @Autowired
    private StringRedisTemplate redis;


    public String shortenUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.isBlank()) {
            throw new InvalidUrlException("URL cannot be empty");
        }

        if (!isValidUrl(originalUrl)) {
            throw new InvalidUrlException("Invalid URL format");
        }

        UrlMapping mapping = new UrlMapping();
        mapping.setOriginalUrl(originalUrl);
        mapping = urlRepo.save(mapping);

        String shortCode = Base62Encoder.encode(mapping.getId());
        mapping.setShortCode(shortCode);
        urlRepo.save(mapping);

        try {
            redis.opsForValue().set(shortCode, originalUrl, 1, TimeUnit.HOURS);
        } catch (Exception e) {
            System.err.println("Redis error: " + e.getMessage());
            // log warning but don’t fail the service
        }

        return shortCode;
    }

    private boolean isValidUrl(String url) {
        try {
            new java.net.URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getOriginalUrl(String shortCode) {
        String fromCache = redis.opsForValue().get(shortCode);

        // ✅ Always update click count in the DB
        urlRepo.findByShortCode(shortCode).ifPresent(url -> {
            url.setClickCount(url.getClickCount() + 1);
            urlRepo.save(url); // persist the updated count
        });

        // ✅ If in cache, return directly
        if (fromCache != null) {
            return fromCache;
        }

        // ✅ Otherwise, get from DB, cache it, and return
        return urlRepo.findByShortCode(shortCode)
                .map(url -> {
                    redis.opsForValue().set(shortCode, url.getOriginalUrl(), 1, TimeUnit.HOURS);
                    return url.getOriginalUrl();
                })
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found"));
    }
    public Integer getClickCount(String shortCode) {
        // Try to get the click count from Redis cache first
        String clickCountFromCache = redis.opsForValue().get(shortCode + ":clickCount");

        if (clickCountFromCache != null) {
            // If cache hit, return the cached value (convert it to int)
            return Integer.parseInt(clickCountFromCache);
        } else {
            // If not in cache, fetch it from the database
            Optional<UrlMapping> urlMappingOpt = urlRepo.findByShortCode(shortCode);

            if (urlMappingOpt.isPresent()) {
                UrlMapping urlMapping = urlMappingOpt.get();

                // Store the click count in cache for future use (TTL can be added if needed)
                redis.opsForValue().set(shortCode + ":clickCount", String.valueOf(urlMapping.getClickCount()));

                // Return the click count from database
                return urlMapping.getClickCount();
            } else {
                // Handle the case when the shortCode doesn't exist
                throw new UrlNotFoundException("Short URL not found: " + shortCode);
            }
        }
    }



}
