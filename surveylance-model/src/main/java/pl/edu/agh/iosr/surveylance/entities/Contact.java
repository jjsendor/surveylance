package pl.edu.agh.iosr.surveylance.entities;

import java.util.List;

import org.apache.tapestry5.beaneditor.Validate;

import com.google.gdata.data.contacts.ContactEntry;

/**
 * This class represents Google Contact entry.
 * 
 * @author stefan
 * @author kornel
 */
public class Contact {

	private String mail;
	private String displayName;
	private List<String> groups;

	/**
	 * Constructor to convert Google's ContactEntry to qualified data structure.
	 * 
	 * @param contactEntry
	 *            google contact entry
	 */
	public Contact(ContactEntry contactEntry) {
		this.mail = contactEntry.getEmailAddresses().get(0).getAddress();
		this.displayName = contactEntry.getTitle().getPlainText();
	}

	/**
	 * 
	 * @param mail
	 *            e-mail address
	 * @param displayName
	 *            displayed name
	 * @param groups
	 *            group list that contact belongs
	 */
	public Contact(String mail, String displayName, List<String> groups) {
		super();
		this.mail = mail;
		this.displayName = displayName;
		this.groups = groups;
	}

	/**
	 * This method returns contact's display name.
	 * 
	 * @return contact's display name
	 */
	@Validate("required,min=2,max=120")
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * This method sets contact's display name.
	 * 
	 * @param displayName
	 *            contact's display name
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * This method returns contact's e-mail address.
	 * 
	 * @return contact's e-mail address
	 */
	@Validate("required,min=5,max=120,regexp")
	public String getMail() {
		return this.mail;
	}

	/**
	 * This method sets contact's e-mail address.
	 * 
	 * @param mail
	 *            contact's e-mail address
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}

	/**
	 * Check if this contact belongs to any group.
	 * 
	 * @return <code>true</code> if there is any group contains this contact,
	 *         <code>false</code> otherwise
	 */
	public boolean isGroupsExist() {
		List<String> temp = this.getGroups();
		if (temp == null)
			return false;
		else
			return this.getGroups().size() > 0;
	}

	/**
	 * This method returns contact's groups.
	 * 
	 * @return contact's groups
	 */
	public List<String> getGroups() {
		return this.groups;
	}

	/**
	 * This method gets contact's groups.
	 * 
	 * @param groups
	 *            contact's groups
	 */
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	/**
	 * This method returns contact as a String object.
	 * 
	 * @return contact as a String object
	 */
	public String toString() {
		return this.getDisplayName() + " " + this.getMail();
	}

}
