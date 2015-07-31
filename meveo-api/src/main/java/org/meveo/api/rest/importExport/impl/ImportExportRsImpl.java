package org.meveo.api.rest.importExport.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.utilities.ImportExportResponseDto;
import org.meveo.api.exception.LoginException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.importExport.ImportExportRs;
import org.meveo.export.EntityExportImportService;
import org.meveo.export.ExportImportStatistics;

/**
 * @author Andrius Karpavicius
 * 
 */
@ApplicationScoped
@Interceptors({ LoggingInterceptor.class })
public class ImportExportRsImpl extends BaseRs implements ImportExportRs {

    @Inject
    private EntityExportImportService entityExportImportService;

    private LinkedHashMap<String, Future<ExportImportStatistics>> executionResults = new LinkedHashMap<String, Future<ExportImportStatistics>>();

    @Override
    public ImportExportResponseDto importData(MultipartFormDataInput input) {

        try {
            // Only for authentication check
            getCurrentUser();

            cleanupImportResults();

            Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
            List<InputPart> inputParts = uploadForm.get("file");
            if (inputParts == null) {
                return new ImportExportResponseDto(ActionStatusEnum.FAIL, MeveoApiErrorCode.MISSING_PARAMETER, "Missing a file. File is expected as part name 'file'");
            }
            InputPart inputPart = inputParts.get(0);
            String fileName = getFileName(inputPart.getHeaders());
            if (fileName == null) {
                return new ImportExportResponseDto(ActionStatusEnum.FAIL, MeveoApiErrorCode.MISSING_PARAMETER, "Missing a file name");
            }

            // Convert the uploaded file from inputstream to a file

            File tempFile = null;
            try {
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                tempFile = File.createTempFile(FilenameUtils.getBaseName(fileName).replaceAll(" ", "_"), "." + FilenameUtils.getExtension(fileName));
                FileUtils.copyInputStreamToFile(inputStream, tempFile);

            } catch (IOException e) {
                log.error("Failed to save uploaded {} file to temp file {}", fileName, tempFile, e);
                return new ImportExportResponseDto(ActionStatusEnum.FAIL, MeveoApiErrorCode.GENERIC_API_EXCEPTION, e.getClass().getName() + " " + e.getMessage());
            }
            String executionId = (new Date()).getTime() + "_" + fileName;

            log.info("Received file {} from remote meveo instance. Saved to {} for importing. Execution id {}", fileName, tempFile.getAbsolutePath(), executionId);
            Future<ExportImportStatistics> exportImportFuture = entityExportImportService.importEntities(tempFile, fileName.replaceAll(" ", "_"), false, false, null);

            executionResults.put(executionId, exportImportFuture);
            return new ImportExportResponseDto(executionId);

        } catch (LoginException e) {
            log.error("Failed to authenticate for a rest call", e);
            return new ImportExportResponseDto(ActionStatusEnum.FAIL, MeveoApiErrorCode.AUTHENTICATION_AUTHORIZATION_EXCEPTION, e.getMessage());

        } catch (Exception e) {
            log.error("Failed to import data from rest call", e);
            return new ImportExportResponseDto(ActionStatusEnum.FAIL, MeveoApiErrorCode.GENERIC_API_EXCEPTION, e.getClass().getName() + " " + e.getMessage());
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
            return new ImportExportResponseDto(ActionStatusEnum.FAIL, MeveoApiErrorCode.INVALID_PARAMETER, "Execution with id " + executionId + " has expired");
        }

        if (future.isDone()) {
            try {
                log.info("Remote import execution {} status is {}", executionId, future.get());
                return new ImportExportResponseDto(executionId, future.get());

            } catch (InterruptedException | ExecutionException e) {
                return new ImportExportResponseDto(ActionStatusEnum.FAIL, MeveoApiErrorCode.GENERIC_API_EXCEPTION, "Failed while executing import " + e.getClass().getName() + " "
                        + e.getMessage());
            }
        } else {
            log.info("Remote import execution {} status is still in progress", executionId);
            return new ImportExportResponseDto(executionId);
        }
    }

    /**
     * Remove expired import results - keep for 1 hour only
     */
    private void cleanupImportResults() {

        long hourAgo = (new Date()).getTime() - 3600000;
        for (String key : executionResults.keySet()) {

            long exportTime = Long.parseLong(key.substring(0, key.indexOf('_')));
            if (exportTime < hourAgo) {
                log.debug("Removing remote import execution result {}", key);
                executionResults.remove(key);
            }
        }
    }
}
