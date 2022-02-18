package org.meveo.admin.job;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.AggregationConfiguration.AggregationOption;
import org.meveo.admin.job.AggregationConfiguration.DateAggregationOption;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;

@Stateless
public class InvoiceLinesJob extends Job {

	public static final String  INVOICE_LINES_IL_AGGREGATION_OPTIONS = "JobInstance_InvoiceLinesJob_ILAggregationOptions";
	
	public static final String  INVOICE_LINES_IL_DATE_AGGREGATION_OPTIONS = "JobInstance_InvoiceLinesJob_ILDateAggregationOptions";
	
	@Inject
    private InvoiceLinesJobBean invoiceLinesBean;

    @Override
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        invoiceLinesBean.execute(result, jobInstance);
        return result;
    }
    
    
    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();

  
        CustomFieldTemplate ilAggregationCF = new CustomFieldTemplate();
        ilAggregationCF.setCode("ILAggregationOptions");
        ilAggregationCF.setAppliesTo("JobInstance_InvoiceLinesJob");
        ilAggregationCF.setActive(true);
        ilAggregationCF.setDescription(resourceMessages.getString("AggregationOptions"));
        ilAggregationCF.setFieldType(CustomFieldTypeEnum.CHECKBOX_LIST);
        ilAggregationCF.setStorageType(CustomFieldStorageTypeEnum.LIST);
        ilAggregationCF.setValueRequired(false);
        SortedMap<String, String> ilAggregationList = new TreeMap();
        for (AggregationOption e : AggregationOption.values()) {
            ilAggregationList.put(e.name(), resourceMessages.getString(e.getLabel()));
        }
        ilAggregationCF.setListValues(ilAggregationList);
        result.put(INVOICE_LINES_IL_AGGREGATION_OPTIONS, ilAggregationCF);
        
        
        CustomFieldTemplate ilDateAggregationCF = new CustomFieldTemplate();
        ilDateAggregationCF.setCode("ILDateAggregationOptions");
        ilDateAggregationCF.setAppliesTo("JobInstance_InvoiceLinesJob");
        ilDateAggregationCF.setActive(true);
        ilDateAggregationCF.setDescription(resourceMessages.getString("DateAggregationOptions"));
        ilDateAggregationCF.setFieldType(CustomFieldTypeEnum.LIST);
        ilDateAggregationCF.setStorageType(CustomFieldStorageTypeEnum.SINGLE);
        ilDateAggregationCF.setValueRequired(false);
        SortedMap<String, String> ilDateAggregationCFList = new TreeMap();
        for (DateAggregationOption e : DateAggregationOption.values()) {
        	ilDateAggregationCFList.put(e.name(), resourceMessages.getString(e.getLabel()));
        }
        ilDateAggregationCF.setListValues(ilDateAggregationCFList);
        result.put(INVOICE_LINES_IL_DATE_AGGREGATION_OPTIONS, ilDateAggregationCF);


        return result;
    }
    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.INVOICING;
    }
}