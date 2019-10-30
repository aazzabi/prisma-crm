package Utils;

import java.util.Properties;
import java.util.logging.Level;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jboss.logging.Logger;


public class JavaMailUtil {
	public static void sendMail(String recipient, String subject, String content) throws Exception {
		System.out.println("preparing to send email");
		
		Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");	
        
        String myEmail = "prisma.crm.2019@gmail.com";
        String password = "prismacrm2019";
        
        Session session = Session.getInstance(props, new Authenticator() {
        	@Override
        	protected PasswordAuthentication getPasswordAuthentication() {
        		return new PasswordAuthentication(myEmail, password);
        	}
        });
        
        Message message = prepareMessage(session, recipient, myEmail,subject, content);
        Transport.send(message);
        
		System.out.println("sended !");

    }
	
	
	private static Message prepareMessage(Session session , String r , String email, String subject,String content) {
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(email));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(r));
			message.setSubject(subject);
			message.setContent(content, "text/html");
			return message;
		} catch (Exception ex) {
			Logger.getLogger(JavaMailUtil.class.getName()).log(null, Level.SEVERE, null, ex);
		}		
		return null;
	}

}
