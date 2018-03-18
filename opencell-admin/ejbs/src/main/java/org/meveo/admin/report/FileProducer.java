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
package org.meveo.admin.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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

    public Map<String, Object> parameters = new HashMap<String, Object>();

    public JasperReport jasperReport;

    public JasperPrint jasperPrint;

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

        try {
            InputStream reportTemplate = new FileInputStream(reportFileName);
            jasperReport = (JasperReport) JRLoader.loadObject(reportTemplate);
            if (dataSourceFile != null) {
                JRCsvDataSource dataSource = createDataSource(dataSourceFile);
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
                JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
            }
        } catch (JRException e) {
            log.error("failed to generate PDF file", e);
        } catch (FileNotFoundException e) {
            throw new NoTemplateException();
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
