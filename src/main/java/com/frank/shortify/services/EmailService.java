package com.frank.shortify.services;

import com.frank.shortify.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    public void sendEmailConfirmation(User user, String token, String baseUrlFront) {
        String confirmUrl = baseUrlFront + "/confirm-email?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        if (!fromAddress.isEmpty()) {
            message.setFrom(fromAddress);
        }
        message.setSubject("Confirma tu correo en Shortify");
        message.setText("Hola,\n\nPor favor, haz clic en el siguiente enlace para confirmar tu correo:\n"
                + confirmUrl + "\n\nSi no has solicitado esta cuenta, puedes ignorar este mensaje.");

        mailSender.send(message);
    }
}

