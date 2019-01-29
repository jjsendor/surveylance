package pl.edu.agh.iosr.surveylance.service.exceptions;

/**
 * Exception used for user login service. This exception is thrown, when there
 * are problems with logging user in.
 *
 * @author kuba
 */
public class LoginException extends RuntimeException {

	private static final long serialVersionUID = -4285277989716240371L;

	/**
	 * Constructs new LoginException object.
	 *
	 * @param	message	message text asociated with exception
	 */
	public LoginException(String message) {
		super(message);
	}

	/**
	 * Constructs new LoginException object with given cause.
	 *
	 * @param	message	message text asociated with exception
	 * @param	cause	exception which caused throwing LoginException
	 */
	public LoginException(String message, Throwable cause) {
		super(message, cause);
	}

}
