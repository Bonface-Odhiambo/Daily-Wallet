package com.dailywallet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpesaService {

    private final WebClient.Builder webClientBuilder;

    @Value("${app.mpesa.consumer-key}")
    private String consumerKey;

    @Value("${app.mpesa.consumer-secret}")
    private String consumerSecret;

    @Value("${app.mpesa.shortcode}")
    private String shortcode;

    @Value("${app.mpesa.passkey}")
    private String passkey;

    @Value("${app.mpesa.environment}")
    private String environment;

    @Value("${app.mpesa.callback-url}")
    private String callbackUrl;

    private String getApiUrl() {
        return "production".equalsIgnoreCase(environment) 
            ? "https://api.safaricom.co.ke" 
            : "https://sandbox.safaricom.co.ke";
    }

    public void initiateStkPush(String phoneNumber, BigDecimal amount, String referenceNumber) {
        String accessToken = getAccessToken();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String password = Base64.getEncoder().encodeToString(
            (shortcode + passkey + timestamp).getBytes(StandardCharsets.UTF_8)
        );

        Map<String, Object> request = new HashMap<>();
        request.put("BusinessShortCode", shortcode);
        request.put("Password", password);
        request.put("Timestamp", timestamp);
        request.put("TransactionType", "CustomerPayBillOnline");
        request.put("Amount", amount.intValue());
        request.put("PartyA", phoneNumber);
        request.put("PartyB", shortcode);
        request.put("PhoneNumber", phoneNumber);
        request.put("CallBackURL", callbackUrl + "/stk");
        request.put("AccountReference", referenceNumber);
        request.put("TransactionDesc", "DailyWallet Deposit");

        try {
            WebClient webClient = webClientBuilder.baseUrl(getApiUrl()).build();
            
            Map<String, Object> response = webClient.post()
                .uri("/mpesa/stkpush/v1/processrequest")
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

            log.info("STK Push response for {} environment: {}", environment, response);
        } catch (Exception e) {
            log.error("Error initiating STK push", e);
            throw new RuntimeException("Failed to initiate M-Pesa payment", e);
        }
    }

    public void initiateB2C(String phoneNumber, BigDecimal amount, String referenceNumber) {
        String accessToken = getAccessToken();

        Map<String, Object> request = new HashMap<>();
        request.put("InitiatorName", "DailyWallet");
        request.put("SecurityCredential", getSecurityCredential());
        request.put("CommandID", "BusinessPayment");
        request.put("Amount", amount.intValue());
        request.put("PartyA", shortcode);
        request.put("PartyB", phoneNumber);
        request.put("Remarks", "DailyWallet Withdrawal");
        request.put("QueueTimeOutURL", callbackUrl + "/b2c/timeout");
        request.put("ResultURL", callbackUrl + "/b2c/result");
        request.put("Occasion", referenceNumber);

        try {
            WebClient webClient = webClientBuilder.baseUrl(getApiUrl()).build();
            
            Map<String, Object> response = webClient.post()
                .uri("/mpesa/b2c/v1/paymentrequest")
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

            log.info("B2C response for {} environment: {}", environment, response);
        } catch (Exception e) {
            log.error("Error initiating B2C", e);
            throw new RuntimeException("Failed to process withdrawal", e);
        }
    }

    private String getAccessToken() {
        try {
            String auth = consumerKey + ":" + consumerSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            WebClient webClient = webClientBuilder.baseUrl(getApiUrl()).build();
            
            log.info("Authenticating with M-Pesa {} environment", environment);
            
            Map<String, Object> response = webClient.get()
                .uri("/oauth/v1/generate?grant_type=client_credentials")
                .header("Authorization", "Basic " + encodedAuth)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

            return (String) response.get("access_token");
        } catch (Exception e) {
            log.error("Error getting access token from {} environment", environment, e);
            throw new RuntimeException("Failed to authenticate with M-Pesa", e);
        }
    }

    private String getSecurityCredential() {
        return Base64.getEncoder().encodeToString("DailyWalletSecurityCredential".getBytes(StandardCharsets.UTF_8));
    }
}
