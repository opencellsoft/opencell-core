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
package org.meveo.admin.report;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.exception.NoTemplateException;
import org.meveo.model.crm.Provider;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 * This file producer class is used to generate PDF file
 */
@Named
public class FileProducer {
    @Inject
    protected Logger log;

    protected Map<String, Object> parameters = new HashMap<String, Object>();

    private JasperReport jasperReport;

    private JasperPrint jasperPrint;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    /**
     * @param dataSourceFile Data source CSV file
     * @param fileName Filename of new file
     * @param reportFileName Report template
     * @param parameters template parameters
     */
    public void generatePDFfile(File dataSourceFile, String fileName, String reportFileName, Map<String, Object> parameters) {

        try(InputStream reportTemplate = new FileInputStream(reportFileName)) {
            jasperReport = (JasperReport) JRLoader.loadObject(reportTemplate);
            if (dataSourceFile != null) {
                JRCsvDataSource dataSource = createDataSource(dataSourceFile);
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
                JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
            }
        } catch (FileNotFoundException e) {
            throw new NoTemplateException();
        } catch (JRException | IOException e) {
            log.error("failed to generate PDF file", e);
        }
    }

    public JRCsvDataSource createDataSource(File dataSourceFile) throws FileNotFoundException {
        JRCsvDataSource ds = new JRCsvDataSource(dataSourceFile);
        // DecimalFormat df = new DecimalFormat("0.00");
        // NumberFormat nf = NumberFormat.getInstance(Locale.US);
        // ds.setNumberFormat(nf);
        ds.setFieldDelimiter(';');
        ds.setRecordDelimiter("\n");
        ds.setUseFirstRowAsHeader(true);
        // String[] columnNames = new String[] { "Nom du compte client",
        // "Code operation", "Référence comptable",
        // "Date de l'opération", "Date d'exigibilité", "Débit", "Credit",
        // "Solde client" };
        // ds.setColumnNames(columnNames);
        return ds;
    }

}
