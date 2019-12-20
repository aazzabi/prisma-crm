package Utils;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import Entities.Client;

import java.util.Properties;
public class Mailer {
	private static final String senderEmail = "atef.ayedi@esprit.tn";
	private static final String senderPassword = "tomahawk1994";
	public static void sendAsHtml(String to, String title, String html,Client client) throws MessagingException {
	      System.out.println("Sending email to " + to);

	      Session session = createSession();

	      //create message using session
	      MimeMessage message = new MimeMessage(session);
	      prepareEmailMessage(message, to, title, html,null);
	      //sending message
	      Transport.send(message);
	      System.out.println("Done");
	  }
	
	 private static Session createSession() {
	      Properties props = new Properties();
	      props.put("mail.smtp.auth", "true");
	      props.put("mail.smtp.starttls.enable", "true");
	      props.put("mail.smtp.host", "smtp.gmail.com"); 
	      props.put("mail.smtp.port", "587");

	      Session session = Session.getInstance(props, new javax.mail.Authenticator() {
	          protected PasswordAuthentication getPasswordAuthentication() {
	              return new PasswordAuthentication(senderEmail, senderPassword);
	          }
	      });
	      return session;
	  }
	 
	 private static void prepareEmailMessage(MimeMessage message, String to, String title, String html,Client client)
	          throws MessagingException {
	      message.setContent(html, "text/html; charset=utf-8");
	      message.setFrom(new InternetAddress(senderEmail));
	      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
	      message.setSubject(title);
	  }
	

}
