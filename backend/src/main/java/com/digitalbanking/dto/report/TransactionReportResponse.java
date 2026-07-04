package com.digitalbanking.dto.report;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class TransactionReportResponse {
    private String reportType;
    private String fromDate;
    private String toDate;
    private BigDecimal totalAmount;
    private Integer totalCount;
    private List<ReportEntry> entries;

    @Data
    public static class ReportEntry {
        private String date;
        private String type;
        private Integer count;
        private BigDecimal amount;
    }
}
