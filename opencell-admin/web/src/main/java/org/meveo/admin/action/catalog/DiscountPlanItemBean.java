package org.meveo.admin.action.catalog;

import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.DiscountPlanItemService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class DiscountPlanItemBean extends BaseBean<DiscountPlanItem> {

    private static final long serialVersionUID = -2345373648137067066L;

    @Inject
    private DiscountPlanItemService discountPlanItemService;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    public DiscountPlanItemBean() {
        super(DiscountPlanItem.class);
    }

    @Override
    protected IPersistenceService<DiscountPlanItem> getPersistenceService() {
        return discountPlanItemService;
    }

    public List<InvoiceSubCategory> getInvoiceSubCategories(InvoiceCategory invoiceCategory) {
        if (invoiceCategory != null) {
            return invoiceSubCategoryService.findByInvoiceCategory(invoiceCategory);
        } else {
            return null;
        }
    }
}
