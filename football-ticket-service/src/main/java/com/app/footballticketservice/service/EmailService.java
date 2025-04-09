package com.app.footballticketservice.service;

import com.app.footballticketservice.config.SystemConfigService;
import com.app.footballticketservice.dto.BookSendEmailDTO;
import com.app.footballticketservice.model.OtpEmail;
import com.app.footballticketservice.model.User;
import com.app.footballticketservice.utils.Constants;
import com.app.footballticketservice.utils.ImageUploader;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.stringtemplate.v4.ST;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

@Log
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EmailService {
    private static final int NUMBER_SEND_EMAIL_THREADS = 10;
    private static final String MAIL = "mail";

    SystemConfigService systemConfigService;
    ExecutorService sendEmailExecutor = Executors.newFixedThreadPool(NUMBER_SEND_EMAIL_THREADS);
    OtpService otpService;

    public void sendEmailAsync(JavaMailSenderImpl mailSender, MimeMessage mimeMessage) {
        sendEmailExecutor.submit(() -> {
            try {
                mailSender.send(mimeMessage);
            } catch (Exception ex) {
                log.log(
                        Level.SEVERE,
                        String.format(
                                "-sendMailAsync : Error while sending email: Info mailSender {from: %s, host: %s, port: %s}",
                                mailSender.getUsername(),
                                mailSender.getHost(),
                                mailSender.getPort()
                        ),
                        ex
                );
            }
        });
    }

    public void sendEmailWelcome(User user) throws MessagingException {
        var subject = systemConfigService.getConfigValue(Constants.EMAIL_SUBJECT_REGISTER_WELCOME);
        var emailContent = buildEmail(Constants.EMAIL_REGISTER_WELCOME_TEMPLATE, Map.of("email", user.getEmail()));

        send(subject, emailContent, user.getEmail());
    }

    public void sendEmailOtp(String email) throws MessagingException {
        var subject = systemConfigService.getConfigValue(Constants.EMAIL_SUBJECT_OTP);
        var generatedOtp = otpService.generateOtp(email);
        var otpEmail = OtpEmail.builder()
                               .email(email)
                               .otpNo(generatedOtp)
                               .build();
        var emailContent = buildEmail(Constants.EMAIL_OTP_TEMPLATE, Map.of(MAIL, otpEmail));
        send(subject, emailContent, email);
    }

    public void send(String subject, String content, String email) throws MessagingException {
        var mailSender = buildJavaMailSender();
        var mailMessage = mailSender.createMimeMessage();
        var mimeMessageHelper = new MimeMessageHelper(mailMessage, "UTF-8");
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setText(content, true);
        mimeMessageHelper.setSubject(subject);
        sendEmailAsync(mailSender, mailMessage);
    }

    public void sendBooking(BookSendEmailDTO dto, User user) throws MessagingException, IOException {
        var homeLogo = ImageUploader.uploadImage(dto.getHomeLogo());
        var awayLogo = ImageUploader.uploadImage(dto.getAwayLogo());
        dto.setHomeLogo(homeLogo);
        dto.setAwayLogo(awayLogo);
        var content = buildEmail(Map.of("model", dto));
        var subject = "Bill booking ticket for match " + dto.getHomeTeam() + " vs " + dto.getAwayTeam();
        send(subject, content, user.getEmail());
    }


    private JavaMailSenderImpl buildJavaMailSender() {
        var host = systemConfigService.getConfigValue(Constants.EMAIL_HOST);
        var port = Integer.parseInt(systemConfigService.getConfigValue(Constants.EMAIL_PORT));
        var username = systemConfigService.getConfigValue(Constants.EMAIL_USERNAME);
        var password = systemConfigService.getConfigValue(Constants.EMAIL_PASSWORD);
        var mailSender = new JavaMailSenderImpl();
        var mailProperties = buildJavaMailProperties();
        mailSender.setJavaMailProperties(mailProperties);
        mailSender.setProtocol("smtp");
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setDefaultEncoding("UTF-8");
        return mailSender;
    }

    private Properties buildJavaMailProperties() {
        var mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", true);
        mailProperties.put("mail.smtp.starttls.enable", true);
        mailProperties.put("mail.smtp.starttls.required", true);
        mailProperties.put("mail.smtp.socketFactory.port", 465);
        mailProperties.put("mail.smtp.debug", true);
        mailProperties.put("mail.smtp.ssl.checkserveridentity", true);
        mailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        mailProperties.put("mail.smtp.socketFactory.fallback", false);
        return mailProperties;
    }

    private String buildEmail(Map<String, Object> args, String path) {
        try {
            var resource = new ClassPathResource(path);
            var emailContent = Files.readString(Paths.get(resource.getURI()), StandardCharsets.UTF_8);

            return processTemplate(emailContent, args);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error loading email template!", e);
            return "Error loading email template!";
        }
    }

    private String buildEmail(Map<String, Object> args) {
        var emailContent = systemConfigService.getConfigValue(Constants.BILL_EMAIL);
        return processTemplate(emailContent, args);
    }

    private String buildEmail(String templateName, Map<String, Object> args) {
        // Lấy nội dung từ SystemConfig nếu có
        String emailContent = systemConfigService.getConfigValue(templateName);
        return processTemplate(emailContent, args);
    }

    private String processTemplate(String emailContent, Map<String, Object> args) {
        if (StringUtils.isNotBlank(emailContent)) {
            ST template = new ST(emailContent, Constants.DOLLAR, Constants.DOLLAR);
            if (args != null) {
                for (var entrySet : args.entrySet()) {
                    template.add(entrySet.getKey(), entrySet.getValue());
                }
            }
            return template.render();
        }
        return "Invalid email template!";
    }
}
