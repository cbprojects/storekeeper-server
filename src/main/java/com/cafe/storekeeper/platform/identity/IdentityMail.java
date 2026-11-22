package com.cafe.storekeeper.platform.identity;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.util.Date;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public class IdentityMail {
    private final ObjectProvider<JavaMailSender> sender;
    private final String from;
    private final String publicUrl;

    public IdentityMail(ObjectProvider<JavaMailSender> sender,
            @Value("${storekeeper.mail.from:noreply@localhost}") String from,
            @Value("${storekeeper.public-url:http://localhost:4200}") String publicUrl) {
        this.sender = sender;
        this.from = from;
        this.publicUrl = publicUrl.replaceAll("/+$", "");
    }

    public void send(Account account, String purpose, String token) {
        if (!purpose.equals("verify") && !purpose.equals("reset"))
            throw new IllegalArgumentException("Unknown email purpose");
        JavaMailSender transport = sender.getIfAvailable();
        if (transport == null) throw new IdentityProblem(503, "Correo no configurado; intenta más tarde");
        boolean verify = purpose.equals("verify");
        String title = verify ? "Confirma tu correo electrónico" : "Restablece tu contraseña";
        String intro = verify ? "Gracias por crear tu cuenta en Storekeeper. Confirma tu correo para comenzar a administrar tus negocios."
                : "Recibimos una solicitud para restablecer la contraseña de tu cuenta de Storekeeper.";
        String action = verify ? "Confirmar correo" : "Restablecer contraseña";
        String expiry = verify ? "Este enlace vence en 24 horas y solo se puede usar una vez."
                : "Este enlace vence en 1 hora y solo se puede usar una vez.";
        String safety = verify ? "Si no creaste esta cuenta, puedes ignorar este correo."
                : "Si no solicitaste este cambio, ignora este correo. Tu contraseña seguirá siendo la misma.";
        String url = publicUrl + "/#/account/" + purpose + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        String greeting = "Hola, " + account.name() + ".";
        String plain = title + "\n\n" + greeting + "\n\n" + intro + "\n\n" + action + ":\n" + url
                + "\n\n" + expiry + "\n\n" + safety + "\nNunca compartas este enlace ni tu contraseña."
                + "\n\nStorekeeper · Seguridad de tu cuenta\nCorreo automático relacionado con tu cuenta; no contiene publicidad.";
        String html = """
                <!doctype html>
                <html lang="es"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>%s</title></head>
                <body style="margin:0;background:#f5f8fc;color:#142c4a;font-family:Inter,Arial,Helvetica,sans-serif;line-height:1.6">
                <div style="display:none;max-height:0;overflow:hidden;mso-hide:all">%s %s</div>
                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0"><tr><td align="center" style="padding:32px 16px">
                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="max-width:600px;background:#ffffff;border:1px solid #dce5f0;border-radius:12px">
                <tr><td style="padding:28px 32px;background:#eaf3ff;color:#0868dc;font-family:Outfit,Inter,Arial,sans-serif;font-size:24px;font-weight:bold;border-top:4px solid #0868dc"><img src="cid:storekeeper-logo" width="48" height="48" alt="" style="vertical-align:middle;margin-right:12px">Store Keeper</td></tr>
                <tr><td style="padding:32px"><p style="margin:0 0 12px;font-size:12px;color:#566b85;letter-spacing:1px">SEGURIDAD DE TU CUENTA</p>
                <h1 style="margin:0 0 24px;font-family:Outfit,Inter,Arial,sans-serif;font-size:26px;line-height:1.3;color:#142c4a">%s</h1>
                <p>%s</p><p>%s</p>
                <table role="presentation" cellspacing="0" cellpadding="0" style="margin:28px 0"><tr><td bgcolor="#0868dc" style="border-radius:6px;text-align:center"><a href="%s" style="display:inline-block;padding:14px 24px;color:#ffffff;text-decoration:none;font-weight:bold">%s</a></td></tr></table>
                <p style="font-weight:bold">%s</p>
                <p style="font-size:14px;color:#566b85">Si el botón no funciona, copia y pega este enlace en tu navegador:</p>
                <p style="font-size:13px;word-break:break-all;overflow-wrap:anywhere"><a href="%s" style="color:#0868dc">%s</a></p>
                <hr style="border:0;border-top:1px solid #dce5f0;margin:28px 0">
                <p style="font-size:14px">%s</p><p style="font-size:14px">Nunca compartas este enlace ni tu contraseña.</p>
                </td></tr><tr><td style="padding:24px 32px;background:#f8fafc;font-size:12px;color:#566b85">Storekeeper · Seguridad de tu cuenta<br>Correo automático relacionado con tu cuenta; no contiene publicidad.</td></tr>
                </table></td></tr></table></body></html>
                """.formatted(escape(title), escape(intro), escape(expiry), escape(title), escape(greeting), escape(intro),
                        escape(url), escape(action), escape(expiry), escape(url), escape(url), escape(safety));
        try {
            var message = transport.createMimeMessage();
            var helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setFrom(new jakarta.mail.internet.InternetAddress(from, "Storekeeper", StandardCharsets.UTF_8.name()));
            helper.setTo(account.email());
            helper.setSubject(title + " | Storekeeper");
            helper.setSentDate(new Date());
            var alternative = new jakarta.mail.internet.MimeMultipart("alternative");
            var textPart = new jakarta.mail.internet.MimeBodyPart();
            textPart.setText(plain, StandardCharsets.UTF_8.name());
            alternative.addBodyPart(textPart);
            var htmlPart = new jakarta.mail.internet.MimeBodyPart();
            htmlPart.setText(html, StandardCharsets.UTF_8.name(), "html");
            var related = new jakarta.mail.internet.MimeMultipart("related");
            related.addBodyPart(htmlPart);
            var logo = new jakarta.mail.internet.MimeBodyPart();
            logo.setDataHandler(new jakarta.activation.DataHandler(new org.springframework.core.io.ClassPathResource("mail/logo.png").getURL()));
            logo.setDisposition(jakarta.mail.Part.INLINE);
            logo.setHeader("Content-ID", "<storekeeper-logo>");
            logo.setFileName("storekeeper.png");
            related.addBodyPart(logo);
            var richPart = new jakarta.mail.internet.MimeBodyPart();
            richPart.setContent(related);
            alternative.addBodyPart(richPart);
            message.setContent(alternative);
            message.setHeader("Content-Language", "es");
            message.setHeader("Auto-Submitted", "auto-generated");
            message.setHeader("X-Auto-Response-Suppress", "All");
            message.saveChanges(); // Generates MIME-Version and a unique Message-ID.
            transport.send(message);
        } catch (MessagingException | java.io.IOException failure) {
            throw new MailPreparationException("No fue posible preparar el correo", failure);
        }
    }

    private static String escape(String text) { return HtmlUtils.htmlEscape(text, StandardCharsets.UTF_8.name()); }
}
