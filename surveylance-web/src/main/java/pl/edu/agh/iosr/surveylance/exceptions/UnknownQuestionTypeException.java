package pl.edu.agh.iosr.surveylance.exceptions;

import pl.edu.agh.iosr.surveylance.data.QuestionType;

/**
 * Exception thrown when question {@link QuestionType type} cannot be
 * determined.
 *
 * @author kuba
 *
 */
public class UnknownQuestionTypeException extends RuntimeException {

	private static final long serialVersionUID = 3760819202283172111L;

	/**
	 * {@inheritDoc}
	 */
	public UnknownQuestionTypeException() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public UnknownQuestionTypeException(String message) {
		super(message);
	}

}
