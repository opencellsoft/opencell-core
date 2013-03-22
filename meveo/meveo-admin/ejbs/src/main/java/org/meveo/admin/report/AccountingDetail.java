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
package org.meveo.admin.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.bi.OutputFormatEnum;
import org.meveo.model.bi.Report;
import org.meveo.model.datawarehouse.DWHAccountOperation;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.reporting.impl.DWHAccountOperationService;
import org.slf4j.Logger;

@Named
public class AccountingDetail extends FileProducer implements Reporting {
	@Inject
	protected Logger log;

	@Inject
	private CustomerAccountService customerAccountService;
	
	@Inject
	private DWHAccountOperationService accountOperationTransformationService;
	
	@Inject
	private AccountOperationService accountOperationService;

	private String reportsFolder;
	private String templateFilename;
	public Map<String, Object> parameters = new HashMap<String, Object>();
	public HashMap<String, BigDecimal> balances = new HashMap<String, BigDecimal>();
	public HashMap<String, String> customerNames = new HashMap<String, String>();

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	public void generateAccountingDetailFile(String providerCode, Date startDate, Date endDate,
			OutputFormatEnum outputFormat) {
		try {
			File file = null;
			if (outputFormat == OutputFormatEnum.PDF) {
				file = File.createTempFile("tempAccountingDetail", ".csv");
			} else if (outputFormat == OutputFormatEnum.CSV) {
				StringBuilder sb = new StringBuilder(getFilename(providerCode));
				sb.append(".csv");
				file = new File(sb.toString());
			}
			FileWriter writer = new FileWriter(file);
			writer.append("N° compte client;Nom du compte client;Code operation;Référence comptable;Date de l'opération;Date d'exigibilité;Débit;Credit;Solde client");
			writer.append('\n');
			List<DWHAccountOperation> list = accountOperationTransformationService
					.getAccountingDetailRecords(providerCode, new Date());
			Iterator<DWHAccountOperation> itr = list.iterator();
			String previousAccountCode = null;
			BigDecimal solde = BigDecimal.ZERO;
			BigDecimal amount = BigDecimal.ZERO;
			while (itr.hasNext()) {
				DWHAccountOperation accountOperationTransformation = itr.next();
				if (previousAccountCode != null) {
					if (!previousAccountCode
							.equals(accountOperationTransformation.getAccountCode())) {
						writer.append(String.valueOf(solde).replace('.', ','));
						solde = BigDecimal.ZERO;
					}
					writer.append('\n');
				}
				if (accountOperationTransformation.getStatus() == 2) {
					AccountOperation accountOperation = accountOperationService
							.findById(accountOperationTransformation.getId());
					amount = accountOperation.getUnMatchingAmount();
				} else {
					amount = accountOperationTransformation.getAmount();
				}

				amount = accountOperationTransformation.getAmount();
				if (accountOperationTransformation.getCategory() == 1) {
					solde = solde.subtract(amount);
				} else {
					solde = solde.add(amount);
				}
				previousAccountCode = accountOperationTransformation.getAccountCode();
				writer.append(accountOperationTransformation.getAccountCode() + ";"); // Num
																						// compte
																						// client
				writer.append(accountOperationTransformation.getAccountDescription() + ";");
				writer.append(accountOperationTransformation.getOccCode() + ";");
				writer.append(accountOperationTransformation.getReference() + ";");
				writer.append(sdf.format(accountOperationTransformation.getTransactionDate()) + ";");
				writer.append(sdf.format(accountOperationTransformation.getDueDate()) + ";");
				if (accountOperationTransformation.getCategory() == 0)
					writer.append((amount + ";").replace('.', ','));
				else
					writer.append("0;");
				if (accountOperationTransformation.getCategory() == 1)
					writer.append((amount + ";").replace('.', ','));
				else
					writer.append("0;");

			}
			writer.append(String.valueOf(solde).replace('.', ','));
			writer.append('\n');

			writer.flush();
			writer.close();
			if (outputFormat == OutputFormatEnum.PDF) {
				parameters.put("startDate", startDate);
				parameters.put("endDate", endDate);
				parameters.put("provider", providerCode);

				StringBuilder sb = new StringBuilder(getFilename(providerCode));
				sb.append(".pdf");
				generatePDFfile(file, sb.toString(), templateFilename, parameters);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getCustomerName(String customerAccountCode) {
		String result = "";
		if (customerNames.containsKey(customerAccountCode)) {
			result = customerNames.get(customerAccountCode);
		} else {
			CustomerAccount account = customerAccountService.findByCode(customerAccountCode);
			if (account.getName() != null) {
				result = account.getName().getTitle().getCode();
				if (account.getName().getFirstName() != null) {
					result += " " + account.getName().getFirstName();
				}
				if (account.getName().getLastName() != null) {
					result += " " + account.getName().getLastName();
				}
			}
		}
		return result;
	}

	public BigDecimal getCustomerBalanceDue(String customerAccountCode, Date atDate) {
		BigDecimal result = BigDecimal.ZERO;
		if (balances.containsKey(customerAccountCode)) {
			result = balances.get(customerAccountCode);
		} else {
			try {
				result = customerAccountService.customerAccountBalanceDue(null,
						customerAccountCode, atDate);
				balances.put(customerAccountCode, result);
			} catch (BusinessException e) {
				log.error("Error while getting balance dues", e);
			}
		}
		return result;
	}

	public String getFilename(String providerName) {

		String DATE_FORMAT = "dd-MM-yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		StringBuilder sb = new StringBuilder();
		sb.append(reportsFolder);
		sb.append(providerName + "_");
		sb.append("INVENTAIRE_CCLIENT_");
		sb.append(sdf.format(new Date()).toString());
		return sb.toString();
	}

	public void export(Report report) {
		ParamBean param = ParamBean.getInstance("meveo-admin.properties");
		reportsFolder = param.getProperty("reportsURL");
		String jasperTemplatesFolder = param.getProperty("reports.jasperTemplatesFolder");
		templateFilename = jasperTemplatesFolder + "accountingDetail.jasper";
		accountOperationTransformationService = null;
		customerAccountService = null; 
		accountOperationService = null; 

		generateAccountingDetailFile(report.getProvider() == null ? null : report.getProvider()
				.getCode(), report.getStartDate(), report.getEndDate(), report.getOutputFormat());

	}

}
