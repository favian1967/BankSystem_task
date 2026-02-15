package com.favian.bank_test_case.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Service
public class EncryptionService {

    @Value("${card.encryption-key:12345678901234567890123456789012}")
    private String secret;

    private SecretKeySpec secretKeySpec;

    @PostConstruct
    public void init() {
        log.info("Encryption key length: {} characters", secret.length());
        
        if (secret.length() < 32) {
            secret = String.format("%-32s", secret).replace(' ', '0');
            log.warn("Encryption key was too short, padded to 32 characters");
        } else if (secret.length() > 32) {
            secret = secret.substring(0, 32);
            log.warn("Encryption key was too long, truncated to 32 characters");
        }
        
        secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
        log.info("EncryptionService initialized successfully");
    }

    public String encrypt(String value) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String encryptedValue) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decoded = Base64.getDecoder().decode(encryptedValue);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
