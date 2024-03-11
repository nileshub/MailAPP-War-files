package mailapp;

import java.io.IOException;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SendEmail extends HttpServlet {

    public static boolean sendEmail(String message, String subject, String fromUser,
                                   String toUser, String password, String hostName,
                                   String isAnyAttachment, String attachmentPath) throws MessagingException {

        // Replace with your actual credentials, ensuring proper security measures (e.g., environment variables)
        final String user = fromUser;
        final String pwd = password;

        // Configure email properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", hostName);

        // Create a session with authentication
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, pwd);
                    }
                });

        // Create a MimeMessage object
        MimeMessage mimeMessage = new MimeMessage(session);

        // Set message headers
        mimeMessage.setFrom(new InternetAddress(user));
        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toUser));
        mimeMessage.setSubject(subject);

        // Create the message body
        BodyPart messageBody = new MimeBodyPart();
        messageBody.setContent(message, "text/html; charset=utf-8");

        // Create a multipart message for attachments (if any)
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBody);

        // Attach files if necessary
        if ("Y".equalsIgnoreCase(isAnyAttachment) && attachmentPath != null && !attachmentPath.isEmpty()) {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new FileDataSource(attachmentPath);
            attachmentPart.setDataHandler(new DataHandler(source));
            String filename = attachmentPath.substring(attachmentPath.lastIndexOf("/") + 1);
            attachmentPart.setFileName(filename);
            multipart.addBodyPart(attachmentPart);
        }

        // Set the message content
        mimeMessage.setContent(multipart);

        // Send the email
        Transport transport = session.getTransport("smtp");
        transport.connect(hostName, user, pwd);
        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
        transport.close();

        System.out.println("Email sent successfully.");
        return true;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] toUsers = {"nileshgautam245@dbatu.ac.in"}; // You can add multiple recipients in an array
        String message = "hello sir," +
            "/n" +
            "this message is sent using the app below the code for it is attached please check";
        String subject = "Test Email";
        String senderEmail = "nileshgw245@gmail.com"; // Replace with your actual email address, ensuring proper security measures
        String password = "vami kpze sncm lsaa"; // Replace with a secure password retrieved from environment variables or a secure storage mechanism
        String hostName = "smtp.gmail.com";

        // Optional attachment handling
        String hasAttachment = "N"; // Change to "Y" if attaching a file
        String attachmentPath;
        attachmentPath = ""; // Replace with the absolute path to your file if attaching

        try {
            sendEmail(message, subject, senderEmail, toUsers[0], password, hostName, hasAttachment, attachmentPath);
        } catch (MessagingException e) {
            System.out.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
