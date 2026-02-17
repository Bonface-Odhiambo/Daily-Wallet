package com.dailywallet.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class ReferenceNumberGenerator {
    
    private static final Random random = new Random();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    
    public static String generateTransactionReference() {
        String timestamp = LocalDateTime.now().format(formatter);
        int randomNum = 1000 + random.nextInt(9000);
        return "TXN" + timestamp + randomNum;
    }
    
    public static String generateOtp(int length) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}
