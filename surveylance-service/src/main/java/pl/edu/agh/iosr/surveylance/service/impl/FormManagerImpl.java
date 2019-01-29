package pl.edu.agh.iosr.surveylance.service.impl;

import java.util.List;

import pl.edu.agh.iosr.surveylance.dao.FormDAO;
import pl.edu.agh.iosr.surveylance.entities.Form;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.service.FormManager;

/**
 * Implementation of the {@link FormDAO} interface.
 * 
 * @author kornel
 */
public class FormManagerImpl implements FormManager {

	private FormDAO formDAO;

	/** {@inheritDoc} */
	@Override
	public List<Form> getFormsBySurvey(Survey survey) {
		return this.formDAO.findBySurvey(survey);
	}

	/** {@inheritDoc} */
	@Override
	public void setFormDAO(FormDAO formDAO) {
		this.formDAO = formDAO;
	}

	/** {@inheritDoc} */
	@Override
	public Form getFormByID(Long id) {
		return this.formDAO.findById(id, false);
	}

}
