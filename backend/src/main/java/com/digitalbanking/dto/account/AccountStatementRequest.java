package com.digitalbanking.dto.account;

import lombok.Data;

@Data
public class AccountStatementRequest {
    private String accountNumber;
    private String fromDate;
    private String toDate;
    private Integer page;
    private Integer size;
    private String type;
}
