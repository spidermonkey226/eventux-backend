package com.eventux.backend.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final JavaMailSender mailSender;
    private final String frontendBaseUrl;
    private final String rsvpPath;
    private final String defaultFrom;

    public MailService(JavaMailSender mailSender,
                       @Value("${app.frontendBaseUrl}") String frontendBaseUrl,
                       @Value("${app.rsvpPath:/rsvp}") String rsvpPath,
                       @Value("${app.mail.from:}") String defaultFrom) {
        this.mailSender = mailSender;
        this.frontendBaseUrl = frontendBaseUrl;
        this.rsvpPath = rsvpPath;
        this.defaultFrom = defaultFrom;
    }

    @Async
    public void sendInviteEmail(String toEmail, String toName, int eventId, String eventName, String token) {
        String link = String.format("%s%s?eventId=%d&token=%s", frontendBaseUrl, rsvpPath, eventId, token);
        String subject = "You're invited: " + eventName;
        String html = """
      <div style="font-family:Arial,sans-serif">
        <h2>You're invited: %s</h2>
        <p>Hi %s,</p>
        <p>Please confirm your attendance:</p>
        <p><a href="%s" style="background:#4F46E5;color:#fff;padding:10px 16px;text-decoration:none;border-radius:6px">RSVP Now</a></p>
        <p>Or open this link:<br>%s</p>
      </div>
    """.formatted(eventName, toName == null ? "" : toName, link, link);

        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, "UTF-8");
            if (!defaultFrom.isBlank()) h.setFrom(defaultFrom);
            h.setTo(toEmail);
            h.setSubject(subject);
            h.setText(html, true);
            mailSender.send(msg);
        } catch (Exception ignored) {
            // log if you have a logger
        }
    }
}
