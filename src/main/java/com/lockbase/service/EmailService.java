package com.lockbase.service;

import org.springframework.mail.javamail.JavaMailSender;
import lombok.RequiredArgsConstructor;
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
        <body style="margin:0; padding:50px; font-family:Arial, sans-serif; background:linear-gradient(to right,#6366f1,#fb923c); color:#444;">
            <div style="max-width:480px; margin:30px auto; background-color:#1e2530; border-radius:16px; box-shadow:0 6px 16px rgba(0,0,0,0.15); padding:36px 32px; text-align:center;">
            <h1 style="margin-top:10px; font-size:45px; font-weight:700; color:#818cf8; margin-bottom:24px; letter-spacing:0.5px;">
              Lockbase
            </h1>
            <p style="font-size:16px; color:#d1d5db; margin:0 0 10px 0;">Hi there,</p>
            <p style="font-size:16px; color:#d1d5db; margin:0;">Your one-time password (OTP) is:</p>

            <div style=" font-size: 32px; font-weight: bold; color: #4f46e5; margin: 28px 0; padding: 12px 0; background-color: #f1f5f9; border-radius: 10px; " > %s </div>

            <p style="font-size:14px; color:#9ca3af; margin:0 0 16px 0;">
              This OTP is valid for <b>10 minutes</b>. Please do not share it with anyone.
            </p>

            <p style="font-size:14px; color:#6b7280; margin:0 0 8px 0;">Already have an account?</p>
            <a href="https://yourdomain.com/sign-in" style="color:#3b82f6; text-decoration:none; font-size:15px;">Sign In</a>
            </div>
        </body>
    </html>""".formatted(otp);
    }
}
