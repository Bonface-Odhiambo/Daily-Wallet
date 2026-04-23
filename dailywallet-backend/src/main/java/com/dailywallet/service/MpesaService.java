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

    /**
     * Initiate STK Push for payment
     */
    public Map<String, Object> initiateStkPush(String phoneNumber, String phoneNumber2, Integer amount, String accountReference, String transactionDesc) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String accessToken = getAccessToken();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String password = Base64.getEncoder().encodeToString(
                (shortcode + passkey + timestamp).getBytes(StandardCharsets.UTF_8)
            );

            Map<String, Object> stkRequest = new HashMap<>();
            stkRequest.put("BusinessShortCode", shortcode);
            stkRequest.put("Password", password);
            stkRequest.put("Timestamp", timestamp);
            stkRequest.put("TransactionType", "CustomerPayBillOnline");
            stkRequest.put("Amount", amount);
            stkRequest.put("PartyA", phoneNumber2);
            stkRequest.put("PartyB", shortcode);
            stkRequest.put("PhoneNumber", phoneNumber2);
            stkRequest.put("CallBackURL", "http://localhost:8080/api/mpesa/callback/stk");
            stkRequest.put("AccountReference", accountReference);
            stkRequest.put("TransactionDesc", transactionDesc);

            WebClient webClient = webClientBuilder.baseUrl(getApiUrl()).build();
            
            Map<String, Object> response = webClient.post()
                .uri("/mpesa/stkpush/v1/processrequest")
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(stkRequest)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

            log.info("STK Push response: {}", response);
            
            result.put("success", true);
            result.put("checkoutRequestId", response.get("CheckoutRequestID"));
            result.put("merchantRequestId", response.get("MerchantRequestID"));
            result.put("responseCode", response.get("ResponseCode"));
            result.put("responseDescription", response.get("ResponseDescription"));
            result.put("customerMessage", response.get("CustomerMessage"));
            
        } catch (Exception e) {
            log.error("Error initiating STK Push", e);
            result.put("success", false);
            result.put("error", "Failed to initiate STK Push: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Initiate M-Pesa deposit to wallet
     */
    public Map<String, Object> initiateDeposit(String phoneNumber, String phoneNumber2, Integer amount) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String accountReference = "JIMUDU-" + phoneNumber;
            String transactionDesc = "Deposit to Jimudu Wallet";
            
            Map<String, Object> stkResult = initiateStkPush(phoneNumber, phoneNumber2, amount, accountReference, transactionDesc);
            
            result.put("success", stkResult.get("success"));
            result.put("checkoutRequestId", stkResult.get("checkoutRequestId"));
            result.put("amount", amount);
            result.put("phoneNumber", phoneNumber2);
            result.put("transactionType", "DEPOSIT");
            
        } catch (Exception e) {
            log.error("Error initiating deposit", e);
            result.put("success", false);
            result.put("error", "Failed to initiate deposit: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Initiate M-Pesa withdrawal from wallet
     */
    public Map<String, Object> initiateWithdrawal(String phoneNumber, String phoneNumber2, Integer amount) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String accessToken = getAccessToken();
            String securityCredential = getSecurityCredential();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

            Map<String, Object> b2cRequest = new HashMap<>();
            b2cRequest.put("InitiatorName", "DailyWalletAPI");
            b2cRequest.put("SecurityCredential", securityCredential);
            b2cRequest.put("CommandID", "BusinessPayment");
            b2cRequest.put("Amount", amount);
            b2cRequest.put("PartyA", shortcode);
            b2cRequest.put("PartyB", phoneNumber2);
            b2cRequest.put("Remarks", "Jimudu Wallet Withdrawal");
            b2cRequest.put("QueueTimeOutURL", "http://localhost:8080/api/mpesa/callback/b2c/timeout");
            b2cRequest.put("ResultURL", "http://localhost:8080/api/mpesa/callback/b2c/result");
            b2cRequest.put("Occasion", "Withdrawal");

            WebClient webClient = webClientBuilder.baseUrl(getApiUrl()).build();
            
            Map<String, Object> response = webClient.post()
                .uri("/mpesa/b2c/v1/paymentrequest")
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(b2cRequest)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

            log.info("B2C withdrawal response: {}", response);
            
            result.put("success", true);
            result.put("conversationId", response.get("ConversationID"));
            result.put("originatorConversationId", response.get("OriginatorConversationID"));
            result.put("responseCode", response.get("ResponseCode"));
            result.put("responseDescription", response.get("ResponseDescription"));
            result.put("amount", amount);
            result.put("phoneNumber", phoneNumber2);
            result.put("transactionType", "WITHDRAWAL");
            
        } catch (Exception e) {
            log.error("Error initiating withdrawal", e);
            result.put("success", false);
            result.put("error", "Failed to initiate withdrawal: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Check transaction status
     */
    public Map<String, Object> checkTransactionStatus(String checkoutRequestId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String accessToken = getAccessToken();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String password = Base64.getEncoder().encodeToString(
                (shortcode + passkey + timestamp).getBytes(StandardCharsets.UTF_8)
            );

            Map<String, Object> statusRequest = new HashMap<>();
            statusRequest.put("BusinessShortCode", shortcode);
            statusRequest.put("Password", password);
            statusRequest.put("Timestamp", timestamp);
            statusRequest.put("CheckoutRequestID", checkoutRequestId);

            WebClient webClient = webClientBuilder.baseUrl(getApiUrl()).build();
            
            Map<String, Object> response = webClient.post()
                .uri("/mpesa/stkpushquery/v1/query")
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(statusRequest)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

            log.info("Transaction status response: {}", response);
            
            result.put("success", true);
            result.put("checkoutRequestId", checkoutRequestId);
            result.put("responseCode", response.get("ResponseCode"));
            result.put("responseDescription", response.get("ResponseDescription"));
            result.put("resultCode", response.get("ResultCode"));
            result.put("resultDesc", response.get("ResultDesc"));
            
        } catch (Exception e) {
            log.error("Error checking transaction status", e);
            result.put("success", false);
            result.put("error", "Failed to check transaction status: " + e.getMessage());
        }
        
        return result;
    }
}
