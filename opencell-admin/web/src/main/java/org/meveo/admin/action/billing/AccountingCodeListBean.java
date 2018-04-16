package org.meveo.admin.action.billing;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

import org.meveo.model.billing.AccountingCode;

/**
 * Controller to manage list view of {@link AccountingCode}.
 * 
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.0
 **/
@Named
@ConversationScoped
public class AccountingCodeListBean extends AccountingCodeBean {

    private static final long serialVersionUID = 7930022522613448157L;

}
