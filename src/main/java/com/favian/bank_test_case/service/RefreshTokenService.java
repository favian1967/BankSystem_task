package com.favian.bank_test_case.service;

import com.favian.bank_test_case.entity.RefreshToken;
import com.favian.bank_test_case.entity.User;
import com.favian.bank_test_case.exception.exceptions.RefreshTokenExpiredException;
import com.favian.bank_test_case.exception.exceptions.RefreshTokenNotFoundException;
import com.favian.bank_test_case.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration:86400000}")
    private long refreshExpiration;

    public String createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpiration));
        
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RefreshTokenNotFoundException());
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException();
        }
        return token;
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
