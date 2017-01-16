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

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.DiscriminatorValue;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.international.status.builder.BundleKey;
import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InvoiceJasperNotFoundException;
import org.meveo.admin.exception.InvoiceXmlNotFoundException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.Auditable;
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
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceAgregateService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.XMLInvoiceCreator;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.omnifaces.cdi.ViewScoped;
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

	private ParamBean paramBean = ParamBean.getInstance();

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
	@RequestParam()
	private Instance<Long> adjustedInvoiceIdParam;

	@Inject
	@RequestParam()
	private Instance<Boolean> detailedParam;

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

	@SuppressWarnings({ "unchecked" })
	@Override
	public Invoice initEntity() {
		Invoice invoice = super.initEntity();

		if (adjustedInvoiceIdParam != null && adjustedInvoiceIdParam.get() != null) {
			if (detailedParam != null && invoice.isTransient()) {
				if (isDetailed()) {
					invoice.setDetailedInvoice(true);
				} else {
					invoice.setDetailedInvoice(false);
				}
			}
		}

		if (invoice.isTransient() && adjustedInvoiceIdParam != null && adjustedInvoiceIdParam.get() != null) {
			if (invoice.getAdjustedInvoice() == null) {
				Invoice adjustedInvoice = invoiceService.findById(adjustedInvoiceIdParam.get());
				invoice.setAdjustedInvoice(adjustedInvoice);
				invoice.setBillingRun(adjustedInvoice.getBillingRun());
				invoice.setDueDate(new Date());
				invoice.setInvoiceDate(new Date());				
				invoice.setPaymentMethod(adjustedInvoice.getPaymentMethod());				
				try {
					invoice.setInvoiceType(invoiceTypeService.getDefaultAdjustement(getCurrentUser()));
				} catch (BusinessException e) {					
					log.error("cant get InvoiceType ",e);
				}
				if(adjustedInvoice.getBillingAccount()!=null){
					billingAccountId=adjustedInvoice.getBillingAccount().getId();
				}

				// duplicate rated transaction for detailed
				// invoice adjustment
				if (isDetailed()) {
					uiRatedTransactions = new ArrayList<>();

					for (RatedTransaction ratedTransaction : ratedTransactionService.listByInvoice(adjustedInvoice)) {
						RatedTransaction newRatedTransaction = new RatedTransaction(ratedTransaction);

						newRatedTransaction.setInvoiceSubCategory(ratedTransaction.getInvoiceSubCategory());
						newRatedTransaction.setInvoice(invoice);
						newRatedTransaction.setAdjustedRatedTx(ratedTransaction);

						uiRatedTransactions.add(newRatedTransaction);
					}
				} else {
					Auditable auditable = new Auditable();
					auditable.setCreator(getCurrentUser());
					auditable.setCreated(new Date());
					uiSubCategoryInvoiceAgregates = new ArrayList<SubCategoryInvoiceAgregate>();
					for (InvoiceAgregate invoiceAgregate : adjustedInvoice.getInvoiceAgregates()) {
						if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
							CategoryInvoiceAgregate categoryInvoiceAgregate = (CategoryInvoiceAgregate) invoiceAgregate;
							CategoryInvoiceAgregate newCategoryInvoiceAgregate = new CategoryInvoiceAgregate(
									categoryInvoiceAgregate);

							newCategoryInvoiceAgregate.setSubCategoryInvoiceAgregates(null);
							newCategoryInvoiceAgregate.setInvoice(invoice);
							newCategoryInvoiceAgregate.setAuditable(auditable);

							if (categoryInvoiceAgregate.getSubCategoryInvoiceAgregates() != null) {
								for (SubCategoryInvoiceAgregate subCategoryInvoiceAgregate : categoryInvoiceAgregate
										.getSubCategoryInvoiceAgregates()) {
									SubCategoryInvoiceAgregate newSubCategoryInvoiceAgregate = new SubCategoryInvoiceAgregate(
											subCategoryInvoiceAgregate);

									newSubCategoryInvoiceAgregate.setInvoice(invoice);
									newSubCategoryInvoiceAgregate.setAuditable(auditable);
									newSubCategoryInvoiceAgregate.setOldAmountWithoutTax(subCategoryInvoiceAgregate
											.getAmountWithoutTax());
									newSubCategoryInvoiceAgregate.setOldAmountWithTax(subCategoryInvoiceAgregate
											.getAmountWithTax());
									newSubCategoryInvoiceAgregate.setAmountWithTax(subCategoryInvoiceAgregate
											.getAmountWithTax());
									newSubCategoryInvoiceAgregate.setAmountTax(subCategoryInvoiceAgregate
											.getAmountTax());
									newSubCategoryInvoiceAgregate
											.setCategoryInvoiceAgregate(newCategoryInvoiceAgregate);

									newCategoryInvoiceAgregate
											.addSubCategoryInvoiceAggregate(newSubCategoryInvoiceAgregate);

									if (subCategoryInvoiceAgregate.getSubCategoryTaxes() != null) {
										for (Tax tax : subCategoryInvoiceAgregate.getSubCategoryTaxes()) {
											newSubCategoryInvoiceAgregate.addSubCategoryTax(tax);
										}
									}

									uiSubCategoryInvoiceAgregates.add(newSubCategoryInvoiceAgregate);
								}
							}
						} else if (invoiceAgregate instanceof TaxInvoiceAgregate) {
							TaxInvoiceAgregate taxInvoiceAgregate = (TaxInvoiceAgregate) invoiceAgregate;
							TaxInvoiceAgregate newTaxInvoiceAgregate = new TaxInvoiceAgregate(taxInvoiceAgregate);

							newTaxInvoiceAgregate.setInvoice(invoice);
							newTaxInvoiceAgregate.setAuditable(auditable);
							newTaxInvoiceAgregate.setTax(taxInvoiceAgregate.getTax());
						}
					}
				}
			}
		} else if (adjustedInvoiceIdParam != null && adjustedInvoiceIdParam.get() != null) {
			if (!isDetailed()) {
				// load subCategoryInvoiceAggregates
				uiSubCategoryInvoiceAgregates = (List<SubCategoryInvoiceAgregate>) invoiceAgregateService
						.listByInvoiceAndType(invoice,
								SubCategoryInvoiceAgregate.class.getAnnotation(DiscriminatorValue.class).value());
			} else {
				// detailed
				uiRatedTransactions = ratedTransactionService.listByInvoice(entity);
			}
		} else {
			getPersistenceService().refresh(invoice);
		}

		return invoice;
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
			try {
				filters.put("invoiceType", invoiceTypeService.getDefaultCommertial(ba.getAuditable().getCreator()));
			} catch (BusinessException e) {				
				log.error("Error on geting invoiceType",e);
			}
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
			entity.setPdf(null);
			invoiceService.update(entity, getCurrentUser());
			messages.info(new BundleKey("messages", "delete.successful"));
		} catch (Exception e) {
			log.error("failed to generate PDF ", e);
		}
	}

	public void generatePdf() {
		try {
			invoiceService.producePdf(entity.getId(), getCurrentUser());
			entity=invoiceService.refreshOrRetrieve(entity);
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

	public File getXmlInvoiceDir() {	
		return new File(invoiceService.getBillingRunPath(getEntity().getBillingRun(), getEntity().getInvoiceDate(), getCurrentProvider().getCode()));
	}

	public void generateXMLInvoice() throws BusinessException {
		try {
			xmlInvoiceCreator.createXMLInvoice(entity.getId(),getCurrentUser());
			messages.info(new BundleKey("messages", "invoice.xmlGeneration"));
		} catch (Exception e) {
			log.error("failed to generate xml invoice", e);
		}

	}

	public String downloadXMLInvoice() {
		String thePrefix =""; 
		if(getEntity().getInvoiceType().getCode().equals(invoiceTypeService.getAdjustementCode())){
			thePrefix =paramBean.getProperty("invoicing.invoiceAdjustment.prefix", "_IA_"); 
		}
		String fileName = thePrefix+(entity.getInvoiceNumber() != null ? entity.getInvoiceNumber() : entity
				.getTemporaryInvoiceNumber()) + ".xml";

		return downloadXMLInvoice(fileName);
	}

	public String downloadXMLInvoiceAdjustment() {
		String fileName = paramBean.getProperty("invoicing.invoiceAdjustment.prefix", "_IA_")
				+ entity.getInvoiceNumber() + ".xml";

		return downloadXMLInvoice(fileName);
	}

	public String downloadXMLInvoice(String fileName) {
		log.info("start to download...");

		File file = new File(getXmlInvoiceDir().getAbsolutePath() + File.separator + fileName);
		OutputStream out = null;
		InputStream fin = null;
		try {
			javax.faces.context.FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
			HttpServletResponse res = (HttpServletResponse) context.getExternalContext().getResponse();
			res.setContentType("application/force-download");
			res.setContentLength((int) file.length());
			res.addHeader("Content-disposition", "attachment;filename=\"" + fileName + "\"");

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

        boolean fileDeleted = xmlInvoiceCreator.deleteXmlInvoice(getEntity());
        if (fileDeleted) {
            messages.info(new BundleKey("messages", "delete.successful"));
        } else {
            messages.info(new BundleKey("messages", "error.delete.unexpected"));
        }
    }

	public boolean isXmlInvoiceAlreadyGenerated() {
		String thePrefix =""; 
		if(getEntity().getInvoiceType().getCode().equals(invoiceTypeService.getAdjustementCode())){
			thePrefix =paramBean.getProperty("invoicing.invoiceAdjustment.prefix", "_IA_"); 
		} 
		String fileDir = getXmlInvoiceDir().getAbsolutePath()
				+ File.separator+thePrefix
				+ (getEntity().getInvoiceNumber() != null ? getEntity().getInvoiceNumber() : getEntity()
						.getTemporaryInvoiceNumber()) + ".xml";
		File file = new File(fileDir);
		return file.exists();
	}

	public boolean isXmlInvoiceAdjustmentAlreadyGenerated() {
		String fileDir = getXmlInvoiceDir().getAbsolutePath()
				+ File.separator
				+ paramBean.getProperty("invoicing.invoiceAdjustment.prefix", "_IA_")
				+ (getEntity().getInvoiceNumber() != null ? getEntity().getInvoiceNumber() : getEntity()
						.getTemporaryInvoiceNumber()) + ".xml";
		File file = new File(fileDir);
		return file.exists();
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
		// invoiceService.recomputeSubCategoryAggregate(entity, currentUser);
		invoiceService.recomputeAggregates(entity, currentUser);
	}

	public void reComputeDetailedInvoiceAdjustment(RatedTransaction ratedTx) {
		ratedTx.recompute(entity.getProvider().isEntreprise());
	}

	public void testListener() {
		log.debug("testListener");
	}

	public Instance<Long> getAdjustedInvoiceIdParam() {
		return adjustedInvoiceIdParam;
	}

	public void setAdjustedInvoiceIdParam(Instance<Long> adjustedInvoiceIdParam) {
		this.adjustedInvoiceIdParam = adjustedInvoiceIdParam;
	}

	public Long getAdjustedInvoiceId() {
		if (getEntity() != null && getEntity().getAdjustedInvoice() != null) {
			return getEntity().getAdjustedInvoice().getId();
		}

		return adjustedInvoiceIdParam.get();
	}

	public String saveOrUpdateInvoiceAdjustment() throws Exception {
		if (entity.isTransient()) {			
			if (isDetailed()) {
				for (RatedTransaction rt : uiRatedTransactions) {
					ratedTransactionService.create(rt, getCurrentUser());
				}	
			}
			super.saveOrUpdate(false);
			if(billingAccountId!=0){
				BillingAccount billingAccount = billingAccountService.findById(billingAccountId);
				entity.setBillingAccount(billingAccount);
				String invoiceNumber=invoiceService.getInvoiceNumber(entity, getCurrentUser());
				entity.setInvoiceNumber(invoiceNumber);
			} 	 
		}	
		if (isDetailed()) {
			ratedTransactionService.createInvoiceAndAgregates(entity.getBillingAccount(), entity,null, null, new Date(),getCurrentUser());
		} else {
			if (entity.getAmountWithoutTax() == null) {
				invoiceService.recomputeAggregates(entity, getCurrentUser());
			}
			entity = invoiceService.update(entity, getCurrentUser());
		} 
		entity = invoiceService.refreshOrRetrieve(entity);
		entity.getAdjustedInvoice().getLinkedInvoices().add(entity);
		invoiceService.update(entity.getAdjustedInvoice(), getCurrentUser());

		invoiceService.commit();
		// create xml invoice adjustment
		xmlInvoiceCreator.createXMLInvoice(entity.getId(),getCurrentUser());
		
		// create pdf
		invoiceService.producePdf(entity.getId(), currentUser);

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
		if (detailedInvoiceAdjustment == null && detailedParam != null && detailedParam.get() != null) {
			detailedInvoiceAdjustment = detailedParam.get();
		}

		return detailedInvoiceAdjustment;
	}

	public Instance<Boolean> getDetailedParam() {
		return detailedParam;
	}

	public void setDetailedParam(Instance<Boolean> detailedParam) {
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
