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
 * This class represents form for particular survey and user.
 * 
 * @author fibi
 * @author kornel
 */
@Entity()
@Table(name = "Forms")
public class Form extends pl.edu.agh.iosr.surveylance.entities.Entity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String hash;

	private Boolean submitted;

	private Double x;

	private Double y;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "survey_id")
	private Survey survey;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	/**
	 * Public constructor.
	 */
	public Form() {
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
	 * This method returns survey.
	 * 
	 * @return survey
	 */
	public Survey getSurvey() {
		return this.survey;
	}

	/**
	 * This method sets survey.
	 * 
	 * @param survey
	 *            survey
	 */
	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	/**
	 * This method returns form's user.
	 * 
	 * @return form's user
	 */
	public User getuser() {
		return this.user;
	}

	/**
	 * This method sets form's user.
	 * 
	 * @param user
	 *            form's user
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * This method returns form's hash string.
	 * 
	 * @return form's hash string
	 */
	public String getHash() {
		return this.hash;
	}

	/**
	 * This method sets form's hash string.
	 * 
	 * @param hash
	 *            form's hash string
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * This method returns flag if form has been submitted.
	 * 
	 * @return flag if form has been submitted
	 */
	public Boolean getSubmitted() {
		return this.submitted;
	}

	/**
	 * This method sets flag if form has been submitted
	 * 
	 * @param submitted
	 *            flag if form has been submitted
	 */
	public void setSubmitted(Boolean submitted) {
		this.submitted = submitted;
	}

	/**
	 * Sets latitude.
	 * 
	 * @param x
	 *            latitude coordinate
	 */
	public void setX(Double x) {
		this.x = x;
	}

	/**
	 * Returns latitude.
	 * 
	 * @return x latitude coordinate
	 */
	public Double getX() {
		return this.x;
	}

	/**
	 * Sets longitude.
	 * 
	 * @param y
	 *            longitude coordinate
	 */
	public void setY(Double y) {
		this.y = y;
	}

	/**
	 * Returns longitude.
	 * 
	 * @return y longitude coordinate
	 */
	public Double getY() {
		return this.y;
	}

}
