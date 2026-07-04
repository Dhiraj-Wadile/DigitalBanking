package com.digitalbanking.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    private String baseUrl;
    private String frontendUrl;
    private String supportEmail;
    private String companyName;

    private Mail mail = new Mail();
    private Otp otp = new Otp();
    private Encryption encryption = new Encryption();

    @Data
    public static class Mail {
        private String from;
        private String fromName;
    }

    @Data
    public static class Otp {
        private Integer length;
        private Long expiryMinutes;
        private Integer maxAttempts;
    }

    @Data
    public static class Encryption {
        private String secretKey;
        private String iv;
    }
}
