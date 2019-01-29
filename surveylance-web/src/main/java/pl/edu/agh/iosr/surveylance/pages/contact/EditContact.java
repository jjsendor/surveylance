package pl.edu.agh.iosr.surveylance.pages.contact;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;

import pl.edu.agh.iosr.surveylance.entities.Contact;
import pl.edu.agh.iosr.surveylance.service.ContactManager;

import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.contacts.ContactEntry;

public class EditContact {

	@Inject
	private ContactManager contactManager;

	@Persist
	private Contact contact;

	@Persist
	private ContactEntry contactEntry;

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Contact getContact() {
		return contact;
	}

	void onActivate(String mail) {
		contactEntry = contactManager.getContactEntryByMail(mail);
		contact = new Contact(contactEntry);
	}

	public Object onSubmit() {
		contactEntry.setTitle(new PlainTextConstruct(contact.getDisplayName()));
		contactEntry.getEmailAddresses().get(0).setAddress(contact.getMail());
		contactManager.updateEntry(contactEntry);

		return ContactList.class;
	}

}
