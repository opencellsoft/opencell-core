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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;

import org.meveo.admin.action.admin.CurrentProvider;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.payments.impl.AccountOperationService;
import org.slf4j.Logger;

@Named
public class BordereauRemiseCheque {

	private static String REPORTS_URL = "";// TODO:
											// ResourceBundle.instance().getString("reportsURL");
	// private static String OCC_CODE =
	// ResourceBundle.instance().getString("occ.templatePaymentCheckCode");
	private static String REPORT_NAME = "REMISE-CHEQUE";
	
	@Inject
	protected Logger log;

	/** Status messages. */
	/*TODO: @Inject
	protected StatusMessages statusMessages;*/
	
	@Inject
	private AccountOperationService accountOperationService;

	public JasperReport jasperReport;

	public JasperPrint jasperPrint;

	public JasperDesign jasperDesign;

	public Map<String, Object> parameters = new HashMap<String, Object>();

	private Date date = new Date();

	public void generateReport() {
		String fileName = "reports/bordereauRemiseCheque.jasper";
		InputStream reportTemplate = this.getClass().getClassLoader().getResourceAsStream(fileName);
		parameters.put("date", new Date());
		String providerCode = currentProvider.getCode();

		String[] occCodes = null; //TODO: ResourceBundle.instance().getString("report.occ.templatePaymentCheckCodes").split(",");
		try {
			jasperReport = (JasperReport) JRLoader.loadObject(reportTemplate);
			File dataSourceFile = generateDataFile(occCodes);
			if (dataSourceFile != null) {
				FacesContext context = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) context.getExternalContext()
						.getResponse();
				response.setContentType("application/pdf"); // fill in
				response.setHeader("Content-disposition", "attachment; filename="
						+ generateFileName(providerCode));

				JRCsvDataSource dataSource = createDataSource(dataSourceFile);
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
				JasperExportManager.exportReportToPdfFile(jasperPrint,
						generateFileName(providerCode));
				//TODO: statusMessages.addFromResourceBundle(Severity.INFO, "report.reportCreted");
				OutputStream os;
				try {
					os = response.getOutputStream();
					JasperExportManager.exportReportToPdfStream(jasperPrint, os);
					os.flush();
					os.close();
					context.responseComplete();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				//TODO: statusMessages.addFromResourceBundle(Severity.INFO, "bordereauRemiseCheque.noData");
			}
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JRCsvDataSource createDataSource(File dataSourceFile) throws FileNotFoundException {
		JRCsvDataSource ds = new JRCsvDataSource(dataSourceFile);
		// DecimalFormat df = new DecimalFormat("0.00");
		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		ds.setNumberFormat(nf);
		ds.setFieldDelimiter(';');
		ds.setRecordDelimiter("\n");
		ds.setUseFirstRowAsHeader(true);
		String[] columnNames = new String[] { "customerAccountId", "title", "name", "firstname",
				"amount" };
		ds.setColumnNames(columnNames);
		return ds;
	}

	@Inject
	@CurrentProvider
	private Provider currentProvider;

	public File generateDataFile(String[] occCodes) {

		List<AccountOperation> records = new ArrayList<AccountOperation>();
		for (String occCode : occCodes) {
			records.addAll(accountOperationService.getAccountOperations(this.date, occCode,
					currentProvider));
		}
		Iterator<AccountOperation> itr = records.iterator();
		try {
			File temp = File.createTempFile("bordereauRemiseChequeDS", ".csv");
			FileWriter writer = new FileWriter(temp);
			writer.append("customerAccountId;title;name;firstname;amount");
			writer.append('\n');
			if (records.size() == 0) {
				return null;
			}

			while (itr.hasNext()) {
				AccountOperation ooc = itr.next();
				CustomerAccount ca = ooc.getCustomerAccount();
				writer.append(ca.getCode() + ";");
				writer.append(ca.getName().getTitle().getCode() + ";");
				writer.append(ca.getName() + ";");
				writer.append(ca.getName().getFirstName() + ";");
				writer.append(ooc.getAmount().toString());
				writer.append('\n');
			}
			writer.flush();
			writer.close();
			return temp;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String generateFileName(String providerCode) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		StringBuilder sb = new StringBuilder(providerCode + "_");
		sb.append(REPORT_NAME);
		sb.append("_");
		sb.append(df.format(this.date));
		sb.append(".pdf");
		return REPORTS_URL + sb.toString();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<BordereauRemiseChequeRecord> convertList(List<Object> rows) {
		List<BordereauRemiseChequeRecord> bordereauRemiseChequeRecords = new ArrayList<BordereauRemiseChequeRecord>();
		return bordereauRemiseChequeRecords;
	}

}
