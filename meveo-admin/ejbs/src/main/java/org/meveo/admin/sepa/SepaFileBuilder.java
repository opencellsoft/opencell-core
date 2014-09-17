/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.admin.sepa;

//import java.text.Normalizer;
import java.math.RoundingMode;
import java.util.Date;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.GrpHdr;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.GrpHdr.InitgPty;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.Cdtr;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrAcct;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrAcct.Id;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrAgt;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrAgt.FinInstnId;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrSchmeId;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrSchmeId.Id.PrvtId;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrSchmeId.Id.PrvtId.Othr;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrSchmeId.Id.PrvtId.Othr.SchmeNm;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.Dbtr;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DbtrAcct;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DbtrAgt;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DrctDbtTx;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DrctDbtTx.MndtRltdInf;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.InstdAmt;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.PmtId;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.PmtTpInf;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.PmtTpInf.LclInstrm;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.PmtTpInf.SvcLvl;
import org.meveo.admin.utils.ArConfig;
//import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.shared.DateUtils;


@Stateless @LocalBean
public class SepaFileBuilder {

	private static final Logger logger = Logger.getLogger(SepaFileBuilder.class.getName());


	public void addHeader(CstmrDrctDbtInitn Message,  DDRequestLOT ddRequestLOT) throws Exception {
		GrpHdr  groupHeader=new GrpHdr();
		Message.setGrpHdr(groupHeader);
		groupHeader.setMsgId(ArConfig.getDDRequestHeaderReference()+"-"+ddRequestLOT.getId());
		groupHeader.setCreDtTm(DateUtils.dateToXMLGregorianCalendar(new Date()));
		groupHeader.setNbOfTxs(ddRequestLOT.getInvoicesNumber());
		groupHeader.setCtrlSum(ddRequestLOT.getInvoicesAmount().setScale(2, RoundingMode.HALF_UP));
		InitgPty initgPty=new InitgPty();
		initgPty.setNm(ddRequestLOT.getProvider().getDescription());
		groupHeader.setInitgPty(initgPty);
		
	}

	public void addPaymentInformation(CstmrDrctDbtInitn Message,DDRequestItem dDRequestItem) throws Exception {
		
		logger.info("addPaymentInformation dDRequestItem id="+dDRequestItem.getId());
		Provider provider=dDRequestItem.getProvider();
		
		PmtInf PaymentInformation=new PmtInf();
		Message.getPmtInf().add(PaymentInformation);
		PaymentInformation.setPmtInfId(ArConfig.getDDRequestHeaderReference()+"-"+dDRequestItem.getId());
		PaymentInformation.setPmtMtd("DD");
		PaymentInformation.setNbOfTxs(dDRequestItem.getInvoices().size());
		PaymentInformation.setCtrlSum(dDRequestItem.getAmountInvoices().setScale(2, RoundingMode.HALF_UP));
		PmtTpInf PaymentTypeInformation=new PmtTpInf();
		PaymentInformation.setPmtTpInf(PaymentTypeInformation);
		SvcLvl ServiceLevel=new SvcLvl();
		PaymentTypeInformation.setSvcLvl(ServiceLevel);
		ServiceLevel.setCd("SEPA");
		LclInstrm LocalInstrument=new LclInstrm();
		PaymentTypeInformation.setLclInstrm(LocalInstrument);
		LocalInstrument.setCd("CORE");
		PaymentTypeInformation.setSeqTp("FRST");
		
		PaymentInformation.setReqdColltnDt(DateUtils.dateToXMLGregorianCalendar(new Date())); //à revoir
		
		BankCoordinates providerBC=provider.getBankCoordinates();
		Cdtr Creditor=new Cdtr();
		PaymentInformation.setCdtr(Creditor);
		Creditor.setNm(Creditor.getNm());
		CdtrAcct CreditorAccount=new CdtrAcct();
		PaymentInformation.setCdtrAcct(CreditorAccount);
		Id Identification=new Id();
		CreditorAccount.setId(Identification);
		Identification.setIBAN(providerBC.getIban());
		
		CdtrAgt CreditorAgent=new CdtrAgt();
		PaymentInformation.setCdtrAgt(CreditorAgent);
		FinInstnId FinancialInstitutionIdentification=new FinInstnId();
		CreditorAgent.setFinInstnId(FinancialInstitutionIdentification);
		FinancialInstitutionIdentification.setBIC(providerBC.getBic());
		CdtrSchmeId CreditorSchemeIdentification=new CdtrSchmeId();
		PaymentInformation.setCdtrSchmeId(CreditorSchemeIdentification);
		org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrSchmeId.Id CdtrSchmeId=new org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrSchmeId.Id();
		CreditorSchemeIdentification.setId(CdtrSchmeId);
		PrvtId privateidentifier=new PrvtId();
		CdtrSchmeId.setPrvtId(privateidentifier);
		Othr other=new Othr();
		privateidentifier.setOthr(other);
		other.setId(providerBC.getIcs());
		SchmeNm SchemeName=new SchmeNm();
		other.setSchmeNm(SchemeName);
		SchemeName.setPrtry("SEPA");
		for(RecordedInvoice invoice:dDRequestItem.getInvoices()){
			addTransaction(invoice, PaymentInformation);
		}
	
	
	}

	public void addTransaction(RecordedInvoice invoice,PmtInf PaymentInformation) throws Exception {
		CustomerAccount ca=invoice.getCustomerAccount();
		
		DrctDbtTxInf DirectDebitTransactionInformation=new DrctDbtTxInf();
		PaymentInformation.getDrctDbtTxInf().add(DirectDebitTransactionInformation);
		PmtId PaymentIdentification=new PmtId();
		DirectDebitTransactionInformation.setPmtId(PaymentIdentification);
		PaymentIdentification.setInstrId(invoice.getReference());
		PaymentIdentification.setEndToEndId(invoice.getReference());
		InstdAmt InstructedAmount=new InstdAmt();
		DirectDebitTransactionInformation.setInstdAmt(InstructedAmount);
		InstructedAmount.setValue(invoice.getAmount().setScale(2, RoundingMode.HALF_UP));
		InstructedAmount.setCcy("EUR");
		DrctDbtTx DirectDebitTransaction=new DrctDbtTx();
		DirectDebitTransactionInformation.setDrctDbtTx(DirectDebitTransaction);
		MndtRltdInf MandateRelatedInformation=new MndtRltdInf();
		DirectDebitTransaction.setMndtRltdInf(MandateRelatedInformation);
		MandateRelatedInformation.setMndtId(ca.getMandateIdentification());
		MandateRelatedInformation.setDtOfSgntr(DateUtils.dateToXMLGregorianCalendar(ca.getMandateDate()));
		
		DbtrAgt DebtorAgent=new DbtrAgt();
		DirectDebitTransactionInformation.setDbtrAgt(DebtorAgent);
		org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DbtrAgt.FinInstnId FinancialInstitutionIdentification=new org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DbtrAgt.FinInstnId();
		FinancialInstitutionIdentification.setBIC(invoice.getPaymentInfo6());
		DebtorAgent.setFinInstnId(FinancialInstitutionIdentification);
		
		Dbtr Debtor=new Dbtr();
		DirectDebitTransactionInformation.setDbtr(Debtor);
		Debtor.setNm(ca.getDescription());
		
		DbtrAcct DebtorAccount=new DbtrAcct();
		DirectDebitTransactionInformation.setDbtrAcct(DebtorAccount);
		org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DbtrAcct.Id Identification=new org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DbtrAcct.Id();
		Identification.setIBAN(invoice.getPaymentInfo());
		DebtorAccount.setId(Identification);
			
	}


	/*private String enleverAccent(String value) {
		if (StringUtils.isBlank(value)) {
			return value;
		}
		return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");
	}*/
}
