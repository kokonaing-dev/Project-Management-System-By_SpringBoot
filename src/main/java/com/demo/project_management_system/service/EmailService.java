package com.demo.project_management_system.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {


    private final JavaMailSender mailSender;

    public int generateOtp(int length) {
        String numbers = "0123456789";
        Random random = new Random();
        char[] otp = new char[length];

        for (int i = 0; i < length; i++) {
            otp[i] = numbers.charAt(random.nextInt(numbers.length()));
        }

        String otpString = new String(otp);
        return Integer.parseInt(otpString);
    }

    public boolean sendOtpEmail(String toEmail, int otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("shalchat2023@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("OTP for Forgot password");
            String text = "Your OTP is: " + otp;
            helper.setText(text);

            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean sendinviteLink(String toEmail,String inviteLink) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("shalchat2023@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("invite link for User Registration");
            String text = "Please use the following link to complete your registration: " + inviteLink;
            helper.setText(text);

            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}

