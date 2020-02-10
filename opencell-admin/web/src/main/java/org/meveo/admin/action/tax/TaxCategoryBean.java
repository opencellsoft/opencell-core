package org.meveo.admin.action.tax;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.tax.TaxCategory;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.tax.TaxCategoryService;

/**
 * Back bean implementation for GUI details page for Tax category
 * 
 * @author Andrius Karpavicius
 *
 */
@Named
@ViewScoped
public class TaxCategoryBean extends BaseBean<TaxCategory> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link TaxCategory} service. Extends {@link PersistenceService}.
     */
    @Inject
    private TaxCategoryService entityService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public TaxCategoryBean() {
        super(TaxCategory.class);
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<TaxCategory> getPersistenceService() {
        return entityService;
    }
}