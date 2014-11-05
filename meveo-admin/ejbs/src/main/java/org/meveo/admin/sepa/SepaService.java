package org.meveo.admin.sepa;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.sepa.jaxb.Pain002;
import org.meveo.admin.sepa.jaxb.Pain002.CstmrPmtStsRpt;
import org.meveo.admin.sepa.jaxb.Pain002.CstmrPmtStsRpt.OrgnlGrpInfAndSts;
import org.meveo.admin.sepa.jaxb.Pain002.CstmrPmtStsRpt.OrgnlPmtInfAndSts;
import org.meveo.admin.sepa.jaxb.Pain002.CstmrPmtStsRpt.OrgnlPmtInfAndSts.TxInfAndSts;
import org.meveo.admin.sepa.jaxb.Pain008;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn;
import org.meveo.admin.utils.ArConfig;
import org.meveo.commons.utils.ImportFileFiltre;
import org.meveo.commons.utils.JAXBUtils;
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
import org.meveo.service.payments.impl.MatchingAmountService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.RecordedInvoiceService;

@Stateless
@LocalBean
public class SepaService extends PersistenceService<DDRequestItem> {
	private static final java.util.logging.Logger logger = java.util.logging.Logger
			.getLogger(SepaService.class.getName());

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
	MatchingAmountService matchingAmountService;

	@Inject
	SepaFileBuilder sepaFileBuilder;

	@EJB
	SepaService sepaService;

	public void createDDRquestLot(Date fromDueDate, Date toDueDate, User user,
			Provider provider) throws BusinessEntityException, Exception {
		logger.info("createDDRquestLot fromDueDate:" + fromDueDate
				+ "  toDueDate:" + toDueDate + "  user:"
				+ (user == null ? "null" : user.getUserName()));
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
		List<RecordedInvoice> invoices = recordedInvoiceService.getInvoices(
				fromDueDate, toDueDate, provider.getCode());
		if ((invoices == null) || (invoices.isEmpty())) {
			throw new BusinessEntityException("no invoices!");
		}
		OCCTemplate directDebitTemplate = oCCTemplateService
				.getDirectDebitOCCTemplate(provider.getCode());
		if (directDebitTemplate == null) {
			throw new BusinessException("OCC doesn't exist. code="
					+ ArConfig.getDirectDebitOccCode());
		}
		logger.info("number invoices : " + invoices.size());

		BigDecimal totalAmount = BigDecimal.ZERO;

		DDRequestLOT ddRequestLOT = new DDRequestLOT();
		ddRequestLOT.setProvider(provider);
		ddRequestLOT.setInvoicesNumber(Integer.valueOf(invoices.size()));
		dDRequestLOTService.create(ddRequestLOT, user);
		List<DDRequestItem> ddrequestItems = new ArrayList<DDRequestItem>();
		Map<CustomerAccount, List<RecordedInvoice>> customerAccountInvoices = new HashMap<CustomerAccount, List<RecordedInvoice>>();
		for (RecordedInvoice invoice : invoices) {
			if (customerAccountInvoices.containsKey(invoice
					.getCustomerAccount())) {
				customerAccountInvoices.get(invoice.getCustomerAccount()).add(
						invoice);
			} else {
				List<RecordedInvoice> tmp = new ArrayList<RecordedInvoice>();
				tmp.add(invoice);
				customerAccountInvoices.put(invoice.getCustomerAccount(), tmp);
			}
		}
		for (Map.Entry<CustomerAccount, List<RecordedInvoice>> e : customerAccountInvoices
				.entrySet()) {
			DDRequestItem ddrequestItem = new DDRequestItem();

			BigDecimal amount = e.getValue().get(0).getNetToPay();

			if (amount == null) {
				amount = customerAccountService
						.customerAccountBalanceDueWithoutLitigation(
								e.getValue().get(0).getCustomerAccount()
										.getId(), null, e.getValue().get(0)
										.getDueDate());
			}
			if (BigDecimal.ZERO.compareTo(amount) == 0) {
				continue;
			}
			logger.info("ddrequestItem : " + ddrequestItem.getId() + "amount="
					+ amount);
			ddrequestItem.setAmount(amount);
			BigDecimal totalInvoices = BigDecimal.ZERO;
			ddrequestItem.setBillingAccountName(e.getValue().get(0)
					.getBillingAccountName());
			ddrequestItem.setCustomerAccount(e.getValue().get(0)
					.getCustomerAccount());
			ddrequestItem.setDdRequestLOT(ddRequestLOT);
			ddrequestItem.setDueDate(e.getValue().get(0).getDueDate());
			ddrequestItem.setPaymentInfo(e.getValue().get(0).getPaymentInfo());
			ddrequestItem
					.setPaymentInfo1(e.getValue().get(0).getPaymentInfo1());
			ddrequestItem
					.setPaymentInfo2(e.getValue().get(0).getPaymentInfo2());
			ddrequestItem
					.setPaymentInfo3(e.getValue().get(0).getPaymentInfo3());
			ddrequestItem
					.setPaymentInfo4(e.getValue().get(0).getPaymentInfo4());
			ddrequestItem
					.setPaymentInfo5(e.getValue().get(0).getPaymentInfo5());
			ddrequestItem.setProvider(e.getValue().get(0).getProvider());
			ddrequestItem.setReference(e.getValue().get(0).getReference());
			ddrequestItem.setInvoices(e.getValue());
			for (RecordedInvoice invoice : e.getValue()) {
				totalInvoices = totalInvoices.add(invoice.getAmount());
				invoice.setDdRequestLOT(ddRequestLOT);
				invoice.setDdRequestItem(ddrequestItem);
				recordedInvoiceService.update(invoice);
				ddRequestLOT.getInvoices().add(invoice);
				logger.info("invoice reference : " + invoice.getReference()
						+ " processed");
			}
			ddrequestItem.setAmountInvoices(totalInvoices);
			ddrequestItems.add(ddrequestItem);
			sepaService.create(ddrequestItem, user);
			totalAmount = totalAmount.add(ddrequestItem.getAmountInvoices());
		}
		if (!ddrequestItems.isEmpty()) {

			ddRequestLOT.setDdrequestItems(ddrequestItems);
			ddRequestLOT.setInvoicesAmount(totalAmount);
			String ddFileName = getDDFileName(ddRequestLOT);
			ddRequestLOT.setFileName(ddFileName);
			createPaymentsForDDRequestLot(ddRequestLOT, directDebitTemplate,
					user);

			exportDDRequestLot(ddRequestLOT, ddrequestItems, totalAmount,
					provider, ddFileName);
			ddRequestLOT.setSendDate(new Date());
			dDRequestLOTService.update(ddRequestLOT, user);

			logger.info("ddRequestLOT created , totalAmount:"
					+ ddRequestLOT.getInvoicesAmount());
			logger.info("Successful createDDRquestLot fromDueDate:"
					+ fromDueDate + "  toDueDate:" + toDueDate + " provider:"
					+ provider.getCode());

		} else {
			throw new BusinessEntityException("No ddRequestItems!");
		}
	}

	public String getDDFileName(DDRequestLOT ddRequestLot) {
		String fileName = ArConfig.getDDRequestFileNamePrefix()
				+ ddRequestLot.getId();
		fileName = fileName + "_" + ddRequestLot.getProvider().getCode();
		fileName = fileName
				+ DateUtils.formatDateWithPattern(new Date(),
						ArConfig.getDDRequestFileNameExtension());
		String outputDir = ArConfig.getDDRequestOutputDirectory();
		File dir = new File(outputDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return outputDir + File.separator + fileName;
	}

	public String exportDDRequestLot(Long ddRequestLotId) throws Exception {
		logger.info("exportDDRequestLot ddRequestLotId:" + ddRequestLotId);
		if (ddRequestLotId == null) {
			throw new Exception("ddRequestLotId is null!");
		}
		DDRequestLOT ddRequestLOT = (DDRequestLOT) dDRequestLOTService
				.findById(ddRequestLotId);
		if (ddRequestLOT == null) {
			throw new Exception("ddRequestLotId doesn't exist");
		}
		String fileName = exportDDRequestLot(ddRequestLOT,
				ddRequestLOT.getDdrequestItems(),
				ddRequestLOT.getInvoicesAmount(), ddRequestLOT.getProvider(),
				getDDFileName(ddRequestLOT));

		logger.info("Successful exportDDRequestLot ddRequestLotId:"
				+ ddRequestLOT.getId() + " , fileName:" + fileName);
		return fileName;
	}

	private String exportDDRequestLot(DDRequestLOT ddRequestLot,
			List<DDRequestItem> ddrequestItems, BigDecimal totalAmount,
			Provider provider, String fileName) throws Exception {
		Pain008 document = new Pain008();
		CstmrDrctDbtInitn Message = new CstmrDrctDbtInitn();
		document.setCstmrDrctDbtInitn(Message);
		document.setXmlns("urn:iso:std:iso:20022:tech:xsd:pain.008.001.02");
		sepaFileBuilder.addHeader(Message, ddRequestLot);
		for (DDRequestItem ddrequestItem : ddrequestItems) {
			sepaFileBuilder.addPaymentInformation(Message, ddrequestItem);
		}

		JAXBUtils.marshaller(document, new File(fileName));
		return fileName;
	}

	private void createPaymentsForDDRequestLot(DDRequestLOT ddRequestLOT,
			OCCTemplate directDebitTemplate, User user)
			throws BusinessException {
		logger.info("createPaymentsForDDRequestLot ddRequestLotId:"
				+ ddRequestLOT.getId() + " user :"
				+ (user == null ? "null" : user.getUserName()));
		if (user == null) {
			throw new BusinessException("user is empty!");
		}

		if (ddRequestLOT.isPaymentCreated()) {
			throw new BusinessException("Payment Already created.");
		}

		for (DDRequestItem ddrequestItem : ddRequestLOT.getDdrequestItems()) {
			if (BigDecimal.ZERO.compareTo(ddrequestItem.getAmount()) == 0) {
				logger.info("invoice:" + ddrequestItem.getReference()
						+ " balanceDue:" + BigDecimal.ZERO
						+ " no DIRECTDEBIT transaction ");
			} else {
				boolean addMatching = ddrequestItem.getAmountInvoices()
						.compareTo(ddrequestItem.getAmount()) == 0;
				List<RecordedInvoice> invoicesList = ddrequestItem
						.getInvoices();
				AutomatedPayment automatedPayment = createPayment(
						ddrequestItem.getProvider(),
						PaymentMethodEnum.DIRECTDEBIT,
						directDebitTemplate,
						ddrequestItem.getAmount(),
						ddrequestItem.getCustomerAccount(),
						ddrequestItem.getReference(),
						ddRequestLOT.getFileName(),
						ddRequestLOT.getSendDate(),
						DateUtils.addDaysToDate(new Date(),
								ArConfig.getDateValueAfter()),
						ddRequestLOT.getSendDate(), ddRequestLOT.getSendDate(),
						invoicesList, addMatching,
						MatchingTypeEnum.A_DERICT_DEBIT);
				ddrequestItem.setAutomatedPayment(automatedPayment);
				sepaService.update(ddrequestItem, user);

			}
		}
		ddRequestLOT.setPaymentCreated(true);
		dDRequestLOTService.update(ddRequestLOT, user);

		logger.info("Successful createPaymentsForDDRequestLot ddRequestLotId:"
				+ ddRequestLOT.getId());

	}

	public AutomatedPayment createPayment(Provider provider,
			PaymentMethodEnum paymentMethodEnum, OCCTemplate occTemplate,
			BigDecimal amount, CustomerAccount customerAccount,
			String reference, String bankLot, Date depositDate,
			Date bankCollectionDate, Date dueDate, Date transactionDate,
			List<RecordedInvoice> listOCCforMatching, boolean isToMatching,
			MatchingTypeEnum matchingTypeEnum) throws BusinessException {
		log.info("create payment for amount:" + amount + " paymentMethodEnum:"
				+ paymentMethodEnum + " isToMatching:" + isToMatching
				+ "  customerAccount:" + customerAccount.getCode() + "...");

		User user = userService.getSystemUser();
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
		automatedPayment.setAccountCodeClientSide(occTemplate
				.getAccountCodeClientSide());
		automatedPayment.setCustomerAccount(customerAccount);
		automatedPayment.setReference(reference);
		automatedPayment.setBankLot(bankLot);
		automatedPayment.setDepositDate(depositDate);
		automatedPayment.setBankCollectionDate(bankCollectionDate);
		automatedPayment.setDueDate(dueDate);
		automatedPayment.setTransactionDate(transactionDate);
		if (isToMatching && listOCCforMatching.size() > 0) {
			automatedPayment.setMatchingStatus(MatchingStatusEnum.L);
			automatedPayment.setUnMatchingAmount(BigDecimal.ZERO);
			automatedPayment.setMatchingAmount(amount);
		} else {
			automatedPayment.setMatchingStatus(MatchingStatusEnum.O);
			automatedPayment.setUnMatchingAmount(amount);
			automatedPayment.setMatchingAmount(BigDecimal.ZERO);
		}

		automatedPaymentService.create(automatedPayment, user);
		if (isToMatching) {
			MatchingCode matchingCode = new MatchingCode();
			BigDecimal amountToMatch = BigDecimal.ZERO;
			for (int i = 0; i < listOCCforMatching.size(); i++) {
				AccountOperation accountOperation = (AccountOperation) listOCCforMatching
						.get(i);
				amountToMatch = accountOperation.getUnMatchingAmount();
				accountOperation.setMatchingAmount(accountOperation
						.getMatchingAmount().add(amountToMatch));
				accountOperation.setUnMatchingAmount(accountOperation
						.getUnMatchingAmount().subtract(amountToMatch));
				accountOperation.setMatchingStatus(MatchingStatusEnum.L);
				accountOperation.getAuditable().setUpdated(new Date());
				accountOperation.getAuditable().setUpdater(user);
				accountOperationService.update(accountOperation);
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
			matchingCode.setProvider(provider);
			matchingCodeService.create(matchingCode, user);
			log.info("matching created  for 1 automatedPayment and "
					+ listOCCforMatching.size() + " occ");
		} else {
			log.info("no matching created ");
		}
		log.info("automatedPayment created for amount:"
				+ automatedPayment.getAmount());
		return automatedPayment;
	}

	public List<File> getFilesToProcess(File dir, String prefix, String ext) {
		List<File> files = new ArrayList<File>();
		ImportFileFiltre filtre = new ImportFileFiltre(prefix, ext);
		File[] listFile = dir.listFiles(filtre);
		if (listFile == null) {
			return files;
		}
		for (File file : listFile) {
			if (file.isFile()) {
				files.add(file);
			}
		}
		return files;
	}

	public void processRejectFile(File file, String fileName, Provider provider)
			throws JAXBException, Exception {
		Pain002 pain002 = (Pain002) JAXBUtils.unmarshaller(Pain002.class, file);

		CstmrPmtStsRpt cstmrPmtStsRpt = pain002.getCstmrPmtStsRpt();

		OrgnlGrpInfAndSts orgnlGrpInfAndSts = cstmrPmtStsRpt
				.getOrgnlGrpInfAndSts();

		if (orgnlGrpInfAndSts == null) {
			throw new Exception(
					"OriginalGroupInformationAndStatus tag doesn't exist");
		}
		String dDRequestLOTref = orgnlGrpInfAndSts.getOrgnlMsgId();
		String[] dDRequestLOTrefSplited = dDRequestLOTref.split("-");

		DDRequestLOT dDRequestLOT = dDRequestLOTService.findById(Long
				.valueOf(dDRequestLOTrefSplited[1]));
		if (dDRequestLOT == null) {
			throw new Exception("DDRequestLOT doesn't exist. id="
					+ dDRequestLOTrefSplited[1]);
		}
		if (orgnlGrpInfAndSts.getGrpSts() != null
				&& "RJCT".equals(orgnlGrpInfAndSts.getGrpSts())) {
			// original message rejected at protocol level control

			for (RecordedInvoice recordedInvoice : dDRequestLOT.getInvoices()) {
				for (MatchingAmount matchingAmount : recordedInvoice
						.getMatchingAmounts()) {
					matchingAmountService.unmatching(matchingAmount.getId(),
							userService.getSystemUser());
				}
				AutomatedPayment automatedPayment = recordedInvoice
						.getDdRequestItem().getAutomatedPayment();
				if (automatedPayment != null) {
					automatedPayment.setUnMatchingAmount(automatedPayment
							.getMatchingAmount());
					automatedPayment.setMatchingAmount(BigDecimal.ZERO);
					automatedPayment.setMatchingStatus(MatchingStatusEnum.R);
					automatedPaymentService.update(automatedPayment);
				}
			}

			dDRequestLOT.setReturnStatusCode(orgnlGrpInfAndSts.getStsRsnInf()
					.getRsn().getCd());
		} else {
			OrgnlPmtInfAndSts orgnlPmtInfAndSts = cstmrPmtStsRpt
					.getOrgnlPmtInfAndSts();
			BigDecimal unmatchingAmount = BigDecimal.ZERO;
			for (TxInfAndSts txInfAndSts : orgnlPmtInfAndSts.getTxInfAndSts()) {
				if ("RJCT".equals(txInfAndSts.getTxSts())) {
					RecordedInvoice invoice = recordedInvoiceService
							.getRecordedInvoice(
									txInfAndSts.getOrgnlEndToEndId(), provider);
					for (MatchingAmount matchingAmount : invoice
							.getMatchingAmounts()) {
						unmatchingAmount.add(invoice.getMatchingAmount());
						matchingAmountService.unmatching(
								matchingAmount.getId(),
								userService.getSystemUser());
					}
					DDRequestItem ddrequestItem = invoice.getDdRequestItem();
					AutomatedPayment automatedPayment = ddrequestItem
							.getAutomatedPayment();
					if (automatedPayment != null) {
						automatedPayment
								.setMatchingAmount(automatedPayment
										.getMatchingAmount().subtract(
												unmatchingAmount));
						if (automatedPayment.getMatchingAmount().compareTo(
								BigDecimal.ZERO) == 0) {
							automatedPayment
									.setMatchingStatus(MatchingStatusEnum.R);
							automatedPaymentService.update(automatedPayment);
						}

					}

				}
			}

		}
		dDRequestLOT.setReturnFileName(file.getName());
		dDRequestLOTService.update(dDRequestLOT);
	}
}
