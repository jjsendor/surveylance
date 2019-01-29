package pl.edu.agh.iosr.surveylance.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This class represents single survey's component.
 * 
 * @author kornel
 */
@Entity()
@Table(name = "Components")
public class Component extends pl.edu.agh.iosr.surveylance.entities.Entity
		implements Comparable<Component> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer modifications;

	private Integer position;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "component_id")
	private Component parentComponent;

	/**
	 * Public constructor.
	 */
	public Component() {
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
	 * This method returns component's position.
	 * 
	 * @return component's position
	 */
	public Integer getPosition() {
		return this.position;
	}

	/**
	 * This method sets component's position.
	 * 
	 * @param position
	 *            component's position
	 */
	public void setPosition(Integer position) {
		this.position = position;
	}

	/**
	 * This method returns parent component of this component.
	 * 
	 * @return parent component
	 */
	public Component getParentComponent() {
		return this.parentComponent;
	}

	/**
	 * This method sets parent component of this component.
	 * 
	 * @param parentComponent
	 *            parent component
	 */
	public void setParentComponent(Component parentComponent) {
		this.parentComponent = parentComponent;
	}

	/**
	 * This method compares this object with object given as parameter, and
	 * returns proper value.
	 * 
	 * @param o
	 *            Component object to compare
	 * @return 1 if this component is greater than given, 0 if equal, and -1 if
	 *         less
	 */
	@Override
	public int compareTo(Component o) {
		if (this.position > o.getPosition())
			return 1;
		if (this.position == o.getPosition())
			return 0;
		return -1;
	}

}
