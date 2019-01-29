package pl.edu.agh.iosr.surveylance.exceptions;

import pl.edu.agh.iosr.surveylance.data.QuestionKind;

/**
 * Exception thrown when question {@link QuestionKind kind} cannot be
 * determined.
 *
 * @author kuba
 *
 */
public class UnknownQuestionKindException extends RuntimeException {

	private static final long serialVersionUID = 8689451660345297814L;

	/**
	 * {@inheritDoc}
	 */
	public UnknownQuestionKindException() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public UnknownQuestionKindException(String message) {
		super(message);
	}

}
