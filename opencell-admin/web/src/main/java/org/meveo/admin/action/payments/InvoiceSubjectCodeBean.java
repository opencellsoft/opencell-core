package org.meveo.admin.action.payments;

import java.util.List;

import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.UntdidInvoiceSubjectCode;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.UntdidInvoiceSubjectCodeService;

@Named

public class InvoiceSubjectCodeBean extends BaseBean<UntdidInvoiceSubjectCode> {

        private static final long serialVersionUID = 1L;

        /**
         * Injected @{link OCCTemplate} service. Extends {@link PersistenceService}.
         */
        @Inject
        private UntdidInvoiceSubjectCodeService invoiceSubjectCodeService;
    
        /**
         * Constructor. Invokes super constructor and provides class type of this
         * bean for {@link BaseBean}.
         */
        public InvoiceSubjectCodeBean() {
            super(UntdidInvoiceSubjectCode.class);
            showDeprecatedWarning();
        }
    
        /**
         * Factory method for entity to edit. If objectId param set load that entity
         * from database, otherwise create new.
         * @return account operation template
         * 
         */
        @Produces
        @Named("invoiceSubjectCode")
        public UntdidInvoiceSubjectCode init() {
            return initEntity();
        }
        
        @Override
        public String getNewViewName() {
            return "invoiceSubjectCodeDetail";
        }
    
        @Override
        protected String getListViewName() {
            return "invoiceSubjectCodes";
        }
    
        @Override
        public String getEditViewName() {
            return "invoiceSubjectCodeDetail";
        }
    
        public List<UntdidInvoiceSubjectCode> listInvoiceSubjectCode() {
            return (List<UntdidInvoiceSubjectCode>) invoiceSubjectCodeService
                    .getListInvoiceSubjectCodeByName();
        }
    
        /**
         * @see org.meveo.admin.action.BaseBean#getPersistenceService()
         */
        @Override
        protected IPersistenceService<UntdidInvoiceSubjectCode> getPersistenceService() {
            return invoiceSubjectCodeService;
        }

}
