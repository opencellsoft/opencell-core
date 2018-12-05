package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.CustomFieldBean;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.DiscountPlanItemService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 **/
@Named
@ViewScoped
public class DiscountPlanItemBean extends CustomFieldBean<DiscountPlanItem> {

	private static final long serialVersionUID = -2345373648137067066L;

	@Inject
	private DiscountPlanItemService discountPlanItemService;

	@Inject
	private InvoiceSubCategoryService invoiceSubCategoryService;

	public DiscountPlanItemBean() {
		super(DiscountPlanItem.class);
	}

	@Override
	public DiscountPlanItem initEntity() {
		DiscountPlanItem dpi = new DiscountPlanItem();
		dpi.setAccountingCode(appProvider.getDiscountAccountingCode());
		return dpi;
	}

	@Override
	protected IPersistenceService<DiscountPlanItem> getPersistenceService() {
		return discountPlanItemService;
	}

	public List<InvoiceSubCategory> getInvoiceSubCategories(InvoiceCategory invoiceCategory) {
		if (invoiceCategory != null) {
			return invoiceSubCategoryService.findByInvoiceCategory(invoiceCategory);
		} else {
			return new ArrayList<>();
		}
	}
}
