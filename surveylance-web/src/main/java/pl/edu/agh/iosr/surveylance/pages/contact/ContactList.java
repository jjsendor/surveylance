package pl.edu.agh.iosr.surveylance.pages.contact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.ioc.annotations.Inject;

import pl.edu.agh.iosr.surveylance.entities.Contact;
import pl.edu.agh.iosr.surveylance.service.ContactManager;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

public class ContactList {

	@ApplicationState(create = false)
	private UserSessionInfo userInfo;

	@Inject
	@Service("contactManager")
	private ContactManager contactManager;

	@SuppressWarnings("unused")
	@Property
	private Contact contact;

	@SuppressWarnings("unused")
	@Property
	private String mail;

	@Property
	private List<String> addedMails = new ArrayList<String>();

	public boolean isMailAdded() {
		return addedMails.size() > 0;
	}

	@SuppressWarnings("unused")
	@Property
	private String group;

	@SuppressWarnings("unused")
	@Property
	private Collection<String> groupNames;

	void onActivate() {
		contactManager.setSessionToken(userInfo.getSessionToken());
		groupNames = contactManager.getGroupNames().values();
	}

	public List<Contact> getContacts() {
		return contactManager.getContacts();
	}

	public void onActionFromDelete(String mail_) {
		addedMails.remove(mail_);
	}

	public void onActionFromAdd(String mail_) {
		addedMails.add(mail_);
	}

	public boolean isMessage() {
		return true;
	}

}
