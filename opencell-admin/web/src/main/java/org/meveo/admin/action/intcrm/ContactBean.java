/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.action.intcrm;

import java.io.IOException;
import java.util.Scanner;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.api.crm.ContactApi;
import org.meveo.commons.utils.FileUtils;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.Name;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.intcrm.impl.ContactService;
import org.slf4j.Logger;

import com.opencsv.exceptions.CsvException;


@Named

public class ContactBean extends BaseBean<Contact> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4860235761800843336L;

	@Inject
	private
	ContactService contactService;
	
	@Inject
	ContactApi contactApi;
	
	@Inject	
	private Logger log;
	
	private Part file;
	private String fileContent;
		
	  
	public ContactBean() {
		super(Contact.class);
	}
	
	
	@Override
	public Contact initEntity() {
		super.initEntity();
		if (entity.getAddress() == null) {
            entity.setAddress(new Address());
        }
        if (entity.getName() == null) {
            entity.setName(new Name());
        }
        return entity;
	}
	
	@Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        String outcome = super.saveOrUpdate(killConversation);

        if (outcome != null) {
            return getEditViewName(); 
        }
        return null;
    }
	
	public void removeAllContacts() throws BusinessException {
		log.debug("Removing Contacts");
		contactService.removeAllContacts();
		log.debug("Contacts Removed");
	}
	
	@SuppressWarnings("resource")
	@ActionMethod
	public String upload() {
		String message = null;
	    	try {
				fileContent = new Scanner(file.getInputStream()).useDelimiter("\\A").next();
		    	contactApi.importCSVText(fileContent);
		    	message = FileUtils.getFileAsString(System.getProperty("jboss.server.temp.dir") + "\\LastContactError.log");
		    	
		    	if(message != null)
		    		return message;
		    	else return "Success";
			} catch (IOException | CsvException e) {
				return "Failed to read error log";
			}
	}
	
	public Part getFile() {
	    return file;
	}
	 
	  public void setFile(Part file) {
	    this.file = file;
	}

	@Override
	protected IPersistenceService<Contact> getPersistenceService() {
		return contactService;
	}
	

    @Override
    protected String getDefaultSort() {
        return "code";
    }
}
