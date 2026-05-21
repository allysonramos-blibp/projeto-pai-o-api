package com.devlapa.o_pai_o.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void enviarEmailRecuperacao(String emailDestino, String nomeUsuario, String link) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(emailDestino);
            helper.setSubject("Recuperação de Senha - Ó Pai, Ó");

            String htmlConteudo = "<h3>Olá, " + nomeUsuario + "!</h3>"
                    + "<p>Você solicitou a recuperação de senha para o sistema <strong>Ó Pai, Ó</strong>.</p>"
                    + "<p>Clique no link abaixo para definir uma nova senha (válido por 2 horas):</p>"
                    + "<a href=\"" + link + "\" style=\"background-color: #E67E22; color: white; padding: 10px 20px; text-decoration: none; border-radius: 8px; font-weight: bold;\">Redefinir Minha Senha</a><br><br>"
                    + "<p>Se não foi você, ignore este e-mail.</p>";

            helper.setText(htmlConteudo, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Falha ao enviar e-mail", e);
        }
    }
}