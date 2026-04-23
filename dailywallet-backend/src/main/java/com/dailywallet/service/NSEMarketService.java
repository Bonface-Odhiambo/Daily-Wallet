package com.dailywallet.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class NSEMarketService {

    @Value("${app.market.api-key}")
    private String apiKey;

    @Value("${app.market.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Get stock data for a given NSE symbol
     * Uses Alpha Vantage TIME_SERIES_DAILY function
     * Results are cached for 15 minutes to avoid rate limits
     */
    @Cacheable(value = "stock-history", key = "#symbol")
    public Map<String, Object> getStockData(String symbol) {
        String url = baseUrl + "?function=TIME_SERIES_DAILY" +
                     "&symbol=" + symbol + ".NBO" +
                     "&apikey=" + apiKey +
                     "&outputsize=compact";
        
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        
        // Check for API error messages
        if (response != null && response.containsKey("Error Message")) {
            throw new RuntimeException("Invalid symbol or API error: " + response.get("Error Message"));
        }
        
        if (response != null && response.containsKey("Note")) {
            throw new RuntimeException("API rate limit exceeded: " + response.get("Note"));
        }
        
        return response;
    }

    /**
     * Get current quote for a given NSE symbol
     * Uses Alpha Vantage GLOBAL_QUOTE function
     * Results are cached for 15 minutes to avoid rate limits
     */
    @Cacheable(value = "stock-quote", key = "#symbol")
    public Map<String, Object> getQuote(String symbol) {
        String url = baseUrl + "?function=GLOBAL_QUOTE" +
                     "&symbol=" + symbol + ".NBO" +
                     "&apikey=" + apiKey;
        
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        
        // Check for API error messages
        if (response != null && response.containsKey("Error Message")) {
            throw new RuntimeException("Invalid symbol or API error: " + response.get("Error Message"));
        }
        
        if (response != null && response.containsKey("Note")) {
            throw new RuntimeException("API rate limit exceeded: " + response.get("Note"));
        }
        
        return response;
    }

    /**
     * Get multiple quotes for popular NSE stocks
     * This is a convenience method that calls getQuote for each symbol
     */
    public Map<String, Map<String, Object>> getMultipleQuotes(String[] symbols) {
        Map<String, Map<String, Object>> quotes = new java.util.HashMap<>();
        
        for (String symbol : symbols) {
            try {
                quotes.put(symbol, getQuote(symbol));
            } catch (Exception e) {
                // Log error but continue with other symbols
                System.err.println("Error fetching quote for " + symbol + ": " + e.getMessage());
            }
        }
        
        return quotes;
    }
}
