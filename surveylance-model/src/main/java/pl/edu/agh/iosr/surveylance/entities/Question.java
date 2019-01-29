package pl.edu.agh.iosr.surveylance.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import pl.edu.agh.iosr.surveylance.data.QuestionKind;
import pl.edu.agh.iosr.surveylance.data.QuestionType;

/**
 * This class represents single survey's question.
 * 
 * @author kornel
 */
@Entity()
@Table(name = "Questions")
public class Question extends pl.edu.agh.iosr.surveylance.entities.Entity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer modifications;

	@OneToOne
	@JoinColumn(name = "parent_component_id")
	private Component parentComponent;

	@Enumerated(EnumType.STRING)
	private QuestionType type;

	@Enumerated(EnumType.STRING)
	private QuestionKind kind;

	private String content;

	/**
	 * Public constructor.
	 */
	public Question() {
		this.modifications = new Integer(0);
	}

	/**
	 * This method returns question's id.
	 * 
	 * @return question's id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * This method returns number of question's modifications.
	 * 
	 * @return number of question's modifications
	 */
	public Integer getModifications() {
		return this.modifications;
	}

	/**
	 * This method sets number of question's modifications.
	 * 
	 * @param modifications
	 *            number of questiont's modifications
	 */
	public void setModifications(Integer modifications) {
		this.modifications = modifications;
	}

	/**
	 * This method returns question's parent component.
	 * 
	 * @return question's parent component
	 */
	public Component getParentComponent() {
		return this.parentComponent;
	}

	/**
	 * This method sets question's parent component.
	 * 
	 * @param parentComponent
	 *            question's parent component
	 */
	public void setParentComponent(Component parentComponent) {
		this.parentComponent = parentComponent;
	}

	/**
	 * This method returns question type.
	 * 
	 * @return question type
	 */
	public QuestionType getType() {
		return this.type;
	}

	/**
	 * This method sets question type.
	 * 
	 * @param type
	 *            question type
	 */
	public void setType(QuestionType type) {
		this.type = type;
	}

	/**
	 * This method returns question kind.
	 * 
	 * @return question kind
	 */
	public QuestionKind getKind() {
		return this.kind;
	}

	/**
	 * This method sets question kind.
	 * 
	 * @param kind
	 *            question kind
	 */
	public void setKind(QuestionKind kind) {
		this.kind = kind;
	}

	/**
	 * This method returns question content.
	 * 
	 * @return question content
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * This method sets question content.
	 * 
	 * @param content
	 *            question content
	 */
	public void setContent(String content) {
		this.content = content;
	}

}
