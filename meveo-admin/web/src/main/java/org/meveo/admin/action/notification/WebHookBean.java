package org.meveo.admin.action.notification;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.RejectedImportException;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.commons.utils.CsvReader;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.model.notification.StrategyImportTypeEnum;
import org.meveo.model.notification.WebHook;
import org.meveo.model.notification.WebHookMethodEnum;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.notification.WebHookService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Standard backing bean for {@link WebHook} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 */
@Named
@ViewScoped
public class WebHookBean extends BaseBean<WebHook> {

    private static final long serialVersionUID = -5605274745661054861L;

    @Inject
    WebHookService webHookService;
    
    @Inject
    CounterTemplateService counterTemplateService;
    
    ParamBean paramBean = ParamBean.getInstance();
    
    private StrategyImportTypeEnum strategyImportType;
    CsvBuilder csv = null;
	private String providerDir=paramBean.getProperty("providers.rootDir","/tmp/meveo_integr");
	private String existingEntitiesCsvFile=null;

    CsvReader csvReader = null;
    private UploadedFile file; 
    
    private static final int CODE= 0;
    private static final int CLASS_NAME_FILTER= 1;
    private static final int EVENT_TYPE_FILTER= 2;
    private static final int EL_FILTER=3;
    private static final int ACTIVE= 4; 
    private static final int EL_ACTION= 5; 
    private static final int HOST= 6; 
    private static final int PORT= 7; 
    private static final int PAGE= 8; 
    private static final int HTTP_METHOD= 9; 
    private static final int USERNAME= 10; 
    private static final int PASSWORD= 11;
    private static final int HEADERS= 12;
    private static final int PARAMS= 13;
    private static final int COUNTER_TEMPLATE= 14;
    

    public WebHookBean() {
        super(WebHook.class);
    }

    @Override
    protected IPersistenceService<WebHook> getPersistenceService() {
        return webHookService;
    }

    @Override
    public WebHook initEntity() {
        WebHook webhook = super.initEntity();

        extractMapTypeFieldFromEntity(webhook.getHeaders(), "headers");
        extractMapTypeFieldFromEntity(webhook.getParams(), "params");

        return webhook;
    }

    @Override
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        updateMapTypeFieldInEntity(entity.getHeaders(), "headers");
        updateMapTypeFieldInEntity(entity.getParams(), "params");

        return super.saveOrUpdate(killConversation);
    }
    
	public void exportToFile() throws Exception {
		CsvBuilder csv = new CsvBuilder();
		csv.appendValue("Code");
		csv.appendValue("Classename filter");
		csv.appendValue("Event type filter");
		csv.appendValue("El filter");
		csv.appendValue("Active");
		csv.appendValue("El action");
		csv.appendValue("Host");
		csv.appendValue("Port");
		csv.appendValue("Page");
		csv.appendValue("HTTP method");
		csv.appendValue("Username");
		csv.appendValue("Password");
		csv.appendValue("Headers");
		csv.appendValue("Parameters");
		csv.appendValue("Counter template");
		csv.startNewLine();
		for (WebHook webHook : webHookService.list()) {
			csv.appendValue(webHook.getCode());
			csv.appendValue(webHook.getClassNameFilter());
			csv.appendValue(webHook.getEventTypeFilter() + "");
			csv.appendValue(webHook.getElFilter());
			csv.appendValue(webHook.isDisabled() + "");
			csv.appendValue(webHook.getElAction());
			csv.appendValue(webHook.getHost());
			csv.appendValue(webHook.getPort() + "");
			csv.appendValue(webHook.getPage());
			csv.appendValue(webHook.getHttpMethod() + "");
			csv.appendValue(webHook.getUsername());
			csv.appendValue(webHook.getPassword());
			
			StringBuffer headers=new StringBuffer();
			if(webHook.getHeaders()!=null){
				String sep="";
				for(String key:webHook.getHeaders().keySet()){
					String valueHeaders=webHook.getHeaders().get(key);
					headers.append(sep).append(key).append(":").append(Base64.encodeBase64String(valueHeaders.getBytes()));
					sep="|";
					}
				csv.appendValue(headers.toString());	
			}
			StringBuffer params=new StringBuffer(); 
			if(webHook.getParams()!=null){
				String sep="";
				for(String key:webHook.getParams().keySet()){
					String valueParams=webHook.getParams().get(key);
					params.append(sep).append(key).append(":").append(Base64.encodeBase64String(valueParams.getBytes()));
					sep="|";
					}
				csv.appendValue(params.toString());
			    }
				csv.appendValue(webHook.getCounterTemplate()!=null?  webHook.getCounterTemplate().getCode(): null);
				csv.startNewLine();
			}
		InputStream inputStream = new ByteArrayInputStream(csv.toString()
				.getBytes());
		csv.download(inputStream, "WebHooks.csv");
	}


public void handleFileUpload(FileUploadEvent event) throws Exception {
	try {
		file = event.getFile();
	    log.info("handleFileUpload " + file);
	    upload();
	} catch (BusinessException e) {
		log.error(e.getMessage(),e);
		messages.error(e.getMessage());
	} catch (IOException e) {
		log.error(e.getMessage(),e);
		messages.error(e.getMessage());
	}
    
}
	public void upload() throws IOException, BusinessException {
		if (file != null) {
			csvReader = new CsvReader(file.getInputstream(), ';',
					Charset.forName("ISO-8859-1"));
			csvReader.readHeaders();
			try {
				String existingEntitiesCSV=paramBean.getProperty("existingEntities.csv.dir", "existingEntitiesCSV");
				File dir=new File(providerDir+File.separator+getCurrentProvider().getCode()+File.separator+existingEntitiesCSV);
				dir.mkdirs();
				existingEntitiesCsvFile= dir.getAbsolutePath()+File.separator+"WebHooks_"+new SimpleDateFormat("ddMMyyyyHHmmSS").format(new Date())+".csv";
				csv = new CsvBuilder();
				boolean isEntityAlreadyExist=false;
				while (csvReader.readRecord()) {
					String[] values = csvReader.getValues();
					WebHook existingEntity = webHookService.findByCode(
							values[CODE], getCurrentProvider());
					if (existingEntity != null) {
						checkSelectedStrategy(values, existingEntity,isEntityAlreadyExist);
						isEntityAlreadyExist=true;
					} else {
						WebHook webHook = new WebHook();
						webHook.setCode(values[CODE]);
						webHook.setClassNameFilter(values[CLASS_NAME_FILTER]);
						webHook.setEventTypeFilter(NotificationEventTypeEnum
								.valueOf(values[EVENT_TYPE_FILTER]));
						webHook.setElFilter(values[EL_FILTER]);
						webHook.setDisabled(Boolean
								.parseBoolean(values[ACTIVE]));
						webHook.setElAction(values[EL_ACTION]);
						webHook.setHost(values[HOST]);
						webHook.setPort(Integer.parseInt(values[PORT]));
						webHook.setPage(values[PAGE]);
						webHook.setHttpMethod(WebHookMethodEnum
								.valueOf(values[HTTP_METHOD]));
						webHook.setUsername(values[USERNAME]);
						webHook.setPassword(values[PASSWORD]);
						
						if(values[HEADERS]!=null && values[HEADERS].length()>0){
						String[] mapElements=values[HEADERS].split("\\|");
						if(mapElements!=null && mapElements.length>0){
							Map<String,String> headers = new HashMap<String, String>();
							for(String element:mapElements){
								String[] param=element.split(":");
								String value=new String(Base64.decodeBase64(param[1]));
								headers.put(param[0], value);
							}
							webHook.setHeaders(headers);
						  }
						}
						if(values[PARAMS]!=null && values[PARAMS].length()>0){
							String[] mapElements=values[PARAMS].split("\\|");
							if(mapElements!=null && mapElements.length>0){
								Map<String,String> params = new HashMap<String, String>();
								for(String element:mapElements){
									String[] param=element.split(":");
									String value=new String(Base64.decodeBase64(param[1]));
									params.put(param[0], value);
								}
								webHook.setParams(params);
							  }
							}
						if (!StringUtils.isBlank(values[COUNTER_TEMPLATE])) {
							CounterTemplate counterTemplate = counterTemplateService
									.findByCode(values[COUNTER_TEMPLATE],
											getCurrentProvider());
							webHook.setCounterTemplate(counterTemplate != null ? counterTemplate
									: null);
						}
						webHookService.create(webHook);
					}
				}
				if(isEntityAlreadyExist && strategyImportType.equals(StrategyImportTypeEnum.REJECT_EXISTING_RECORDS)){
					csv.writeFile(csv.toString().getBytes(), existingEntitiesCsvFile);
				}
				messages.info(new BundleKey("messages", "import.csv.successful"));
			} catch (RejectedImportException e) {
				messages.error(new BundleKey("messages", e.getMessage()));
			}
		
		}
	}

	public void checkSelectedStrategy(String[] values, WebHook existingEntity,boolean isEntityAlreadyExist)
			throws RejectedImportException, IOException {
		if (strategyImportType.equals(StrategyImportTypeEnum.UPDATED)) {
			existingEntity.setClassNameFilter(values[CLASS_NAME_FILTER]);
			existingEntity.setEventTypeFilter(NotificationEventTypeEnum
					.valueOf(values[EVENT_TYPE_FILTER]));
			existingEntity.setElFilter(values[EL_FILTER]);
			existingEntity.setDisabled(Boolean.parseBoolean(values[ACTIVE]));
			existingEntity.setElAction(values[EL_ACTION]);
			existingEntity.setHost(values[HOST]);
			existingEntity.setPort(Integer.parseInt(values[PORT]));
			existingEntity.setPage(values[PAGE]);
			existingEntity.setHttpMethod(WebHookMethodEnum
					.valueOf(values[HTTP_METHOD]));
			existingEntity.setUsername(values[USERNAME]);
			existingEntity.setPassword(values[PASSWORD]);
			if(values[HEADERS]!=null && values[HEADERS].length()>0){
				String[] mapElements=values[HEADERS].split("\\|");
				if(mapElements!=null && mapElements.length>0){
					Map<String,String> headers = new HashMap<String, String>();
					for(String element:mapElements){
						String[] param=element.split(":");
						String value=new String(Base64.decodeBase64(param[1]));
						headers.put(param[0],value);
						
					}
					existingEntity.setHeaders(headers);
				  }
				}
				if(values[PARAMS]!=null && values[PARAMS].length()>0){
					String[] mapElements=values[PARAMS].split("\\|");
					if(mapElements!=null && mapElements.length>0){
						Map<String,String> params = new HashMap<String, String>();
						for(String element:mapElements){
							String[] param=element.split(":");
							String value=new String(Base64.decodeBase64(param[1]));
							params.put(param[0],value);
						}
						existingEntity.setParams(params);
					  }
					}
			if (!StringUtils.isBlank(values[COUNTER_TEMPLATE])) {
				CounterTemplate counterTemplate = counterTemplateService
						.findByCode(values[COUNTER_TEMPLATE],
								getCurrentProvider());
				existingEntity
						.setCounterTemplate(counterTemplate != null ? counterTemplate
								: null);
			}
			webHookService.update(existingEntity);
			
		}else if (strategyImportType
					.equals(StrategyImportTypeEnum.REJECTE_IMPORT)) {
				throw new RejectedImportException("notification.rejectImport");
			}
			else if (strategyImportType.equals(StrategyImportTypeEnum.REJECT_EXISTING_RECORDS)) {
			if(!isEntityAlreadyExist){		
				csv.appendValue("Code");
				csv.appendValue("Classename filter");
				csv.appendValue("Event type filter");
				csv.appendValue("El filter");
				csv.appendValue("Active");
				csv.appendValue("El action");
				csv.appendValue("Host");
				csv.appendValue("Port");
				csv.appendValue("Page");
				csv.appendValue("HTTP method");
				csv.appendValue("Username");
				csv.appendValue("Password");
				csv.appendValue("Headers");
				csv.appendValue("Parameters");
				csv.appendValue("Counter template");
			}
			csv.startNewLine();
			csv.appendValue(values[CODE]);
			csv.appendValue(values[CLASS_NAME_FILTER]);
			csv.appendValue(values[EVENT_TYPE_FILTER]);
			csv.appendValue(values[EL_FILTER]);
			csv.appendValue(values[ACTIVE]);
			csv.appendValue(values[EL_ACTION]);
			csv.appendValue(values[HOST]);
			csv.appendValue(values[PORT]);
			csv.appendValue(values[PAGE]);
			csv.appendValue(values[HTTP_METHOD]);
			csv.appendValue(values[USERNAME]);
			csv.appendValue(values[PASSWORD]);
			csv.appendValue(values[COUNTER_TEMPLATE]);
			}	
	}

	public StrategyImportTypeEnum getStrategyImportType() {
		return strategyImportType;
	}

	public void setStrategyImportType(StrategyImportTypeEnum strategyImportType) {
		this.strategyImportType = strategyImportType;
	}
	
	

}
