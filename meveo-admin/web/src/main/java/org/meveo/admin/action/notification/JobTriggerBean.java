package org.meveo.admin.action.notification;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.UpdateMapTypeFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.RejectedImportException;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.commons.utils.CsvReader;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.ObservableEntity;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.notification.JobTrigger;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.model.notification.StrategyImportTypeEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.notification.JobTriggerService;
import org.meveo.service.script.ScriptInstanceService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@Named
@ViewScoped
public class JobTriggerBean extends UpdateMapTypeFieldBean<JobTrigger> {

	private static final long serialVersionUID = 6473465285480945644L;

	@Inject
	private JobTriggerService jobTriggerService;
	
	@Inject
	private JobInstanceService jobInstanceService;
	
	@Inject
	private ScriptInstanceService scriptInstanceService;

	ParamBean paramBean = ParamBean.getInstance();

	CsvReader csvReader = null;
	private UploadedFile file;

	private static final int CODE = 0;
	private static final int CLASS_NAME_FILTER = 1;
	private static final int EL_FILTER = 2;
	private static final int ACTIVE = 3;
	private static final int SCRIPT_INSTANCE_CODE = 4;
	private static final int EVENT_TYPE_FILTER = 5;
	private static final int JOB_INSTANCE_CODE = 6;

	private StrategyImportTypeEnum strategyImportType;

	CsvBuilder csv = null;
	private String providerDir = paramBean.getProperty("providers.rootDir", "/tmp/meveo_integr");
	private String existingEntitiesCsvFile = null;

	public JobTriggerBean() {
		super(JobTrigger.class);
	}

	@Override
	protected IPersistenceService<JobTrigger> getPersistenceService() {
		return jobTriggerService;
	}

    @Override
    public JobTrigger initEntity() {
    	JobTrigger jobTrigger = super.initEntity();
        extractMapTypeFieldFromEntity(jobTrigger.getJobParams(), "jobParams");
        extractMapTypeFieldFromEntity(jobTrigger.getParams(), "params");

        return jobTrigger;
    }

    @Override
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

    
       updateMapTypeFieldInEntity(entity.getParams(), "params");

        return super.saveOrUpdate(killConversation);
    }
    
	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("provider");
	}

	public void exportToFile() throws Exception {
		CsvBuilder csv = new CsvBuilder();
		csv.appendValue("Code");
		csv.appendValue("Classename filter");
		csv.appendValue("El filter");
		csv.appendValue("Active");
		csv.appendValue("Script instance code");
		csv.appendValue("Event type filter");
		csv.appendValue("Job instance code");
		csv.startNewLine();
		for (JobTrigger jobTrigger :(!filters.isEmpty()&& filters.size()>0) ? getLazyDataModel():jobTriggerService.list()) {
			csv.appendValue(jobTrigger.getCode());
			csv.appendValue(jobTrigger.getClassNameFilter());
			csv.appendValue(jobTrigger.getElFilter());
			csv.appendValue(jobTrigger.isDisabled() + "");
			csv.appendValue((jobTrigger.getScriptInstance()==null?"":jobTrigger.getScriptInstance().getCode()));
			csv.appendValue(jobTrigger.getEventTypeFilter() + "");
			csv.appendValue((jobTrigger.getJobInstance()==null?"":jobTrigger.getJobInstance().getCode()));
			csv.startNewLine();
		}
		InputStream inputStream = new ByteArrayInputStream(csv.toString().getBytes());
		csv.download(inputStream, "JobTriggers.csv");
	}

    public void handleFileUpload(FileUploadEvent event) throws Exception {
        try {
            file = event.getFile();
            log.debug("File uploaded " + file.getFileName());
            upload();
            messages.info(new BundleKey("messages", "import.csv.successful"));
        } catch (Exception e) {
            log.error("Failed to handle uploaded file {}", event.getFile().getFileName(), e);
            messages.error(new BundleKey("messages", "import.csv.failed"), e.getClass().getSimpleName() + " " + e.getMessage());
        }
    }

    private void upload() throws IOException, BusinessException {
        if (file == null) {
            return;
        }
        csvReader = new CsvReader(file.getInputstream(), ';', Charset.forName("ISO-8859-1"));
        csvReader.readHeaders();

        String existingEntitiesCSV = paramBean.getProperty("existingEntities.csv.dir", "existingEntitiesCSV");
        File dir = new File(providerDir + File.separator + getCurrentProvider().getCode() + File.separator + existingEntitiesCSV);
        dir.mkdirs();
        existingEntitiesCsvFile = dir.getAbsolutePath() + File.separator + "JobTriggers_" + new SimpleDateFormat("ddMMyyyyHHmmSS").format(new Date()) + ".csv";
        csv = new CsvBuilder();
        boolean isEntityAlreadyExist = false;
        while (csvReader.readRecord()) {
            String[] values = csvReader.getValues();
            JobTrigger existingEntity = jobTriggerService.findByCode(values[CODE], getCurrentProvider());
            if (existingEntity != null) {
                checkSelectedStrategy(values, existingEntity, isEntityAlreadyExist);
                isEntityAlreadyExist = true;
            } else {
                JobTrigger notif = new JobTrigger();
                notif.setCode(values[CODE]);
                notif.setClassNameFilter(values[CLASS_NAME_FILTER]);
                notif.setElFilter(values[EL_FILTER]);
                notif.setDisabled(Boolean.parseBoolean(values[ACTIVE]));                
                if (!StringUtils.isBlank(values[SCRIPT_INSTANCE_CODE])) {
                    ScriptInstance scriptInstance = scriptInstanceService.findByCode(values[SCRIPT_INSTANCE_CODE], getCurrentProvider()); 
                    notif.setScriptInstance(scriptInstance);
                }  
                notif.setEventTypeFilter(NotificationEventTypeEnum.valueOf(values[EVENT_TYPE_FILTER]));
                if (!StringUtils.isBlank(values[JOB_INSTANCE_CODE])) {
                    JobInstance jobInstance = jobInstanceService.findByCode(values[JOB_INSTANCE_CODE], getCurrentProvider()); 
                    notif.setJobInstance(jobInstance);
                }  
                jobTriggerService.create(notif);
            }
        }
        if (isEntityAlreadyExist && strategyImportType.equals(StrategyImportTypeEnum.REJECT_EXISTING_RECORDS)) {
            csv.writeFile(csv.toString().getBytes(), existingEntitiesCsvFile);
        }
    }

    public void checkSelectedStrategy(String[] values, JobTrigger existingEntity, boolean isEntityAlreadyExist) throws RejectedImportException {
		if (strategyImportType.equals(StrategyImportTypeEnum.UPDATED)) {
			existingEntity.setClassNameFilter(values[CLASS_NAME_FILTER]);
			existingEntity.setElFilter(values[EL_FILTER]);
			existingEntity.setDisabled(Boolean.parseBoolean(values[ACTIVE]));
            if (!StringUtils.isBlank(values[SCRIPT_INSTANCE_CODE])) {
                ScriptInstance scriptInstance = scriptInstanceService.findByCode(values[SCRIPT_INSTANCE_CODE], getCurrentProvider()); 
                existingEntity.setScriptInstance(scriptInstance);
            } 
			existingEntity.setEventTypeFilter(NotificationEventTypeEnum.valueOf(values[EVENT_TYPE_FILTER]));
			jobTriggerService.update(existingEntity);
		} else if (strategyImportType.equals(StrategyImportTypeEnum.REJECTE_IMPORT)) {
			throw new RejectedImportException("jobTrigger.rejectImport");
		} else if (strategyImportType.equals(StrategyImportTypeEnum.REJECT_EXISTING_RECORDS)) {
			if (!isEntityAlreadyExist) {
				csv.appendValue("Code");
				csv.appendValue("Classename filter");
				csv.appendValue("El filter");
				csv.appendValue("Active");
				csv.appendValue("Script instance code");
				csv.appendValue("Event type filter");
				csv.appendValue("Job instance code");
			}
			csv.startNewLine();
			csv.appendValue(values[CODE]);
			csv.appendValue(values[CLASS_NAME_FILTER]);
			csv.appendValue(values[EL_FILTER]);
			csv.appendValue(values[ACTIVE]);
			csv.appendValue(values[SCRIPT_INSTANCE_CODE]);
			csv.appendValue(values[EVENT_TYPE_FILTER]);
			csv.appendValue(values[JOB_INSTANCE_CODE]);
		}

	}

	public StrategyImportTypeEnum getStrategyImportType() {
		return strategyImportType;
	}

	public void setStrategyImportType(StrategyImportTypeEnum strategyImportType) {
		this.strategyImportType = strategyImportType;
	}

    /**
     * Autocomplete method for class filter field - search entity type classes with @ObservableEntity annotation
     * 
     * @param query A partial class name (including a package)
     * @return A list of classnames
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<String> autocompleteClassNames(String query) {

        List<Class> classes = null;
        try {
            classes = ReflectionUtils.getClasses("org.meveo.model");
        } catch (Exception e) {
            log.error("Failed to get a list of classes for a model package", e);
            return null;
        }

        String queryLc = query.toLowerCase();
        List<String> classNames = new ArrayList<String>();
        for (Class clazz : classes) {
            if (clazz.isAnnotationPresent(Entity.class) && clazz.isAnnotationPresent(ObservableEntity.class) && clazz.getName().toLowerCase().contains(queryLc)) {
                classNames.add(clazz.getName());
            }
        }

        Collections.sort(classNames);
        return classNames;
    }
    public Map<String, List<HashMap<String, String>>> getMapTypeFieldValues() {
        return mapTypeFieldValues;
    }

    public void setMapTypeFieldValues(Map<String, List<HashMap<String, String>>> mapTypeFieldValues) {
        this.mapTypeFieldValues = mapTypeFieldValues;
    }

	/**
     * Remove a value from a map type field attribute used to gather field values in GUI
     * 
     * @param fieldName Field name
     * @param valueInfo Value to remove
     */
    public void removeMapTypeFieldValue(String fieldName, Map<String, String> valueInfo) {
        mapTypeFieldValues.get(fieldName).remove(valueInfo);
    }

    /**
     * Add a value to a map type field attribute used to gather field values in GUI
     * 
     * @param fieldName Field name
     */
    public void addMapTypeFieldValue(String fieldName) {
        if (!mapTypeFieldValues.containsKey(fieldName)) {
            mapTypeFieldValues.put(fieldName, new ArrayList<HashMap<String, String>>());
        }
        mapTypeFieldValues.get(fieldName).add(new HashMap<String, String>());
    }

    /**
     * Extract values from a Map type field in an entity to mapTypeFieldValues attribute used to gather field values in GUI
     * 
     * @param entityField Entity field
     * @param fieldName Field name
     */
    public void extractMapTypeFieldFromEntity(Map<String, String> entityField, String fieldName) {

        mapTypeFieldValues.remove(fieldName);

        if (entityField != null) {
            List<HashMap<String, String>> fieldValues = new ArrayList<HashMap<String, String>>();
            mapTypeFieldValues.put(fieldName, fieldValues);
            for (Entry<String, String> setInfo : entityField.entrySet()) {
                HashMap<String, String> value = new HashMap<String, String>();
                value.put("key", setInfo.getKey());
                value.put("value", setInfo.getValue());
                fieldValues.add(value);
            }
        }
    }

    /**
     * Update Map type field in an entity from mapTypeFieldValues attribute used to gather field values in GUI
     * 
     * @param entityField Entity field
     * @param fieldName Field name
     */
    public void updateMapTypeFieldInEntity(Map<String, String> entityField, String fieldName) {
        entityField.clear();

        if (mapTypeFieldValues.get(fieldName) != null) {
            for (HashMap<String, String> valueInfo : mapTypeFieldValues.get(fieldName)) {
                if (valueInfo.get("key") != null && !valueInfo.get("key").isEmpty()) {
                    entityField.put(valueInfo.get("key"), valueInfo.get("value") == null ? "" : valueInfo.get("value"));
                }
            }
        }
    }
}