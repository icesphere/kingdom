package com.kingdom.util;

import com.kingdom.model.User;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: John
 * Date: Nov 22, 2010
 * Time: 7:02:55 AM
 */
public class EmailUtil {

    public static final String ADMIN_EMAIL = "icesphere12@gmail.com";

    private static void sendEmail(String email, String subject, String html) throws MessagingException, IOException {
        String host = "smtp.gmail.com";
        String username = "icesphere12";
        String password = "hj1l2a8b";
        Properties props = new Properties();
        props.put("mail.smtps.auth", "true");
        Session session = Session.getInstance(props, null);
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(ADMIN_EMAIL, "Kingdom Admin"));

        msg.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(email, false));

        msg.setSubject(subject);
        msg.setDataHandler(new DataHandler(new ByteArrayDataSource(html, "text/html")));

        msg.setHeader("X-Mailer", "sendhtml");
        msg.setSentDate(new Date());

        Transport t = session.getTransport("smtps");
        try {
            t.connect(host, username, password);
            t.sendMessage(msg, msg.getAllRecipients());
        }
        finally {
            t.close();
        }

    }

    public static void sendAccountRequestEmail(User user) throws IOException, MessagingException {
        StringBuffer sb = new StringBuffer();
        sb.append("<html><body>");
        sb.append("Username: ").append(user.getUsername()).append("<br>");
        sb.append("Temporary Password: ").append(user.getPassword()).append("<br><br>");
        sb.append("Click <a href=\"http://kingdom.servegame.org\">here</a> to login.").append("<br>");
        sb.append("</body></html>");
        sendEmail(user.getEmail(), "Kingdom Account Request", sb.toString());
        sendAccountCreatedEmail(user);
    }

    private static void sendAccountCreatedEmail(User user) throws IOException, MessagingException {
        StringBuffer sb = new StringBuffer();
        sb.append("<html><body>");
        sb.append("Account created: ").append("<br>");
        sb.append("email: ").append(user.getEmail()).append("<br>");
        sb.append("username: ").append(user.getUsername()).append("<br>");
        sb.append("password: ").append(user.getPassword()).append("<br>");
        sb.append("</body></html>");
        sendEmail(ADMIN_EMAIL, "Kingdom Account Created", sb.toString());
    }

    public static void sendForgotLoginEmail(User user) throws IOException, MessagingException {
        StringBuffer sb = new StringBuffer();
        sb.append("<html><body>");
        sb.append("Username: ").append(user.getUsername()).append("<br>");
        sb.append("Password: ").append(user.getPassword()).append("<br><br>");
        sb.append("Click <a href=\"http://kingdom.servegame.org\">here</a> to login.").append("<br>");
        sb.append("</body></html>");
        sendEmail(user.getEmail(), "Kingdom Forgot Login", sb.toString());
    }
}
