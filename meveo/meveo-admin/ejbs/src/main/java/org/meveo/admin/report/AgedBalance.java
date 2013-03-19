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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.commons.utils.ParamBean;
import org.meveo.model.bi.OutputFormatEnum;
import org.meveo.model.bi.Report;
import org.meveo.service.reporting.impl.DWHAccountOperationService;
import org.slf4j.Logger;

@Named
public class AgedBalance extends FileProducer implements Reporting {
	@Inject
	private DWHAccountOperationService accountOperationService;

	final static String DEBIT = "0";
	final static String CREDIT = "1";

	private String reportsFolder;
	private String templateFilename;
	public Map<String, Object> parameters = new HashMap<String, Object>();

	protected Logger log;

	public void generateAgedBalanceFile(String providerCode, Date date,
			OutputFormatEnum outputFormat) {
		try {
			date = new Date();
			File file = null;
			if (outputFormat == OutputFormatEnum.PDF) {
				file = File.createTempFile("tempAgedBalance", ".csv");
			} else if (outputFormat == OutputFormatEnum.CSV) {
				StringBuilder sb = new StringBuilder(getFilename(providerCode, date));
				sb.append(".csv");
				file = new File(sb.toString());
			}
			FileWriter writer = new FileWriter(file);
			int endMonth = calculateEndMonth(date);
			writer.append("Type;Dont non echue;Moins de 3 mois;De 3 a 6 mois;De 6 mois a 1 an;De 1 an a 2 ans;De 2 ans a 3 ans;Plus de 3 ans;Total");
			writer.append('\n');
			writer.append("DEBIT;");
			writer.append((accountOperationService.calculateRecordsBetweenDueMonth(providerCode,
					endMonth, null, DEBIT) + ";").replace('.', ','));
			writer.append((accountOperationService.calculateRecordsBetweenDueMonth(providerCode,
					endMonth - 3, endMonth, DEBIT) + ";").replace('.', ','));
			writer.append((accountOperationService.calculateRecordsBetweenDueMonth(providerCode,
					endMonth - 6, endMonth - 3, DEBIT) + ";").replace('.', ','));
			writer.append((accountOperationService.calculateRecordsBetweenDueMonth(providerCode,
					endMonth - 12, endMonth - 6, DEBIT) + ";").replace('.', ','));
			writer.append((accountOperationService.calculateRecordsBetweenDueMonth(providerCode,
					endMonth - 24, endMonth - 12, DEBIT) + ";").replace('.', ','));
			writer.append((accountOperationService.calculateRecordsBetweenDueMonth(providerCode,
					endMonth - 36, endMonth - 24, DEBIT) + ";").replace('.', ','));
			writer.append((accountOperationService.calculateRecordsBetweenDueMonth(providerCode,
					null, endMonth - 36, DEBIT) + ";").replace('.', ','));
			writer.append((accountOperationService.totalAmount(providerCode, DEBIT).toString())
					.replace('.', ','));
			writer.append('\n');
			writer.append("NB DEBIT;");
			writer.append((accountOperationService.countRecordsBetweenDueMonth(providerCode,
					endMonth, null, DEBIT) + ";").replace('.', ','));
			writer.append((accountOperationService.countRecordsBetweenDueMonth(providerCode,
					endMonth - 3, endMonth, DEBIT) + ";").replace('.', ','));
			writer.append((accountOperationService.countRecordsBetweenDueMonth(providerCode,
					endMonth - 6, endMonth - 3, DEBIT) + ";").replace('.', ','));
			writer.append((accountOperationService.countRecordsBetweenDueMonth(providerCode,
					endMonth - 12, endMonth - 6, DEBIT) + ";").replace('.', ','));
			writer.append((accountOperationService.countRecordsBetweenDueMonth(providerCode,
					endMonth - 24, endMonth - 12, DEBIT) + ";").replace('.', ','));
			writer.append((accountOperationService.countRecordsBetweenDueMonth(providerCode,
					endMonth - 36, endMonth - 24, DEBIT) + ";").replace('.', ','));
			writer.append((accountOperationService.countRecordsBetweenDueMonth(providerCode, null,
					endMonth - 36, DEBIT) + ";").replace('.', ','));
			writer.append(Double.toString(accountOperationService.totalCount(providerCode, DEBIT))
					.replace('.', ','));
			writer.append('\n');

			writer.append('\n');
			writer.append("CREDIT;");
			writer.append((accountOperationService.calculateRecordsBetweenDueMonth(providerCode,
					endMonth, null, CREDIT) + ";").replace('.', ','));
			writer.append((accountOperationService.calculateRecordsBetweenDueMonth(providerCode,
					endMonth - 3, endMonth, CREDIT) + ";").replace('.', ','));
			writer.append((accountOperationService.calculateRecordsBetweenDueMonth(providerCode,
					endMonth - 6, endMonth - 3, CREDIT) + ";").replace('.', ','));
			writer.append((accountOperationService.calculateRecordsBetweenDueMonth(providerCode,
					endMonth - 12, endMonth - 6, CREDIT) + ";").replace('.', ','));
			writer.append((accountOperationService.calculateRecordsBetweenDueMonth(providerCode,
					endMonth - 24, endMonth - 12, CREDIT) + ";").replace('.', ','));
			writer.append((accountOperationService.calculateRecordsBetweenDueMonth(providerCode,
					endMonth - 36, endMonth - 24, CREDIT) + ";").replace('.', ','));
			writer.append((accountOperationService.calculateRecordsBetweenDueMonth(providerCode,
					null, endMonth - 36, CREDIT) + ";").replace('.', ','));
			writer.append((accountOperationService.totalAmount(providerCode, CREDIT).toString())
					.replace('.', ','));
			writer.append('\n');
			writer.append("NB CREDIT;");
			writer.append((accountOperationService.countRecordsBetweenDueMonth(providerCode,
					endMonth, null, CREDIT) + ";").replace('.', ','));
			writer.append((accountOperationService.countRecordsBetweenDueMonth(providerCode,
					endMonth - 3, endMonth, CREDIT) + ";").replace('.', ','));
			writer.append((accountOperationService.countRecordsBetweenDueMonth(providerCode,
					endMonth - 6, endMonth - 3, CREDIT) + ";").replace('.', ','));
			writer.append((accountOperationService.countRecordsBetweenDueMonth(providerCode,
					endMonth - 12, endMonth - 6, CREDIT) + ";").replace('.', ','));
			writer.append((accountOperationService.countRecordsBetweenDueMonth(providerCode,
					endMonth - 24, endMonth - 12, CREDIT) + ";").replace('.', ','));
			writer.append((accountOperationService.countRecordsBetweenDueMonth(providerCode,
					endMonth - 36, endMonth - 24, CREDIT) + ";").replace('.', ','));
			writer.append((accountOperationService.countRecordsBetweenDueMonth(providerCode, null,
					endMonth - 36, CREDIT) + ";").replace('.', ','));
			writer.append(Double.toString(accountOperationService.totalCount(providerCode, CREDIT))
					.replace('.', ','));
			writer.append('\n');

			writer.flush();
			writer.close();
			if (outputFormat == OutputFormatEnum.PDF) {
				parameters.put("startDate", date);
				parameters.put("provider", providerCode);
				StringBuilder sb = new StringBuilder(getFilename(providerCode, date));
				sb.append(".pdf");
				generatePDFfile(file, sb.toString(), templateFilename, parameters);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int calculateEndMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int endYear = calendar.get(Calendar.YEAR);
		int monthInYear = calendar.get(Calendar.MONTH);
		return endYear * 12 + monthInYear;
	}

	public String getFilename(String providerName, Date date) {

		String DATE_FORMAT = "dd-MM-yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		StringBuilder sb = new StringBuilder();
		sb.append(reportsFolder);
		sb.append(providerName);
		sb.append("_BALANCE_AGEE_");
		sb.append(sdf.format(date).toString());
		return sb.toString();
	}

	public void export(Report report) {
		ParamBean param = ParamBean.getInstance("meveo-admin.properties");
		reportsFolder = param.getProperty("reportsURL");
		String jasperTemplatesFolder = param.getProperty("reports.jasperTemplatesFolder");
		templateFilename = jasperTemplatesFolder + "agedBalance.jasper";
		generateAgedBalanceFile(report.getProvider() == null ? null : report.getProvider()
				.getCode(), report.getSchedule(), report.getOutputFormat());
	}
}