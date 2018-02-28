package com.kingdom.util

import com.kingdom.model.User
import java.io.IOException
import java.util.*
import javax.activation.DataHandler
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.util.ByteArrayDataSource

object EmailUtil {

    val ADMIN_EMAIL = "icesphere12@gmail.com"

    @Throws(MessagingException::class, IOException::class)
    private fun sendEmail(email: String, subject: String, html: String) {
        val host = "smtp.gmail.com"
        val username = "icesphere12"
        val password = ""
        val props = Properties()
        props["mail.smtps.auth"] = "true"
        val session = Session.getInstance(props, null)
        val msg = MimeMessage(session)
        msg.setFrom(InternetAddress(ADMIN_EMAIL, "Kingdom Admin"))

        msg.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(email, false))

        msg.subject = subject
        msg.dataHandler = DataHandler(ByteArrayDataSource(html, "text/html"))

        msg.setHeader("X-Mailer", "sendhtml")
        msg.sentDate = Date()

        val t = session.getTransport("smtps")
        try {
            t.connect(host, username, password)
            t.sendMessage(msg, msg.allRecipients)
        } finally {
            t.close()
        }

    }

    @Throws(IOException::class, MessagingException::class)
    fun sendAccountRequestEmail(user: User) {
        val sb = StringBuffer()
        sb.append("<html><body>")
        sb.append("Username: ").append(user.username).append("<br>")
        sb.append("Temporary Password: ").append(user.password).append("<br><br>")
        sb.append("Click <a href=\"http://kingdom.servegame.org\">here</a> to login.").append("<br>")
        sb.append("</body></html>")
        sendEmail(user.email, "Kingdom Account Request", sb.toString())
        sendAccountCreatedEmail(user)
    }

    @Throws(IOException::class, MessagingException::class)
    private fun sendAccountCreatedEmail(user: User) {
        val sb = StringBuffer()
        sb.append("<html><body>")
        sb.append("Account created: ").append("<br>")
        sb.append("email: ").append(user.email).append("<br>")
        sb.append("username: ").append(user.username).append("<br>")
        sb.append("password: ").append(user.password).append("<br>")
        sb.append("</body></html>")
        sendEmail(ADMIN_EMAIL, "Kingdom Account Created", sb.toString())
    }

    @Throws(IOException::class, MessagingException::class)
    fun sendForgotLoginEmail(user: User) {
        val sb = StringBuffer()
        sb.append("<html><body>")
        sb.append("Username: ").append(user.username).append("<br>")
        sb.append("Password: ").append(user.password).append("<br><br>")
        sb.append("Click <a href=\"http://kingdom.servegame.org\">here</a> to login.").append("<br>")
        sb.append("</body></html>")
        sendEmail(user.email, "Kingdom Forgot Login", sb.toString())
    }
}
