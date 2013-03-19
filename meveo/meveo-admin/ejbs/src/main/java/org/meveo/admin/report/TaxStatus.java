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
import org.meveo.service.reporting.impl.JournalEntryService;
import org.slf4j.Logger;

@Named
public class TaxStatus extends FileProducer implements Reporting {

	@Inject
    protected Logger log;

	@Inject
	private JournalEntryService salesTransformationService;

    private String reportsFolder;
    private String templateFilename;
    public Map<String, Object> parameters = new HashMap<String, Object>();

    public void generateTaxStatusFile(String providerCode, Date startDate, Date endDate, OutputFormatEnum outputFormat) {
        // log.info("generateTaxStatusFile({0},{1})", startDate,endDate);
        try {
            // log.info("generateTaxStatusFile : file {0}",
            // getFilename(startDate, endDate));
            File file = null;
            if (outputFormat == OutputFormatEnum.PDF) {
                file = File.createTempFile("tempAccountingDetail", ".csv");
            } else if (outputFormat == OutputFormatEnum.CSV) {
                StringBuilder sb = new StringBuilder(getFilename(providerCode, startDate, endDate));
                sb.append(".csv");
                file = new File(sb.toString());
            }
            FileWriter writer = new FileWriter(file);
            writer.append("Code;Description;Pourcentage;Base HT;Taxe due");
            writer.append('\n');
            List<Object> taxes = salesTransformationService.getTaxRecodsBetweenDate(providerCode, startDate, endDate);
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
        sb.append(providerName);
        sb.append("_TAX_");
        sb.append(sdf.format(startDate).toString());
        sb.append("_");
        sb.append(sdf.format(endDate).toString());
        return sb.toString();
    }

    public void export(Report report) {
        ParamBean param = ParamBean.getInstance("meveo-admin.properties");
        reportsFolder = param.getProperty("reportsURL");
        String jasperTemplatesFolder = param.getProperty("reports.jasperTemplatesFolder");
        templateFilename = jasperTemplatesFolder + "taxStatus.jasper";
        generateTaxStatusFile(report.getProvider() == null ? null : report.getProvider().getCode(), report
                .getStartDate(), report.getEndDate(), report.getOutputFormat());

    }

}
