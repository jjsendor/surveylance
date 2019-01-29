package pl.edu.agh.iosr.surveylance.service;

/**
 * This interface is used to sending e-mails with invitations.
 * 
 * @author kornel
 */
public interface MailService {

	/**
	 * This method sets locale of generated surveys.
	 * 
	 * @param locale
	 *            locale in {@link String} format
	 */
	public void setLocale(String locale);

	/**
	 * This method sends e-mails with invitations to survey which is served
	 * under surveyURL site. E-mails are send on addresses in addressees list.
	 * 
	 * @param surveyURL
	 *            URL address of survey, which invitation concern
	 * @param mail
	 *            e-mail address
	 */
	public void sendInvitation(String surveyURL, String mail);

}
