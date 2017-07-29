/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.billing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InvoiceJasperNotFoundException;
import org.meveo.admin.exception.InvoiceXmlNotFoundException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceCategoryDTO;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubCategoryDTO;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceAgregateService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.XMLInvoiceCreator;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.omnifaces.cdi.Param;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link Invoice} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 */
@Named
@ViewScoped
public class InvoiceBean extends CustomFieldBean<Invoice> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected
	 * 
	 * @{link Invoice} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private InvoiceService invoiceService;

	@Inject
	BillingAccountService billingAccountService;

	@Inject
	CustomerAccountService customerAccountService;

	@Inject
	RatedTransactionService ratedTransactionService;

	@Inject
	InvoiceAgregateService invoiceAgregateService;
	
	@Inject
	InvoiceTypeService invoiceTypeService;	

	@Inject
	XMLInvoiceCreator xmlInvoiceCreator;
	
	@Inject
	@Param
	private Long adjustedInvoiceIdParam;

	@Inject
	@Param
	private Boolean detailedParam;

	private Boolean detailedInvoiceAdjustment;

	private List<SubCategoryInvoiceAgregate> uiSubCategoryInvoiceAgregates;
	private List<RatedTransaction> uiRatedTransactions;
	
	private long billingAccountId;
	
	private boolean isSelectedInvoices=false;
    
	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public InvoiceBean() {
		super(Invoice.class);
	}

	@Override
	public Invoice initEntity() {
		return super.initEntity();
	}

	/**
	 * Method, that is invoked in billing account screen. This method returns
	 * invoices associated with current Billing Account.
	 * 
	 */
	public LazyDataModel<Invoice> getBillingAccountInvoices(BillingAccount ba) {
		if (ba.getCode() == null) {
			log.warn("No billingAccount code");
		} else {
			filters.put("billingAccount", ba);
//			try {
//				filters.put("invoiceType", invoiceTypeService.getDefaultCommertial());
//			} catch (BusinessException e) {				
//				log.error("Error on geting invoiceType",e);
//			}
			return getLazyDataModel();
		}

		return null;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<Invoice> getPersistenceService() {
		return invoiceService;
	}

	public List<InvoiceCategoryDTO> getInvoiceCategories() {
		entity = invoiceService.refreshOrRetrieve(entity);
		LinkedHashMap<String, InvoiceCategoryDTO> headerCategories = new LinkedHashMap<String, InvoiceCategoryDTO>();
		List<CategoryInvoiceAgregate> categoryInvoiceAgregates = new ArrayList<CategoryInvoiceAgregate>();
		for (InvoiceAgregate invoiceAgregate : entity.getInvoiceAgregates()) {
			if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
				CategoryInvoiceAgregate categoryInvoiceAgregate = (CategoryInvoiceAgregate) invoiceAgregate;
				categoryInvoiceAgregates.add(categoryInvoiceAgregate);
			}
		}
		Collections.sort(categoryInvoiceAgregates, new Comparator<CategoryInvoiceAgregate>() {
			public int compare(CategoryInvoiceAgregate c0, CategoryInvoiceAgregate c1) {
				if (c0.getInvoiceCategory() != null && c1.getInvoiceCategory() != null
						&& c0.getInvoiceCategory().getSortIndex() != null
						&& c1.getInvoiceCategory().getSortIndex() != null) {
					return c0.getInvoiceCategory().getSortIndex().compareTo(c1.getInvoiceCategory().getSortIndex());
				}
				return 0;
			}
		});

		for (CategoryInvoiceAgregate categoryInvoiceAgregate : categoryInvoiceAgregates) {
			InvoiceCategory invoiceCategory = categoryInvoiceAgregate.getInvoiceCategory();
			InvoiceCategoryDTO headerCat = null;
			if (headerCategories.containsKey(invoiceCategory.getCode())) {
				headerCat = headerCategories.get(invoiceCategory.getCode());
				headerCat.addAmountWithoutTax(categoryInvoiceAgregate.getAmountWithoutTax());
				headerCat.addAmountWithTax(categoryInvoiceAgregate.getAmountWithTax());
			} else {
				headerCat = new InvoiceCategoryDTO();
				headerCat.setDescription(invoiceCategory.getDescription());
				headerCat.setCode(invoiceCategory.getCode());
				headerCat.setAmountWithoutTax(categoryInvoiceAgregate.getAmountWithoutTax());
				headerCat.setAmountWithTax(categoryInvoiceAgregate.getAmountWithTax());
				headerCategories.put(invoiceCategory.getCode(), headerCat);
			}

			Set<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = categoryInvoiceAgregate
					.getSubCategoryInvoiceAgregates();
			LinkedHashMap<String, InvoiceSubCategoryDTO> headerSubCategories = headerCat.getInvoiceSubCategoryDTOMap();
			for (SubCategoryInvoiceAgregate subCatInvoiceAgregate : subCategoryInvoiceAgregates) {
				InvoiceSubCategory invoiceSubCategory = subCatInvoiceAgregate.getInvoiceSubCategory();
				InvoiceSubCategoryDTO headerSubCat = null;
				if (headerSubCategories.containsKey(invoiceSubCategory.getCode())) {
					headerSubCat = headerSubCategories.get(invoiceSubCategory.getCode());
					headerSubCat.addAmountWithoutTax(subCatInvoiceAgregate.getAmountWithoutTax());
					headerSubCat.addAmountWithTax(subCatInvoiceAgregate.getAmountWithTax());
				} else {
					headerSubCat = new InvoiceSubCategoryDTO();
					headerSubCat.setDescription(invoiceSubCategory.getDescription());
					headerSubCat.setCode(invoiceSubCategory.getCode());
					headerSubCat.setAmountWithoutTax(subCatInvoiceAgregate.getAmountWithoutTax());
					headerSubCat.setAmountWithTax(subCatInvoiceAgregate.getAmountWithTax());
					headerSubCat.setRatedTransactions(ratedTransactionService.getListByInvoiceAndSubCategory(entity,
							invoiceSubCategory));
					headerSubCategories.put(invoiceSubCategory.getCode(), headerSubCat);
				}
			}
		}
		return new ArrayList<InvoiceCategoryDTO>(headerCategories.values());
	}

	public void deleteInvoicePdf() {
		try {
		    entity = invoiceService.refreshOrRetrieve(entity);
			entity = invoiceService.deleteInvoicePdf(entity);
			messages.info(new BundleKey("messages", "invoice.pdfDelete.successful"));
		} catch (Exception e) {
			log.error("failed to delete PDF ", e);
            messages.error(new BundleKey("messages", "invoice.pdfDelete.failed"));
		}
	}

	public void generatePdf() {
		try {

            entity = invoiceService.refreshOrRetrieve(entity);
            entity = invoiceService.produceInvoicePdf(entity);			
			messages.info(new BundleKey("messages", "invoice.pdfGeneration"));
			
		} catch (InvoiceXmlNotFoundException e) {
			messages.error(new BundleKey("messages", "invoice.xmlNotFound"));
		} catch (InvoiceJasperNotFoundException e) {
			messages.error(new BundleKey("messages", "invoice.jasperNotFound"));
		} catch (Exception e) {
			log.error("failed to generate PDF ", e);
		}
	}
	
	public List<SubCategoryInvoiceAgregate> getDiscountAggregates() {
		return invoiceAgregateService.findDiscountAggregates(entity);
	}

	public void generateXMLInvoice() throws BusinessException {
		try {
		    entity=invoiceService.refreshOrRetrieve(entity);          
            invoiceService.produceInvoiceXml(entity);   
            messages.info(new BundleKey("messages", "invoice.xmlGeneration"));
            
		} catch (Exception e) {
			log.error("failed to generate xml invoice", e);
		}

	}

	public String downloadXMLInvoice() {
		String fileName = invoiceService.getFullXmlFilePath(entity, false);

		return downloadXMLInvoice(fileName);
	}

	public String downloadXMLInvoiceAdjustment() {
		String fileName = invoiceService.getFullAdjustmentXmlFilePath(entity);

		return downloadXMLInvoice(fileName);
	}

	public String downloadXMLInvoice(String fileName) {
		log.info("start to download...");
	
		File file = new File(fileName);
		
		OutputStream out = null;
		InputStream fin = null;
		try {
			javax.faces.context.FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
			HttpServletResponse res = (HttpServletResponse) context.getExternalContext().getResponse();
			res.setContentType("application/force-download");
			res.setContentLength((int) file.length());
			res.addHeader("Content-disposition", "attachment;filename=\"" + file.getName() + "\"");

			 out = res.getOutputStream();
			 fin = new FileInputStream(file);

			byte[] buf = new byte[1024];
			int sig = 0;
			while ((sig = fin.read(buf, 0, 1024)) != -1) {
				out.write(buf, 0, sig);
			}
			fin.close();
			out.flush();
			out.close();
			context.responseComplete();
			log.info("download over!");
		} catch (Exception e) {
			log.error("Error:#0, when dowload file: #1", e.getMessage(), file.getAbsolutePath());
		}finally{
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					log.error("Error",e);
				}
			}
			if(fin != null){
				try {
					fin.close();
				} catch (IOException e) {
					log.error("Error",e);
				}
			}
		}
		log.info("downloaded successfully!");
		return null;
	}

    @ActionMethod
    public void deleteXmlInvoice() {

        boolean fileDeleted = invoiceService.deleteInvoiceXml(getEntity());
        if (fileDeleted) {
            messages.info(new BundleKey("messages", "invoice.xmlDelete.successful"));
        } else {
            messages.info(new BundleKey("messages", "invoice.xmlDelete.failed"));
        }
    }

	public boolean isXmlInvoiceAlreadyGenerated() {
		return invoiceService.isInvoiceXmlExist(entity);
	}

	public boolean isXmlInvoiceAdjustmentAlreadyGenerated() {
	    return invoiceService.isInvoiceAdjustmentXmlAlreadyGenerated(entity);
	}

	public boolean isPdfInvoiceAlreadyGenerated() {
        return invoiceService.isInvoicePdfExist(entity);
    }
	
	public void excludeBillingAccounts(BillingRun billingrun) {
		try {
			log.debug("excludeBillingAccounts getSelectedEntities=" + getSelectedEntities().size());
			if (getSelectedEntities() != null && getSelectedEntities().size() > 0) {
				for (Invoice invoice : getSelectedEntities()) {
					invoiceService.deleteInvoice(invoice);
					billingrun.getInvoices().remove(invoice);
				}
				messages.info(new BundleKey("messages", "info.invoicing.billingAccountExcluded"));
			} else {
				messages.error(new BundleKey("messages", "postInvoicingReport.noBillingAccountSelected"));
			}

		} catch (Exception e) {
			log.error("Failed to exclude BillingAccounts!", e);
			messages.error(new BundleKey("messages", "error.execution"));
		}
	}

	public BigDecimal totalInvoiceAdjustmentAmountWithoutTax() {
		BigDecimal total = new BigDecimal(0);
		if (entity != null && uiSubCategoryInvoiceAgregates != null) {
			for (SubCategoryInvoiceAgregate subCategoryInvoiceAgregate : uiSubCategoryInvoiceAgregates) {
			if(subCategoryInvoiceAgregate.getAmountWithoutTax()!=null){
				total = total.add(subCategoryInvoiceAgregate.getAmountWithoutTax());
				}
			}
		}

		return total;
	}

	public BigDecimal totalInvoiceAdjustmentAmountTax() {
		BigDecimal total = new BigDecimal(0);
		if (entity != null && uiSubCategoryInvoiceAgregates != null) {
			for (SubCategoryInvoiceAgregate subCategoryInvoiceAgregate : uiSubCategoryInvoiceAgregates) {
			if(subCategoryInvoiceAgregate.getAmountTax()!=null){
				total = total.add(subCategoryInvoiceAgregate.getAmountTax());
				}
			}
		}

		return total;
	}

	public BigDecimal totalInvoiceAdjustmentAmountWithTax() {
		BigDecimal total = new BigDecimal(0);
		if (entity != null && uiSubCategoryInvoiceAgregates != null) {
			for (SubCategoryInvoiceAgregate subCategoryInvoiceAgregate : uiSubCategoryInvoiceAgregates) {
		    if(subCategoryInvoiceAgregate.getAmountWithTax()!=null){	
				total = total.add(subCategoryInvoiceAgregate.getAmountWithTax());
			}
			}
		}

		return total;
	}

	public BigDecimal totalOldInvoiceAdjustmentAmountWithoutTax() {
		BigDecimal total = new BigDecimal(0);
		if (entity != null && uiSubCategoryInvoiceAgregates != null) {
			for (SubCategoryInvoiceAgregate subCategoryInvoiceAgregate : uiSubCategoryInvoiceAgregates) {
				if(subCategoryInvoiceAgregate.getOldAmountWithoutTax()!=null){
				total = total.add(subCategoryInvoiceAgregate.getOldAmountWithoutTax());
				}
			}
		}

		return total;
	}

	public BigDecimal totalOldInvoiceAdjustmentAmountWithTax() {
		BigDecimal total = new BigDecimal(0);
		if (entity != null && uiSubCategoryInvoiceAgregates != null) {
			for (SubCategoryInvoiceAgregate subCategoryInvoiceAgregate : uiSubCategoryInvoiceAgregates) {
				if(subCategoryInvoiceAgregate.getOldAmountWithTax()!=null){
				total = total.add(subCategoryInvoiceAgregate.getOldAmountWithTax());
				}
			}
		}

		return total;
	}

	/**
	 * Detail invoice adjustments.
	 */

	public BigDecimal totalInvoiceAdjustmentDetailUnitAmountWithoutTax() {
		BigDecimal total = new BigDecimal(0);
		if (entity != null && uiRatedTransactions != null) {
			for (RatedTransaction ratedTransaction : uiRatedTransactions) {
			if(ratedTransaction.getUnitAmountWithoutTax()!=null){
				total = total.add(ratedTransaction.getUnitAmountWithoutTax());
			}
			}
		}

		return total;
	}

	public BigDecimal totalInvoiceAdjustmentDetailUnitAmountWithTax() {
		BigDecimal total = new BigDecimal(0);
		if (entity != null && uiRatedTransactions != null) {
			for (RatedTransaction ratedTransaction : uiRatedTransactions) {
			if(ratedTransaction.getUnitAmountWithTax()!=null){	
				total = total.add(ratedTransaction.getUnitAmountWithTax());
			}	
			}
		}
		return total;
	}

	public BigDecimal totalInvoiceAdjustmentDetailQuantity() {
		BigDecimal total = new BigDecimal(0);
		if (entity != null && uiRatedTransactions != null) {
			for (RatedTransaction ratedTransaction : uiRatedTransactions) {
			   if(ratedTransaction.getQuantity()!=null){
				total = total.add(ratedTransaction.getQuantity());
				}
			}
		}

		return total;
	}

	public BigDecimal totalInvoiceAdjustmentDetailAmountWithoutTax() {
		BigDecimal total = new BigDecimal(0);
		if (entity != null && uiRatedTransactions != null) {
			for (RatedTransaction ratedTransaction : uiRatedTransactions) {
				if(ratedTransaction.getAmountWithoutTax()!=null){
				total = total.add(ratedTransaction.getAmountWithoutTax());
				}
			}
		}

		return total;
	}

	public BigDecimal totalInvoiceAdjustmentDetailAmountWithTax() {
		BigDecimal total = new BigDecimal(0);
		if (entity != null && uiRatedTransactions != null) {
			for (RatedTransaction ratedTransaction : uiRatedTransactions) {
				if(ratedTransaction.getAmountWithTax()!=null){
				total = total.add(ratedTransaction.getAmountWithTax());
				}
			}
		}

		return total;
	}

	public void reComputeInvoiceAdjustment(SubCategoryInvoiceAgregate subCategoryInvoiceAgregate)
			throws BusinessException {
		// invoiceService.recomputeSubCategoryAggregate(entity);
		invoiceService.recomputeAggregates(entity);
	}

	public void reComputeDetailedInvoiceAdjustment(RatedTransaction ratedTx) {
		ratedTx.recompute(appProvider.isEntreprise());
	}

	public void testListener() {
		log.debug("testListener");
	}

	public Long getAdjustedInvoiceIdParam() {
		return adjustedInvoiceIdParam;
	}

	public void setAdjustedInvoiceIdParam(Long adjustedInvoiceIdParam) {
		this.adjustedInvoiceIdParam = adjustedInvoiceIdParam;
	}

	public Long getAdjustedInvoiceId() {
		if (getEntity() != null && getEntity().getAdjustedInvoice() != null) {
			return getEntity().getAdjustedInvoice().getId();
		}

		return adjustedInvoiceIdParam;
	}

	public String saveOrUpdateInvoiceAdjustment() throws Exception {
		if (entity.isTransient()) {			
			if (isDetailed()) {
				for (RatedTransaction rt : uiRatedTransactions) {
					ratedTransactionService.create(rt);
				}	
			}
			super.saveOrUpdate(false);
			if(billingAccountId!=0){
				BillingAccount billingAccount = billingAccountService.findById(billingAccountId);
				entity.setBillingAccount(billingAccount);
				String invoiceNumber=invoiceService.generateInvoiceNumber(entity);
				entity.setInvoiceNumber(invoiceNumber);
			} 	 
		}	
		if (isDetailed()) {
			ratedTransactionService.createInvoiceAndAgregates(entity.getBillingAccount(), entity,null, null, new Date());
		} else {
			if (entity.getAmountWithoutTax() == null) {
				invoiceService.recomputeAggregates(entity);
			}
			entity = invoiceService.update(entity);
		} 
		entity = invoiceService.refreshOrRetrieve(entity);
		entity.getAdjustedInvoice().getLinkedInvoices().add(entity);
		invoiceService.update(entity.getAdjustedInvoice());

		invoiceService.commit();

		// create xml and pdf for invoice adjustment
		entity = invoiceService.generateXmlAndPdfInvoice(entity);

		return "/pages/billing/invoices/invoiceDetail.jsf?objectId=" + entity.getAdjustedInvoice().getId() + "&cid="
				+ conversation.getId() + "&faces-redirect=true&includeViewParams=true";
	}
	
	public void onRowSelectCheckbox(SelectEvent event) {
        isSelectedInvoices=true;
    }
    public void onRowUnSelectCheckbox(UnselectEvent event) {
    	isSelectedInvoices=false;
    }

	public List<SubCategoryInvoiceAgregate> getUiSubCategoryInvoiceAgregates() {
		return uiSubCategoryInvoiceAgregates;
	}

	public void setUiSubCategoryInvoiceAgregates(List<SubCategoryInvoiceAgregate> uiSubCategoryInvoiceAgregates) {
		this.uiSubCategoryInvoiceAgregates = uiSubCategoryInvoiceAgregates;
	}

	public boolean isDetailed() {
		if (detailedInvoiceAdjustment == null && detailedParam != null) {
			detailedInvoiceAdjustment = detailedParam;
		}

		return detailedInvoiceAdjustment;
	}

	public Boolean getDetailedParam() {
		return detailedParam;
	}

	public void setDetailedParam(Boolean detailedParam) {
		this.detailedParam = detailedParam;
	}

	public List<RatedTransaction> getUiRatedTransactions() {
		return uiRatedTransactions;
	}

	public void setUiRatedTransactions(List<RatedTransaction> uiRatedTransactions) {
		this.uiRatedTransactions = uiRatedTransactions;
	}

	public Boolean getDetailedInvoiceAdjustment() {
		return detailedInvoiceAdjustment;
	}

	public void setDetailedInvoiceAdjustment(Boolean detailedInvoiceAdjustment) {
		this.detailedInvoiceAdjustment = detailedInvoiceAdjustment;
	}

	public long getBillingAccountId() {
		return billingAccountId;
	}
	
	public Set<Invoice> getLinkedInvoices(Invoice invoice){
		return invoiceService.refreshOrRetrieve(invoice).getLinkedInvoices();
	}

	public boolean isSelectedInvoices() {
		return isSelectedInvoices;
	}

	public void setSelectedInvoices(boolean isSelectedInvoices) {
		this.isSelectedInvoices = isSelectedInvoices;
	}
	
	

}
