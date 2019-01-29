/**
 * 
 */
package pl.edu.agh.iosr.surveylance.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This class represents results of single question.
 * 
 * @author fibi
 * @author kornel
 */
@Entity()
@Table(name = "SurveyResults")
public class SurveyResult extends pl.edu.agh.iosr.surveylance.entities.Entity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "question_id")
	private Question question;

	@ManyToOne
	@JoinColumn(name = "answer_id")
	private Answer answer;

	@ManyToOne
	@JoinColumn(name = "form_id")
	private Form form;

	private String textAnswer;

	/**
	 * Public constructor.
	 */
	public SurveyResult() {
	}

	/**
	 * This method returns survey result's id.
	 * 
	 * @return decision's id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * This method returns result's question.
	 * 
	 * @return result's question
	 */
	public Question getQuestion() {
		return this.question;
	}

	/**
	 * This method sets result's question.
	 * 
	 * @param question
	 *            result's question
	 */
	public void setQuestion(Question question) {
		this.question = question;
	}

	/**
	 * This method returns result's answer.
	 * 
	 * @return result's answer
	 */
	public Answer getAnswer() {
		return this.answer;
	}

	/**
	 * This method sets result's answer.
	 * 
	 * @param answer
	 *            result's answer
	 */
	public void setAnswer(Answer answer) {
		this.answer = answer;
	}

	/**
	 * This method returns result's text answer.
	 * 
	 * @return textAnswer
	 */
	public String getTextAnswer() {
		return textAnswer;
	}

	/**
	 * @param textAnswer
	 *            sets result's answer
	 */
	public void setTextAnswer(String textAnswer) {
		this.textAnswer = textAnswer;
	}

	/**
	 * This method sets {@link Form} object associated with this object.
	 * 
	 * @param form
	 *            {@link Form} object associated with this object
	 */
	public void setForm(Form form) {
		this.form = form;
	}

	/**
	 * This method returns {@link Form} object associated with this object.
	 * 
	 * @return {@link Form} object associated with this object
	 */
	public Form getForm() {
		return this.form;
	}

}
