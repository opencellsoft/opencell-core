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
import org.meveo.service.reporting.local.JournalEntryServiceLocal;
import org.slf4j.Logger;

@Named
public class Journal extends FileProducer implements Reporting {

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	@Inject
	protected Logger log;

	@Inject
	private JournalEntryServiceLocal journalEntryService;

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
		ParamBean param = ParamBean.getInstance("meveo-admin.properties");
		reportsFolder = param.getProperty("reportsURL");
		separator = param.getProperty("reporting.accountingCode.separator");
		String jasperTemplatesFolder = param.getProperty("reports.jasperTemplatesFolder");
		templateFilename = jasperTemplatesFolder + "journal.jasper";
		generateJournalFile(report.getProvider() == null ? null : report.getProvider().getCode(),
				report.getStartDate(), report.getEndDate(), report.getOutputFormat());

	}

}
