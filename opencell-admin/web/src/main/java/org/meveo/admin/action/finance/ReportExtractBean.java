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

package org.meveo.admin.action.finance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.UpdateMapTypeFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ReportExtractExecutionException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.finance.ReportExtract;
import org.meveo.model.finance.ReportExtractExecutionResult;
import org.meveo.model.finance.ReportExtractResultTypeEnum;
import org.meveo.model.finance.ReportExtractScriptTypeEnum;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.finance.ReportExtractService;
import org.primefaces.model.ByteArrayContent;
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

public class ReportExtractBean extends UpdateMapTypeFieldBean<ReportExtract> {

    private static final long serialVersionUID = -3817116164208834748L;
    private static final String PARAMS_FIELD_NAME = "params";

    @Inject
    private ReportExtractService reportExtractService;

    public ReportExtractBean() {
        super(ReportExtract.class);
    }

    @Override
    public ReportExtract initEntity() {
        entity = super.initEntity();

        if (entity.getParams() != null) {
            extractMapTypeFieldFromEntity(entity.getParams(), PARAMS_FIELD_NAME);
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
            extractMapTypeFieldFromEntity(entity.getParams(), PARAMS_FIELD_NAME);
        }
    }

    @Override
    protected IPersistenceService<ReportExtract> getPersistenceService() {
        return reportExtractService;
    }

    private void addFileExtension() {
        if (!StringUtils.isBlank(entity.getFilenameFormat()) && entity.getReportExtractResultType() != null) {
            int pointLastIndex = entity.getFilenameFormat().lastIndexOf('.');
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
    public String saveOrUpdate(boolean killConversation) {

        addFileExtension();

        if (entity.getScriptType().equals(ReportExtractScriptTypeEnum.SQL)) {
            entity.setScriptInstance(null);
        } else {
            entity.setSqlQuery(null);
        }
        updateMapTypeFieldInEntity(entity.getParams(), PARAMS_FIELD_NAME);

        return super.saveOrUpdate(killConversation);
    }

    @ActionMethod
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public String runReport() {
        String result = null;
        try {
            result = saveOrUpdate(true);
            reportExtractService.runReport(entity);
            messages.info(new BundleKey("messages", "reportExtract.message.generate.ok"));
        } catch (ValidationException validationException) {
            log.error("Custom table structure does not match data structure: {}", validationException.getMessage());
            messages.error("Custom table structure does not match data structure :" + validationException.getMessage());
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

    public StreamedContent getReportFile(ReportExtractExecutionResult reportResult) {
        try {
            String filePath = reportExtractService.getReporFilePath(reportResult);
            File file = new File(filePath);
            
            String mimeType = "text/csv";
            if (!FilenameUtils.getExtension(filePath).equals("csv")) {
                mimeType = "text/html";
            }

            byte[] byteArrayContent;
            try (FileInputStream input = new FileInputStream(file)) {
                byteArrayContent = IOUtils.toByteArray(input);
            }
            return new ByteArrayContent(byteArrayContent, mimeType, filePath.substring(filePath.lastIndexOf(File.separator) + 1));

        } catch (BusinessException | IOException e) {
            log.error("Failed loading report file", e);
            return null;
        }
    }

}
