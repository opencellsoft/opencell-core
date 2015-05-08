package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.DiscountPlanItemService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.job.TimerEntityService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.context.RequestContext;

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

	@Override
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		// check for required fields
		if (getEntity().getInvoiceCategory() == null && getEntity().getInvoiceSubCategory() == null) {
			messages.error(new BundleKey("messages", "message.discountPlanItem.error.requiredFields"));
			return null;
		}

		return super.saveOrUpdate(killConversation);
	}
	
	  public  List<InvoiceSubCategory> getInvoiceSubCategories(InvoiceCategory invoiceCategory){  
	     if(invoiceCategory!=null){
		return invoiceSubCategoryService.findByInvoiceCategory(invoiceCategory, getCurrentUser().getProvider()); 
			}else{
	     return null; 
			}  
		 }

	  @Override
		protected void canDelete() {
			boolean result=true;
			this.delete();
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.addCallbackParam("result", result);
		}}

