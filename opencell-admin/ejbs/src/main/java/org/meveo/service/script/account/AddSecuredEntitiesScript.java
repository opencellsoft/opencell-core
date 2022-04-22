package org.meveo.service.script.account;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.crm.Customer;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.script.finance.ReportExtractScript;

public class AddSecuredEntitiesScript extends ReportExtractScript {
;
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String CUSTOMER_CODES_BY_USER = "select distinct ae.code as code from bi_customers_contacts cuc \r\n"
    		+ "inner join com_contact cc on cuc.contact_id=cc.id\r\n"
    		+ "inner join crm_customer cu on cuc.customer_id=cu.id \r\n"
    		+ "inner join account_entity ae on cu.id=ae.id\r\n"
    		+ "inner join adm_user us on cc.email=us.email \r\n"
    		+ "where us.email=:userEmail ";
	
	private static final String CONTACT_CODES_BY_USER = "select distinct ae.code as code from bi_customers_contacts cuc \r\n"
    		+ "inner join com_contact cc on cuc.contact_id=cc.id\r\n"
    		+ "inner join account_entity ae on cc.id=ae.id\r\n"
    		+ "inner join adm_user us on cc.email=us.email \r\n"
    		+ "where us.email=:userEmail ";
    
    private CustomerService customerService =
            (CustomerService) getServiceInterface("CustomerService");
    private UserService userService =
            (UserService) getServiceInterface("UserService");



    @Override
    public void execute(Map<String, Object> executeContext) throws BusinessException {
        log.info("SetSecuredEntitiesScript started");
        List<User> users=userService.listActive();
        
        log.info("SetSecuredEntitiesScript users size={}",users.size());
        
      users.stream().forEach(user -> addSecuredEntities(user));
       
    }

    private void addSecuredEntities(User user) {
    	try {
    		if(!StringUtils.isBlank(user.getEmail())) {
    			List<String> customerCodes=findCustomCodesByUserEmail(user.getEmail());
            	log.info("addSecuredEntities user email={} customerCodes size={}",user.getEmail(),customerCodes.size());
            	SecuredEntity securedEntity=null;
            	for(String code:customerCodes) {
            		securedEntity=new SecuredEntity();
            		securedEntity.setEntityClass(Customer.class.getName());
            		securedEntity.setCode(code);
            		if(!user.getSecuredEntities().contains(securedEntity)) {
            			log.info("addSecuredEntities user email={}, added securedEntity = {}",user.getEmail(),code);
            			user.getSecuredEntities().add(securedEntity);
            		}
            		
            		Customer customer=customerService.findByCode(code);
            		
            		if(customer!=null && customer.getSeller()!=null) {
            			securedEntity=new SecuredEntity();
                		securedEntity.setEntityClass(Seller.class.getName());
                		securedEntity.setCode(customer.getSeller().getCode());
            			if(!user.getSecuredEntities().contains(securedEntity)) {
                			user.getSecuredEntities().add(securedEntity);
                		}
            		}
            		
            	}
            	
            	List<String> contactCodes=findContactCodesByUserEmail(user.getEmail());
            	log.info("addSecuredEntities user email={} customerCodes size={}",user.getEmail(),contactCodes.size());
            	
            	for(String code:contactCodes) {
            		securedEntity=new SecuredEntity();
            		securedEntity.setEntityClass(Contact.class.getName());
            		securedEntity.setCode(code);
            		if(!user.getSecuredEntities().contains(securedEntity)) {
            			log.info("addSecuredEntities user email={}, added securedEntity = {}",user.getEmail(),code);
            			user.getSecuredEntities().add(securedEntity);
            		}
            	}
            	
            	
            	
                userService.update(user);
    		}
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    
    }
 

    private List<String> findCustomCodesByUserEmail(String email) {
        List<Map<String, Object>> codes = customerService.executeNativeSelectQuery(CUSTOMER_CODES_BY_USER,  Map.of("userEmail", email));
        return codes.stream()
                .map(row -> (String) row.get("code"))
                .collect(Collectors.toList());
    }

    private List<String> findContactCodesByUserEmail(String email) {
        List<Map<String, Object>> codes = customerService.executeNativeSelectQuery(CONTACT_CODES_BY_USER,  Map.of("userEmail", email));
        return codes.stream()
                .map(row -> (String) row.get("code"))
                .collect(Collectors.toList());
    }

}