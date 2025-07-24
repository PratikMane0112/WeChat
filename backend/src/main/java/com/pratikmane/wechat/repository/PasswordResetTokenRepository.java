package com.pratikmane.wechat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pratikmane.wechat.model.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

	PasswordResetToken findByToken(String token);

}
