package org.meveo.apiv2.export;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.utilities.FieldsNotImportedStringCollectionDto;
import org.meveo.api.dto.response.utilities.ImportExportResponseDto;
import org.meveo.api.exception.*;
import org.meveo.export.*;
import org.meveo.model.IEntity;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.model.crm.Provider;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.communication.impl.MeveoInstanceService;
import org.meveo.util.ApplicationProvider;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.meveo.api.MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION;
import static org.reflections.Reflections.log;

public class ImportExportResourceImpl implements ImportExportResource {
    @Inject
    private EntityExportImportService entityExportImportService;

    @Inject
    private MeveoInstanceService meveoInstanceService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    private LinkedHashMap<String, Future<ExportImportStatistics>> executionResults = new LinkedHashMap<>();


    @Override
    public Response exportData(ExportConfig exportConfig) {
        Map<String, Object> parameters = buildParams(exportConfig);
        ExportTemplate template = resolveTemplate(exportConfig);
        if (exportConfig.getFileName() == null) {
            throw new BadRequestException("file name parameter is mandatory");
        }
        template.setName(exportConfig.getFileName());
        var executionId = (new Date()).getTime() + "_" + exportConfig.getFileName();
        Future<ExportImportStatistics> exportImportFuture =
                entityExportImportService.exportEntities(List.of(template), parameters);
        return buildResponse(executionId, exportImportFuture, (Boolean) parameters.getOrDefault("xml", false));
    }

    @Override
    public ImportExportResponseDto importData(MultipartFormDataInput input) {

            cleanupOldImportResults();
            InputPart inputPart = extractFileInputPart(input);
            boolean checkForStatus = extractCheckStatusFlag(input);
            String fileName = getFileName(inputPart.getHeaders());
            if (fileName == null) {
                throw new MissingParameterException("Missing a file name");
            }

            // Convert the uploaded file from inputstream to a file
            File tempFile = extractImportFile(inputPart, fileName);

            String executionId = generateExecutionId(fileName);    
            ExportImportStatistics exportImport= null;
        try {
        	if(tempFile!=null)
        	log.info("Received file {} from remote meveo instance. Saved to {} for importing. Execution id {}", fileName, tempFile.getAbsolutePath(), executionId);
            exportImport = entityExportImportService.importEntitiesSynchronously(tempFile, fileName.replaceAll(" ", "_"), false, true, true, checkForStatus);
        } catch (StatusChangeViolationException e) {
            throw new BusinessApiException(e.getMessage());
        }

        // executionResults.put(executionId, exportImport);
            return importStatisticsToDto(executionId, exportImport);




    }

    public ImportExportResponseDto importStatisticsToDto(String executionId, ExportImportStatistics statistics) {
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
        for (Map.Entry<Class, Integer> summaryInfo : statistics.getSummary().entrySet()) {
            dto.getSummary().put(summaryInfo.getKey().getName(), summaryInfo.getValue());
        }

        dto.setImportResultDto(statistics.getImportResultDto());
        return dto;
    }

    private InputPart extractFileInputPart(MultipartFormDataInput input) {
        List<InputPart> inputParts = input.getFormDataMap().get("uploadedFile");
        if (inputParts == null) {
            throw new MissingParameterException("Missing a file. File is expected as part name 'uploadedFile'");
        }
        return inputParts.get(0);

    }

    private boolean extractCheckStatusFlag(MultipartFormDataInput input) {
        List<InputPart> inputParts = input.getFormDataMap().get("checkStatus");
        if (inputParts == null) {
            return false;
        }
        try {
             return Boolean.valueOf(inputParts.get(0).getBodyAsString());
        }catch (IOException e) { throw new RuntimeException(e);}


    }

    private File extractImportFile(InputPart inputPart, String fileName) {

        File tempFile = null;
            try {
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                tempFile = File.createTempFile(FilenameUtils.getBaseName(fileName).replaceAll(" ", "_"), "." + FilenameUtils.getExtension(fileName));
                FileUtils.copyInputStreamToFile(inputStream, tempFile);
                return tempFile;
            } catch (IOException e) {
                log.error("Failed to save uploaded {} file to temp file {}", fileName, tempFile, e);
                throw new RuntimeException(e);
            }
    }

    private String generateExecutionId(String fileName){
        return (new Date()).getTime() + "_" + fileName;
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

    /**
     * Remove expired import results - keep for 1 hour only
     */
    private void cleanupOldImportResults() {

        long hourAgo = (new Date()).getTime() - 3600000;
        executionResults = executionResults.keySet().stream()
                .filter(key -> Long.parseLong(key.substring(0, key.indexOf('_'))) > hourAgo)
                .collect(Collectors.toMap(key -> key, key -> executionResults.get(key), (x, y) -> y, LinkedHashMap::new));
    }


    private Map<String, Object> buildParams(ExportConfig exportConfig) {
        Map<String, Object> parameters = new HashMap<>();
        if (ExportConfig.ExportType.XML.equals(exportConfig.getExportType())) {
            parameters.put("xml", true);
        }
        if (ExportConfig.ExportType.REMOTE_INSTANCE.equals(exportConfig.getExportType())) {
            parameters.put("remoteInstance", retrieveInstance(exportConfig.getInstanceCode()));
        }
        List<String> entityCodes = exportConfig.getEntityCodes();
        if (entityCodes != null && !entityCodes.isEmpty()) {
            parameters.put("code_in", entityCodes);
        }
        return parameters;
    }

    private ExportTemplate resolveTemplate(ExportConfig exportConfig) {
        var template = new ExportTemplate();
        if (exportConfig.getExportTemplateName() != null) {
            template = entityExportImportService.getExportImportTemplate(exportConfig.getExportTemplateName());
            if (template == null) {
                throw new InvalidParameterException("template with name " + exportConfig.getExportTemplateName() + " does not exist.");
            }
        } else if (exportConfig.getEntityClass() != null) {
            try {
                template.setEntityToExport((Class<? extends IEntity>) Class.forName(exportConfig.getEntityClass()));
            } catch (ClassNotFoundException e) {
                throw new InvalidParameterException("class with name " + exportConfig.getEntityClass() + " does not exist, please use a ful package name");
            }
        } else {
            throw new BadRequestException("you should either provide entity class name to export or export template name as parameter");
        }
        return template;
    }

    private Response buildResponse(String executionId, Future<ExportImportStatistics> exportImportStatistics, boolean isXml) {
        if (isXml) {
            try {
                while (!exportImportStatistics.isDone()) {
                    Thread.sleep(300);
                }
                ExportResponse exportResponse = exportImportStatisticsToDto(executionId, exportImportStatistics.get());
                return Response.ok().entity(exportResponse).build();
            } catch (InterruptedException | ExecutionException exception) {
                throw new MeveoApiException(GENERIC_API_EXCEPTION,
                        "Failed while executing export " + exception.getClass().getName()
                                + " " + exception.getMessage());
            }
        }
        return Response.ok().type(MediaType.APPLICATION_JSON_TYPE)
                .entity(ImmutableExportResponse.builder().executionId(executionId).build())
                .build();
    }

    private ExportResponse exportImportStatisticsToDto(String executionId, ExportImportStatistics statistics) {
        ImmutableExportResponse.Builder builder = ImmutableExportResponse.builder()
                .executionId(executionId);

        if (statistics.getException() != null) {
            builder.exceptionMessage(statistics.getException().getClass().getSimpleName() + ": " + statistics.getException().getMessage());
        }
        builder.errorMessageKey(statistics.getErrorMessageKey());

        if (!statistics.getFieldsNotImported().isEmpty()) {
            builder.fieldsNotImported(new HashMap<>());
            for (Map.Entry<String, Collection<String>> entry : statistics.getFieldsNotImported().entrySet()) {
                builder.fieldsNotImported(Collections.singletonMap(entry.getKey(), entry.getValue().stream().collect(Collectors.joining(", "))));
            }
        }
        for (Map.Entry<Class, Integer> summaryInfo : statistics.getSummary().entrySet()) {
            builder.summary(Collections.singletonMap(summaryInfo.getKey().getName(), summaryInfo.getValue()));
        }

        builder.fileContent(statistics.getFileContent());
        return builder.build();
    }

    private MeveoInstance retrieveInstance(String instanceCode) {
        return ofNullable(meveoInstanceService.findByCode(instanceCode))
                .orElseThrow(() -> new EntityDoesNotExistsException("Instance not found instance code : " + instanceCode));
    }
}
