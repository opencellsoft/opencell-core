package org.meveo.admin.action.payments;

import java.util.List;

import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.UntdidInvoiceCodeType;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.UntdidInvoiceCodeTypeService;

@Named
@ViewScoped
public class InvoiceCodeTypeBean extends BaseBean<UntdidInvoiceCodeType> {

        private static final long serialVersionUID = 1L;

        /**
         * Injected @{link OCCTemplate} service. Extends {@link PersistenceService}.
         */
        @Inject
        private UntdidInvoiceCodeTypeService invoiceCodeTypeService;
    
        /**
         * Constructor. Invokes super constructor and provides class type of this
         * bean for {@link BaseBean}.
         */
        public InvoiceCodeTypeBean() {
            super(UntdidInvoiceCodeType.class);
            showDeprecatedWarning(DEPRECATED_ADMIN_MESSAGE);
        }
    
        /**
         * Factory method for entity to edit. If objectId param set load that entity
         * from database, otherwise create new.
         * @return account operation template
         * 
         */
        @Produces
        @Named("invoiceCodeType")
        public UntdidInvoiceCodeType init() {
            return initEntity();
        }
        
        @Override
        public String getNewViewName() {
            return "invoiceCodeTypeDetail";
        }
    
        @Override
        protected String getListViewName() {
            return "invoiceCodeTypes";
        }
    
        @Override
        public String getEditViewName() {
            return "invoiceCodeTypeDetail";
        }
    
        public List<UntdidInvoiceCodeType> listInvoiceCodeType() {
            return (List<UntdidInvoiceCodeType>) invoiceCodeTypeService
                    .getListInvoiceCodeTypeByName();
        }
    
        /**
         * @see org.meveo.admin.action.BaseBean#getPersistenceService()
         */
        @Override
        protected IPersistenceService<UntdidInvoiceCodeType> getPersistenceService() {
            return invoiceCodeTypeService;
        }

}