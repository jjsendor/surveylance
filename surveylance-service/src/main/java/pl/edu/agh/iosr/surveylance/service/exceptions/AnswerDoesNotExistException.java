package pl.edu.agh.iosr.surveylance.service.exceptions;

/**
 * Exception indicating that answer does not exist in database.
 *
 * @author kuba
 *
 */
public class AnswerDoesNotExistException extends RuntimeException {

	private static final long serialVersionUID = -2094696160358077050L;

	/**
	 * {@inheritDoc}
	 */
	public AnswerDoesNotExistException() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public AnswerDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * {@inheritDoc}
	 */
	public AnswerDoesNotExistException(String message) {
		super(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public AnswerDoesNotExistException(Throwable cause) {
		super(cause);
	}

}
