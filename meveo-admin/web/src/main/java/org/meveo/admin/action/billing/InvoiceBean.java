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
package org.meveo.admin.action.billing;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InvoiceJasperNotFoundException;
import org.meveo.admin.exception.InvoiceXmlNotFoundException;
import org.meveo.admin.job.PDFParametersConstruction;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.billing.BillingAccount;
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
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.XMLInvoiceCreator;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link Invoice} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 */
@Named
@ViewScoped
public class InvoiceBean extends BaseBean<Invoice> {

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
	XMLInvoiceCreator xmlInvoiceCreator;
	

	
	private List<RatedTransaction> ratedTransactions=new ArrayList<RatedTransaction>();
	
	@Inject
	private PDFParametersConstruction pDFParametersConstruction;
 
	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public InvoiceBean() {
		super(Invoice.class);
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
		Collections.sort(categoryInvoiceAgregates,
				new Comparator<CategoryInvoiceAgregate>() {
					public int compare(CategoryInvoiceAgregate c0,
							CategoryInvoiceAgregate c1) {
						if (c0.getInvoiceCategory() != null
								&& c1.getInvoiceCategory() != null
								&& c0.getInvoiceCategory().getSortIndex() != null
								&& c1.getInvoiceCategory().getSortIndex() != null) {
							return c0
									.getInvoiceCategory()
									.getSortIndex()
									.compareTo(
											c1.getInvoiceCategory()
													.getSortIndex());
						}
						return 0;
					}
				});

		for (CategoryInvoiceAgregate categoryInvoiceAgregate : categoryInvoiceAgregates) {
			InvoiceCategory invoiceCategory = categoryInvoiceAgregate
					.getInvoiceCategory();
			InvoiceCategoryDTO headerCat = null;
			if (headerCategories.containsKey(invoiceCategory.getCode())) {
				headerCat = headerCategories.get(invoiceCategory.getCode());
				headerCat.addAmountWithoutTax(categoryInvoiceAgregate
						.getAmountWithoutTax());
				headerCat.addAmountWithTax(categoryInvoiceAgregate
						.getAmountWithTax());
			} else {
				headerCat = new InvoiceCategoryDTO();
				headerCat.setDescription(invoiceCategory.getDescription());
				headerCat.setCode(invoiceCategory.getCode());
				headerCat.setAmountWithoutTax(categoryInvoiceAgregate
						.getAmountWithoutTax());
				headerCat.setAmountWithTax(categoryInvoiceAgregate
						.getAmountWithTax());
				headerCategories.put(invoiceCategory.getCode(), headerCat);
			}
			Set<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = categoryInvoiceAgregate
					.getSubCategoryInvoiceAgregates();
			LinkedHashMap<String, InvoiceSubCategoryDTO> headerSubCategories = headerCat
					.getInvoiceSubCategoryDTOMap();
			for (SubCategoryInvoiceAgregate subCatInvoiceAgregate : subCategoryInvoiceAgregates) {
				InvoiceSubCategory invoiceSubCategory = subCatInvoiceAgregate
						.getInvoiceSubCategory();
				InvoiceSubCategoryDTO headerSUbCat = null;
				if (headerSubCategories.containsKey(invoiceSubCategory
						.getCode())) {
					headerSUbCat = headerSubCategories.get(invoiceSubCategory
							.getCode());
					headerSUbCat.addAmountWithoutTax(subCatInvoiceAgregate
							.getAmountWithoutTax());
					headerSUbCat.addAmountWithTax(subCatInvoiceAgregate
							.getAmountWithTax());
				} else {
					headerSUbCat = new InvoiceSubCategoryDTO();
					headerSUbCat.setDescription(invoiceSubCategory
							.getDescription());
					headerSUbCat.setCode(invoiceSubCategory.getCode());
					headerSUbCat.setAmountWithoutTax(subCatInvoiceAgregate
							.getAmountWithoutTax());
					headerSUbCat.setAmountWithTax(subCatInvoiceAgregate
							.getAmountWithTax());
					headerSubCategories.put(invoiceSubCategory.getCode(),
							headerSUbCat);
				} 
				ratedTransactions=ratedTransactionService.getListByInvoiceAndSubCategory(entity, invoiceSubCategory);	
			}
		}
		return new ArrayList<InvoiceCategoryDTO>(headerCategories.values());
	}

	public String getNetToPay() throws BusinessException {
		BigDecimal balance = customerAccountService.customerAccountBalanceDue(
				null,
				entity.getBillingAccount().getCustomerAccount().getCode(),
				entity.getDueDate());

		if (balance == null) {
			throw new BusinessException("account balance calculation failed");
		}
		BigDecimal netToPay = BigDecimal.ZERO;
		if (entity.getProvider().isEntreprise()) {
			netToPay = entity.getAmountWithTax();
		} else {
			netToPay = entity.getAmountWithTax().add(balance);
		}
		return netToPay.setScale(2, RoundingMode.HALF_UP).toString();
	}
	
	public void deleteInvoicePdf(){
		try{
			entity.setPdf(null);
			invoiceService.update(entity);	
			messages.info(new BundleKey("messages", "delete.successful"));
		} catch (Exception e) {
			log.error("failed to generate PDF ",e);
		}}
	
	public void generatePdf(){
		try {
			Map<String, Object> parameters = pDFParametersConstruction
			   .constructParameters(entity);
			invoiceService.producePdf(parameters, getCurrentUser()); 
			messages.info(new BundleKey("messages", "invoice.pdfGeneration"));
		}catch(InvoiceXmlNotFoundException e){
			 messages.error(new BundleKey("messages", "invoice.xmlNotFound")); 
			}
		catch(InvoiceJasperNotFoundException e){
			messages.error(new BundleKey("messages", "invoice.jasperNotFound")); 
			}
		catch (Exception e) {
			log.error("failed to generate PDF ",e);  
		}	
	}
	
	public List<SubCategoryInvoiceAgregate> getDiscountAggregates() {
		return invoiceAgregateService.findDiscountAggregates(entity); 
	}

	public List<RatedTransaction> getRatedTransactions() {
		return ratedTransactions;
	}

	public void setRatedTransactions(List<RatedTransaction> ratedTransactions) {
		this.ratedTransactions = ratedTransactions;
	}

	public File getXmlInvoiceDir(){
		ParamBean param = ParamBean.getInstance();
		String invoicesDir = param.getProperty("providers.rootDir", "/tmp/meveo"); 
		File billingRundir = new File(invoicesDir + File.separator + getCurrentProvider().getCode() + File.separator + "invoices" + File.separator + "xml"
				 + File.separator + entity.getBillingRun().getId());
		return billingRundir;
	}
	
	public void generateXMLInvoice() throws BusinessException {
		 try{
			xmlInvoiceCreator.createXMLInvoice(entity.getId(), getXmlInvoiceDir());
			messages.info(new BundleKey("messages", "invoice.xmlGeneration")); 
		 }catch(Exception e){
				log.error("failed to generate xml invoice",e);
			}
		
	}
	public String downloadXMLInvoice() {
		log.info("start to download...");
		 String fileName=(entity.getInvoiceNumber() != null ? entity.getInvoiceNumber() : entity.getTemporaryInvoiceNumber())+".xml"; 
		File file = new File(getXmlInvoiceDir().getAbsolutePath()+File.separator+fileName);
		try {
			javax.faces.context.FacesContext context = javax.faces.context.FacesContext
					.getCurrentInstance();
			HttpServletResponse res = (HttpServletResponse) context
					.getExternalContext().getResponse();
			res.setContentType("application/force-download");
			res.setContentLength((int) file.length());
			res.addHeader("Content-disposition", "attachment;filename=\""
					+ fileName + "\"");

			OutputStream out = res.getOutputStream();
			InputStream fin = new FileInputStream(file);

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
			log.error("Error:#0, when dowload file: #1", e.getMessage(),
					file.getAbsolutePath());
		}
		log.info("downloaded successfully!");
		return null;
	}
	
	public void deleteXmlInvoice(){
		try{
		File file = new File(getXmlInvoiceDir().getAbsolutePath()+File.separator+entity.getTemporaryInvoiceNumber()+".xml");
		        if (file.exists()) {
		            file.delete();
		         messages.info(new BundleKey("messages", "delete.successful"));
		            }     
		}catch(Exception e){
			log.error("failed to delete xml invoice ",e);
		}}
	
	public boolean isXmlInvoiceAlreadyGenerated(){ 
		 String fileDir = getXmlInvoiceDir().getAbsolutePath()+File.separator + (entity.getInvoiceNumber() != null ? entity.getInvoiceNumber() : entity.getTemporaryInvoiceNumber())+".xml";
		 File file=new File(fileDir); 
		 return file.exists();	
	  }
	
}
