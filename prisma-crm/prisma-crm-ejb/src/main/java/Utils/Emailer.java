package Utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Emailer {

	private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

	public static boolean SendEmail(String to, String subject, String msg)

	{
		
		final String username = "bims.pasteur@gmail.com";

		final String password = "21626747";

		boolean flag = false;

		Properties props = new Properties();

		props.put("mail.smtp.host", "smtp.gmail.com");

		props.put("mail.smtp.auth", "true");

		props.put("mail.debug", "true");

		props.put("mail.smtp.port", "465");

		props.put("mail.smtp.socketFactory.port", "465");
		
		
		props.put("mail.smtp.socketFactory.class", SSL_FACTORY);

		props.put("mail.smtp.socketFactory.fallback", "false");

		Session session = Session.getInstance(props, new javax.mail.Authenticator()

		{
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("bims.pasteur@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);
			message.setContent(msg, "text/html; charset=utf-8");

			Transport.send(message);
			flag = true;
			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		return flag;
	}
}