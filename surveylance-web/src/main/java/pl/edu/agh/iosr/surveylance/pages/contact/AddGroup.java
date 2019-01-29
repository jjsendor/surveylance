package pl.edu.agh.iosr.surveylance.pages.contact;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;

import pl.edu.agh.iosr.surveylance.service.ContactManager;

/**
 * Responsible for adding and validating contact.
 * 
 * @author Stefan
 */
public class AddGroup {

	@Persist
	private String groupName;

	@Inject
	private ContactManager contactManager;

	public Object onSubmit() {
		contactManager.addGroup(groupName);
		return ContactList.class;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
