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
package org.meveo.admin.sepa;

//import java.text.Normalizer;
import java.math.RoundingMode;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
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
import org.meveo.admin.util.ArConfig;
import org.meveo.commons.utils.ParamBean;
//import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.shared.DateUtils;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

@Stateless
public class SepaFileBuilder {


  
}
