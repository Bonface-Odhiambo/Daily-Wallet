package com.dailywallet.controller;

import com.dailywallet.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mpesa/callback")
@RequiredArgsConstructor
@Slf4j
public class MpesaCallbackController {

    private final TransactionService transactionService;

    @PostMapping("/stk")
    public ResponseEntity<Map<String, String>> stkCallback(@RequestBody Map<String, Object> payload) {
        log.info("STK Push callback received: {}", payload);

        try {
            Map<String, Object> body = (Map<String, Object>) payload.get("Body");
            Map<String, Object> stkCallback = (Map<String, Object>) body.get("stkCallback");
            
            Integer resultCode = (Integer) stkCallback.get("ResultCode");
            
            if (resultCode == 0) {
                Map<String, Object> callbackMetadata = (Map<String, Object>) stkCallback.get("CallbackMetadata");
                Map<String, Object>[] items = (Map<String, Object>[]) callbackMetadata.get("Item");
                
                String mpesaReceiptNumber = null;
                String accountReference = null;
                
                for (Map<String, Object> item : items) {
                    String name = (String) item.get("Name");
                    if ("MpesaReceiptNumber".equals(name)) {
                        mpesaReceiptNumber = (String) item.get("Value");
                    } else if ("AccountReference".equals(name)) {
                        accountReference = (String) item.get("Value");
                    }
                }
                
                if (accountReference != null && mpesaReceiptNumber != null) {
                    transactionService.completeDeposit(accountReference, mpesaReceiptNumber);
                }
            } else {
                log.warn("STK Push failed with result code: {}", resultCode);
            }
        } catch (Exception e) {
            log.error("Error processing STK callback", e);
        }

        return ResponseEntity.ok(Map.of("ResultCode", "0", "ResultDesc", "Success"));
    }

    @PostMapping("/b2c/result")
    public ResponseEntity<Map<String, String>> b2cResultCallback(@RequestBody Map<String, Object> payload) {
        log.info("B2C result callback received: {}", payload);
        return ResponseEntity.ok(Map.of("ResultCode", "0", "ResultDesc", "Success"));
    }

    @PostMapping("/b2c/timeout")
    public ResponseEntity<Map<String, String>> b2cTimeoutCallback(@RequestBody Map<String, Object> payload) {
        log.info("B2C timeout callback received: {}", payload);
        return ResponseEntity.ok(Map.of("ResultCode", "0", "ResultDesc", "Success"));
    }
}
