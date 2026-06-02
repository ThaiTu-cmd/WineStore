package com.doan.WineStore.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@winestore.com}")
    private String from;

    public void sendOtp(String to, String otp) {
        log.info("==============================================");
        log.info("OTP cho email {}: {}", to, otp);
        log.info("==============================================");

        if (mailSender != null) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(from);
                message.setTo(to);
                message.setSubject("WineStore - Mã xác thực OTP");
                message.setText("Mã OTP của bạn là: " + otp + "\n\n"
                    + "Mã có hiệu lực trong 5 phút.\n\n"
                    + "Vui lòng không chia sẻ mã này với bất kỳ ai.\n\n"
                    + "Trân trọng,\nWineStore Team");
                mailSender.send(message);
                log.info("Email OTP đã gửi thành công tới {}", to);
            } catch (Exception e) {
                log.warn("Không thể gửi email tới {}: {}. OTP đã được log ở console.", to, e.getMessage());
            }
        } else {
            log.info("MailSender chưa được cấu hình. OTP sẽ chỉ được log ra console.");
        }
    }
}
