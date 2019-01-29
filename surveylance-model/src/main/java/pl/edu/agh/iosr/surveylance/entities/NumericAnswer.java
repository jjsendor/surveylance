package pl.edu.agh.iosr.surveylance.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * This class represents question numeric answer.
 * 
 * @author fibi
 * @author kornel
 */
@Entity()
@DiscriminatorValue("NumericAnswer")
public class NumericAnswer extends Answer {

	@Column(name = "numeric_value")
	private Double value;

	/**
	 * This method returns answer's value.
	 * 
	 * @return answer's value
	 */
	public Double getValue() {
		return this.value;
	}

	/**
	 * This method sets answer's value.
	 * 
	 * @param value
	 *            answer's value
	 */
	public void setValue(Double value) {
		this.value = value;
	}

}
