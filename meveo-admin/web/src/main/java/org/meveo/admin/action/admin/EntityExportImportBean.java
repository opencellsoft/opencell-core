package org.meveo.admin.action.admin;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.enterprise.context.Conversation;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Entity;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.LazyDataModelWSize;
import org.meveo.api.dto.response.utilities.ImportExportResponseDto;
import org.meveo.commons.utils.ParamBean;
import org.meveo.export.EntityExportImportService;
import org.meveo.export.ExportImportStatistics;
import org.meveo.export.ExportTemplate;
import org.meveo.export.RemoteAuthenticationException;
import org.meveo.export.RemoteImportException;
import org.meveo.model.BaseEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldPeriod;
import org.meveo.model.crm.Provider;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.reflections.Reflections;
import org.slf4j.Logger;

import com.thoughtworks.xstream.XStream;

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
    @CurrentProvider
    private Provider currentProvider;

    private ParamBean param = ParamBean.getInstance();

    private boolean requireFK = true;

    private Provider forceToProvider;

    private ExportTemplate selectedExportTemplate;

    private DataModel<? extends IEntity> dataModelToExport;

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

    public Provider getForceToProvider() {
        return forceToProvider;
    }

    public void setForceToProvider(Provider forceToProvider) {
        this.forceToProvider = forceToProvider;
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
        if (dataModelToExport.getRowCount() > 0) {
            selectedExportTemplate = getExportImportTemplateForClass(dataModelToExport.iterator().next().getClass());
        } else {
            selectedExportTemplate = null;
        }
    }

    @SuppressWarnings("rawtypes")
    public DataModel getDataModelToExport() {
        return dataModelToExport;
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

            final Map<String, ExportTemplate> templates = loadExportImportTemplates(inputFilters);

            exportTemplates = new LazyDataModelWSize() {

                private static final long serialVersionUID = -5796910936316457328L;

                @SuppressWarnings("unchecked")
                @Override
                public List load(int first, int pageSize, String sortField, SortOrder sortOrder, Map filters) {

                    setRowCount(templates.size());

                    if (getRowCount() > 0) {
                        int toNr = first + pageSize;
                        return new LinkedList(templates.entrySet()).subList(first, getRowCount() <= toNr ? getRowCount() : toNr);

                    } else {
                        return new ArrayList();
                    }
                }
            };
        }
        return exportTemplates;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map<String, ExportTemplate> loadExportImportTemplates(Map<String, Object> inputFilters) {

        Map<String, ExportTemplate> templates = new TreeMap<String, ExportTemplate>();

        Reflections reflections = new Reflections("org.meveo.model");
        Set<Class<? extends IEntity>> classes = reflections.getSubTypesOf(IEntity.class);

        if (inputFilters.get(FILTER_TEMPLATENAME) != null) {
            inputFilters.put(FILTER_TEMPLATENAME, ((String) inputFilters.get(FILTER_TEMPLATENAME)).toLowerCase());
        }

        // Don't loop through classes when only "complex" templates are of interest
        if (!inputFilters.containsKey(FILTER_COMPLEX) || !(boolean) inputFilters.get(FILTER_COMPLEX)) {
            for (Class clazz : classes) {

                if (!clazz.isAnnotationPresent(Entity.class) || !IEntity.class.isAssignableFrom(clazz)) {
                    continue;
                }

                // Filter by a template name
                if (inputFilters.get(FILTER_TEMPLATENAME) != null && !clazz.getName().toLowerCase().contains((String) inputFilters.get(FILTER_TEMPLATENAME))) {
                    continue;
                }

                ExportTemplate exportTemplate = new ExportTemplate();

                // Automatically add provider as filtering parameter
                if (BaseEntity.class.isAssignableFrom(clazz)) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("provider", "provider");
                    exportTemplate.setParameters(params);
                }
                // Automatically export custom fields for CF related entities
                if (ICustomFieldEntity.class.isAssignableFrom(clazz) ){
                    exportTemplate.getClassesToExportAsFull().add(CustomFieldInstance.class);
                    exportTemplate.getClassesToExportAsFull().add(CustomFieldPeriod.class);
                }
                exportTemplate.setName(clazz.getSimpleName());
                exportTemplate.setEntityToExport(clazz);
                templates.put(exportTemplate.getName(), exportTemplate);
            }
        }

        // Retrieve complex export template definitions from configuration
        XStream xstream = new XStream();
        xstream.alias("template", ExportTemplate.class);
        xstream.useAttributeFor(ExportTemplate.class, "name");
        xstream.useAttributeFor(ExportTemplate.class, "entityToExport");
        xstream.useAttributeFor(ExportTemplate.class, "canDeleteAfterExport");

        xstream.setMode(XStream.NO_REFERENCES);

        List<ExportTemplate> templatesFromXml = (List<ExportTemplate>) xstream.fromXML(this.getClass().getClassLoader().getResourceAsStream("exportImportTemplates.xml"));

        for (ExportTemplate exportTemplate : templatesFromXml) {
            // Filter by a template name
            if (inputFilters.get("templateName") != null && !exportTemplate.getName().toLowerCase().contains((String) inputFilters.get("templateName"))) {
                continue;
            }
            templates.put(exportTemplate.getName(), exportTemplate);
        }
        return templates;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private ExportTemplate getExportImportTemplateForClass(Class clazz) {

        // Check for a match by a classname in complex export template definitions from configuration
        XStream xstream = new XStream();
        xstream.alias("template", ExportTemplate.class);
        xstream.useAttributeFor(ExportTemplate.class, "name");
        xstream.useAttributeFor(ExportTemplate.class, "entityToExport");
        xstream.useAttributeFor(ExportTemplate.class, "canDeleteAfterExport");

        xstream.setMode(XStream.NO_REFERENCES);

        List<ExportTemplate> templatesFromXml = (List<ExportTemplate>) xstream.fromXML(this.getClass().getClassLoader().getResourceAsStream("exportImportTemplates.xml"));

        for (ExportTemplate exportTemplate : templatesFromXml) {
            // Filter by a template name
            if (exportTemplate.getName().equalsIgnoreCase(clazz.getSimpleName())
                    || (exportTemplate.getEntityToExport() != null && exportTemplate.getEntityToExport().equals(clazz))) {
                return exportTemplate;
            }
        }

        // Create a default export template if not found
        ExportTemplate exportTemplate = new ExportTemplate();

        // Automatically add provider as filtering parameter
        if (BaseEntity.class.isAssignableFrom(clazz)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("provider", "provider");
            exportTemplate.setParameters(params);
        }
        // Automatically export custom fields for CF related entities
        if (ICustomFieldEntity.class.isAssignableFrom(clazz) ){
            exportTemplate.getClassesToExportAsFull().add(CustomFieldInstance.class);
            exportTemplate.getClassesToExportAsFull().add(CustomFieldPeriod.class);
        }
        exportTemplate.setName(clazz.getSimpleName());
        exportTemplate.setEntityToExport(clazz);

        return exportTemplate;
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
        parameters.put("provider", currentProvider);

        try {
            exportImportFuture = entityExportImportService.exportEntities(exportTemplate, parameters, null);
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
     * @param exportTemplate Export template
     */
    public void export() {

        exportImportFuture = null;
        remoteImportResult = null;
        remoteMeveoInstance = (MeveoInstance) exportParameters.get(EntityExportImportService.EXPORT_PARAM_REMOTE_INSTANCE);

        if (exportParameters.get("provider") == null) {
            exportParameters.put("provider", currentProvider);
        }

        try {

            exportImportFuture = entityExportImportService.exportEntities(selectedExportTemplate, exportParameters, dataModelToExport);
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

                exportImportFuture = entityExportImportService.importEntities(tempFile, event.getFile().getFileName().replaceAll(" ", "_"), false, !requireFK, forceToProvider);
                messages.info(new BundleKey("messages", "export.import.inProgress"), event.getFile().getFileName());

            } catch (Exception e) {
                log.error("Failed to import file " + event.getFile().getFileName(), e);
                messages.info(new BundleKey("messages", "export.importFailed"), event.getFile().getFileName(),
                    e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
            }
        }
    }

    public String getDatePattern() {
        return param.getProperty("meveo.dateFormat", "dd/MM/yyyy");
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