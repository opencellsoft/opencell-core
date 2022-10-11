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
package org.meveo.admin.action.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
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

import org.apache.commons.io.IOUtils;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.report.BordereauRemiseChequeRecord;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 * @author anasseh
 * @lastModifiedVersion 5.0
 *
 */
@Named
public class BordereauRemiseCheque {

    private static String REPORT_NAME = "REMISE-CHEQUE";

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    private Messages messages;

    @Inject
    private AccountOperationService accountOperationService;

    public Map<String, Object> parameters = new HashMap<String, Object>();

    /** paramBean Factory allows to get application scope paramBean or provider specific paramBean */
    @Inject
    private ParamBeanFactory paramBeanFactory;

    @Inject
    private FacesContext facesContext;
    
    private Date date = new Date();

    public void generateReport() throws BusinessEntityException {
        String fileName = "reports/bordereauRemiseCheque.jasper";
        InputStream reportTemplate = this.getClass().getClassLoader().getResourceAsStream(fileName);
        parameters.put("date", new Date());

        String[] occCodes = paramBeanFactory.getInstance().getProperty("report.occ.templatePaymentCheckCodes", "PAY_CHK,PAY_NID").split(",");
        try {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportTemplate);
            File dataSourceFile = generateDataFile(occCodes);
            if (dataSourceFile != null) {
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
                response.setContentType("application/pdf"); // fill in
                response.setHeader("Content-disposition", "attachment; filename=" + generateFileName());

                JRCsvDataSource dataSource = createDataSource(dataSourceFile);
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
                JasperExportManager.exportReportToPdfFile(jasperPrint, generateFileName());
                messages.info(new BundleKey("messages", "report.reportCreted"));
                OutputStream os;
                try {
                    os = response.getOutputStream();
                    JasperExportManager.exportReportToPdfStream(jasperPrint, os);
                    os.flush();
                    os.close();
                    facesContext.responseComplete();
                } catch (IOException e) {
                    log.error("failed to export report too PdfStream", e);
                }

            } else {
                messages.info(new BundleKey("messages", "bordereauRemiseCheque.noData"));
            }
        } catch (JRException e) {
            log.error("JR exception ", e);
        } catch (FileNotFoundException e) {
            log.error("file not found exception ", e);
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
        String[] columnNames = new String[] { "customerAccountId", "title", "name", "firstname", "amount" };
        ds.setColumnNames(columnNames);
        return ds;
    }

    /**
     * @param occCodes array of account operation codes
     * @return generated data file csv
     * @throws BusinessEntityException business exception.
     */
    public File generateDataFile(String[] occCodes) throws BusinessEntityException {

        List<AccountOperation> records = new ArrayList<AccountOperation>();
        for (String occCode : occCodes) {
            records.addAll(accountOperationService.getAccountOperations(this.date, occCode));
        }
        Iterator<AccountOperation> itr = records.iterator();
        FileWriter writer = null;
        try {
            File temp = File.createTempFile("bordereauRemiseChequeDS", ".csv");
            writer = new FileWriter(temp);
            writer.append("customerAccountId;title;name;firstname;amount");
            writer.append('\n');
            if (records.size() == 0) {
                writer.close();
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
            return temp;
        } catch (IOException e) {
            log.error("failed to generate data file", e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
        return null;
    }

    public String generateFileName() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder sb = new StringBuilder(appProvider.getCode() + "_");
        sb.append(REPORT_NAME);
        sb.append("_");
        sb.append(df.format(this.date));
        sb.append(".pdf");

        String reportsUrl = paramBeanFactory.getInstance().getProperty("reportsURL", "/opt/jboss/files/reports/");
        return reportsUrl + sb.toString();
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
