package pl.edu.agh.iosr.surveylance.service.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.contacts.ContactGroupFeed;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.util.ServiceException;

import pl.edu.agh.iosr.surveylance.entities.Contact;
import pl.edu.agh.iosr.surveylance.service.ContactManager;

/**
 * Implements {@link ContactManager} interface. Provides contacts manager using
 * Google Contacts API.
 * 
 * @author stefan
 * @author kuba
 */
public class ContactManagerImpl implements ContactManager {

	private static final Logger logger = Logger
			.getLogger(ContactManagerImpl.class);

	private static final int MAX_RESULTS = 1000;

	private ContactsService contactsService = new ContactsService(
			"surveylance-1.0");

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSessionToken(String sessionToken) {
		contactsService.setAuthSubToken(sessionToken);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Contact> getContacts() {
		List<Contact> contactsList = new ArrayList<Contact>();

		for (ContactEntry contactEntry : getContactEntries())
			contactsList.add(new Contact(contactEntry));

		return contactsList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addContact(Contact contact) {
		ContactEntry contactEntry = new ContactEntry();
		contactEntry.setTitle(new PlainTextConstruct(contact.getDisplayName()));

		Email primaryMail = new Email();
		primaryMail.setAddress(contact.getMail());
		primaryMail.setRel("http://schemas.google.com/g/2005#home");
		primaryMail.setPrimary(true);
		contactEntry.addEmailAddress(primaryMail);

		try {
			URL feedUrl = new URL(
					"http://www.google.com/m8/feeds/contacts/default/full");
			contactsService.insert(feedUrl, contactEntry);
		} catch (Exception e) {
			logger.error("Exception while adding contact " + e.getMessage());
		}
	}

	/**
	 * Used to get all contact entries from feed.
	 * 
	 * @return list of all contacts entries from feed
	 */
	private List<ContactEntry> getContactEntries() {
		URL feedUrl = null;
		ContactFeed resultFeed = null;

		try {
			feedUrl = new URL(
					"http://www.google.com/m8/feeds/contacts/default/full");
		} catch (MalformedURLException e1) {
			logger.error("Exception while getting entries " + e1.getMessage());
		}

		Query myQuery = new Query(feedUrl);
		myQuery.setMaxResults(MAX_RESULTS);

		try {
			resultFeed = contactsService.query(myQuery, ContactFeed.class);
		} catch (IOException e) {
			logger.error("Exception while getting entries " + e.getMessage());
		} catch (ServiceException e) {
			logger.error("Exception while getting entries " + e.getMessage());
		}

		List<ContactEntry> entries = resultFeed.getEntries();
		Collections.sort(entries, new Comparator<ContactEntry>() {

			@Override
			public int compare(ContactEntry o1, ContactEntry o2) {
				return o2.getUpdated().compareTo(o1.getUpdated());
			}

		});
		return entries;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContactEntry getContactEntryByMail(String mail) {
		ContactEntry result = null;
		for (ContactEntry contactEntryTemp : getContactEntries()) {
			if (contactEntryTemp.getEmailAddresses().get(0).getAddress()
					.compareTo(mail) == 0) {
				result = contactEntryTemp;
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Contact getContactByMail(String mail) {
		Contact result = null;
		for (Contact contactTemp : getContacts()) {
			if (contactTemp.getMail().compareTo(mail) == 0)
				result = contactTemp;
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEntry(ContactEntry contactEntry) {
		URL editUrl = null;

		try {
			editUrl = new URL(contactEntry.getEditLink().getHref());
		} catch (MalformedURLException e) {
			logger.error("Exception while updating entry " + e.getMessage());
		}

		try {
			contactsService.update(editUrl, contactEntry);
		} catch (IOException e) {
			logger.error("Exception while updating entry " + e.getMessage());
		} catch (ServiceException e) {
			logger.error("Exception while updating entry " + e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getGroupNames() {
		Map<String, String> groups = new HashMap<String, String>();

		URL feedUrl2 = null;
		try {
			feedUrl2 = new URL(
					"http://www.google.com/m8/feeds/groups/default/full");
		} catch (MalformedURLException e) {
			logger
					.error("Exception while creating feed URL: "
							+ e.getMessage());
		}
		ContactGroupFeed resultFeed2 = null;
		try {
			resultFeed2 = contactsService.getFeed(feedUrl2,
					ContactGroupFeed.class);
		} catch (IOException e) {
			logger.error("Exception while getting feed: " + e.getMessage());
		} catch (ServiceException e) {
			logger.error("Exception while getting feed (service): "
					+ e.getMessage());
		}
		for (int i = 0; i < resultFeed2.getEntries().size(); i++) {
			ContactGroupEntry groupEntry = resultFeed2.getEntries().get(i);

			String[] splitted = groupEntry.getSelfLink().getHref().split("/");

			groups.put(splitted[splitted.length - 1], groupEntry.getTitle()
					.getPlainText());

		}

		return groups;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addGroup(String groupName) {
		ContactGroupEntry group = new ContactGroupEntry();
		group.setTitle(new PlainTextConstruct(groupName));

		URL postUrl = null;
		try {
			postUrl = new URL(
					"http://www.google.com/m8/feeds/groups/default/full");
		} catch (MalformedURLException e) {
			logger.error("Exception while post URL: " + e.getMessage());
		}
		try {
			contactsService.insert(postUrl, group);
		} catch (IOException e) {
			logger.error("Exception while inserting: " + e.getMessage());
		} catch (ServiceException e) {
			logger.error("Exception while inserting (service): "
					+ e.getMessage());
		}
	}

}
