package org.meveo.admin.action.payments;

import java.util.List;

import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.IsoIcd;
import org.meveo.model.billing.UntdidInvoiceSubjectCode;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.IsoIcdService;
import org.meveo.service.billing.impl.UntdidInvoiceSubjectCodeService;

@Named
@ViewScoped
public class IsoIcdBean extends BaseBean<IsoIcd> {

        private static final long serialVersionUID = 1L;

        /**
         * Injected @{link OCCTemplate} service. Extends {@link PersistenceService}.
         */
        @Inject
        private IsoIcdService isoIcdService;
    
        /**
         * Constructor. Invokes super constructor and provides class type of this
         * bean for {@link BaseBean}.
         */
        public IsoIcdBean() {
            super(IsoIcd.class);
            showDeprecatedWarning();
        }
    
        /**
         * Factory method for entity to edit. If objectId param set load that entity
         * from database, otherwise create new.
         * @return account operation template
         * 
         */
        @Produces
        @Named("isoIcd")
        public IsoIcd init() {
            return initEntity();
        }
        
        @Override
        public String getNewViewName() {
            return "isoIcdDetail";
        }
    
        @Override
        protected String getListViewName() {
            return "isoIcds";
        }
    
        @Override
        public String getEditViewName() {
            return "isoIcdDetail";
        }
    
        public List<IsoIcd> listInvoiceSubjectCode() {
            return (List<IsoIcd>) isoIcdService
                    .getListIsoIcdByName();
        }
    
        /**
         * @see org.meveo.admin.action.BaseBean#getPersistenceService()
         */
        @Override
        protected IPersistenceService<IsoIcd> getPersistenceService() {
            return isoIcdService;
        }

}
