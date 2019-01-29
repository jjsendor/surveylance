package pl.edu.agh.iosr.surveylance.service.exceptions;

/**
 * Exception indicating that question does not exist in database.
 *
 * @author kuba
 *
 */
public class QuestionDoesNotExistException extends RuntimeException {

	private static final long serialVersionUID = -1011578345394338506L;

	/**
	 * {@inheritDoc}
	 */
	public QuestionDoesNotExistException() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public QuestionDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * {@inheritDoc}
	 */
	public QuestionDoesNotExistException(String message) {
		super(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public QuestionDoesNotExistException(Throwable cause) {
		super(cause);
	}

}
