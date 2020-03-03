package org.meveo.admin.action.tax;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.tax.TaxClassService;

/**
 * Back bean implementation for GUI details page for Tax class
 * 
 * @author Andrius Karpavicius
 *
 */
@Named
@ViewScoped
public class TaxClassBean extends BaseBean<TaxClass> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link TaxClass} service. Extends {@link PersistenceService}.
     */
    @Inject
    private TaxClassService entityService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public TaxClassBean() {
        super(TaxClass.class);
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<TaxClass> getPersistenceService() {
        return entityService;
    }
}