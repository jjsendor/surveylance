package pl.edu.agh.iosr.surveylance.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.InheritanceType;
import javax.persistence.DiscriminatorType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This class represents question answer.
 * 
 * @author fibi
 */
@Entity()
@Table(name = "Answers")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "answerType", discriminatorType = DiscriminatorType.STRING)
public abstract class Answer extends
		pl.edu.agh.iosr.surveylance.entities.Entity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer modifications;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "question_id")
	private Question question;

	private Integer position;

	/**
	 * Public constructor.
	 */
	public Answer() {
		this.modifications = new Integer(0);
	}

	/**
	 * This method returns question answer's id.
	 * 
	 * @return question answer's id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * This method returns number of question answer's modifications.
	 * 
	 * @return number of question answer's modifications
	 */
	public Integer getModifications() {
		return this.modifications;
	}

	/**
	 * This method sets number of question answer's modifications.
	 * 
	 * @param modifications
	 *            number of question answer's modifications
	 */
	public void setModifications(Integer modifications) {
		this.modifications = modifications;
	}

	/**
	 * This method returns question object associated with this object.
	 * 
	 * @return question object associated with this object
	 */
	public Question getQuestion() {
		return this.question;
	}

	/**
	 * This method sets question object associated with this object.
	 * 
	 * @param question
	 *            question object associated with this object
	 */
	public void setQuestion(Question question) {
		this.question = question;
	}

	/**
	 * This method returns answer's position.
	 * 
	 * @return answer's position
	 */
	public Integer getPosition() {
		return this.position;
	}

	/**
	 * This method sets answer's position.
	 * 
	 * @param position
	 *            answer's position
	 */
	public void setPosition(Integer position) {
		this.position = position;
	}

}
