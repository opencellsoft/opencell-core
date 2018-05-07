import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;


import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;	
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.communication.contact.Contact;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.intcrm.impl.AddressBookService;
import org.meveo.service.intcrm.impl.ContactService;


@Named
@ViewScoped
public class ImportContactBean extends CustomFieldBean<Contact>{
	
	@Inject
    private ContactService contactService;
	
	@Inject
    private AddressBookService addressBookService;
	
	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public ImportContactBean() {
		super(Contact.class);
	}

	
    public void show() {
        System.out.println("Testing Bean");
    }
    
    /**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<Contact> getPersistenceService() {
		return contactService;
	}

}
