package pl.edu.agh.iosr.surveylance.service.impl;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.apache.log4j.Logger;

import pl.edu.agh.iosr.surveylance.service.MailService;

/**
 * This class implements functionality of {@link MailService} interface.
 * 
 * @author kornel
 */
public class MailServiceImpl implements MailService {

	private static final Logger logger = Logger
			.getLogger(MailServiceImpl.class);

	private static final String EMAIL_SUBJECT = "donotreplay";

	private String messageContent = null;
	private Properties mailManagerProperties = null;
	private Authenticator authenticator = null;

	/**
	 * Public constructor.
	 * 
	 * @param locale
	 *            locale in {@link String} format
	 */
	public MailServiceImpl(String locale) {
		this.loadProperties(locale);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendInvitation(String surveyURL, String mail) {
		// check if mail server are loaded
		if (this.mailManagerProperties == null)
			return;

		// create e-mail content
		this.messageContent = this.mailManagerProperties
				.getProperty("survey-content");
		if (this.messageContent == null) {
			logger
					.error("Could not load e-mail content from appriopriate .properties file.");
			return;
		}
		this.messageContent += surveyURL;

		// prepare session with e-mail server
		Session session = Session.getDefaultInstance(
				this.mailManagerProperties, this.authenticator);

		// prepare new message
		Message message = new MimeMessage(session);
		try {
			// prepare e-mail messages
			message.setSubject(EMAIL_SUBJECT);
			message.setText(this.messageContent);
		} catch (MessagingException ex) {
			logger.error(ex.toString() + "\n" + ex.getMessage());
			return;
		}

		// iterate through addresses' list and send e-mails
		try {
			message.setRecipient(RecipientType.TO, new InternetAddress(mail));

			Transport.send(message);
		} catch (AddressException ex) {
			logger.warn(ex.toString() + "\n" + ex.getMessage());
		} catch (MessagingException ex) {
			logger.error(ex.toString() + "\n" + ex.getMessage());
		}
	}

	/**
	 * This method loads properties used by {@link MailServiceImpl} to
	 * communicate with mail server.
	 * 
	 * @param locale
	 *            locale of properties which should be loaded
	 * @return filled {@link Properties} object or null if an error occurred
	 */
	private Properties loadMailManagerProperties(String locale) {
		Properties properties = new Properties();
		try {
			if (locale == null)
				properties.load(MailServiceImpl.class
						.getResourceAsStream(MailServiceImpl.class
								.getSimpleName()
								+ ".properties"));
			else {
				try {
					properties.load(MailServiceImpl.class
							.getResourceAsStream(MailServiceImpl.class
									.getSimpleName()
									+ "_" + locale + ".properties"));
				} catch (NullPointerException ex) {
					properties.load(MailServiceImpl.class
							.getResourceAsStream(MailServiceImpl.class
									.getSimpleName()
									+ ".properties"));
				}
			}
		} catch (IOException ex) {
			logger.error(ex.toString() + "\n" + ex.getMessage());
			return null;
		} catch (IllegalArgumentException ex) {
			logger.error(ex.toString() + "\n" + ex.getMessage());
			return null;
		}
		return properties;
	}

	/**
	 * SMTPAuthenticator is used to do simple authentication when the SMTP
	 * server requires it.
	 */
	private class SMTPAuthenticator extends Authenticator {

		private String username = null;
		private String password = null;

		/**
		 * Public constructor.
		 * 
		 * @param username
		 *            user login to smtp server
		 * @param password
		 *            user password to smtp server
		 */
		public SMTPAuthenticator(String username, String password) {
			this.username = username;
			this.password = password;
		}

		/**
		 * This method returns {@link PasswordAuthentication} object which
		 * should be used to authenticate.
		 * 
		 * @return {@link PasswordAuthentication} object
		 */
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(this.username, this.password);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLocale(String locale) {
		this.loadProperties(locale);
	}

	/**
	 * This method loads or reloads properties of manager.
	 * 
	 * @param locale
	 *            locale in {@link String} format
	 */
	private void loadProperties(String locale) {
		// load properties
		this.mailManagerProperties = this.loadMailManagerProperties(locale);

		// prepare authenticator object
		this.authenticator = new SMTPAuthenticator(this.mailManagerProperties
				.getProperty("mail.username"), this.mailManagerProperties
				.getProperty("mail.password"));
	}

}
