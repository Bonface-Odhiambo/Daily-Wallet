package com.dailywallet.dto.response;

import com.dailywallet.model.enums.TransactionCategory;
import com.dailywallet.model.enums.TransactionStatus;
import com.dailywallet.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private TransactionType transactionType;
    private TransactionCategory category;
    private TransactionStatus status;
    private BigDecimal amount;
    private BigDecimal fee;
    private String referenceNumber;
    private String mpesaReceiptNumber;
    private String recipientPhoneNumber;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
