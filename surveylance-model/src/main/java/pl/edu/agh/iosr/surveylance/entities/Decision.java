package pl.edu.agh.iosr.surveylance.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This class represents decision.
 * 
 * @author fibi
 */
@Entity()
@Table(name = "Decisions")
public class Decision extends pl.edu.agh.iosr.surveylance.entities.Entity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fork_component_id")
	private ForkComponent forkComponent;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "component_id")
	private Component component;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "answer_id")
	private Answer answer;

	/**
	 * Public constructor.
	 */
	public Decision() {
	}

	/**
	 * This method returns decision's id.
	 * 
	 * @return decision's id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * This method returns decision's fork component.
	 * 
	 * @return decision's fork component
	 */
	public ForkComponent getForkComponent() {
		return this.forkComponent;
	}

	/**
	 * This method sets decision's fork component.
	 * 
	 * @param forkComponent
	 *            decision's fork component
	 */
	public void setForkComponent(ForkComponent forkComponent) {
		this.forkComponent = forkComponent;
	}

	/**
	 * This method returns decision's component.
	 * 
	 * @return decision's component
	 */
	public Component getComponent() {
		return this.component;
	}

	/**
	 * This method sets decision's component.
	 * 
	 * @param component
	 *            decision's component
	 */
	public void setComponent(Component component) {
		this.component = component;
	}

	/**
	 * This method returns decision's answer.
	 * 
	 * @return decision's answer
	 */
	public Answer getAnswer() {
		return this.answer;
	}

	/**
	 * This method sets decision's answer.
	 * 
	 * @param answer
	 *            decision's answer
	 */
	public void setAnswer(Answer answer) {
		this.answer = answer;
	}

}
