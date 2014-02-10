package org.meveo.api;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.InvoiceDto;
import org.meveo.api.dto.RatedTransactionDTO;
import org.meveo.api.dto.SubCategoryInvoiceAgregateDto;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceAgregateService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.RecordedInvoiceService;


/**
 * @author R.AITYAAZZA
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class InvoiceApi extends BaseApi {

    @Inject
    RecordedInvoiceService recordedInvoiceService;

    @Inject
    ProviderService providerService;

    @Inject
    CustomerAccountService customerAccountService;

    @Inject
    BillingAccountService billingAccountService;
    @Inject
    BillingRunService billingRunService;

    @Inject
    InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    RatedTransactionService ratedTransactionService;

    @Inject
    OCCTemplateService  oCCTemplateService;

    @Inject
    private InvoiceAgregateService invoiceAgregateService;

    @Inject
    InvoiceService invoiceService;

    @Inject
    TaxService taxService;

    ParamBean paramBean=ParamBean.getInstance();
    public void createInvoice(InvoiceDto invoiceDTO) throws BusinessException{

        Provider provider=em.find(Provider.class,invoiceDTO.getProviderId());
        User currentUser = em.find(User.class,invoiceDTO.getCurrentUserId());
        if (invoiceDTO.getSubCategoryInvoiceAgregates().size()>0
				&& !StringUtils
						.isBlank(invoiceDTO.getBillingAccountCode())
				&& !StringUtils.isBlank(invoiceDTO.getDueDate())
				&& !StringUtils.isBlank(invoiceDTO.getAmountTax())
				&& !StringUtils.isBlank(invoiceDTO.getAmountWithoutTax())
				&& !StringUtils.isBlank(invoiceDTO.getAmountWithTax())) {
        	 BillingAccount billingAccount=billingAccountService.findByCode(em,invoiceDTO.getBillingAccountCode(), provider);
             String invoiceSubCategoryCode=paramBean.getProperty("invoiceSubCategory.code.default");
             String taxCode=paramBean.getProperty("tax.code.default");
             Tax tax=taxService.findByCode(em, taxCode);
             InvoiceSubCategory invoiceSubCategory=invoiceSubCategoryService.findByCode(em,invoiceSubCategoryCode);
             BillingRun br=new BillingRun();
             br.setStartDate(new Date());
             br.setProvider(provider);
             br.setStatus(BillingRunStatusEnum.VALIDATED);
             billingRunService.create(em, br, currentUser, provider);

               Invoice invoice = new Invoice();
               invoice.setBillingAccount(billingAccount);
               invoice.setBillingRun(br);
               invoice.setAuditable(br.getAuditable());
               invoice.setProvider(provider);
               Date invoiceDate = new Date();
               invoice.setInvoiceDate(invoiceDate);
               invoice.setDueDate(invoiceDTO.getDueDate());
               invoice.setPaymentMethod(billingAccount.getPaymentMethod());
               invoice.setAmount(invoiceDTO.getAmount());
               invoice.setAmountTax(invoiceDTO.getAmountTax());
               invoice.setAmountWithoutTax(invoiceDTO.getAmountWithoutTax());
               invoice.setAmountWithTax(invoiceDTO.getAmountWithTax());
               invoice.setDiscount(invoiceDTO.getDiscount());

               invoiceService.create(em, invoice, currentUser, provider);
             UserAccount userAccount=billingAccount.getDefaultUserAccount();

             for(SubCategoryInvoiceAgregateDto subCategoryInvoiceAgregateDTO : invoiceDTO.getSubCategoryInvoiceAgregates()){
            	  if (subCategoryInvoiceAgregateDTO.getRatedTransactions().size()==0
          				|| StringUtils.isBlank(subCategoryInvoiceAgregateDTO.getItemNumber())
          				|| StringUtils.isBlank(subCategoryInvoiceAgregateDTO.getAmountTax())
          				|| StringUtils.isBlank(subCategoryInvoiceAgregateDTO.getAmountWithoutTax())
          				|| StringUtils.isBlank(subCategoryInvoiceAgregateDTO.getAmountWithTax())
          				|| StringUtils.isBlank(subCategoryInvoiceAgregateDTO.getAmountWithoutTax())) {
            		  throw new BusinessException("these category fields are mandatory: ItemNumber,AmountTax,AmountWithoutTax,AmountWithTax,RatedTransactions");
            	  }
                 SubCategoryInvoiceAgregate subCategoryInvoiceAgregate=new SubCategoryInvoiceAgregate();
                 subCategoryInvoiceAgregate.setAmountWithoutTax(subCategoryInvoiceAgregateDTO.getAmountWithoutTax());
                 subCategoryInvoiceAgregate.setAmountWithTax(subCategoryInvoiceAgregateDTO.getAmountWithTax());
                 subCategoryInvoiceAgregate.setAmountTax(subCategoryInvoiceAgregateDTO.getAmountTax());
                 subCategoryInvoiceAgregate.setAccountingCode(subCategoryInvoiceAgregateDTO.getAccountingCode());
                 subCategoryInvoiceAgregate.setBillingAccount(billingAccount);
                 subCategoryInvoiceAgregate.setUserAccount(userAccount);
                 subCategoryInvoiceAgregate.setBillingRun(br);
                 subCategoryInvoiceAgregate.setInvoice(invoice);
                 subCategoryInvoiceAgregate.setSubCategoryTax(tax);
                 subCategoryInvoiceAgregate.setItemNumber(subCategoryInvoiceAgregateDTO.getItemNumber());
                 subCategoryInvoiceAgregate.setInvoiceSubCategory(invoiceSubCategory);
                 subCategoryInvoiceAgregate.setWallet(userAccount.getWallet());

                 CategoryInvoiceAgregate categoryInvoiceAgregate=new CategoryInvoiceAgregate();
                 categoryInvoiceAgregate.setAmountWithTax(subCategoryInvoiceAgregateDTO.getAmountWithTax());
                 categoryInvoiceAgregate.setAmountWithoutTax(subCategoryInvoiceAgregateDTO.getAmountWithoutTax());
                 categoryInvoiceAgregate.setAmountTax(subCategoryInvoiceAgregateDTO.getAmountTax());
                 categoryInvoiceAgregate.setBillingAccount(billingAccount);
                 categoryInvoiceAgregate.setBillingRun(br);
                 categoryInvoiceAgregate.setInvoice(invoice);
                 categoryInvoiceAgregate.setItemNumber(subCategoryInvoiceAgregateDTO.getItemNumber());
                 categoryInvoiceAgregate.setUserAccount(billingAccount.getDefaultUserAccount());
                 categoryInvoiceAgregate.setInvoiceCategory(invoiceSubCategory.getInvoiceCategory());
                 invoiceAgregateService.create(em, categoryInvoiceAgregate, currentUser, provider);


                 TaxInvoiceAgregate taxInvoiceAgregate=new TaxInvoiceAgregate();
                 taxInvoiceAgregate.setAmountWithoutTax(subCategoryInvoiceAgregateDTO.getAmountWithoutTax());
                 taxInvoiceAgregate.setAmountTax(subCategoryInvoiceAgregateDTO.getAmountTax());
                 taxInvoiceAgregate.setAmountWithTax(subCategoryInvoiceAgregateDTO.getAmountWithTax());
                 taxInvoiceAgregate.setTaxPercent(subCategoryInvoiceAgregateDTO.getTaxPercent());
                 taxInvoiceAgregate.setBillingAccount(billingAccount);
                 taxInvoiceAgregate.setBillingRun(br);
                 taxInvoiceAgregate.setInvoice(invoice);
                 taxInvoiceAgregate.setUserAccount(billingAccount.getDefaultUserAccount());
                 taxInvoiceAgregate.setItemNumber(subCategoryInvoiceAgregateDTO.getItemNumber());
                 taxInvoiceAgregate.setTax(tax);
                 invoiceAgregateService.create(em,taxInvoiceAgregate, currentUser, provider);


                 subCategoryInvoiceAgregate.setCategoryInvoiceAgregate(categoryInvoiceAgregate);
                 subCategoryInvoiceAgregate.setTaxInvoiceAgregate(taxInvoiceAgregate);
                 invoiceAgregateService.create(em,subCategoryInvoiceAgregate, currentUser, provider);

                 for(RatedTransactionDTO ratedTransaction:subCategoryInvoiceAgregateDTO.getRatedTransactions()){
                     RatedTransaction meveoRatedTransaction=new RatedTransaction(null, ratedTransaction.getUsageDate(), ratedTransaction.getUnitAmountWithoutTax(),
                             ratedTransaction.getUnitAmountWithTax(), ratedTransaction.getUnitAmountTax(), ratedTransaction.getQuantity(), ratedTransaction.getAmountWithoutTax(), ratedTransaction.getAmountWithTax(),
                             ratedTransaction.getAmountTax(),RatedTransactionStatusEnum.BILLED, provider, null, billingAccount, invoiceSubCategory,null,null,null);
                     meveoRatedTransaction.setCode(ratedTransaction.getCode());
                     meveoRatedTransaction.setDescription(ratedTransaction.getDescription());
                     meveoRatedTransaction.setBillingRun(br);
                     meveoRatedTransaction.setInvoice(invoice);
                     meveoRatedTransaction.setWallet(userAccount.getWallet());
                     ratedTransactionService.create(em,meveoRatedTransaction, currentUser, provider);

                 }


             }
        }else{

  		  throw new BusinessException("these invoice fields are mandatory: AmountTax,AmountWithoutTax,AmountWithTax,dueDate,billingAccountCode,SubCategoryInvoiceAgregates");
        }
        
       



    }

}