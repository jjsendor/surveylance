package pl.edu.agh.iosr.surveylance.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * This class represents single survey object.
 * 
 * @author kornel
 */
@Entity()
@Table(name = "Surveys")
public class Survey extends pl.edu.agh.iosr.surveylance.entities.Entity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer modifications;

	private String name;

	private String description;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "owner_id")
	private User owner;

	@OneToOne
	@JoinColumn(name = "root_component_id")
	private Component rootComponent;

	private Date expirationDate;

	private Boolean isReadonly;

	/**
	 * Public constructor.
	 */
	public Survey() {
		this.modifications = new Integer(0);
		this.isReadonly = false;
	}

	/**
	 * This method returns survey's id.
	 * 
	 * @return survey's id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * This method returns number of survey's modifications.
	 * 
	 * @return number of survey's modifications
	 */
	public Integer getModifications() {
		return this.modifications;
	}

	/**
	 * This method sets number of survey's modifications.
	 * 
	 * @param modifications
	 *            number of survey's modifications
	 */
	public void setModifications(Integer modifications) {
		this.modifications = modifications;
	}

	/**
	 * This method returns survey's name.
	 * 
	 * @return survey's name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * This method sets survey's name.
	 * 
	 * @param name
	 *            survey's name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This method returns survey's description.
	 * 
	 * @return survey's description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * This method sets survey's description.
	 * 
	 * @param description
	 *            survey's description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * This method returns survey's owner.
	 * 
	 * @return survey's owner
	 */
	public User getOwner() {
		return this.owner;
	}

	/**
	 * This method sets survey's owner.
	 * 
	 * @param owner
	 *            survey's owner
	 */
	public void setOwner(User owner) {
		this.owner = owner;
	}

	/**
	 * This method returns root component of this survey.
	 * 
	 * @return root component
	 */
	public Component getRootComponent() {
		return this.rootComponent;
	}

	/**
	 * This method sets root component of this survey.
	 * 
	 * @param rootComponent
	 *            root component of this survey
	 */
	public void setRootComponent(Component rootComponent) {
		this.rootComponent = rootComponent;
	}

	/**
	 * This method returns survey's expiration date.
	 * 
	 * @return survey's expiration date
	 */
	public Date getExpirationDate() {
		return this.expirationDate;
	}

	/**
	 * This method sets survey's expiration date.
	 * 
	 * @param expirationDate
	 *            survey's expiration date
	 */
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	/**
	 * This method marks this survey as readonly.
	 */
	public void setReadonlyFlag() {
		this.isReadonly = true;
	}

	/**
	 * This method checks if this survey is marked as readonly.
	 * 
	 * @return true if survey is marked as readonly, false otherwise
	 */
	public Boolean isReadonly() {
		return this.isReadonly;
	}

}
