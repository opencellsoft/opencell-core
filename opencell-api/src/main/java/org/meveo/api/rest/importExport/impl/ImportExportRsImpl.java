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

package org.meveo.api.rest.importExport.impl;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.meveo.api.MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION;
import static org.meveo.api.dto.ActionStatusEnum.FAIL;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.utilities.FieldsNotImportedStringCollectionDto;
import org.meveo.api.dto.response.utilities.ImportExportRequestDto;
import org.meveo.api.dto.response.utilities.ImportExportResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.importExport.ImportExportRs;
import org.meveo.export.EntityExportImportService;
import org.meveo.export.ExportImportStatistics;
import org.meveo.export.ExportTemplate;
import org.meveo.export.RemoteAuthenticationException;
import org.meveo.model.IEntity;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.service.communication.impl.MeveoInstanceService;

/**
 * @author Andrius Karpavicius
 * 
 */
@ApplicationScoped
@Interceptors({ WsRestApiInterceptor.class })
public class ImportExportRsImpl extends BaseRs implements ImportExportRs {

    @Inject
    private EntityExportImportService entityExportImportService;

    @Inject
    private MeveoInstanceService meveoInstanceService;

    private LinkedHashMap<String, Future<ExportImportStatistics>> executionResults = new LinkedHashMap<>();

    @Override
    public ImportExportResponseDto importData(MultipartFormDataInput input) {

        try {
            // Check user has utilities/remoteImport permission
            if (!currentUser.hasRole("remoteImport")) {
                throw new RemoteAuthenticationException("User does not have utilities/remoteImport permission");
            }

            cleanupImportResults();

            Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
            List<InputPart> inputParts = uploadForm.get("file");
            if (inputParts == null) {
                return new ImportExportResponseDto(FAIL, MeveoApiErrorCodeEnum.MISSING_PARAMETER, "Missing a file. File is expected as part name 'file'");
            }
            InputPart inputPart = inputParts.get(0);
            String fileName = getFileName(inputPart.getHeaders());
            if (fileName == null) {
                return new ImportExportResponseDto(FAIL, MeveoApiErrorCodeEnum.MISSING_PARAMETER, "Missing a file name");
            }

            // Convert the uploaded file from inputstream to a file

            File tempFile = null;
            try {
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                tempFile = File.createTempFile(FilenameUtils.getBaseName(fileName).replaceAll(" ", "_"), "." + FilenameUtils.getExtension(fileName));
                FileUtils.copyInputStreamToFile(inputStream, tempFile);

            } catch (IOException e) {
                log.error("Failed to save uploaded {} file to temp file {}", fileName, tempFile, e);
                return new ImportExportResponseDto(FAIL, GENERIC_API_EXCEPTION, e.getClass().getName() + " " + e.getMessage());
            }
            String executionId = (new Date()).getTime() + "_" + fileName;

            log.info("Received file {} from remote meveo instance. Saved to {} for importing. Execution id {}", fileName, tempFile.getAbsolutePath(), executionId);
            Future<ExportImportStatistics> exportImportFuture = entityExportImportService.importEntities(tempFile, fileName.replaceAll(" ", "_"), false, false, appProvider);

            executionResults.put(executionId, exportImportFuture);
            return new ImportExportResponseDto(executionId);

        } catch (RemoteAuthenticationException e) {
            log.error("Failed to authenticate for a rest call {}", e.getMessage());
            return new ImportExportResponseDto(FAIL, MeveoApiErrorCodeEnum.AUTHENTICATION_AUTHORIZATION_EXCEPTION, e.getMessage());

        } catch (Exception e) {
            log.error("Failed to import data from rest call", e);
            return new ImportExportResponseDto(FAIL, GENERIC_API_EXCEPTION, e.getClass().getName() + " " + e.getMessage());
        }

    }

    /**
     * Obtain a filename from a header. Header sample: { Content-Type=[image/png], Content-Disposition=[form-data; name="file"; filename="filename.extension"] }
     **/
    private String getFileName(MultivaluedMap<String, String> header) {

        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {

                String[] name = filename.split("=");

                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return null;
    }

    @Override
    public ImportExportResponseDto checkImportDataResult(String executionId) {
        log.debug("Checking remote import execution status {}", executionId);

        Future<ExportImportStatistics> future = executionResults.get(executionId);
        if (future == null) {
            return new ImportExportResponseDto(FAIL, MeveoApiErrorCodeEnum.INVALID_PARAMETER, "Execution with id " + executionId + " has expired");
        }

        if (future.isDone()) {
            try {
                log.info("Remote import execution {} status is {}", executionId, future.get());
                return exportImportStatisticsToDto(executionId, future.get());

            } catch (InterruptedException | ExecutionException e) {
                return new ImportExportResponseDto(FAIL, GENERIC_API_EXCEPTION, "Failed while executing import " + e.getClass().getName()
                        + " " + e.getMessage());
            }
        } else {
            log.info("Remote import execution {} status is still in progress", executionId);
            return new ImportExportResponseDto(executionId);
        }
    }

    @Override
    public ImportExportResponseDto exportData(ImportExportRequestDto importExportRequestDto) {
        Map<String, Object> parameters = buildParamFrom(importExportRequestDto);
        var template = new ExportTemplate();
        var fileName = importExportRequestDto.getFileName();
        template.setName(fileName);
        var executionId = (new Date()).getTime() + "_" + fileName;
        Future<ExportImportStatistics> exportImportFuture =
                entityExportImportService.exportEntities(List.of(template), parameters);
        executionResults.put(executionId, exportImportFuture);
        return buildResponse(importExportRequestDto.getExportType(), executionId, exportImportFuture);
    }

    private Map<String, Object> buildParamFrom(ImportExportRequestDto exportData) {
        Map<String, Object> parameters = new HashMap<>();

        if(exportData.getExportType().equalsIgnoreCase("zip")) {
            parameters.put("zip", true);
        }
        if (exportData.getExportType().equalsIgnoreCase("remoteInstance")) {
            parameters.put("remoteInstance", retrieveInstance(exportData.getInstanceCode()));
        }
        return parameters;
    }

    private MeveoInstance retrieveInstance(String instanceCode) {
        return ofNullable(meveoInstanceService.findByCode(instanceCode))
                    .orElseThrow(() -> new EntityNotFoundException("Instance not found instance code : " + instanceCode));
    }

    private ImportExportResponseDto buildResponse(String responseType, String executionId,
                                                  Future<ExportImportStatistics> exportImportStatistics) {
        if(responseType.equalsIgnoreCase("API_Response") && exportImportStatistics.isDone()) {
            try {
                return exportImportStatisticsToDto(executionId, exportImportStatistics.get());
            } catch (InterruptedException | ExecutionException exception) {
                return new ImportExportResponseDto(FAIL, GENERIC_API_EXCEPTION,
                        "Failed while executing export " + exception.getClass().getName()
                                + " " + exception.getMessage());
            }
        } else {
            return new ImportExportResponseDto(executionId);
        }
    }

    @Override
    public ImportExportResponseDto entityList(ImportExportRequestDto importExportRequestDto) {
        var template = new ExportTemplate();
        template.setName(importExportRequestDto.getFileName());
        Class entityToExport;
        try {
            entityToExport = Class.forName(importExportRequestDto.getEntityToExport());
        } catch (ClassNotFoundException exception) {
            return new ImportExportResponseDto(FAIL, GENERIC_API_EXCEPTION, "Failed to load Entity");
        }
        template.setEntityToExport(entityToExport);
        String result = entityExportImportService.generateEntitiesList(template);
        return new ImportExportResponseDto(result);
    }

    @Override
    public ImportExportResponseDto exportDataFromEntityList(MultipartFormDataInput input) {
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        var template = new ExportTemplate();
        var requestDto = new ImportExportRequestDto();

        InputPart inputPart = buildInputPart(uploadForm, "file");
        InputPart parameter = buildInputPart(uploadForm, "exportType");
        template.setName(getFileName(inputPart.getHeaders()));
        var executionId = (new Date()).getTime() + "_" + template.getName();
        try {
            InputStream inputStream = inputPart.getBody(InputStream.class, null);
            InputStream inputParam = parameter.getBody(InputStream.class, null);
            String exportType = readParameters(inputParam);
            requestDto.setExportType(exportType);
            if(exportType.equalsIgnoreCase("remoteInstance")) {
                InputPart remoteInstance = buildInputPart(uploadForm, "instanceCode");
                InputStream instanceStream = remoteInstance.getBody(InputStream.class, null);
                requestDto.setInstanceCode(readParameters(instanceStream));
            }
            Map<String, Object> parameters = buildParamFrom(requestDto);
            List<String> entitiesFromFile = readFile(inputStream);
            template.setClassesToExportAsFull(entitiesToExport(entitiesFromFile));
            Future<ExportImportStatistics> exportImportFuture =
                    entityExportImportService.exportEntities(List.of(template), parameters);
            executionResults.put(executionId, exportImportFuture);
            return buildResponse(exportType, executionId, exportImportFuture);
        } catch (IOException exception) {
            log.error(exception.getMessage());
            return new ImportExportResponseDto(FAIL, GENERIC_API_EXCEPTION, exception.getMessage());
        }
    }

    private InputPart buildInputPart(Map<String, List<InputPart>> uploadForm, String param) {
        List<InputPart> inputParts = uploadForm.get(param);
        return ofNullable(inputParts.get(0))
                    .orElseThrow(() -> new MeveoApiException("Missing a file. File is expected as part name 'file'"));
    }

    private String readParameters(InputStream body) {
        return new BufferedReader(new InputStreamReader(body))
                                            .lines()
                                            .collect(joining());
    }

    private List<String> readFile(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return reader.lines()
                    .map(line -> line.split(","))
                    .map(array -> array[1])
                    .collect(toList());
    }

    private List<Class<? extends IEntity>> entitiesToExport(List<String> entities) {
        List<Class<? extends IEntity>> entitiesToExport = new ArrayList<>();
        Class entityToExport;
        for (String entity: entities) {
            try {
                entityToExport = Class.forName(entity);
                entitiesToExport.add(entityToExport);
            } catch (ClassNotFoundException exception) {
                log.error(exception.getMessage());
            }
        }
        return entitiesToExport;
    }

    /**
     * Remove expired import results - keep for 1 hour only
     */
    private void cleanupImportResults() {

        long hourAgo = (new Date()).getTime() - 3600000;

        List<String> keysToRemove = new ArrayList<>();

        for (String key : executionResults.keySet()) {
            long exportTime = Long.parseLong(key.substring(0, key.indexOf('_')));
            if (exportTime < hourAgo) {
                keysToRemove.add(key);
            }
        }

        for (String key : keysToRemove) {
            log.debug("Removing remote import execution result {}", key);
            executionResults.remove(key);
        }
    }

    @SuppressWarnings("rawtypes")
    public ImportExportResponseDto exportImportStatisticsToDto(String executionId, ExportImportStatistics statistics) {
        ImportExportResponseDto dto = new ImportExportResponseDto(executionId);

        if (statistics.getException() != null) {
            dto.setExceptionMessage(statistics.getException().getClass().getSimpleName() + ": " + statistics.getException().getMessage());
        }
        dto.setFailureMessageKey(statistics.getErrorMessageKey());

        if (!statistics.getFieldsNotImported().isEmpty()) {
            dto.setFieldsNotImported(new HashMap<>());
            for (Map.Entry<String, Collection<String>> entry : statistics.getFieldsNotImported().entrySet()) {
                dto.getFieldsNotImported().put(entry.getKey(), new FieldsNotImportedStringCollectionDto(entry.getValue()));
            }
        }
        dto.setSummary(new HashMap<>());
        for (Entry<Class, Integer> summaryInfo : statistics.getSummary().entrySet()) {
            dto.getSummary().put(summaryInfo.getKey().getName(), summaryInfo.getValue());
        }

        return dto;
    }
}