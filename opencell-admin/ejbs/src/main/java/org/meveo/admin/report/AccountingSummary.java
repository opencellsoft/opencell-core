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
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.commons.io.IOUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.bi.OutputFormatEnum;
import org.meveo.model.bi.Report;
import org.meveo.service.reporting.impl.DWHAccountOperationService;
import org.slf4j.Logger;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
@Named
public class AccountingSummary extends FileProducer implements Reporting {

    static final Comparator<AccountingSummaryObject> OCC_CODE_ORDER = new Comparator<AccountingSummaryObject>() {
        public int compare(AccountingSummaryObject e1, AccountingSummaryObject e2) {
            return e2.getOccCode().compareTo(e2.getOccCode());
        }
    };

    @Inject
    private DWHAccountOperationService accountOperationTransformationService;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    private String templateFilename;
    
    public void generateAccountingSummaryFile(Date startDate, Date endDate, OutputFormatEnum outputFormat) {
        FileWriter writer = null;
        try {
            File file = null;
            if (outputFormat == OutputFormatEnum.PDF) {
                file = File.createTempFile("tempAccountingSummary", ".csv");
            } else if (outputFormat == OutputFormatEnum.CSV) {
                StringBuilder sb = new StringBuilder(getFilename());
                sb.append(".csv");
                file = new File(sb.toString());
            }
            writer = new FileWriter(file);
            writer.append("Code opération;Libellé de l'opération;Débit;Crédit");
            writer.append('\n');
            List<Object> listCategory1 = accountOperationTransformationService.getAccountingSummaryRecords(new Date(), 1);
            List<Object> listCategory0 = accountOperationTransformationService.getAccountingSummaryRecords(new Date(), 0);
            List<AccountingSummaryObject> list = new ArrayList<AccountingSummaryObject>();
            list.addAll(parseObjectList(listCategory0, 0));
            list.addAll(parseObjectList(listCategory1, 1));
            Collections.sort(list, OCC_CODE_ORDER);

            Iterator<AccountingSummaryObject> itr = list.iterator();
            while (itr.hasNext()) {
                AccountingSummaryObject accountingSummaryObject = itr.next();
                writer.append(accountingSummaryObject.getOccCode() + ";");
                writer.append(accountingSummaryObject.getOccDescription() + ";");
                if (accountingSummaryObject.getCategory() == 0)
                    writer.append(accountingSummaryObject.getAmount().toString().replace('.', ',') + ";");
                else
                    writer.append("0;");
                if (accountingSummaryObject.getCategory() == 1)
                    writer.append(accountingSummaryObject.getAmount().toString().replace('.', ','));
                else
                    writer.append("0");
                writer.append('\n');
            }
            writer.flush();
            if (outputFormat == OutputFormatEnum.PDF) {
                parameters.put("startDate", startDate);
                parameters.put("endDate", endDate);
                StringBuilder sb = new StringBuilder(getFilename());
                sb.append(".pdf");
                generatePDFfile(file, sb.toString(), templateFilename, parameters);
            }
        } catch (IOException e) {
            log.error("failed to generate accounting summary file", e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    public List<AccountingSummaryObject> parseObjectList(List<Object> list, int category) {
        List<AccountingSummaryObject> accountingSummaryObjectList = new ArrayList<AccountingSummaryObject>();
        Iterator<Object> itr = list.iterator();
        while (itr.hasNext()) {
            Object[] row = (Object[]) itr.next();
            AccountingSummaryObject accountingSummaryObject = new AccountingSummaryObject();
            accountingSummaryObject.setOccCode((String) row[0]);
            accountingSummaryObject.setOccDescription((String) row[1]);
            BigDecimal amount = (BigDecimal) row[2];
            accountingSummaryObject.setAmount(amount);
            accountingSummaryObject.setCategory(category);
            accountingSummaryObjectList.add(accountingSummaryObject);
        }
        return accountingSummaryObjectList;
    }

    public String getFilename() {
        String DATE_FORMAT = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        ParamBean param = paramBeanFactory.getInstance();
        String reportsFolder = param.getProperty("reportsURL", "/opt/jboss/files/reports/");
        StringBuilder sb = new StringBuilder();
        sb.append(reportsFolder);
        sb.append(appProvider.getCode());
        sb.append("_RECAP_INVENTAIRE_CCLIENT_");
        sb.append(sdf.format(new Date()).toString());
        return sb.toString();
    }

    public void export(Report report) {
        ParamBean param = paramBeanFactory.getInstance();
        String reportsFolder = param.getProperty("reportsURL", "/opt/jboss/files/reports/");
        String jasperTemplatesFolder = param.getProperty("reports.jasperTemplatesFolder", "/opt/jboss/files/reports/JasperTemplates/");
        templateFilename = jasperTemplatesFolder + "accountingSummary.jasper";
        generateAccountingSummaryFile(report.getStartDate(), report.getEndDate(), report.getOutputFormat());
    }

}

class AccountingSummaryObject {
    private String occCode;
    private String occDescription;
    private BigDecimal amount;
    private int category;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getOccCode() {
        return occCode;
    }

    public void setOccCode(String occCode) {
        this.occCode = occCode;
    }

    public String getOccDescription() {
        return occDescription;
    }

    public void setOccDescription(String occDescription) {
        this.occDescription = occDescription;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

}
