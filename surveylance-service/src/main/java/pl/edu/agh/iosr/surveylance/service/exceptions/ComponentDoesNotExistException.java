package pl.edu.agh.iosr.surveylance.service.exceptions;

/**
 * Exception indicating that component does not exist in database.
 *
 * @author kuba
 *
 */
public class ComponentDoesNotExistException extends RuntimeException {

	private static final long serialVersionUID = 223660614958094164L;

	/**
	 * {@inheritDoc}
	 */
	public ComponentDoesNotExistException() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public ComponentDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * {@inheritDoc}
	 */
	public ComponentDoesNotExistException(String message) {
		super(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public ComponentDoesNotExistException(Throwable cause) {
		super(cause);
	}

}
