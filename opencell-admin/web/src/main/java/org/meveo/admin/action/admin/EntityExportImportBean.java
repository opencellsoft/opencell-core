package org.meveo.admin.action.admin;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.enterprise.context.Conversation;
import javax.faces.model.DataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.api.dto.response.utilities.ImportExportResponseDto;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.export.EntityExportImportService;
import org.meveo.export.ExportImportStatistics;
import org.meveo.export.ExportTemplate;
import org.meveo.export.RemoteAuthenticationException;
import org.meveo.export.RemoteImportException;
import org.meveo.model.IEntity;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.model.crm.Provider;
import org.meveo.util.ApplicationProvider;
import org.meveo.util.view.LazyDataModelWSize;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;

@Named
@ViewScoped
public class EntityExportImportBean implements Serializable {

    private static final long serialVersionUID = -8190794739629651135L;

    private static String FILTER_TEMPLATENAME = "templateName";
    private static String FILTER_COMPLEX = "complex";

    @Inject
    private Logger log;

    /** Search filters. */
    protected Map<String, Object> filters = new HashMap<String, Object>();

    @Inject
    private EntityExportImportService entityExportImportService;

    @Inject
    protected Messages messages;

    @Inject
    protected Conversation conversation;

    @Inject
    @ApplicationProvider
    private Provider appProvider;

    /** paramBeanFactory */
    @Inject
    private ParamBeanFactory paramBeanFactory;

    private boolean requireFK = true;

    private ExportTemplate selectedExportTemplate;

    private DataModel<? extends IEntity> dataModelToExport;

    private List<? extends IEntity> selectedEntitiesToExport;

    /** Entity selection for export search criteria. */
    protected Map<String, Object> exportParameters = initExportParameters();

    /**
     * Datamodel for lazy dataloading in export templates.
     */
    @SuppressWarnings("rawtypes")
    protected LazyDataModel exportTemplates;

    private Future<ExportImportStatistics> exportImportFuture;

    private MeveoInstance remoteMeveoInstance;

    private ImportExportResponseDto remoteImportResult;

    public boolean isRequireFK() {
        return requireFK;
    }

    public void setRequireFK(boolean requireFK) {
        this.requireFK = requireFK;
    }

    public ExportTemplate getSelectedExportTemplate() {
        return selectedExportTemplate;
    }

    public void setSelectedExportTemplate(ExportTemplate selectedExportTemplate) {
        this.selectedExportTemplate = selectedExportTemplate;
    }

    public void setDataModelToExport(DataModel<? extends IEntity> dataModelToExport) {
        this.dataModelToExport = dataModelToExport;

        // Determine applicable template by matching a class name
        if (dataModelToExport.getRowCount() > 0 && selectedExportTemplate == null) {
            selectedExportTemplate = getExportImportTemplateForClass(dataModelToExport.iterator().next().getClass());
            // } else { Now that dataModelToExport or selectedEntitiesToExport can be set, dont reset selectedExportTemplate value
            // selectedExportTemplate = null;
        }
    }

    @SuppressWarnings("rawtypes")
    public DataModel getDataModelToExport() {
        return dataModelToExport;
    }

    public void setSelectedEntitiesToExport(List<? extends IEntity> selectedEntitiesToExport) {
        this.selectedEntitiesToExport = selectedEntitiesToExport;

        // Determine applicable template by matching a class name
        if (selectedEntitiesToExport != null && !selectedEntitiesToExport.isEmpty() && selectedExportTemplate == null) {
            selectedExportTemplate = getExportImportTemplateForClass(selectedEntitiesToExport.get(0).getClass());
            // } else {
            // selectedExportTemplate = null;
        }
    }

    public List<? extends IEntity> getSelectedEntitiesToExport() {
        return selectedEntitiesToExport;
    }

    /**
     * Clean search fields in datatable.
     */
    public void cleanExportTemplates() {
        exportTemplates = null;
        filters = new HashMap<String, Object>();
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }

    public Map<String, Object> getExportParameters() {
        return exportParameters;
    }

    public void setExportParameters(Map<String, Object> exportParameters) {
        this.exportParameters = exportParameters;
    }

    public void searchExportTemplates() {
        exportTemplates = null;
    }

    /**
     * Get Export template data model
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
    public LazyDataModel getExportTemplates() {
        return getExportTemplates(filters, false);
    }

    /**
     * Get Export template data model
     * 
     * @param inputFilters
     * @param forceReload
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
    public LazyDataModel getExportTemplates(Map<String, Object> inputFilters, boolean forceReload) {
        if (exportTemplates == null || forceReload) {

            // final Map<String, Object> filters = inputFilters;

            final Map<String, ExportTemplate> templates = filterExportImportTemplates(inputFilters);

            exportTemplates = new LazyDataModelWSize() {

                private static final long serialVersionUID = -5796910936316457328L;

                @SuppressWarnings("unchecked")
                @Override
                public List load(int first, int pageSize, String sortField, SortOrder sortOrder, Map filters) {

                    setRowCount(templates.size());

                    if (getRowCount() > 0) {
                        int toNr = first + pageSize;
                        if (toNr > templates.size()) {
                            first = 0;
                            pageSize = templates.size() - 1;
                        }
                        return new LinkedList(templates.entrySet()).subList(first, getRowCount() <= toNr ? getRowCount() : toNr);

                    } else {
                        return new ArrayList();
                    }
                }
            };
        }
        return exportTemplates;
    }

    /**
     * Filter export/import templates by name and groupedTemplate [yes/no]
     * 
     * @param inputFilters A map of filter values
     * @return A map of export templates with template naem as a key and export/import template as a value
     */
    private Map<String, ExportTemplate> filterExportImportTemplates(Map<String, Object> inputFilters) {

        Map<String, ExportTemplate> templates = new TreeMap<String, ExportTemplate>();

        boolean groupedOnly = inputFilters.containsKey(FILTER_COMPLEX) && (boolean) inputFilters.get(FILTER_COMPLEX);
        String templateName = inputFilters.get("templateName") != null ? ((String) inputFilters.get(FILTER_TEMPLATENAME)).toLowerCase() : null;

        for (ExportTemplate template : entityExportImportService.getExportImportTemplates().values()) {
            if ((!groupedOnly || (groupedOnly && template.isGroupedTemplate()))
                    && (templateName == null || (templateName != null && template.getName().toLowerCase().contains(templateName)))) {
                templates.put(template.getName(), template);
            }
        }

        return templates;
    }

    /**
     * Get export template for a particular class
     * 
     * @param clazz Class
     * @return Export/import template definition
     */
    @SuppressWarnings({ "rawtypes" })
    private ExportTemplate getExportImportTemplateForClass(Class clazz) {
        return entityExportImportService.getExportImportTemplate(clazz);
    }

    /**
     * Export entities for a given export template. No entity search criteria.
     * 
     * @param exportTemplate Export template
     */
    public void export(ExportTemplate exportTemplate) {

        exportImportFuture = null;
        remoteImportResult = null;
        remoteMeveoInstance = null;

        Map<String, Object> parameters = new HashMap<String, Object>();

        try {
            exportImportFuture = entityExportImportService.exportEntities(exportTemplate, parameters, null, null);
            messages.info(new BundleKey("messages", "export.exported"), exportTemplate.getName());

        } catch (Exception e) {
            exportImportFuture = null;
            log.error("Failed to export entities for {} template", selectedExportTemplate, e);
            messages.info(new BundleKey("messages", "export.exportFailed"), exportTemplate.getName(), e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }

        exportParameters = new HashMap<String, Object>();

    }

    /**
     * Export entities for a selected export template and given search criteria
     * 
     */
    public void export() {

        exportImportFuture = null;
        remoteImportResult = null;
        remoteMeveoInstance = (MeveoInstance) exportParameters.get(EntityExportImportService.EXPORT_PARAM_REMOTE_INSTANCE);

        try {

            exportImportFuture = entityExportImportService.exportEntities(selectedExportTemplate, exportParameters, dataModelToExport, selectedEntitiesToExport);
            messages.info(new BundleKey("messages", "export.exported"), selectedExportTemplate.getName());

        } catch (Exception e) {
            exportImportFuture = null;
            log.error("Failed to export entities for {} template with parameters {}", selectedExportTemplate, exportParameters, e);
            messages.info(new BundleKey("messages", "export.exportFailed"), selectedExportTemplate.getName(),
                e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }

        exportParameters = initExportParameters();
    }

    /**
     * Handle a file upload and import the file
     * 
     * @param event File upload event
     */
    public void uploadImportFile(FileUploadEvent event) {
        exportImportFuture = null;
        if (event.getFile() != null) {
            try {

                File tempFile = File.createTempFile(FilenameUtils.getBaseName(event.getFile().getFileName()).replaceAll(" ", "_"),
                    "." + FilenameUtils.getExtension(event.getFile().getFileName()));
                FileUtils.copyInputStreamToFile(event.getFile().getInputstream(), tempFile);

                exportImportFuture = entityExportImportService.importEntities(tempFile, event.getFile().getFileName().replaceAll(" ", "_"), false, !requireFK, null);
                messages.info(new BundleKey("messages", "export.import.inProgress"), event.getFile().getFileName());

            } catch (Exception e) {
                log.error("Failed to import file " + event.getFile().getFileName(), e);
                messages.info(new BundleKey("messages", "export.importFailed"), event.getFile().getFileName(),
                    e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
            }
        }
    }

    public String getDatePattern() {
        return paramBeanFactory.getInstance().getDateFormat();
    }

    protected void beginConversation() {
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    protected void endConversation() {
        if (!conversation.isTransient()) {
            conversation.end();
        }
    }

    public void preRenderView() {
        beginConversation();
    }

    public Future<ExportImportStatistics> getExportImportFuture() {
        return exportImportFuture;
    }

    public ImportExportResponseDto getRemoteImportResult() {
        return remoteImportResult;
    }

    private HashMap<String, Object> initExportParameters() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("zip", true);
        return params;
    }

    public void checkRemoteImportStatus() {
        if (!exportImportFuture.isDone()) {
            return;
        }
        try {
            String executionId = exportImportFuture.get().getRemoteImportExecutionId();
            if (executionId != null) {

                ImportExportResponseDto checkStatusResult = entityExportImportService.checkRemoteMeveoInstanceImportStatus(executionId, remoteMeveoInstance);
                if (checkStatusResult.isDone()) {
                    remoteImportResult = checkStatusResult;
                }
            }
        } catch (InterruptedException | ExecutionException | RemoteAuthenticationException | RemoteImportException e) {
            log.error("Failed to access export execution result", e);
        }
    }
}