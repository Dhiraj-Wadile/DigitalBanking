package com.digitalbanking.util;

import java.security.SecureRandom;
import java.util.UUID;

public class ReferenceGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateAccountNumber() {
        return "DB" + System.currentTimeMillis() + RANDOM.nextInt(1000);
    }

    public static String generateTransactionReference() {
        return "TXN" + System.currentTimeMillis() + RANDOM.nextInt(10000);
    }

    public static String generatePaymentReference() {
        return "PAY" + System.currentTimeMillis() + RANDOM.nextInt(10000);
    }

    public static String generateCardNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        int checkDigit = calculateLuhnCheckDigit(sb.toString());
        sb.append(checkDigit);
        return sb.toString();
    }

    private static int calculateLuhnCheckDigit(String number) {
        int sum = 0;
        boolean alternate = true;
        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));
            if (alternate) {
                digit *= 2;
                if (digit > 9) digit -= 9;
            }
            sum += digit;
            alternate = !alternate;
        }
        return (10 - (sum % 10)) % 10;
    }

    public static String generateLoanAccountNumber() {
        return "LN" + System.currentTimeMillis() + RANDOM.nextInt(1000);
    }

    public static String generateCustomerNumber() {
        return "CUST" + System.currentTimeMillis() + RANDOM.nextInt(100);
    }

    public static String generateWalletNumber() {
        return "WLT" + System.currentTimeMillis() + RANDOM.nextInt(1000);
    }

    public static String generateOtp(int length) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(RANDOM.nextInt(10));
        }
        return otp.toString();
    }

    public static String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    public static String generateIdempotencyKey() {
        return UUID.randomUUID().toString();
    }
}
