package com.lockbase.service;

import org.springframework.mail.javamail.JavaMailSender;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private static final int OTP_LENGTH = 6;

    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public Timestamp getExpiryTimestamp(int minutes) {
        return Timestamp.from(Instant.now().plus(minutes, ChronoUnit.MINUTES));
    }

    public boolean sendOtp(String recipientEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("Your Lockbase OTP Code");

            String htmlContent = buildHtmlContent(otp);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String buildHtmlContent(String otp) {
        return """
            <html>
              <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #1f2937; color: white;">
                <div style="max-width: 500px; margin: auto; background-color: #111827; border-radius: 12px; padding: 30px; text-align: center;">
                  <h1 style="font-size: 36px; font-weight: 700; background: linear-gradient(to right, #6366f1, #fb923c); -webkit-background-clip: text; color: transparent; margin-bottom: 20px;">
                    Lockbase
                  </h1>
                  <p style="font-size: 18px; color: #d1d5db;">Hi there,</p>
                  <p style="font-size: 18px; color: #d1d5db;">Your one-time password (OTP) is:</p>
                  <div style="font-size: 32px; font-weight: bold; color: #60a5fa; margin: 20px 0;">%s</div>
                  <p style="color: #9ca3af;">This code will expire in <b>5 minutes</b>. Do not share it with anyone.</p>
                  <div style="margin-top: 30px; border-top: 1px solid #374151; padding-top: 20px;">
                    <p style="font-size: 14px; color: #6b7280;">Already have an account?</p>
                    <a href="https://yourdomain.com/sign-in" style="color: #3b82f6; text-decoration: none; font-size: 16px;">Sign In</a>
                  </div>
                </div>
              </body>
            </html>
            """.formatted(otp);
    }
}
