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
package org.meveo.admin.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.commons.utils.ParamBean;
import org.meveo.model.bi.OutputFormatEnum;
import org.meveo.model.bi.Report;
import org.meveo.model.datawarehouse.DWHAccountOperation;
import org.meveo.service.reporting.impl.DWHAccountOperationService;
import org.slf4j.Logger;

@Named
public class AccountingJournal extends FileProducer implements Reporting {
	@Inject
	protected Logger log;

	@Inject
	private DWHAccountOperationService accountOperationService;

	private String reportsFolder;

	private String separator;

	private String templateFilename;
	public Map<String, Object> parameters = new HashMap<String, Object>();

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	public void generateJournalFile(String providerCode, Date startDate, Date endDate,
			OutputFormatEnum outputFormat) {
		try {
			File file = null;
			if (outputFormat == OutputFormatEnum.PDF) {
				file = File.createTempFile("tempAccountingJournal", ".csv");
			} else if (outputFormat == OutputFormatEnum.CSV) {
				StringBuilder sb = new StringBuilder(getFilename(providerCode, startDate, endDate));
				sb.append(".csv");
				file = new File(sb.toString());
			}
			FileWriter writer = new FileWriter(file);
			writer.append("Date G.L.;Code operation;Libele operation;No de client;Ste;CG;CA;DA;CR;IC;GP;Debit;Credit");
			writer.append('\n');
			List<DWHAccountOperation> records = accountOperationService
					.getAccountingJournalRecords(providerCode, startDate, endDate);
			for (DWHAccountOperation operation : records) {
				// first line
				writer.append(sdf.format(operation.getTransactionDate()) + ";");// operation
				// Date
				writer.append(operation.getOccCode() + ";");// operation Code
				writer.append(operation.getOccDescription() + ";");// operation
				// Description
				writer.append(operation.getAccountCode() + ";");// customerAccountCode
				if (operation.getAccountingCode() != null) {// accountingCode
					// (debit)
					writer.append(operation.getAccountingCode().toString()
							.replace(separator.toCharArray()[0], ';')
							+ ";");
				} else {
					writer.append("00000;00000;0000;000;0000;00000000;00000;");
				}
				if (operation.getCategory() == 0) {// case debit
					writer.append((operation.getAmount() + ";").replace('.', ','));// amount
					// Debit
					writer.append(";");// amount Credit
				} else {
					writer.append(";");// amount Debit
					writer.append((operation.getAmount() + ";").replace('.', ','));// amount
					// Credit
				}
				writer.append('\n');

				// line client side
				writer.append(sdf.format(operation.getTransactionDate()) + ";");// operation
				// Date
				writer.append(operation.getOccCode() + ";");// operation Code
				writer.append(operation.getOccDescription() + ";");// operation
				// Description
				writer.append(operation.getAccountCode() + ";");// customerAccountCode
				if (operation.getAccountingCodeClientSide() != null) {// accountingCode
					// (debit)
					writer.append(operation.getAccountingCodeClientSide().toString()
							.replace(separator.toCharArray()[0], ';')
							+ ";");
				} else {
					writer.append("00000;00000;0000;000;0000;00000000;00000;");
				}
				if (operation.getCategory() == 0) {// case debit
					writer.append(";");// amount Debit
					writer.append((operation.getAmount() + ";").replace('.', ','));// amount
					// Credit
				} else {
					writer.append((operation.getAmount() + ";").replace('.', ','));// amount
					// Debit
					writer.append(";");// amount Credit
				}
				writer.append('\n');
			}
			// then write invoices

			writer.flush();
			writer.close();
			if (outputFormat == OutputFormatEnum.PDF) {
				parameters.put("startDate", startDate);
				parameters.put("endDate", endDate);
				parameters.put("provider", providerCode);
				StringBuilder sb = new StringBuilder(getFilename(providerCode, startDate, endDate));
				sb.append(".pdf");
				generatePDFfile(file, sb.toString(), templateFilename, parameters);
			}
		} catch (IOException e) {
			log.error("failed to generate journal file ",e);
		}
	}

	public String getFilename(String providerName, Date startDate, Date endDate) {

		String DATE_FORMAT = "dd-MM-yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		StringBuilder sb = new StringBuilder();
		sb.append(reportsFolder);
		sb.append(providerName);
		sb.append("_JOURNAL_TRESO_");
		sb.append(sdf.format(new Date()).toString());
		sb.append("_du_");
		sb.append(sdf.format(startDate).toString());
		sb.append("_au_");
		sb.append(sdf.format(endDate).toString());
		return sb.toString();
	}

	public void export(Report report) {
		ParamBean param = ParamBean.getInstance();
		reportsFolder = param.getProperty("reportsURL","/opt/jboss/files/reports/");
		separator = param.getProperty("reporting.accountingCode.separator",",");
		String jasperTemplatesFolder = param.getProperty("reports.jasperTemplatesFolder","/opt/jboss/files/reports/JasperTemplates/");
		templateFilename = jasperTemplatesFolder + "accountingJournal.jasper";
		generateJournalFile(report.getProvider() == null ? null : report.getProvider().getCode(),
				report.getStartDate(), report.getEndDate(), report.getOutputFormat());

	}

}
