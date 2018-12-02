package org.meveo.admin.action.finance;

import java.io.*;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.UpdateMapTypeFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ReportExtractExecutionException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.finance.ReportExtract;
import org.meveo.model.finance.ReportExtractResultTypeEnum;
import org.meveo.model.finance.ReportExtractScriptTypeEnum;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.finance.ReportExtractService;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * Controller to manage detail view of {@link ReportExtract}.
 * 
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.3
 **/
@Named
@ViewScoped
public class ReportExtractBean extends UpdateMapTypeFieldBean<ReportExtract> {

    private static final long serialVersionUID = -3817116164208834748L;

    @Inject
    private ReportExtractService reportExtractService;

    public ReportExtractBean() {
        super(ReportExtract.class);
    }

    @Override
    public ReportExtract initEntity() {
        entity = super.initEntity();

        if (entity.getParams() != null) {
            extractMapTypeFieldFromEntity(entity.getParams(), "params");
        }

        if (entity.getReportExtractResultType() == null) {
            entity.setReportExtractResultType(ReportExtractResultTypeEnum.CSV);
        }

        if (StringUtils.isBlank(entity.getStyle())) {
            entity.setStyle(
                "body {font-family: monospace;}\ntable {border-collapse: collapse;}\ntd,th {border: 1px solid black; padding: 3px 10px; text-align: center;}\nth {font-weight: bold; background-color: #aaa}\ntr:nth-child(odd) {background-color: #fff}\ntr:nth-child(even) {background-color: #eee}\ntr:hover {background-color: #fdd;}\ntd:hover {background-color: #fcc;}\n");
        }

        return entity;
    }

    public void initEntity(ReportExtract e) {
        entity = e;

        if (entity.getParams() != null) {
            extractMapTypeFieldFromEntity(entity.getParams(), "params");
        }
    }

    @Override
    protected IPersistenceService<ReportExtract> getPersistenceService() {
        return reportExtractService;
    }

    private void addFileExtension() {
        if (!StringUtils.isBlank(entity.getFilenameFormat()) && entity.getReportExtractResultType() != null) {
            int pointLastIndex = entity.getFilenameFormat().lastIndexOf(".");
            if (pointLastIndex > -1) {
                String fileExtension = entity.getFilenameFormat().substring(pointLastIndex + 1);
                if (!fileExtension.equalsIgnoreCase(entity.getReportExtractResultType().name())) {
                    entity.setFilenameFormat(entity.getFilenameFormat().substring(0, pointLastIndex + 1) + entity.getReportExtractResultType().name().toLowerCase());
                }
            } else {
                entity.setFilenameFormat(entity.getFilenameFormat() + "." + entity.getReportExtractResultType().name().toLowerCase());
            }
        }
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        addFileExtension();

        if (entity.getScriptType().equals(ReportExtractScriptTypeEnum.SQL)) {
            entity.setScriptInstance(null);
        } else {
            entity.setSqlQuery(null);
        }
        updateMapTypeFieldInEntity(entity.getParams(), "params");

        return super.saveOrUpdate(killConversation);
    }

    @ActionMethod
    public String runReport() {
        String result = null;
        try {
            result = saveOrUpdate(true);
            reportExtractService.runReport(entity);
            messages.info(new BundleKey("messages", "reportExtract.message.generate.ok"));
        } catch (BusinessException | ReportExtractExecutionException e) {
            log.error("Failed running report: {}", e.getMessage());
            messages.error(e.getMessage());
        }

        return result;
    }

    @ActionMethod
    public String runReportFromList() {
        String result = null;

        try {
            result = saveOrUpdate(false);
            reportExtractService.runReport(entity);
            messages.info(new BundleKey("messages", "reportExtract.message.generate.ok"));
        } catch (BusinessException | ReportExtractExecutionException e) {
            log.error("Failed running report: {}", e.getMessage());
            messages.error(e.getMessage());
        }

        return result;
    }

    public StreamedContent getReportFile(ReportExtract entity) {
        InputStream stream = null;

        try {
            String filePath = reportExtractService.getReporFile(entity);
            File file = new File(filePath);
            
            String mimeType = "text/csv";
            if (!FilenameUtils.getExtension(filePath).equals("csv")) {
                mimeType = "text/html";
            }

            return new DefaultStreamedContent(new FileInputStream(file), mimeType, filePath.substring(filePath.lastIndexOf(File.separator) + 1));

        } catch (BusinessException | IOException e) {
            log.error("Failed loading repor file={}", e.getMessage());
            return null;
        } finally {
            IOUtils.closeQuietly(stream);   
        }

    }

}
