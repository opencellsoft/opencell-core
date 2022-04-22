package org.meveo.service.script.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.crm.Customer;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.intcrm.impl.ContactService;
import org.meveo.service.script.module.ModuleScript;
import org.meveo.service.security.SecuredBusinessEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class SetUserSecuredEntitiesScript extends ModuleScript {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String CUSTOMER_CODES_BY_CONTACT = "select distinct ae.code as code from bi_customers_contacts cuc \r\n"
    		+ "inner join com_contact cc on cuc.contact_id=cc.id\r\n"
    		+ "inner join crm_customer cu on cuc.customer_id=cu.id \r\n"
    		+ "inner join account_entity ae on cu.id=ae.id\r\n"
    		+ "where cc.id=:contactId ";
    
    private CustomerService customerService =
            (CustomerService) getServiceInterface("CustomerService");
    private UserService userService =
            (UserService) getServiceInterface("UserService");
    private ContactService contactService =
            (ContactService) getServiceInterface("ContactService");



    @Override
    public void execute(Map<String, Object> methodContext) throws BusinessException {
        Contact contact = (Contact) methodContext.get("contact");
        contact=contactService.findById(contact.getId());
        if(contact == null) {
            throw new BusinessException("the contact is not found");
        }
        User user=userService.findByEmail(contact.getEmail());
        Map<String, Object> params = new HashMap<>();
        params.put("email", user.getEmail());
        SecuredEntity securedEntity=null;
        if (user!=null) {
        	securedEntity=new SecuredEntity();
    		securedEntity.setEntityClass(Contact.class.getName());
    		securedEntity.setCode(contact.getCode());
    		if(!user.getSecuredEntities().contains(securedEntity)) {
    			log.info("SetUserSecuredEntities user email={}, added contact = {}",user.getEmail(),contact.getCode());
    			user.getSecuredEntities().add(securedEntity);
    		}
    		
    		List<String> customerCodes=findCustomerCodeByContactId(contact.getId());
        	log.info("SetUserSecuredEntities user email={} customerCodes size={}",user.getEmail(),customerCodes.size());
        	for(String code:customerCodes) {
        		securedEntity=new SecuredEntity();
        		securedEntity.setEntityClass(Customer.class.getName());
        		securedEntity.setCode(code);
        		if(!user.getSecuredEntities().contains(securedEntity)) {
        			log.info("SetUserSecuredEntities user email={}, added customer = {}",user.getEmail(),code);
        			user.getSecuredEntities().add(securedEntity);
        		}	
        	}	
        }
        
    }

    private List<String> findCustomerCodeByContactId(Long contactId) {
        List<Map<String, Object>> codes = customerService.executeNativeSelectQuery(CUSTOMER_CODES_BY_CONTACT,  Map.of("contactId", contactId));
        return codes.stream()
                .map(row -> (String) row.get("code"))
                .collect(Collectors.toList());
    }

}
