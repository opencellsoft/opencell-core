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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.bi.OutputFormatEnum;
import org.meveo.model.bi.Report;
import org.meveo.service.reporting.impl.JournalEntryService;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
@Named
public class TaxStatus extends FileProducer implements Reporting {

    @Inject
    private JournalEntryService salesTransformationService;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    public void generateTaxStatusFile(Date startDate, Date endDate, OutputFormatEnum outputFormat) {
        // log.info("generateTaxStatusFile({},{})", startDate,endDate);
        FileWriter writer = null;
        try {
            // log.info("generateTaxStatusFile : file {}",
            // getFilename(startDate, endDate));
            File file = null;
            if (outputFormat == OutputFormatEnum.PDF) {
                file = File.createTempFile("tempAccountingDetail", ".csv");
            } else if (outputFormat == OutputFormatEnum.CSV) {
                StringBuilder sb = new StringBuilder(getFilename(startDate, endDate));
                sb.append(".csv");
                file = new File(sb.toString());
            }
            writer = new FileWriter(file);
            writer.append("Code;Description;Pourcentage;Base HT;Taxe due");
            writer.append('\n');
            List<Object> taxes = salesTransformationService.getTaxRecodsBetweenDate(startDate, endDate);
            Iterator<Object> itr = taxes.iterator();
            while (itr.hasNext()) {
                Object[] row = (Object[]) itr.next();
                if (row[0] != null)
                    writer.append(row[0].toString() + ";");
                else
                    writer.append(";");
                if (row[1] != null)
                    writer.append(row[1].toString() + ";");
                else
                    writer.append(";");
                if (row[2] != null)
                    writer.append(row[2].toString().replace('.', ',') + ";");
                else
                    writer.append(";");
                if (row[3] != null)
                    writer.append(row[3].toString().replace('.', ',') + ";");
                else
                    writer.append(";");
                if (row[4] != null)
                    writer.append(row[4].toString().replace('.', ','));
                writer.append('\n');
            }
            writer.flush();
            
            if (outputFormat == OutputFormatEnum.PDF) {
                parameters.put("startDate", startDate);
                parameters.put("endDate", endDate);
                StringBuilder sb = new StringBuilder(getFilename(startDate, endDate));
                sb.append(".pdf");
                ParamBean param = paramBeanFactory.getInstance();
                String jasperTemplatesFolder = param.getProperty("reports.jasperTemplatesFolder", "/opt/jboss/files/reports/JasperTemplates/");
                String templateFilename = jasperTemplatesFolder + "taxStatus.jasper";
                generatePDFfile(file, sb.toString(), templateFilename, parameters);
            }
        } catch (IOException e) {
            log.error("failed to generate tax status file", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception ex) {
                    log.error("failed to close writer", ex);
                }
            }
        }
    }

    public String getFilename(Date startDate, Date endDate) {

        String DATE_FORMAT = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        StringBuilder sb = new StringBuilder();
        ParamBean param = paramBeanFactory.getInstance();
        String reportsFolder = param.getProperty("reportsURL", "/opt/jboss/files/reports/");
        sb.append(reportsFolder);
        sb.append(appProvider.getCode());
        sb.append("_TAX_");
        sb.append(sdf.format(startDate).toString());
        sb.append("_");
        sb.append(sdf.format(endDate).toString());
        return sb.toString();
    }

    public void export(Report report) {
        generateTaxStatusFile(report.getStartDate(), report.getEndDate(), report.getOutputFormat());

    }

}
