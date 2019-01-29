package pl.edu.agh.iosr.surveylance.service;

import java.util.List;
import java.util.Map;

import com.google.gdata.data.contacts.ContactEntry;

import pl.edu.agh.iosr.surveylance.entities.Contact;

/**
 * Manages user's contacts using Google Contacts API.
 * 
 * @author kuba
 */
public interface ContactManager {

	/**
	 * Sets AuthSub session token.
	 * 
	 * @param sessionToken
	 *            session token to be set
	 */
	public void setSessionToken(String sessionToken);

	/**
	 * Adds contact to user's contacts list by converting application specific
	 * Contact structure into Google Acount Contact Entry.
	 * 
	 * @param contact
	 *            contact being added
	 */
	public void addContact(Contact contact);

	/**
	 * Finds ContactEntry (Google class) by mail attached as a parameter used
	 * when editing existing contact, where we need to keep reference to it.
	 * 
	 * @param mail
	 *            contact's e-mail address
	 * 
	 * @return ContactEntry corresponding to contact with such e-mail
	 */
	public ContactEntry getContactEntryByMail(String mail);

	/**
	 * Returns contact entity asociated with given e-mail address.
	 * 
	 * @param mail
	 *            contact's e-mail address
	 * 
	 * @return contact entity asociated with given mail
	 */
	public Contact getContactByMail(String mail);

	/**
	 * Looks for all currently logged in user's contacts and returns it's list.
	 * 
	 * @return all user's contacts entities lists
	 */
	public List<Contact> getContacts();

	/**
	 * Updates contact entry.
	 * 
	 * @param contactEntry
	 *            contact entry to be updated
	 */
	public void updateEntry(ContactEntry contactEntry);

	/**
	 * Looks for all currently created groups.
	 * 
	 * @return all groups
	 */
	public Map<String, String> getGroupNames();

	/**
	 * Adds a group with a given name
	 */
	public void addGroup(String groupName);

}
