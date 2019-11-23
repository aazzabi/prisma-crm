package Utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;

public class Sms {
	public static final String ACCOUNT_SID = "ACaa7959fde0f0487448b8c720b5dc3d8e";
	public static final String AUTH_TOKEN = "fa3f25b29249b3ba0c359687ccb2024c";

	public static String SendSMS(String numero, String body) {
		TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
		List params = new ArrayList();
		params.add(new BasicNameValuePair("To", numero));
		params.add(new BasicNameValuePair("From", "(610) 572-7434"));
		params.add(new BasicNameValuePair("Body", body));

		MessageFactory messageFactory = client.getAccount().getMessageFactory();
		Message message;
		try {
			message = messageFactory.create(params);
			System.out.println(message.getSid());
			return message.getSid();

		} catch (TwilioRestException e) {
			System.err.println("errooooooot" + e);
			return null;
		}

	}

}