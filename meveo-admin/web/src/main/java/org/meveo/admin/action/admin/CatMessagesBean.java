/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.admin;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.BusinessEntity;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.Tax;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.shared.Title;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.omnifaces.cdi.ViewScoped;

/**
 * Standard backing bean for {@link CatMessages} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 */
@Named
@ViewScoped
public class CatMessagesBean extends BaseBean<CatMessages> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link CatMessages} service. Extends
	 * {@link PersistenceService} .
	 */
	@Inject
	private CatMessagesService catMessagesService;


	@Inject
	private TitleService titleService;
	@Inject
	private TaxService taxService;
	@Inject
	private InvoiceCategoryService invoiceCategoryService;
	@Inject
	private InvoiceSubCategoryService invoiceSubCategoryService;
	@Inject 
	private UsageChargeTemplateService usageChargeTemplateService;
	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;
	@Inject
	private RecurringChargeTemplateService recurringChargeTemplateService;
	@Inject
	private PricePlanMatrixService pricePlanMatrixService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public CatMessagesBean() {
		super(CatMessages.class);
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<CatMessages> getPersistenceService() {
		return catMessagesService;
	}

	@Override
	protected String getListViewName() {
		return "catMessagess";
	}
	
	protected BusinessEntity getObject(CatMessages catMessages){
		if(catMessages==null){
			return null;
		}
		String messagesCode=catMessages.getMessageCode();
		String[] codes=messagesCode.split("_");
		
		if(codes!=null&&codes.length==2){
			Long id=null;
			try{
				id=Long.valueOf(codes[1]);
			}catch(Exception e){
				return null;
			}
			if("Title".equals(codes[0])){
				return titleService.findById(id);
			}else if("Tax".equals(codes[0])){
				return taxService.findById(id);
			}else if("InvoiceCategory".equals(codes[0])){
				return invoiceCategoryService.findById(id);
			}else if("InvoiceSubCategory".equals(codes[0])){
				return invoiceSubCategoryService.findById(id);
			}else if("UsageChargeTemplate".equals(codes[0])){
				return usageChargeTemplateService.findById(id);
			}else if("OneShotChargeTemplate".equals(codes[0])){
				return oneShotChargeTemplateService.findById(id);
			}else if("RecurringChargeTemplate".equals(codes[0])){
				return recurringChargeTemplateService.findById(id);
			}else if("PricePlanMatrix".equals(codes[0])){
				return pricePlanMatrixService.findById(id);
			}
		}
		
		return null;
	}
	
	protected Map<String,String> getObjectTypes(){
		Map<String,String> result=new HashMap<String,String>();
		result.put("Title_*","Titles and civilities");
		result.put("Tax_*","Taxes");
		result.put("InvoiceCategory_*","Invoice subcategories");
		result.put("InvoiceSubCategory_*"
			,"Invoice subcategories");
		result.put("*ChargeTemplate_*"
			,"Charges");
		result.put("PricePlanMatrix_*","Price plans");
		return result;
	}

	@Override
	protected boolean canDelete(CatMessages entity) {
		// TODO Auto-generated method stub
		return true;
	}
	
}
