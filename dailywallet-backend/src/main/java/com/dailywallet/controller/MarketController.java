package com.dailywallet.controller;

import com.dailywallet.dto.response.ApiResponse;
import com.dailywallet.service.NSEMarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/market")
public class MarketController {

    @Autowired
    private NSEMarketService marketService;

    /**
     * Get current quote for a specific NSE stock symbol
     * Example: GET /api/market/quote/SCOM
     */
    @GetMapping("/quote/{symbol}")
    public ResponseEntity<?> getQuote(@PathVariable String symbol) {
        try {
            Map<String, Object> quote = marketService.getQuote(symbol.toUpperCase());
            return ResponseEntity.ok(new ApiResponse<>(true, "Quote retrieved successfully", quote));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error fetching quote: " + e.getMessage(), null));
        }
    }

    /**
     * Get historical stock data for a specific NSE stock symbol
     * Example: GET /api/market/history/SCOM
     */
    @GetMapping("/history/{symbol}")
    public ResponseEntity<?> getHistory(@PathVariable String symbol) {
        try {
            Map<String, Object> history = marketService.getStockData(symbol.toUpperCase());
            return ResponseEntity.ok(new ApiResponse<>(true, "History retrieved successfully", history));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error fetching history: " + e.getMessage(), null));
        }
    }

    /**
     * Get quotes for multiple popular NSE stocks
     * Example: GET /api/market/quotes?symbols=SCOM,EQTY,KCB,COOP,ABSA,EABL
     */
    @GetMapping("/quotes")
    public ResponseEntity<?> getMultipleQuotes(@RequestParam String symbols) {
        try {
            String[] symbolArray = symbols.split(",");
            Map<String, Map<String, Object>> quotes = marketService.getMultipleQuotes(symbolArray);
            return ResponseEntity.ok(new ApiResponse<>(true, "Quotes retrieved successfully", quotes));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error fetching quotes: " + e.getMessage(), null));
        }
    }

    /**
     * Health check for market data service
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Market data service is operational", null));
    }
}
