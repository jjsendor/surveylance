package pl.edu.agh.iosr.surveylance.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * This class represents question text answer.
 * 
 * @author fibi
 */
@Entity()
@DiscriminatorValue("TextAnswer")
public class TextAnswer extends Answer {

	@Column(name = "string_value")
	private String value;

	/**
	 * This method returns answer's value.
	 * 
	 * @return answer's value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * This method sets answer's value.
	 * 
	 * @param value
	 *            answer's value
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
