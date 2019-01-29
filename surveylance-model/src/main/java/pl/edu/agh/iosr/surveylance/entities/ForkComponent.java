package pl.edu.agh.iosr.surveylance.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * This class represents fork survey component.
 * 
 * @author kornel
 */
@Entity()
@Table(name = "ForkComponents")
public class ForkComponent extends pl.edu.agh.iosr.surveylance.entities.Entity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer modifications;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "parent_component_id")
	private Component parentComponent;

	/**
	 * Public constructor.
	 */
	public ForkComponent() {
		this.modifications = new Integer(0);
	}

	/**
	 * This method returns component's id.
	 * 
	 * @return component's id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * This method returns number of component's modifications.
	 * 
	 * @return number of component's modifications
	 */
	public Integer getModifications() {
		return this.modifications;
	}

	/**
	 * This method sets number of component's modifications.
	 * 
	 * @param modifications
	 *            number of component's modifications
	 */
	public void setModifications(Integer modifications) {
		this.modifications = modifications;
	}

	/**
	 * This method returns component's parent component.
	 * 
	 * @return component's parent component
	 */
	public Component getParentComponent() {
		return this.parentComponent;
	}

	/**
	 * This method sets component's parent component.
	 * 
	 * @param parentComponent
	 *            component's parent component
	 */
	public void setParentComponent(Component parentComponent) {
		this.parentComponent = parentComponent;
	}

}
