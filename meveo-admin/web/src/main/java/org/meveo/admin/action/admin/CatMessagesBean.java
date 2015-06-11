/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.admin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.commons.utils.CsvReader;
import org.meveo.model.BusinessEntity;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.Tax;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.shared.Title;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Standard backing bean for {@link CatMessages} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 */
@Named
@ViewScoped
public class CatMessagesBean extends BaseBean<CatMessages> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link CatMessages} service. Extends
	 * {@link PersistenceService} .
	 */
	@Inject
	private CatMessagesService catMessagesService;


	@Inject
	private TitleService titleService;
	@Inject
	private TaxService taxService;
	@Inject
	private InvoiceCategoryService invoiceCategoryService;
	@Inject
	private InvoiceSubCategoryService invoiceSubCategoryService;
	@Inject 
	private UsageChargeTemplateService usageChargeTemplateService;
	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;
	@Inject
	private RecurringChargeTemplateService recurringChargeTemplateService;
	@Inject
	private PricePlanMatrixService pricePlanMatrixService;
	
	private BusinessEntity businessEntity;
	private String popupId;
	private final String MESSAGE_CODE="%s_%d";
	private String objectType;
	
	CsvReader csvReader = null;
	private UploadedFile file;
	private static final int OBJECT_TYPE = 0;
	private static final int CODE = 1;
	private static final int LANGUAGE_CODE = 3;
	private static final int DESCRIPTION_TRANSLATION = 4;  

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public CatMessagesBean() {
		super(CatMessages.class);
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<CatMessages> getPersistenceService() {
		return catMessagesService;
	}

	@Override
	protected String getListViewName() {
		return "catMessagess";
	}
	
	protected BusinessEntity getObject(CatMessages catMessages){
		if(catMessages==null){
			return null;
		}
		String messagesCode=catMessages.getMessageCode();
		String[] codes=messagesCode.split("_");
		
		if(codes!=null&&codes.length==2){
			Long id=null;
			try{
				id=Long.valueOf(codes[1]);
			}catch(Exception e){
				return null;
			}
			if("Title".equals(codes[0])){
				return titleService.findById(id);
			}else if("Tax".equals(codes[0])){
				return taxService.findById(id);
			}else if("InvoiceCategory".equals(codes[0])){
				return invoiceCategoryService.findById(id);
			}else if("InvoiceSubCategory".equals(codes[0])){
				return invoiceSubCategoryService.findById(id);
			}else if("UsageChargeTemplate".equals(codes[0])){
				return usageChargeTemplateService.findById(id);
			}else if("OneShotChargeTemplate".equals(codes[0])){
				return oneShotChargeTemplateService.findById(id);
			}else if("RecurringChargeTemplate".equals(codes[0])){
				return recurringChargeTemplateService.findById(id);
			}else if("PricePlanMatrix".equals(codes[0])){
				return pricePlanMatrixService.findById(id);
			}
		}
		
		return null;
	}
	
	protected Map<String,String> getObjectTypes(){
		Map<String,String> result=new HashMap<String,String>();
		result.put("Title_*","Titles and civilities");
		result.put("Tax_*","Taxes");
		result.put("InvoiceCategory_*","Invoice categories");
		result.put("InvoiceSubCategory_*","Invoice subcategories");
		result.put("*ChargeTemplate_*","Charges");
		result.put("PricePlanMatrix_*","Price plans");
		return result;
	}

	public void exportToFile() throws Exception {
		CsvBuilder csv = new CsvBuilder();
		csv.appendValue("Object type");
		csv.appendValue("Code");
		csv.appendValue("Basic description");
		csv.appendValue("Language");
		csv.appendValue("Description translation");
		csv.startNewLine();
		for (CatMessages catMessages : (!filters.isEmpty()&& filters.size()>0) ? getLazyDataModel():catMessagesService.list()) {
			csv.appendValue(catMessages.getObjectType());
			csv.appendValue(getObject(catMessages).getCode());
			csv.appendValue(getObject(catMessages).getDescription());
			csv.appendValue(catMessages.getLanguageCode());
			csv.appendValue(catMessages.getDescription()); 
			csv.startNewLine();
		}
		InputStream inputStream = new ByteArrayInputStream(csv.toString().getBytes());
		csv.download(inputStream, "CatMessages.csv");
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
        while (csvReader.readRecord()) {
            String messageCode = null;
            String[] values = csvReader.getValues();
            if (values[OBJECT_TYPE].equals("Price plans")) {
                PricePlanMatrix pricePlanMatrix = pricePlanMatrixService.findByCode(values[CODE], getCurrentProvider());
                if (pricePlanMatrix != null) {
                    messageCode = pricePlanMatrix.getClass().getSimpleName() + "_" + pricePlanMatrix.getId();
                }
            }
            if (values[OBJECT_TYPE].equals("Charges")) {
                UsageChargeTemplate usageChargeTemplate = usageChargeTemplateService.findByCode(values[CODE], getCurrentProvider());
                if (usageChargeTemplate != null) {
                    messageCode = usageChargeTemplate.getClass().getSimpleName() + "_" + usageChargeTemplate.getId();
                }
                RecurringChargeTemplate recurringChargeTemplate = recurringChargeTemplateService.findByCode(values[CODE], getCurrentProvider());
                if (recurringChargeTemplate != null) {
                    messageCode = recurringChargeTemplate.getClass().getSimpleName() + "_" + recurringChargeTemplate.getId();
                }
                OneShotChargeTemplate oneShotChargeTemplate = oneShotChargeTemplateService.findByCode(values[CODE], getCurrentProvider());
                if (oneShotChargeTemplate != null) {
                    messageCode = oneShotChargeTemplate.getClass().getSimpleName() + "_" + oneShotChargeTemplate.getId();
                }
            }
            if (values[OBJECT_TYPE].equals("Titles and civilities")) {
                Title title = titleService.findByCode(getCurrentProvider(), values[CODE]);
                if (title != null) {
                    messageCode = title.getClass().getSimpleName() + "_" + title.getId();
                }
            }
            if (values[OBJECT_TYPE].equals("Taxes")) {
                Tax tax = taxService.findByCode(values[CODE], getCurrentProvider());
                if (tax != null) {
                    messageCode = tax.getClass().getSimpleName() + "_" + tax.getId();
                }
            }
            if (values[OBJECT_TYPE].equals("Invoice categories")) {
                InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(values[CODE], getCurrentProvider());
                if (invoiceCategory != null) {
                    messageCode = invoiceCategory.getClass().getSimpleName() + "_" + invoiceCategory.getId();
                }
            }
            if (values[OBJECT_TYPE].equals("Invoice subcategories")) {
                InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(values[CODE], getCurrentProvider());
                if (invoiceSubCategory != null) {
                    messageCode = invoiceSubCategory.getClass().getSimpleName() + "_" + invoiceSubCategory.getId();
                }
            }

            if (messageCode != null) {
                CatMessages existingEntity = catMessagesService.findByCodeAndLanguage(messageCode, values[LANGUAGE_CODE], getCurrentProvider());
                if (existingEntity != null) {
                    existingEntity.setDescription(values[DESCRIPTION_TRANSLATION]);
                    catMessagesService.update(existingEntity);
                } else {
                    CatMessages catMessages = new CatMessages();
                    catMessages.setMessageCode(messageCode);
                    catMessages.setLanguageCode(values[LANGUAGE_CODE]);
                    catMessages.setDescription(values[DESCRIPTION_TRANSLATION]);
                    catMessagesService.create(catMessages);
                }
            }
        }

    }

	public BusinessEntity getBusinessEntity() {
		return businessEntity;
	}

	public void setBusinessEntity(BusinessEntity businessEntity) throws BusinessException{
		this.businessEntity = businessEntity;
		if(businessEntity!=null){
			CatMessages temp=catMessagesService.findByCodeAndLanguage(String.format(MESSAGE_CODE, getEntityClass().getSimpleName(),businessEntity.getId()), entity.getLanguageCode(), getCurrentProvider());
			if(temp!=null){
				this.setObjectId(temp.getId());
				initEntity();
			}
		}
	}

	public String getPopupId() {
		if(this.getObjectType()!=null&&this.getObjectType()!=""){
			String type=getObjectType();
			popupId=type.replaceAll("\\*", "");
		}
		return popupId;
	}

	 public void updateBusinessEntity(){
		 this.businessEntity=null;
	 }

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	@Override
	public String saveOrUpdate(boolean killConversation)
			throws BusinessException {
		if(entity.isTransient()){
			entity.setMessageCode(String.format(MESSAGE_CODE,getEntityClass().getSimpleName(),businessEntity.getId()));
		}
		return super.saveOrUpdate(killConversation);
	}
	
	@SuppressWarnings("rawtypes")
	private Class getEntityClass() throws BusinessException{
		Class clazz=null;
		if(businessEntity instanceof Title){
			clazz=Title.class;
		}else if(businessEntity instanceof RecurringChargeTemplate){
			clazz=RecurringChargeTemplate.class;
		}else if(businessEntity instanceof OneShotChargeTemplate){
			clazz=OneShotChargeTemplate.class;
		}else if(businessEntity instanceof UsageChargeTemplate){
			clazz=UsageChargeTemplate.class;
		}else if(businessEntity instanceof PricePlanMatrix){
			clazz=PricePlanMatrix.class;
		}else if(businessEntity instanceof InvoiceCategory){
			clazz=InvoiceCategory.class;
		}else if(businessEntity instanceof InvoiceSubCategory){
			clazz=InvoiceSubCategory.class;
		}else if(businessEntity instanceof Tax){
			clazz=Tax.class;
		}
		if(clazz==null){
			throw new BusinessException("Wrong class type!");
		}
		return clazz;
	}
}
