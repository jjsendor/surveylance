package pl.edu.agh.iosr.surveylance.pages.contact;

import java.util.Map;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;

import pl.edu.agh.iosr.surveylance.entities.Contact;
import pl.edu.agh.iosr.surveylance.service.ContactManager;

/**
 * Responsible for adding and validating contact.
 * 
 * @author Stefan
 */
public class AddContact {

	@Persist
	private Contact contact;

	@Inject
	private ContactManager contactManager;

	public Object onSubmit() {
		contactManager.addContact(contact);
		return ContactList.class;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	private Map<String, String> groupNames;

	public void setGroupNames(Map<String, String> groupNames) {
		this.groupNames = groupNames;
	}

	public Map<String, String> getGroupNames() {
		return groupNames;
	}

	public void onActivate() {
		setGroupNames(contactManager.getGroupNames());
		// TODO stefan check if it's correct to provide third parameter as null
		// in this case!!!
		// stefan: dunno :] (this field is required by tapestry but never used
		// by me)
		contact = new Contact("", "", null);
	}

}
