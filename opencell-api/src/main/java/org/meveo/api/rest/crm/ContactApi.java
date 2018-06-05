package org.meveo.api.rest.crm;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.crm.ContactDto;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Country;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class ContactApi extends BaseApi {

    

	public void create(ContactDto postData) {
	    if (StringUtils.isBlank(postData.getName().getFirstName())) {
	        missingParameters.add("firstName");
	    }

	    if (StringUtils.isBlank(postData.getName().getLastName())) {
	        missingParameters.add("lastName");
	    }
	    
	    if (StringUtils.isBlank(postData.getEmail())) {
	        missingParameters.add("email");
	    }
	    

        try {
			handleMissingParameters();
		} catch (MissingParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Contact contact = new Contact();
        Name name = new Name(new Title("M.", false), postData.getName().getFirstName(), postData.getName().getLastName());
        Country country = new Country();
        Address address = new Address(postData.getAddress().getAddress1(), postData.getAddress().getAddress2(), postData.getAddress().getAddress3(), postData.getAddress().getZipCode(), postData.getAddress().getCity(), country, postData.getAddress().getState());
        contact.setName(name);
        
        
	}
}
