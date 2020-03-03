package org.meveo.admin.action.tax;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.DatePeriod;
import org.meveo.model.tax.TaxMapping;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.tax.TaxMappingService;

/**
 * Back bean implementation for GUI details page for Tax mapping
 * 
 * @author Andrius Karpavicius
 *
 */
@Named
@ViewScoped
public class TaxMappingBean extends BaseBean<TaxMapping> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link TaxMapping} service. Extends {@link PersistenceService}.
     */
    @Inject
    private TaxMappingService entityService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public TaxMappingBean() {
        super(TaxMapping.class);
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<TaxMapping> getPersistenceService() {
        return entityService;
    }

    @Override
    public TaxMapping initEntity() {

        super.initEntity();
        if (entity.getValid() == null) {
            entity.setValid(new DatePeriod());
        }
        return entity;
    }
}