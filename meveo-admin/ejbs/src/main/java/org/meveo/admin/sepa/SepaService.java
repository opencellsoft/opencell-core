package org.meveo.admin.sepa;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.dunning.DunningUtils;
import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.sepa.jaxb.Pain008;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn;
import org.meveo.admin.utils.ArConfig;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AutomatedPayment;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.AutomatedPaymentService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.DDRequestLOTService;
import org.meveo.service.payments.impl.DDRequestLotOpService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.RecordedInvoiceService;

@Stateless
@LocalBean
public class SepaService
  extends PersistenceService<DDRequestItem>
{
  private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(SepaService.class.getName());

 @Inject
  RecordedInvoiceService recordedInvoiceService;
  @Inject
  CustomerAccountService customerAccountService;
  @Inject
  DDRequestLotOpService dDRequestLotOpService;
  @Inject
  DDRequestLOTService dDRequestLOTService;
  @Inject
  OCCTemplateService oCCTemplateService;
  @Inject
  UserService userService;
  @Inject
  AutomatedPaymentService automatedPaymentService;
  @Inject
  AccountOperationService accountOperationService;
  @Inject
  MatchingCodeService matchingCodeService;
  @Inject
  SepaFileBuilder sepaFileBuilder;
  
  public void createDDRquestLot(Date fromDueDate, Date toDueDate, User user, Provider provider)
    throws BusinessEntityException,Exception
  {
    logger.info("createDDRquestLot fromDueDate:" + fromDueDate + "  toDueDate:" + toDueDate + "  user:" + (user == null ? "null" : user.getUserName()));
    if (provider == null) {
      throw new BusinessEntityException("provider is empty");
    }
    if (fromDueDate == null) {
      throw new BusinessEntityException("fromDuDate is empty");
    }
    if (toDueDate == null) {
      throw new BusinessEntityException("toDueDate is empty");
    }
    if (fromDueDate.after(toDueDate)) {
      throw new BusinessEntityException("fromDueDate is after toDueDate");
    }
    if (user == null) {
      throw new BusinessEntityException("user is null");
    }
    List<RecordedInvoice> invoices = this.recordedInvoiceService.getInvoices(fromDueDate, toDueDate, provider.getCode());
    if ((invoices == null) || (invoices.isEmpty())) {
      throw new BusinessEntityException("no invoices!");
    }
    logger.info("number invoices : " + invoices.size());
    
    BigDecimal totalAmount = BigDecimal.ZERO;
    
      DDRequestLOT ddRequestLOT = new DDRequestLOT();
      ddRequestLOT.setProvider(provider);
      ddRequestLOT.setAuditable(DunningUtils.getAuditable(user));
      ddRequestLOT.setInvoicesNumber(Integer.valueOf(invoices.size()));
      List<DDRequestItem> ddrequestItems = new ArrayList<DDRequestItem>();
      Map<CustomerAccount, List<RecordedInvoice>> customerAccountInvoices = new HashMap();
      for (RecordedInvoice invoice : invoices) {
			if (customerAccountInvoices.containsKey(invoice.getCustomerAccount())) {
				customerAccountInvoices.get(invoice.getCustomerAccount()).add(invoice);
			} else {
				List<RecordedInvoice> tmp = new ArrayList<RecordedInvoice>();
				tmp.add(invoice);
				customerAccountInvoices.put(invoice.getCustomerAccount(), tmp);
			}
		}
      for (Map.Entry<CustomerAccount, List<RecordedInvoice>> e : customerAccountInvoices.entrySet())
      {
        DDRequestItem ddrequestItem = new DDRequestItem();
        BigDecimal totalInvoices = BigDecimal.ZERO;
		ddrequestItem.setBillingAccountName(e.getValue().get(0).getBillingAccountName());
		ddrequestItem.setCustomerAccount(e.getValue().get(0).getCustomerAccount());
		ddrequestItem.setDdRequestLOT(ddRequestLOT);
		ddrequestItem.setDueDate(e.getValue().get(0).getDueDate());
		ddrequestItem.setPaymentInfo(e.getValue().get(0).getPaymentInfo());
		ddrequestItem.setPaymentInfo1(e.getValue().get(0).getPaymentInfo1());
		ddrequestItem.setPaymentInfo2(e.getValue().get(0).getPaymentInfo2());
		ddrequestItem.setPaymentInfo3(e.getValue().get(0).getPaymentInfo3());
		ddrequestItem.setPaymentInfo4(e.getValue().get(0).getPaymentInfo4());
		ddrequestItem.setPaymentInfo5(e.getValue().get(0).getPaymentInfo5());
		ddrequestItem.setProvider(e.getValue().get(0).getProvider());
		ddrequestItem.setReference(e.getValue().get(0).getReference());
		ddrequestItem.setAuditable(DunningUtils.getAuditable(user));
		ddrequestItem.setInvoices(e.getValue());
		for (RecordedInvoice invoice : e.getValue()) {
			totalInvoices = totalInvoices.add(invoice.getAmount());
			invoice.setDdRequestLOT(ddRequestLOT);
			invoice.setDdRequestItem(ddrequestItem);
			invoice.getAuditable().setUpdated(new Date());
			invoice.getAuditable().setUpdater(user);
			recordedInvoiceService.update(invoice);
			ddRequestLOT.getInvoices().add(invoice);
			logger.info("invoice reference : " + invoice.getReference() + " processed");
		}
		ddrequestItem.setAmountInvoices(totalInvoices);
		ddrequestItems.add(ddrequestItem);
		create(ddrequestItem);
		totalAmount = totalAmount.add(ddrequestItem.getAmountInvoices());
      }
      if (!ddrequestItems.isEmpty())
      {
        ddRequestLOT.setDdrequestItems(ddrequestItems);
        ddRequestLOT.setInvoicesAmount(totalAmount);
        ddRequestLOT.setFileName(exportDDRequestLot(ddRequestLOT, ddrequestItems, totalAmount, provider));
        ddRequestLOT.setSendDate(new Date());
        ddRequestLOT.getAuditable().setUpdated(new Date());
        ddRequestLOT.getAuditable().setUpdater(user);
        this.dDRequestLOTService.update(ddRequestLOT);
      }
      else
      {
        throw new BusinessEntityException("No ddRequestItems!");
      }
      createPaymentsForDDRequestLot(ddRequestLOT, user);
      logger.info("ddRequestLOT created , totalAmount:" + ddRequestLOT.getInvoicesAmount());
      logger.info("Successful createDDRquestLot fromDueDate:" + fromDueDate + "  toDueDate:" + toDueDate + " provider:" + provider.getCode());
    }
  
  
  public String exportDDRequestLot(Long ddRequestLotId)
    throws Exception
  {
    logger.info("exportDDRequestLot ddRequestLotId:" + ddRequestLotId);
    if (ddRequestLotId == null) {
      throw new Exception("ddRequestLotId is null!");
    }
    DDRequestLOT ddRequestLOT = (DDRequestLOT)this.dDRequestLOTService.findById(ddRequestLotId);
    if (ddRequestLOT == null) {
      throw new Exception("ddRequestLotId doesn't exist");
    }
    String fileName = exportDDRequestLot(ddRequestLOT, ddRequestLOT.getDdrequestItems(), ddRequestLOT.getInvoicesAmount(), ddRequestLOT.getProvider());
    
    logger.info("Successful exportDDRequestLot ddRequestLotId:" + ddRequestLOT.getId() + " , fileName:" + fileName);
    return fileName;
  }
  
  private String exportDDRequestLot(DDRequestLOT ddRequestLot, List<DDRequestItem> ddrequestItems, BigDecimal totalAmount, Provider provider)
    throws Exception
  {
    Pain008 document=new Pain008();
	CstmrDrctDbtInitn Message =new CstmrDrctDbtInitn();
	document.setCstmrDrctDbtInitn(Message);
    sepaFileBuilder.addHeader(Message, ddRequestLot);
    for (DDRequestItem ddrequestItem : ddrequestItems) {
    	sepaFileBuilder.addPaymentInformation(Message, ddrequestItem);
    	}
    
    String fileName =ArConfig.getDDRequestFileNamePrefix()+ddRequestLot.getId();
    fileName = fileName + "_" + provider.getCode();
    fileName = fileName + DateUtils.formatDateWithPattern(new Date(), ArConfig.getDDRequestFileNameExtension());
    String outputDir = ArConfig.getDDRequestOutputDirectory();
    

	JAXBUtils.marshaller(document, new File(outputDir + File.separator + fileName));
    return fileName;
  }
  
  private void createPaymentsForDDRequestLot(DDRequestLOT ddRequestLOT, User user)
    throws Exception
  {
    logger.info("createPaymentsForDDRequestLot ddRequestLotId:" + ddRequestLOT.getId() + " user :" + (user == null ? "null" : user.getUserName()));
    if (user == null) {
      throw new Exception("user is empty!");
    }
    if (ddRequestLOT.isPaymentCreated()) {
      throw new Exception("Payment Already created.");
    }
    if ((ddRequestLOT.getInvoices() == null) || (ddRequestLOT.getInvoices().isEmpty())) {
      throw new Exception("No Invoices Founded");
    }
    OCCTemplate directDebitTemplate = this.oCCTemplateService.getDirectDebitOCCTemplate(ddRequestLOT.getProvider().getCode());
    if(directDebitTemplate==null){
    	throw new Exception("OCC doesn't exist. code="+ArConfig.getDirectDebitOccCode());
    }
    try
    {
      for (DDRequestItem ddrequestItem : ddRequestLOT.getDdrequestItems()) {
        if (BigDecimal.ZERO.compareTo(ddrequestItem.getAmount()) == 0)
        {
          logger.info("invoice:" + ddrequestItem.getReference() + " balanceDue:" + BigDecimal.ZERO + " no DIRECTDEBIT transaction ");
        }
        else
        {
          boolean addMatching = ddrequestItem.getAmountInvoices().compareTo(ddrequestItem.getAmount()) == 0;
          List<RecordedInvoice> invoicesList = ddrequestItem.getInvoices();
          createPayment(ddrequestItem.getProvider(), PaymentMethodEnum.DIRECTDEBIT, directDebitTemplate, ddrequestItem.getAmount(), ddrequestItem.getCustomerAccount(), ddrequestItem.getReference(), ddRequestLOT.getFileName(), ddRequestLOT.getSendDate(), DateUtils.addDaysToDate(new Date(), ArConfig.getDateValueAfter()), ddRequestLOT.getSendDate(), ddRequestLOT.getSendDate(), invoicesList, addMatching, MatchingTypeEnum.A_DERICT_DEBIT);
        }
      }
      ddRequestLOT.setPaymentCreated(true);
      ddRequestLOT.getAuditable().setUpdated(new Date());
      ddRequestLOT.getAuditable().setUpdater(user);
      this.dDRequestLOTService.update(ddRequestLOT);
      logger.info("Successful createPaymentsForDDRequestLot ddRequestLotId:" + ddRequestLOT.getId());
    }
    catch (Exception e)
    {
      throw e;
    }
  }
  
  public void createPayment(Provider provider, PaymentMethodEnum paymentMethodEnum, OCCTemplate occTemplate, BigDecimal amount, CustomerAccount customerAccount, String reference, String bankLot, Date depositDate, Date bankCollectionDate, Date dueDate, Date transactionDate, List<RecordedInvoice> listOCCforMatching, boolean isToMatching, MatchingTypeEnum matchingTypeEnum)
    throws Exception
  {
    this.log.info("create payment for amount:" + amount + " paymentMethodEnum:" + paymentMethodEnum + " isToMatching:" + isToMatching + "  customerAccount:" + customerAccount.getCode() + "...");
    
    User user = this.userService.getSystemUser();
    AutomatedPayment automatedPayment = new AutomatedPayment();
    automatedPayment.setProvider(provider);
    automatedPayment.setPaymentMethod(paymentMethodEnum);
    automatedPayment.setAmount(amount);
    automatedPayment.setUnMatchingAmount(amount);
    automatedPayment.setMatchingAmount(BigDecimal.ZERO);
    automatedPayment.setAccountCode(occTemplate.getAccountCode());
    automatedPayment.setOccCode(occTemplate.getCode());
    automatedPayment.setOccDescription(occTemplate.getDescription());
    automatedPayment.setTransactionCategory(occTemplate.getOccCategory());
    automatedPayment.setAccountCodeClientSide(occTemplate.getAccountCodeClientSide());
    automatedPayment.setCustomerAccount(customerAccount);
    automatedPayment.setReference(reference);
    automatedPayment.setBankLot(bankLot);
    automatedPayment.setDepositDate(depositDate);
    automatedPayment.setBankCollectionDate(bankCollectionDate);
    automatedPayment.setDueDate(dueDate);
    automatedPayment.setTransactionDate(transactionDate);
    automatedPayment.setAuditable(DunningUtils.getAuditable(user));
    automatedPayment.setMatchingStatus(MatchingStatusEnum.O);
    this.automatedPaymentService.create(automatedPayment);
    if (isToMatching)
    {
      MatchingCode matchingCode = new MatchingCode();
      BigDecimal amountToMatch = BigDecimal.ZERO;
      for (int i = 0; i < listOCCforMatching.size(); i++)
      {
        AccountOperation accountOperation = (AccountOperation)listOCCforMatching.get(i);
        amountToMatch = accountOperation.getUnMatchingAmount();
        accountOperation.setMatchingAmount(accountOperation.getMatchingAmount().add(amountToMatch));
        accountOperation.setUnMatchingAmount(accountOperation.getUnMatchingAmount().subtract(amountToMatch));
        accountOperation.setMatchingStatus(MatchingStatusEnum.L);
        accountOperation.getAuditable().setUpdated(new Date());
        accountOperation.getAuditable().setUpdater(user);
        this.accountOperationService.update(accountOperation);
        MatchingAmount matchingAmount = new MatchingAmount();
        matchingAmount.setProvider(accountOperation.getProvider());
        matchingAmount.updateAudit(user);
        matchingAmount.setAccountOperation(accountOperation);
        matchingAmount.setMatchingCode(matchingCode);
        matchingAmount.setMatchingAmount(amountToMatch);
        accountOperation.getMatchingAmounts().add(matchingAmount);
        matchingCode.getMatchingAmounts().add(matchingAmount);
      }
      matchingCode.setMatchingAmountDebit(amount);
      matchingCode.setMatchingAmountCredit(amount);
      matchingCode.setMatchingDate(new Date());
      matchingCode.setMatchingType(matchingTypeEnum);
      matchingCode.setAuditable(DunningUtils.getAuditable(user));
      matchingCode.setProvider(provider);
      this.matchingCodeService.create(matchingCode);
      this.log.info("matching created  for 1 automatedPayment and " + (listOCCforMatching.size() - 1) + " occ");
    }
    else
    {
      this.log.info("no matching created ");
    }
    this.log.info("automatedPayment created for amount:" + automatedPayment.getAmount());
  }
}
