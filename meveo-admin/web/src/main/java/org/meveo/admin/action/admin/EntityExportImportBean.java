package org.meveo.admin.action.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Future;

import javax.enterprise.context.Conversation;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Entity;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.export.EntityExportImportService;
import org.meveo.export.ExportImportStatistics;
import org.meveo.export.ExportTemplate;
import org.meveo.model.BaseEntity;
import org.meveo.model.IEntity;
import org.meveo.model.crm.Provider;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;

import com.thoughtworks.xstream.XStream;

@Named
@ViewScoped
public class EntityExportImportBean implements Serializable {

    private static final long serialVersionUID = -8190794739629651135L;

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
    private Provider provider;

    private ParamBean param = ParamBean.getInstance();

    private boolean requireFK;

    private Provider forceToProvider;

    private ExportTemplate selectedExportTemplate;

    /** Entity selection for export search criteria. */
    protected Map<String, Object> exportParameters = new HashMap<String, Object>();

    /**
     * Datamodel for lazy dataloading in export templates.
     */
    @SuppressWarnings("rawtypes")
    protected LazyDataModel exportTemplates;

    private Future<ExportImportStatistics> exportImportFuture;

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

            exportTemplates = new LazyDataModel() {

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

        List<Class> classes = null;
        try {
            classes = ReflectionUtils.getClasses("org.meveo.model");
        } catch (Exception e) {
            log.error("Failed to get a list of classes for a model package", e);
        }

        if (inputFilters.get("templateName") != null) {
            inputFilters.put("templateName", ((String) inputFilters.get("templateName")).toLowerCase());
        }

        if (!inputFilters.containsKey("complex") || !(boolean) inputFilters.get("complex")) {
            for (Class clazz : classes) {

                if (!clazz.isAnnotationPresent(Entity.class) || !IEntity.class.isAssignableFrom(clazz)) {
                    continue;
                }

                // Filter by a template name
                if (inputFilters.get("templateName") != null && !clazz.getName().toLowerCase().contains((String) inputFilters.get("templateName"))) {
                    continue;
                }

                ExportTemplate exportTemplate = new ExportTemplate();

                if (BaseEntity.class.isAssignableFrom(clazz)) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("provider", "provider");
                    exportTemplate.setParameters(params);
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

    /**
     * Export entities for a given export template. No entity search criteria.
     * 
     * @param exportTemplate Export template
     */
    public void export(ExportTemplate exportTemplate) {

        exportImportFuture = null;
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("provider", provider);

        try {
            exportImportFuture = entityExportImportService.exportEntities(exportTemplate, parameters);
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

        if (exportParameters.get("provider") == null) {
            exportParameters.put("provider", provider);
        }

        try {

            exportImportFuture = entityExportImportService.exportEntities(selectedExportTemplate, exportParameters);
            messages.info(new BundleKey("messages", "export.exported"), selectedExportTemplate.getName());

        } catch (Exception e) {
            exportImportFuture = null;
            log.error("Failed to export entities for {} template with parameters {}", selectedExportTemplate, exportParameters, e);
            messages.info(new BundleKey("messages", "export.exportFailed"), selectedExportTemplate.getName(),
                e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }

        exportParameters = new HashMap<String, Object>();
    }

    public void uploadImportFile(FileUploadEvent event) {
        exportImportFuture = null;
        if (event.getFile() != null) {
            try {
                exportImportFuture = entityExportImportService.importEntities(event.getFile().getInputstream(), event.getFile().getFileName(), false, !requireFK, forceToProvider);
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
}