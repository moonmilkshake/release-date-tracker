import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Class that handles the sending of emails via use of gmail-host and TLS-port.
 */
class EmailSender {
    private static final String HOST = "smtp.gmail.com";
    private static final String PORT = "587";
    private static final String username = ""; // ADD email-sender to include functionality in program
    private static final String password = ""; // ADD password to include functionality in program
    private final String to;
    private final String subject;
    private final String messageContent;
    private boolean emailSent = false;

    /**
     * Constructor for EmailSender that is parameterized.
     * @param to Email address to receiver.
     * @param subject Title of email.
     * @param messageContent Text contents of email.
     */
    public EmailSender(String to, String subject, String messageContent) {
        this.to = to;
        this.subject = subject;
        this.messageContent = messageContent;
    }

    /**
     * Method that handles the sending of emails. It creates an instance of Properties, Sessions
     * and Message to perform its task. Uses a time-out to hinder getting stuck on send.
     */
    public boolean send() {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", HOST);
        prop.put("mail.smtp.port", PORT);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.timeout", "5000");
        prop.put("mail.smtp.connectiontimeout", "5000");

        Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(messageContent);

            Transport.send(message);
            emailSent = true;
        } catch (AddressException e) {
            System.out.println("AddressException: " + e.getMessage());
        } catch (MessagingException e) {
            System.out.println("MessagingException: " + e.getMessage());
        }
        return emailSent;
    }

}