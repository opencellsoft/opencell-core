/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.sepa;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.sepa.jaxb.Pain002;
import org.meveo.admin.sepa.jaxb.Pain002.CstmrPmtStsRpt;
import org.meveo.admin.sepa.jaxb.Pain002.CstmrPmtStsRpt.OrgnlGrpInfAndSts;
import org.meveo.admin.sepa.jaxb.Pain002.CstmrPmtStsRpt.OrgnlPmtInfAndSts;
import org.meveo.admin.sepa.jaxb.Pain002.CstmrPmtStsRpt.OrgnlPmtInfAndSts.TxInfAndSts;
import org.meveo.admin.sepa.jaxb.pain001.AmountType3Choice;
import org.meveo.admin.sepa.jaxb.pain001.CreditTransferTransactionInformation10;
import org.meveo.admin.sepa.jaxb.pain001.CustomerCreditTransferInitiationV03;
import org.meveo.admin.sepa.jaxb.pain001.GroupHeader32;
import org.meveo.admin.sepa.jaxb.pain001.PaymentInstructionInformation3;
import org.meveo.admin.sepa.jaxb.pain001.PaymentMethod3Code;
import org.meveo.admin.sepa.jaxb.pain001.PaymentTypeInformation19;
import org.meveo.admin.sepa.jaxb.pain001.RemittanceInformation5;
import org.meveo.admin.sepa.jaxb.pain008.AccountIdentification4Choice;
import org.meveo.admin.sepa.jaxb.pain008.ActiveOrHistoricCurrencyAndAmount;
import org.meveo.admin.sepa.jaxb.pain008.BranchAndFinancialInstitutionIdentification4;
import org.meveo.admin.sepa.jaxb.pain008.CashAccount16;
import org.meveo.admin.sepa.jaxb.pain008.CustomerDirectDebitInitiationV02;
import org.meveo.admin.sepa.jaxb.pain008.DirectDebitTransaction6;
import org.meveo.admin.sepa.jaxb.pain008.DirectDebitTransactionInformation9;
import org.meveo.admin.sepa.jaxb.pain008.Document;
import org.meveo.admin.sepa.jaxb.pain008.FinancialInstitutionIdentification7;
import org.meveo.admin.sepa.jaxb.pain008.GenericPersonIdentification1;
import org.meveo.admin.sepa.jaxb.pain008.GroupHeader39;
import org.meveo.admin.sepa.jaxb.pain008.LocalInstrument2Choice;
import org.meveo.admin.sepa.jaxb.pain008.MandateRelatedInformation6;
import org.meveo.admin.sepa.jaxb.pain008.Party6Choice;
import org.meveo.admin.sepa.jaxb.pain008.PartyIdentification32;
import org.meveo.admin.sepa.jaxb.pain008.PaymentIdentification1;
import org.meveo.admin.sepa.jaxb.pain008.PaymentInstructionInformation4;
import org.meveo.admin.sepa.jaxb.pain008.PaymentMethod2Code;
import org.meveo.admin.sepa.jaxb.pain008.PaymentTypeInformation20;
import org.meveo.admin.sepa.jaxb.pain008.PersonIdentification5;
import org.meveo.admin.sepa.jaxb.pain008.PersonIdentificationSchemeName1Choice;
import org.meveo.admin.sepa.jaxb.pain008.SequenceType1Code;
import org.meveo.admin.sepa.jaxb.pain008.ServiceLevel8Choice;
import org.meveo.admin.util.ArConfig;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.catalog.impl.CalendarBankingService;
import org.meveo.service.payments.impl.AbstractDDRequestBuilder;
import org.meveo.service.payments.impl.PaymentGatewayService;
import org.meveo.util.DDRequestBuilderClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SepaFile.
 *
 * @author anasseh
 * @author Wassim Drira
 * @author Said Ramli
 * @author hznibar
 * @lastModifiedVersion 5.3
 */
@DDRequestBuilderClass
public class SepaFile extends AbstractDDRequestBuilder {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(SepaFile.class);

	/** The PATTERN that the IBAN should match. */
	private static final String IBAN_PATTERN = "[A-Z]{2,2}[0-9]{2,2}[a-zA-Z0-9]{1,30}";

	/** The location of SDD schema. */
	private static final String SDD_SCHEMA_LOCATION = "https://github.com/w2c/sepa-sdd-xml-generator/blob/master/validation_schemes/pain.008.001.02.xsd";

	/** The location of SCT schema. */
	private static final String SCT_SCHEMA_LOCATION = "https://github.com/digitick/php-sepa-xml/blob/master/tests/pain.001.001.03.xsd";

	/** The Constant SIMPLE_DATE_PATTERN. */
	private static final String SIMPLE_DATE_PATTERN = "yyyyMMdd";

	/** The Constant EMPTY_STRING. */
	private static final String EMPTY_STRING = "";

	/** The Constant DASH_STRING. */
	private static final String DASH_STRING = "-";

	/** The Constant DOUBLE_POINT. */
	private static final String DOUBLE_POINT = "\\..";

	/** The Constant COMMA_STRING. */
	private static final String COMMA_STRING = ",";

	/** The underscore separator used at sepa file name. */
	private static final String UNDERSCORE_SEPARATOR = "_";

	/** The Constant EURO_CCY. */
	private static final String EURO_CCY = "EUR";

	/** The Constant REJECT_STS_CODE. */
	private static final String REJECT_STS_CODE = "RJCT";

	/** The Constant SEPA_SERVICE_LEVEL_CD. */
	private static final String SEPA_SERVICE_LEVEL_CD = "SEPA";

	/** The Constant SEPA_LOCAL_INSTRUMENT_CODE. */
	private static final String SEPA_LOCAL_INSTRUMENT_CODE = "CORE";

	/** The Constant NUMBER_OF_TRANSACTIONS. */
	private static final String NUMBER_OF_TRANSACTIONS_1 = "1";

	/** The Constant FR_COUNTRY. */
	private static final String FR_COUNTRY = "FR";

	/** The Constant NOTPROVIDED_BIC. */
	private static final String NOTPROVIDED_BIC = "NOTPROVIDED";

	/** The Constant CATEGORY_PURPOSE_CODE. */
	private static final String CATEGORY_PURPOSE_CODE = "SUPP";

	private CalendarBankingService calendarBankingService = (CalendarBankingService) EjbUtils.getServiceInterface(CalendarBankingService.class.getSimpleName());

	@Override
	public String getDDFileName(DDRequestLOT ddRequestLot, Provider appProvider) throws BusinessException {
		if (ddRequestLot.getPaymentOrRefundEnum().getOperationCategoryToProcess() == OperationCategoryEnum.DEBIT) {
			return getFileName(ddRequestLot, appProvider, ArConfig.getDDRequestFileNamePrefix(), ArConfig.getDDRequestFileNameExtension(), ArConfig.getDDRequestOutputDirectory());
		} else {
			return getFileName(ddRequestLot, appProvider, ArConfig.getSCTRequestFileNamePrefix(), ArConfig.getSCTRequestFileNameExtension(), ArConfig.getSCTRequestOutputDir());
		}
	}

	@Override
	public void generateDDRequestLotFile(DDRequestLOT ddRequestLot, Provider appProvider) throws BusinessException {
		if (ddRequestLot.getPaymentOrRefundEnum().getOperationCategoryToProcess() == OperationCategoryEnum.DEBIT) {
			generateDDRequestLotFileForSSD(ddRequestLot, appProvider);
		} else {
			generateDDRequestLotFileForSCT(ddRequestLot, appProvider);
		}
	}

	@Override
	public DDRejectFileInfos processSDDRejectedFile(File file) throws BusinessException {
		DDRejectFileInfos ddRejectFileInfos = new DDRejectFileInfos();
		try {
			ddRejectFileInfos.setFileName(file.getName());
			Pain002 pain002 = (Pain002) JAXBUtils.unmarshaller(Pain002.class, file);

			CstmrPmtStsRpt cstmrPmtStsRpt = pain002.getCstmrPmtStsRpt();

			OrgnlGrpInfAndSts orgnlGrpInfAndSts = cstmrPmtStsRpt.getOrgnlGrpInfAndSts();

			if (orgnlGrpInfAndSts == null) {
				throw new BusinessException("OriginalGroupInformationAndStatus tag doesn't exist");
			}

			String dDRequestLOTref = orgnlGrpInfAndSts.getOrgnlMsgId();
			if (dDRequestLOTref == null || dDRequestLOTref.indexOf(DASH_STRING) < 0) {
				throw new BusinessException("Unknown dDRequestLOTref:" + dDRequestLOTref);
			}
			String[] dDRequestLOTrefSplited = dDRequestLOTref.split(DASH_STRING);

			ddRejectFileInfos.setDdRequestLotId(new Long(dDRequestLOTrefSplited[1]));

			if (orgnlGrpInfAndSts.getGrpSts() != null && REJECT_STS_CODE.equals(orgnlGrpInfAndSts.getGrpSts())) {
				ddRejectFileInfos.setTheDDRequestFileWasRejected(true);
				ddRejectFileInfos.setReturnStatusCode(orgnlGrpInfAndSts.getStsRsnInf().getRsn().getCd());
				return ddRejectFileInfos;
			}
			OrgnlPmtInfAndSts orgnlPmtInfAndSts = cstmrPmtStsRpt.getOrgnlPmtInfAndSts();
			for (TxInfAndSts txInfAndSts : orgnlPmtInfAndSts.getTxInfAndSts()) {
				try {
					if (REJECT_STS_CODE.equals(txInfAndSts.getTxSts())) {
						ddRejectFileInfos.getListInvoiceRefsRejected().put(new Long(txInfAndSts.getOrgnlEndToEndId()), REJECT_STS_CODE);
					}
				} catch (Exception e) {
					ddRejectFileInfos.addItemKo();
					log.error("Error on processSDDRejectedFile txInfAndSts.getOrgnlInstrId:" + txInfAndSts.getOrgnlInstrId(), e);
					ddRejectFileInfos.getListErrors().add("Error on processSDDRejectedFile txInfAndSts.getOrgnlInstrId:" + txInfAndSts.getOrgnlInstrId());
				}
			}

		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
		return ddRejectFileInfos;
	}

	@Override
	public DDRejectFileInfos processSCTRejectedFile(File file) throws BusinessException {
		// TODO
		return null;
	}

	/**
	 * Gets the file name.
	 *
	 * @param ddRequestLot        the dd request lot
	 * @param appProvider         the app provider
	 * @param fileNamePrefix      the file name prefix
	 * @param fileNameExtension   the file name extension
	 * @param additionalOutputDir the additional output dir
	 * @return the file name
	 * @throws BusinessException the business exception
	 */
	private String getFileName(DDRequestLOT ddRequestLot, Provider appProvider, String fileNamePrefix, String fileNameExtension, String additionalOutputDir)
			throws BusinessException {
		try {
			ParamBean paramBean = ParamBean.getInstanceByProvider(appProvider.getCode());
			String fileName = fileNamePrefix + ddRequestLot.getId();
			Seller seller = ddRequestLot.getSeller();
			if (seller != null) {
				fileName = fileName + UNDERSCORE_SEPARATOR + seller.getCode();
			} else {
				fileName = fileName + UNDERSCORE_SEPARATOR + appProvider.getCode();
			}

			fileName = fileName + UNDERSCORE_SEPARATOR + DateUtils.formatDateWithPattern(new Date(), SIMPLE_DATE_PATTERN) + fileNameExtension;

			String outputDir = paramBean.getChrootDir(appProvider.getCode());

			outputDir = outputDir + File.separator + additionalOutputDir;
			outputDir = outputDir.replaceAll(DOUBLE_POINT, EMPTY_STRING);

			log.info("DDRequest output directory=" + outputDir);
			File dir = new File(outputDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			return outputDir + File.separator + fileName;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}
	}

	/**
	 * Generate DD request lot file for SSD.
	 *
	 * @param ddRequestLot the dd request lot
	 * @param appProvider  the app provider
	 * @throws BusinessException the business exception
	 */
	private void generateDDRequestLotFileForSSD(DDRequestLOT ddRequestLot, Provider appProvider) throws BusinessException {
		try {
			ParamBean paramBean = ParamBean.getInstanceByProvider(appProvider.getCode());
			Document document = new Document();
			CustomerDirectDebitInitiationV02 message = new CustomerDirectDebitInitiationV02();
			document.setCstmrDrctDbtInitn(message);
			addHeader(message, ddRequestLot, appProvider);
			for (DDRequestItem ddrequestItem : ddRequestLot.getDdrequestItems()) {
				if (!ddrequestItem.hasError()) {
					addPaymentInformation(message, ddrequestItem, appProvider);
				}
			}
			String schemaLocation = paramBean.getProperty("sepa.schemaLocation.pain008", SDD_SCHEMA_LOCATION);
			JAXBUtils.marshaller(document, new File(ddRequestLot.getFileName()), schemaLocation);
		} catch (Exception e) {
			log.error("Error on generateDDRequestLotFileForSSD", e);
			throw new BusinessException(e.getMessage());
		}

	}

	/**
	 * Generate DD request lot file for SCT.
	 *
	 * @param ddRequestLot the dd request lot
	 * @param appProvider  the app provider
	 * @throws BusinessException the business exception
	 */
	private void generateDDRequestLotFileForSCT(DDRequestLOT ddRequestLot, Provider appProvider) throws BusinessException {
		ParamBean paramBean = ParamBean.getInstanceByProvider(appProvider.getCode());

		org.meveo.admin.sepa.jaxb.pain001.Document document;
		CustomerCreditTransferInitiationV03 message;
		List<DDRequestItem> ddrequestItems = ddRequestLot.getDdrequestItems();
		DDRequestItem ddrequestItem;
		Long operationsByFile = ddRequestLot.getDdRequestBuilder().getNbOperationPerFile();
		if (operationsByFile == null || operationsByFile <= 0) {
			operationsByFile = (long) ddrequestItems.size();
		}
		int filesToGenerate = ddrequestItems.size() == 0 ? 0 : (int) Math.ceil(ddrequestItems.size() / (double) operationsByFile);
		int opToGenerateByFile;
		int generatedOps = 0;
		int opWithErrorsByFile;
		BigDecimal totalAmount;
		List<String> generatedFilesNames = new ArrayList<>();
		String fileName;
		for (int fileNumber = 1; fileNumber <= filesToGenerate; fileNumber++) {
			try {
				fileName = getDDFileName(ddRequestLot, appProvider);
				document = new org.meveo.admin.sepa.jaxb.pain001.Document();
				message = new CustomerCreditTransferInitiationV03();
				document.setCstmrCdtTrfInitn(message);
				addSctHeader(message, ddRequestLot, appProvider, fileNumber);
				totalAmount = BigDecimal.ZERO;
				opToGenerateByFile = 0;
				opWithErrorsByFile = 0;
				while (generatedOps < ddrequestItems.size() && opToGenerateByFile < operationsByFile) {
					ddrequestItem = ddrequestItems.get(generatedOps);
					totalAmount = totalAmount.add(ddrequestItem.getAmount());
					if (!ddrequestItem.hasError()) {
						addSctPaymentInformation(message, ddrequestItem, appProvider);
					} else {
						log.error("ddrequestItem with id = " + ddrequestItem.getId() + " has Errors :" + ddrequestItem.getErrorMsg() + ". The file " + fileName
								+ " will not contain all payment informations.");
						opWithErrorsByFile++;
					}
					opToGenerateByFile++;
					generatedOps++;
				}
				if (opToGenerateByFile > opWithErrorsByFile) { // The file is generated only if it contains at least one operation without
																// errors
					message.getGrpHdr().setCtrlSum(totalAmount.setScale(2, RoundingMode.HALF_UP));
					message.getGrpHdr().setNbOfTxs(String.valueOf(opToGenerateByFile));
					// the Pain001 jaxb classes are generated from the xsd located at:
					// https://www.iso20022.org/documents/messages/1_0_version/pain/schemas/pain.001.001.03.zip
					String schemaLocation = paramBean.getProperty("sepa.schemaLocation.pain001", SCT_SCHEMA_LOCATION);

					JAXBUtils.marshaller(document, new File(fileName), schemaLocation);
					generatedFilesNames.add(fileName);
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new BusinessException(e.getMessage());
			}

		}
		ddRequestLot.setFileName(String.join(COMMA_STRING, generatedFilesNames));

	}

	/**
	 * Adds the header of SDD file.
	 *
	 * @param message      the SDD message
	 * @param ddRequestLOT the dd request LOT
	 * @param appProvider  the provider
	 * @throws Exception the exception
	 */
	private void addHeader(CustomerDirectDebitInitiationV02 message, DDRequestLOT ddRequestLOT, Provider appProvider) throws Exception {

		GroupHeader39 groupHeader = new GroupHeader39();
		message.setGrpHdr(groupHeader);
		groupHeader.setMsgId(ArConfig.getDDRequestHeaderReference() + DASH_STRING + ddRequestLOT.getId());
		groupHeader.setCreDtTm(DateUtils.dateToXMLGregorianCalendar(new Date()));
		groupHeader.setNbOfTxs(String.valueOf(ddRequestLOT.getDdrequestItems().size()));
		groupHeader.setCtrlSum(ddRequestLOT.getTotalAmount().setScale(2, RoundingMode.HALF_UP));
		PartyIdentification32 initgPty = new PartyIdentification32();
		if (ddRequestLOT.getSeller() != null) {
			initgPty.setNm(ddRequestLOT.getSeller().getDescription());
		} else {
			initgPty.setNm(appProvider.getDescription());
		}

		groupHeader.setInitgPty(initgPty);

	}

	/**
	 * Adds the payment information of SDD file.
	 *
	 * @param Message       the SDD message
	 * @param dDRequestItem the dd request item
	 * @param appProvider   the provider
	 * @throws Exception the exception
	 */
	private void addPaymentInformation(CustomerDirectDebitInitiationV02 Message, DDRequestItem dDRequestItem, Provider appProvider) throws Exception {

		log.info("addPaymentInformation dDRequestItem id=" + dDRequestItem.getId());
		ParamBean paramBean = ParamBean.getInstanceByProvider(appProvider.getCode());
		PaymentInstructionInformation4 paymentInformation = new PaymentInstructionInformation4();
		Message.getPmtInf().add(paymentInformation);
		paymentInformation.setPmtInfId(ArConfig.getDDRequestHeaderReference() + DASH_STRING + dDRequestItem.getId());
		paymentInformation.setPmtMtd(PaymentMethod2Code.DD);
		paymentInformation.setNbOfTxs(NUMBER_OF_TRANSACTIONS_1);
		paymentInformation.setCtrlSum(dDRequestItem.getAmount().setScale(2, RoundingMode.HALF_UP));
		PaymentTypeInformation20 paymentTypeInformation = new PaymentTypeInformation20();
		paymentInformation.setPmtTpInf(paymentTypeInformation);
		ServiceLevel8Choice serviceLevel = new ServiceLevel8Choice();
		paymentTypeInformation.setSvcLvl(serviceLevel);
		serviceLevel.setCd(SEPA_SERVICE_LEVEL_CD);
		LocalInstrument2Choice localInstrument = new LocalInstrument2Choice();
		paymentTypeInformation.setLclInstrm(localInstrument);
		localInstrument.setCd(paramBean.getProperty("sepa.LclInstrm", SEPA_LOCAL_INSTRUMENT_CODE));
		paymentTypeInformation.setSeqTp(SequenceType1Code.RCUR);

		paymentInformation.setReqdColltnDt(DateUtils.dateToXMLGregorianCalendarFieldUndefined(calendarBankingService.getNextBankWorkingDate(new Date()))); // à revoir

		BankCoordinates bankCoordinates = getBankCoordinates(dDRequestItem.getDdRequestLOT(), appProvider);

		if (bankCoordinates == null) {
			throw new BusinessException("Missing bank information on provider or seller");
		}
		PartyIdentification32 creditor = new PartyIdentification32();
		if (dDRequestItem.getDdRequestLOT().getSeller() != null) {
			creditor.setNm(dDRequestItem.getDdRequestLOT().getSeller().getDescription());
		} else {
			creditor.setNm(appProvider.getDescription());
		}

		paymentInformation.setCdtr(creditor);

		CashAccount16 creditorAccount = new CashAccount16();
		paymentInformation.setCdtrAcct(creditorAccount);
		AccountIdentification4Choice identification = new AccountIdentification4Choice();
		creditorAccount.setId(identification);
		identification.setIBAN(bankCoordinates.getIban());

		BranchAndFinancialInstitutionIdentification4 creditorAgent = new BranchAndFinancialInstitutionIdentification4();
		paymentInformation.setCdtrAgt(creditorAgent);
		FinancialInstitutionIdentification7 financialInstitutionIdentification = new FinancialInstitutionIdentification7();
		creditorAgent.setFinInstnId(financialInstitutionIdentification);
		financialInstitutionIdentification.setBIC(bankCoordinates.getBic());
		PartyIdentification32 creditorSchemeIdentification = new PartyIdentification32();
		paymentInformation.setCdtrSchmeId(creditorSchemeIdentification);
		Party6Choice cdtrSchmeId = new Party6Choice();
		creditorSchemeIdentification.setId(cdtrSchmeId);
		PersonIdentification5 privateidentifier = new PersonIdentification5();
		cdtrSchmeId.setPrvtId(privateidentifier);
		GenericPersonIdentification1 other = new GenericPersonIdentification1();
		privateidentifier.getOthr().add(other);
		other.setId(bankCoordinates.getIcs());
		PersonIdentificationSchemeName1Choice schemeName = new PersonIdentificationSchemeName1Choice();
		other.setSchmeNm(schemeName);
		schemeName.setPrtry(SEPA_SERVICE_LEVEL_CD);
		addTransaction(dDRequestItem, paymentInformation);
	}

	/**
	 * Adds the transaction of SDD file.
	 *
	 * @param dDRequestItem      the dd request item
	 * @param paymentInformation the payment information of SDD file
	 * @throws Exception the exception
	 */
	private void addTransaction(DDRequestItem dDRequestItem, PaymentInstructionInformation4 paymentInformation) throws Exception {
		CustomerAccount ca = dDRequestItem.getAccountOperations().get(0).getCustomerAccount();
		PaymentMethod preferedPaymentMethod = ca.getPreferredPaymentMethod();
		if (preferedPaymentMethod == null || !(preferedPaymentMethod instanceof DDPaymentMethod)) {
			throw new BusinessException("Payment method not valid!");
		}
		BankCoordinates bankCoordinates = ((DDPaymentMethod) preferedPaymentMethod).getBankCoordinates();

		if (bankCoordinates == null) {
			throw new BusinessException("Bank Coordinate is absent for Payment method " + ((DDPaymentMethod) preferedPaymentMethod).getAlias());
		}

		DirectDebitTransactionInformation9 directDebitTransactionInformation = new DirectDebitTransactionInformation9();
		paymentInformation.getDrctDbtTxInf().add(directDebitTransactionInformation);
		PaymentIdentification1 PaymentIdentification = new PaymentIdentification1();
		directDebitTransactionInformation.setPmtId(PaymentIdentification);
		PaymentIdentification.setInstrId(String.valueOf(dDRequestItem.getId()));
		PaymentIdentification.setEndToEndId(String.valueOf(dDRequestItem.getId()));
		ActiveOrHistoricCurrencyAndAmount instructedAmount = new ActiveOrHistoricCurrencyAndAmount();
		directDebitTransactionInformation.setInstdAmt(instructedAmount);
		instructedAmount.setValue(dDRequestItem.getAmount().setScale(2, RoundingMode.HALF_UP));
		instructedAmount.setCcy(EURO_CCY);
		DirectDebitTransaction6 directDebitTransaction = new DirectDebitTransaction6();
		directDebitTransactionInformation.setDrctDbtTx(directDebitTransaction);
		MandateRelatedInformation6 mandateRelatedInformation = new MandateRelatedInformation6();
		directDebitTransaction.setMndtRltdInf(mandateRelatedInformation);

		mandateRelatedInformation.setMndtId(((DDPaymentMethod) preferedPaymentMethod).getMandateIdentification());
		mandateRelatedInformation.setDtOfSgntr(DateUtils.dateToXMLGregorianCalendarFieldUndefined(((DDPaymentMethod) preferedPaymentMethod).getMandateDate()));
		BranchAndFinancialInstitutionIdentification4 debtorAgent = new BranchAndFinancialInstitutionIdentification4();
		directDebitTransactionInformation.setDbtrAgt(debtorAgent);
		FinancialInstitutionIdentification7 financialInstitutionIdentification = new FinancialInstitutionIdentification7();
		financialInstitutionIdentification.setBIC(bankCoordinates.getBic());
		debtorAgent.setFinInstnId(financialInstitutionIdentification);

		PartyIdentification32 debtor = new PartyIdentification32();
		directDebitTransactionInformation.setDbtr(debtor);
		debtor.setNm(ca.getDescription());

		CashAccount16 debtorAccount = new CashAccount16();
		directDebitTransactionInformation.setDbtrAcct(debtorAccount);
		AccountIdentification4Choice identification = new AccountIdentification4Choice();
		identification.setIBAN(bankCoordinates.getIban());
		debtorAccount.setId(identification);

	}

	/**
	 * Adds the payment information for SCT file.
	 *
	 * @param message       the SCT message
	 * @param ddrequestItem the ddrequest item
	 * @param appProvider   the provider
	 * @throws Exception the exception
	 */
	private void addSctPaymentInformation(CustomerCreditTransferInitiationV03 message, DDRequestItem ddrequestItem, Provider appProvider) throws Exception {

		log.info("addPaymentInformation dDRequestItem id=" + ddrequestItem.getId());

		PaymentInstructionInformation3 paymentInformation = new PaymentInstructionInformation3();
		message.getPmtInf().add(paymentInformation);
		paymentInformation.setPmtInfId(ArConfig.getDDRequestHeaderReference() + DASH_STRING + ddrequestItem.getId());
		paymentInformation.setPmtMtd(PaymentMethod3Code.TRF);
		paymentInformation.setBtchBookg(true);
		paymentInformation.setNbOfTxs(String.valueOf(ddrequestItem.getAccountOperations().size()));
		paymentInformation.setCtrlSum(ddrequestItem.getAmount().setScale(2, RoundingMode.HALF_UP));
		PaymentTypeInformation19 paymentTypeInformation = new PaymentTypeInformation19();
		paymentInformation.setPmtTpInf(paymentTypeInformation);
		org.meveo.admin.sepa.jaxb.pain001.ServiceLevel8Choice serviceLevel = new org.meveo.admin.sepa.jaxb.pain001.ServiceLevel8Choice();
		paymentTypeInformation.setSvcLvl(serviceLevel);
		serviceLevel.setCd(SEPA_SERVICE_LEVEL_CD);
		org.meveo.admin.sepa.jaxb.pain001.CategoryPurpose1Choice ctgyPurp = new org.meveo.admin.sepa.jaxb.pain001.CategoryPurpose1Choice();
		paymentTypeInformation.setCtgyPurp(ctgyPurp);
		ctgyPurp.setCd(CATEGORY_PURPOSE_CODE);

		paymentInformation.setReqdExctnDt(DateUtils.dateToXMLGregorianCalendarFieldUndefined(calendarBankingService.getNextBankWorkingDate(new Date())));

		org.meveo.admin.sepa.jaxb.pain001.PartyIdentification32 dbtr = new org.meveo.admin.sepa.jaxb.pain001.PartyIdentification32();
		if (ddrequestItem.getDdRequestLOT().getSeller() != null) {
			dbtr.setNm(ddrequestItem.getDdRequestLOT().getSeller().getDescription());
		} else {
			dbtr.setNm(appProvider.getDescription());
		}

		paymentInformation.setDbtr(dbtr);

		org.meveo.admin.sepa.jaxb.pain001.CashAccount16 dbtrAccount = new org.meveo.admin.sepa.jaxb.pain001.CashAccount16();
		paymentInformation.setDbtrAcct(dbtrAccount);
		org.meveo.admin.sepa.jaxb.pain001.AccountIdentification4Choice identification = new org.meveo.admin.sepa.jaxb.pain001.AccountIdentification4Choice();
		dbtrAccount.setId(identification);

		BankCoordinates bankCoordinates = getBankCoordinates(ddrequestItem.getDdRequestLOT(), appProvider);

		if (bankCoordinates == null) {
			throw new BusinessException("Missing bank information on provider");
		}
		// iban pattern
		if (!isMatched(bankCoordinates.getIban(), IBAN_PATTERN)) {
			throw new BusinessException("IBAN not valid!");
		}
		identification.setIBAN(bankCoordinates.getIban());

		dbtrAccount.setCcy(EURO_CCY);
		org.meveo.admin.sepa.jaxb.pain001.BranchAndFinancialInstitutionIdentification4 dbtrAgent = new org.meveo.admin.sepa.jaxb.pain001.BranchAndFinancialInstitutionIdentification4();
		paymentInformation.setDbtrAgt(dbtrAgent);
		org.meveo.admin.sepa.jaxb.pain001.FinancialInstitutionIdentification7 financialInstitutionIdentification = new org.meveo.admin.sepa.jaxb.pain001.FinancialInstitutionIdentification7();
		dbtrAgent.setFinInstnId(financialInstitutionIdentification);
		if (StringUtils.isBlank(bankCoordinates.getBic())) {
			org.meveo.admin.sepa.jaxb.pain001.GenericFinancialIdentification1 othr = new org.meveo.admin.sepa.jaxb.pain001.GenericFinancialIdentification1();
			othr.setId(NOTPROVIDED_BIC);
			financialInstitutionIdentification.setOthr(othr);
		}

		financialInstitutionIdentification.setBIC(bankCoordinates.getBic());
		paymentInformation.setChrgBr(org.meveo.admin.sepa.jaxb.pain001.ChargeBearerType1Code.SLEV);
		addSctTransaction(ddrequestItem, paymentInformation);
	}

	/**
	 * Adds the header for SCT file.
	 *
	 * @param message      the SCT message
	 * @param ddRequestLot the dd request lot
	 * @param appProvider  the provider
	 * @param fileNumber   the file number: used when generatin severals files for
	 *                     one ddRequest lot
	 * @throws Exception the exception
	 */
	private void addSctHeader(CustomerCreditTransferInitiationV03 message, DDRequestLOT ddRequestLot, Provider appProvider, int fileNumber) throws Exception {
		GroupHeader32 groupHeader = new GroupHeader32();
		message.setGrpHdr(groupHeader);
		groupHeader.setMsgId(ArConfig.getSCTRequestHeaderRefrence() + DASH_STRING + ddRequestLot.getId() + DASH_STRING + fileNumber);
		groupHeader.setCreDtTm(DateUtils.dateToXMLGregorianCalendar(new Date()));
		org.meveo.admin.sepa.jaxb.pain001.PartyIdentification32 initgPty = new org.meveo.admin.sepa.jaxb.pain001.PartyIdentification32();
		initgPty.setNm(appProvider.getDescription());
		groupHeader.setInitgPty(initgPty);

	}

	/**
	 * Adds the transaction for SCT file.
	 *
	 * @param ao                 the account operation
	 * @param paymentInformation the payment information of SCT file
	 * @throws Exception the exception
	 */
	private void addSctTransaction(DDRequestItem dDRequestItem, PaymentInstructionInformation3 paymentInformation) throws Exception {
		CustomerAccount ca = dDRequestItem.getAccountOperations().get(0).getCustomerAccount();
		PaymentMethod preferedPaymentMethod = ca.getPreferredPaymentMethod();
		if (preferedPaymentMethod == null || !(preferedPaymentMethod instanceof DDPaymentMethod)) {
			throw new BusinessException("Payment method not valid!");
		}
		BankCoordinates bankCoordinates = ((DDPaymentMethod) preferedPaymentMethod).getBankCoordinates();
		if (bankCoordinates == null) {
			throw new BusinessException("Bank Coordinate is absent for Payment method " + ((DDPaymentMethod) preferedPaymentMethod).getAlias());
		}

		CreditTransferTransactionInformation10 cdtTrfTxInf = new CreditTransferTransactionInformation10();
		paymentInformation.getCdtTrfTxInf().add(cdtTrfTxInf);
		org.meveo.admin.sepa.jaxb.pain001.PaymentIdentification1 paymentIdentification = new org.meveo.admin.sepa.jaxb.pain001.PaymentIdentification1();
		cdtTrfTxInf.setPmtId(paymentIdentification);
		paymentIdentification.setInstrId(String.valueOf(dDRequestItem.getDdRequestLOT().getId()));
		paymentIdentification.setEndToEndId("" + dDRequestItem.getId());

		AmountType3Choice amt = new AmountType3Choice();
		cdtTrfTxInf.setAmt(amt);
		org.meveo.admin.sepa.jaxb.pain001.ActiveOrHistoricCurrencyAndAmount instdAmt = new org.meveo.admin.sepa.jaxb.pain001.ActiveOrHistoricCurrencyAndAmount();

		instdAmt.setValue(dDRequestItem.getAmount().setScale(2, RoundingMode.HALF_UP));
		instdAmt.setCcy(EURO_CCY);
		amt.setInstdAmt(instdAmt);

		org.meveo.admin.sepa.jaxb.pain001.BranchAndFinancialInstitutionIdentification4 cdtrAgent = new org.meveo.admin.sepa.jaxb.pain001.BranchAndFinancialInstitutionIdentification4();
		cdtTrfTxInf.setCdtrAgt(cdtrAgent);
		org.meveo.admin.sepa.jaxb.pain001.FinancialInstitutionIdentification7 finInstnId = new org.meveo.admin.sepa.jaxb.pain001.FinancialInstitutionIdentification7();
		finInstnId.setBIC(bankCoordinates.getBic());
		cdtrAgent.setFinInstnId(finInstnId);
		org.meveo.admin.sepa.jaxb.pain001.PartyIdentification32 cdtr = new org.meveo.admin.sepa.jaxb.pain001.PartyIdentification32();
		cdtTrfTxInf.setCdtr(cdtr);
		cdtr.setNm(ca.getDescription());
		cdtr.setCtryOfRes(FR_COUNTRY);
		org.meveo.admin.sepa.jaxb.pain001.CashAccount16 cdtrAccount = new org.meveo.admin.sepa.jaxb.pain001.CashAccount16();
		cdtTrfTxInf.setCdtrAcct(cdtrAccount);
		org.meveo.admin.sepa.jaxb.pain001.AccountIdentification4Choice identification = new org.meveo.admin.sepa.jaxb.pain001.AccountIdentification4Choice();
		if (!isMatched(bankCoordinates.getIban(), IBAN_PATTERN)) {
			throw new BusinessException("IBAN of the creditor account is not valid!");
		}
		identification.setIBAN(bankCoordinates.getIban());
		cdtrAccount.setId(identification);

		RemittanceInformation5 rmtInf = new RemittanceInformation5();
		cdtTrfTxInf.setRmtInf(rmtInf);
		rmtInf.getUstrd().add("Remboursement " + dDRequestItem.getReference());
	}

	/**
	 * Checks if is matched.
	 *
	 * @param field    the field to validate : character sequence to be matched
	 * @param sPattern The expression to be compiled for regEx pattern
	 * @return true if, and only if, the field matches the matcher s pattern
	 */
	private boolean isMatched(String field, String sPattern) {
		if (field == null || sPattern == null) {
			return false;
		}
		Pattern pattern = Pattern.compile(sPattern);
		Matcher matcher = pattern.matcher(field);
		return matcher.matches();
	}

	private BankCoordinates getBankCoordinates(DDRequestLOT ddRequestLOT, Provider appProvider) throws BusinessException {
		if (ddRequestLOT.getSeller() != null) {
			PaymentGatewayService paymentGatewayService = (PaymentGatewayService) getServiceInterface("PaymentGatewayService");
			PaymentGateway paymentGateway = paymentGatewayService.getPaymentGateway(ddRequestLOT.getSeller(), PaymentMethodEnum.DIRECTDEBIT);
			if (paymentGateway == null) {
				throw new BusinessException("Cant find payment gateway for seller : " + ddRequestLOT.getSeller());
			}
			return paymentGateway.getBankCoordinates();
		} else {
			return appProvider.getBankCoordinates();
		}
	}

}