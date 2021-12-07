package org.meveo.apiv2.export;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.export.EntityExportImportService;
import org.meveo.export.ExportImportStatistics;
import org.meveo.export.ExportTemplate;
import org.meveo.model.IEntity;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.service.communication.impl.MeveoInstanceService;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.meveo.api.MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION;

public class ImportExportResourceImpl implements ImportExportResource {
    @Inject
    private EntityExportImportService entityExportImportService;

    @Inject
    private MeveoInstanceService meveoInstanceService;

    @Override
    public Response exportData(ExportConfig exportConfig) {
        Map<String, Object> parameters = buildParams(exportConfig);
        ExportTemplate template = resolveTemplate(exportConfig);
        if(exportConfig.getFileName()==null){
            throw new BadRequestException("file name parameter is mandatory");
        }
        template.setName(exportConfig.getFileName());
        var executionId = (new Date()).getTime() + "_" + exportConfig.getFileName();
        Future<ExportImportStatistics> exportImportFuture =
                entityExportImportService.exportEntities(List.of(template), parameters);
        return buildResponse(executionId, exportImportFuture, (Boolean) parameters.getOrDefault("xml", false));
    }

    private Map<String, Object> buildParams(ExportConfig exportConfig) {
        Map<String, Object> parameters = new HashMap<>();
        if(ExportConfig.ExportType.XML.equals(exportConfig.getExportType())){
            parameters.put("xml", true);
        }
        if (ExportConfig.ExportType.REMOTE_INSTANCE.equals(exportConfig.getExportType())) {
            parameters.put("remoteInstance", retrieveInstance(exportConfig.getInstanceCode()));
        }
        List<String> entityCodes = exportConfig.getEntityCodes();
        if(entityCodes !=null && !entityCodes.isEmpty()){
            parameters.put("code_in", entityCodes);
        }
        return parameters;
    }

    private ExportTemplate resolveTemplate(ExportConfig exportConfig) {
        var template = new ExportTemplate();
        if(exportConfig.getExportTemplateName() != null){
            template = entityExportImportService.getExportImportTemplate(exportConfig.getExportTemplateName());
            if(template == null){
                throw new InvalidParameterException("template with name "+ exportConfig.getExportTemplateName()+" does not exist.");
            }
        }else if(exportConfig.getEntityClass() != null){
            try {
                template.setEntityToExport((Class<? extends IEntity>) Class.forName(exportConfig.getEntityClass()));
            } catch (ClassNotFoundException e) {
                throw new InvalidParameterException("class with name "+ exportConfig.getEntityClass()+" does not exist, please use a ful package name");
            }
        } else {
            throw new BadRequestException("you should either provide entity class name to export or export template name as parameter");
        }
        return template;
    }

    private Response buildResponse(String executionId, Future<ExportImportStatistics> exportImportStatistics, boolean isXml) {
        if(isXml){
            try {
                while (!exportImportStatistics.isDone()){
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
