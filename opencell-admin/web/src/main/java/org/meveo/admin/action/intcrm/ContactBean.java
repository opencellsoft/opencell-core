package org.meveo.admin.action.intcrm;

import java.io.IOException;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.communication.contact.Contact;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.intcrm.impl.ContactService;
import org.slf4j.Logger;


@Named
@ViewScoped
public class ContactBean extends CustomFieldBean<Contact>{
	@Inject
	private ContactService contactService;
	
	@Inject	
	private Logger log;
	
	private Part file;
	private String fileContent;
	
	private String code;
	private Long id;
	
	  
	public ContactBean() {
		super(Contact.class);
	}
	
	public void importContacts() {
		log.debug("Importing Contacts");
		contactService.parseLinkedInFile(null);
		//contactService.saveContact("Arnaud,UHLRICH,,arnaud.uhlrich@free.Fr,Magellan Partners - ASAPpro,Senior Manager - Director,\"10/20/13 9:34AM\",,SKYPE:uhlricha");
	}
	
	public void findContactByCode() {
		log.debug("Finding Contact");
		Contact contact = contactService.findByCode(code);
		if(contact == null) log.debug("Contact not found");
	}
	
	public void findContactById() {
		log.debug("Finding Contact");
		Contact contact = contactService.findById(id);
		if(contact == null)
			log.debug("Contact not found");
		else {
			log.debug(contact.toString());
		}
	}
	
	public void removeAllContacts() throws BusinessException {
		log.debug("Removing Contacts");
		contactService.removeAllContacts();
		log.debug("Contacts Removed");
	}
	@PostConstruct
	private void init() {
		log.debug("start ContactBean");
	}
	
	public void upload() {
	    try {
	    	fileContent = new Scanner(file.getInputStream())
	          .useDelimiter("\\A").next();
	    	contactService.parseLinkedInFromText(fileContent);
	    } catch (IOException | BusinessException e) {
	      // Error handling
	    }
	}
	
	public Part getFile() {
	    return file;
	}
	 
	  public void setFile(Part file) {
	    this.file = file;
	}
	  
	  

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	protected IPersistenceService<Contact> getPersistenceService() {
		return contactService;
	}
}
