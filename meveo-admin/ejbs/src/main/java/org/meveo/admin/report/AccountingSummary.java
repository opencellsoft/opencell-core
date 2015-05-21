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

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.commons.utils.ParamBean;
import org.meveo.model.bi.OutputFormatEnum;
import org.meveo.model.bi.Report;
import org.meveo.service.reporting.impl.DWHAccountOperationService;
import org.slf4j.Logger;

@Named
public class AccountingSummary extends FileProducer implements Reporting {

    static final Comparator<AccountingSummaryObject> OCC_CODE_ORDER = new Comparator<AccountingSummaryObject>() {
        public int compare(AccountingSummaryObject e1, AccountingSummaryObject e2) {
            return e2.getOccCode().compareTo(e2.getOccCode());
        }
    };

    @Inject
    protected Logger log;

    @Inject
	private DWHAccountOperationService accountOperationTransformationService;

    private String reportsFolder;
    private String templateFilename;
    public Map<String, Object> parameters = new HashMap<String, Object>();

    public void generateAccountingSummaryFile(String providerCode, Date startDate, Date endDate,
            OutputFormatEnum outputFormat) {
        try {
            File file = null;
            if (outputFormat == OutputFormatEnum.PDF) {
                file = File.createTempFile("tempAccountingSummary", ".csv");
            } else if (outputFormat == OutputFormatEnum.CSV) {
                StringBuilder sb = new StringBuilder(getFilename(providerCode));
                sb.append(".csv");
                file = new File(sb.toString());
            }
            FileWriter writer = new FileWriter(file);
            writer.append("Code opération;Libellé de l'opération;Débit;Crédit");
            writer.append('\n');
            List<Object> listCategory1 = accountOperationTransformationService.getAccountingSummaryRecords(
                    providerCode, new Date(), 1);
            List<Object> listCategory0 = accountOperationTransformationService.getAccountingSummaryRecords(
                    providerCode, new Date(), 0);
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
            writer.close();
            if (outputFormat == OutputFormatEnum.PDF) {
                parameters.put("startDate", startDate);
                parameters.put("endDate", endDate);
                parameters.put("provider", providerCode);
                StringBuilder sb = new StringBuilder(getFilename(providerCode));
                sb.append(".pdf");
                generatePDFfile(file, sb.toString(), templateFilename, parameters);
            }
        } catch (IOException e) {
            log.error("failed to generate accounting summary file",e);
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

    public String getFilename(String providerName) {

        String DATE_FORMAT = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        StringBuilder sb = new StringBuilder();
        sb.append(reportsFolder);
        sb.append(providerName);
        sb.append("_RECAP_INVENTAIRE_CCLIENT_");
        sb.append(sdf.format(new Date()).toString());
        return sb.toString();
    }

    public void export(Report report) {
        ParamBean param = ParamBean.getInstance();
		reportsFolder = param.getProperty("reportsURL","/opt/jboss/files/reports/");
		String jasperTemplatesFolder = param.getProperty("reports.jasperTemplatesFolder","/opt/jboss/files/reports/JasperTemplates/");
        templateFilename = jasperTemplatesFolder + "accountingSummary.jasper";
        generateAccountingSummaryFile(report.getProvider() == null ? null : report.getProvider().getCode(), report
                .getStartDate(), report.getEndDate(), report.getOutputFormat());
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
