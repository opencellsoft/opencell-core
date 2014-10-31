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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.commons.utils.ParamBean;
import org.meveo.model.bi.OutputFormatEnum;
import org.meveo.model.bi.Report;
import org.meveo.service.reporting.impl.JournalEntryService;
import org.slf4j.Logger;

@Named
public class Journal extends FileProducer implements Reporting {

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	@Inject
	protected Logger log;

	@Inject
	private JournalEntryService journalEntryService;

	private String reportsFolder;
	private String templateFilename;
	public Map<String, Object> parameters = new HashMap<String, Object>();

	private String separator;

	public void generateJournalFile(String providerCode, Date startDate, Date endDate,
			OutputFormatEnum outputFormat) {
		try {
			File file = null;
			if (outputFormat == OutputFormatEnum.PDF) {
				file = File.createTempFile("tempJournal", ".csv");
			} else if (outputFormat == OutputFormatEnum.CSV) {
				StringBuilder sb = new StringBuilder(getFilename(providerCode, startDate, endDate));
				sb.append(".csv");
				file = new File(sb.toString());
			}
			FileWriter writer = new FileWriter(file);
			writer.append("Date G.L.;No de Facture;No de client;Ste;CG;CA;DA;CR;IC;GP;Debit;Credit");
			writer.append('\n');
			List<Object> records = journalEntryService.getJournalRecords(providerCode, startDate,
					endDate);
			Iterator<Object> itr = records.iterator();
			while (itr.hasNext()) {
				Object[] row = (Object[]) itr.next();
				String type = row[0] + "";
				// writer.append(row[0] + ";");
				writer.append(row[1] == null ? "" : sdf.format(row[1]) + ";");// invoiceDate
				writer.append(row[2] + ";");// invoiceNumber
				writer.append(row[3] + ";");// customerAccountCode
				if (row[4] != null) {// accountingCode
					writer.append(row[4].toString().replace(separator.toCharArray()[0], ';') + ";");
				} else {
					writer.append("00000;00000;0000;000;0000;00000000;00000;");
				}
				String credit = "";
				String debit = "";
				if ("I".equals(type)) {
					debit = row[7] + "";// amount with tax
				} else if ("T".equals(type)) {
					credit = row[6] + "";// amount tax
				} else {
					credit = row[5] + "";// amount without tax
				}
				writer.append(debit.replace('.', ',') + ";");// amount Debit
				writer.append(credit.replace('.', ','));// amount Credit
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
			e.printStackTrace();
		}
	}

	public String getFilename(String providerName, Date startDate, Date endDate) {

		String DATE_FORMAT = "dd-MM-yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		StringBuilder sb = new StringBuilder();
		sb.append(reportsFolder);
		if (providerName != null) {
			sb.append(providerName + "_");
		}
		sb.append("JOURNAL_VENTE_");
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
		templateFilename = jasperTemplatesFolder + "journal.jasper";
		generateJournalFile(report.getProvider() == null ? null : report.getProvider().getCode(),
				report.getStartDate(), report.getEndDate(), report.getOutputFormat());

	}

}
