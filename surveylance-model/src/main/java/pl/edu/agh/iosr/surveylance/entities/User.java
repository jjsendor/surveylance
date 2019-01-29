package pl.edu.agh.iosr.surveylance.entities;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.UniqueConstraint;

/**
 * This class represents single user.
 * 
 * @author kornel
 */
@Entity()
@Table(name = "Users", uniqueConstraints = { @UniqueConstraint(columnNames = { "googleId" }) })
public class User extends pl.edu.agh.iosr.surveylance.entities.Entity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String googleId;

	/**
	 * Public constructor.
	 */
	public User() {
	}

	/**
	 * This method returns user's id.
	 * 
	 * @return user's id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * This method returns user's google id.
	 * 
	 * @return user's google id
	 */
	public String getGoogleId() {
		return this.googleId;
	}

	/**
	 * This method sets user's google id.
	 * 
	 * @param googleId
	 *            user's google id
	 */
	public void setGoogleId(String googleId) {
		this.googleId = googleId;
	}

}
