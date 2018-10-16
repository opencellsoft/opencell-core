package org.meveo.admin.job;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

/**
 * The Class FtpAdapterJob connect to the given ftp server and get files from the given remote path.
 */
@Stateless
public class FtpAdapterJob extends Job {

    /** The ftp adapter job bean. */
    @Inject
    private FtpAdapterJobBean ftpAdapterJobBean;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    @Override
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        String localDirectory = null;
        String remoteServer = null;
        int remotePort = 21;
        String removeOriginalFile = null;
        String remoteDirectory = null;
        String ftpExtension = null;
        String ftpUsername = null;
        String ftpPassword = null;
        String ftpProtocol = null;
        String operation = null;

        try {
            localDirectory = paramBeanFactory.getChrootDir() + ((String) this.getParamOrCFValue(jobInstance, "FtpAdapterJob_localDirectory")).replaceAll("\\..", "");
            remoteServer = (String) this.getParamOrCFValue(jobInstance, "FtpAdapterJob_remoteServer");
            remotePort = ((Long) this.getParamOrCFValue(jobInstance, "FtpAdapterJob_remotePort")).intValue();
            removeOriginalFile = (String) this.getParamOrCFValue(jobInstance, "FtpAdapterJob_removeOriginalFile");
            remoteDirectory = (String) this.getParamOrCFValue(jobInstance, "FtpAdapterJob_remoteDirectory");
            ftpExtension = (String) this.getParamOrCFValue(jobInstance, "FtpAdapterJob_ftpFileExtension");
            ftpUsername = (String) this.getParamOrCFValue(jobInstance, "FtpAdapterJob_ftpUsername");
            ftpPassword = (String) this.getParamOrCFValue(jobInstance, "FtpAdapterJob_ftpPassword");
            ftpProtocol = (String) this.getParamOrCFValue(jobInstance, "FtpAdapterJob_ftpProtocol");
            operation = (String) this.getParamOrCFValue(jobInstance, "FtpAdapterJob_operation");

        } catch (Exception e) {
            log.warn("Cant get customFields for " + jobInstance.getJobTemplate(), e);
        }
        ftpAdapterJobBean.execute(result, jobInstance, localDirectory, remoteServer, remotePort, "true".equalsIgnoreCase(removeOriginalFile), remoteDirectory, ftpExtension,
            ftpUsername, ftpPassword, ftpProtocol, operation);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.MEDIATION;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate localDirectory = new CustomFieldTemplate();
        localDirectory.setCode("FtpAdapterJob_localDirectory");
        localDirectory.setAppliesTo("JOB_FtpAdapterJob");
        localDirectory.setActive(true);
        localDirectory.setDescription(resourceMessages.getString("FtpAdapter.localDirectory"));
        localDirectory.setFieldType(CustomFieldTypeEnum.STRING);
        localDirectory.setValueRequired(true);
        localDirectory.setMaxValue(150L);
        result.put("FtpAdapterJob_localDirectory", localDirectory);

        CustomFieldTemplate remoteServer = new CustomFieldTemplate();
        remoteServer.setCode("FtpAdapterJob_remoteServer");
        remoteServer.setAppliesTo("JOB_FtpAdapterJob");
        remoteServer.setActive(true);
        remoteServer.setDescription(resourceMessages.getString("FtpAdapter.remoteServer"));
        remoteServer.setFieldType(CustomFieldTypeEnum.STRING);
        remoteServer.setValueRequired(true);
        remoteServer.setMaxValue(150L);
        result.put("FtpAdapterJob_remoteServer", remoteServer);

        CustomFieldTemplate remotePort = new CustomFieldTemplate();
        remotePort.setCode("FtpAdapterJob_remotePort");
        remotePort.setAppliesTo("JOB_FtpAdapterJob");
        remotePort.setActive(true);
        remotePort.setDescription(resourceMessages.getString("FtpAdapter.remotePort"));
        remotePort.setFieldType(CustomFieldTypeEnum.LONG);
        remotePort.setValueRequired(true);
        result.put("FtpAdapterJob_remotePort", remotePort);

        CustomFieldTemplate removeOriginalFile = new CustomFieldTemplate();
        removeOriginalFile.setCode("FtpAdapterJob_removeOriginalFile");
        removeOriginalFile.setAppliesTo("JOB_FtpAdapterJob");
        removeOriginalFile.setActive(true);
        removeOriginalFile.setDescription(resourceMessages.getString("FtpAdapter.removeOriginalFile"));
        removeOriginalFile.setFieldType(CustomFieldTypeEnum.LIST);
        Map<String, String> removeOriginalFileListValues = new HashMap<String, String>();
        removeOriginalFileListValues.put("TRUE", "True");
        removeOriginalFileListValues.put("FALSE", "False");
        removeOriginalFile.setListValues(removeOriginalFileListValues);
        removeOriginalFile.setValueRequired(true);
        result.put("FtpAdapterJob_removeOriginalFile", removeOriginalFile);

        CustomFieldTemplate remoteDirectory = new CustomFieldTemplate();
        remoteDirectory.setCode("FtpAdapterJob_remoteDirectory");
        remoteDirectory.setAppliesTo("JOB_FtpAdapterJob");
        remoteDirectory.setActive(true);
        remoteDirectory.setDescription(resourceMessages.getString("FtpAdapter.remoteDirectory"));
        remoteDirectory.setFieldType(CustomFieldTypeEnum.STRING);
        remoteDirectory.setValueRequired(true);
        remoteDirectory.setMaxValue(100L);
        result.put("FtpAdapterJob_remoteDirectory", remoteDirectory);

        CustomFieldTemplate ftpUsername = new CustomFieldTemplate();
        ftpUsername.setCode("FtpAdapterJob_ftpUsername");
        ftpUsername.setAppliesTo("JOB_FtpAdapterJob");
        ftpUsername.setActive(true);
        ftpUsername.setDescription(resourceMessages.getString("FtpAdapter.ftpUsername"));
        ftpUsername.setFieldType(CustomFieldTypeEnum.STRING);
        ftpUsername.setValueRequired(true);
        ftpUsername.setMaxValue(50L);
        result.put("FtpAdapterJob_ftpUsername", ftpUsername);

        CustomFieldTemplate ftpPassword = new CustomFieldTemplate();
        ftpPassword.setCode("FtpAdapterJob_ftpPassword");
        ftpPassword.setAppliesTo("JOB_FtpAdapterJob");
        ftpPassword.setActive(true);
        ftpPassword.setDescription(resourceMessages.getString("FtpAdapter.ftpPassword"));
        ftpPassword.setFieldType(CustomFieldTypeEnum.STRING);
        ftpPassword.setValueRequired(true);
        ftpPassword.setMaxValue(50L);
        result.put("FtpAdapterJob_ftpPassword", ftpPassword);

        CustomFieldTemplate ftpExtension = new CustomFieldTemplate();
        ftpExtension.setCode("FtpAdapterJob_ftpFileExtension");
        ftpExtension.setAppliesTo("JOB_FtpAdapterJob");
        ftpExtension.setActive(true);
        ftpExtension.setDescription(resourceMessages.getString("FtpAdapter.fileExtension"));
        ftpExtension.setFieldType(CustomFieldTypeEnum.STRING);
        ftpExtension.setValueRequired(true);
        ftpExtension.setMaxValue(50L);
        result.put("FtpAdapterJob_fileExtension", ftpExtension);

        CustomFieldTemplate ftpProtocol = new CustomFieldTemplate();
        ftpProtocol.setCode("FtpAdapterJob_ftpProtocol");
        ftpProtocol.setAppliesTo("JOB_FtpAdapterJob");
        ftpProtocol.setActive(true);
        ftpProtocol.setDescription(resourceMessages.getString("FtpAdapter.ftpProtocol"));
        ftpProtocol.setFieldType(CustomFieldTypeEnum.LIST);
        Map<String, String> ftpProtocolListValues = new HashMap<String, String>();
        ftpProtocolListValues.put("FTP", "FTP");
        ftpProtocolListValues.put("SFTP", "SFTP");
        ftpProtocol.setListValues(ftpProtocolListValues);
        ftpProtocol.setValueRequired(true);
        result.put("FtpAdapterJob_ftpProtocol", ftpProtocol);

        CustomFieldTemplate operation = new CustomFieldTemplate();
        operation.setCode("FtpAdapterJob_operation");
        operation.setAppliesTo("JOB_FtpAdapterJob");
        operation.setActive(true);
        operation.setDescription(resourceMessages.getString("FtpAdapter.operation"));
        operation.setFieldType(CustomFieldTypeEnum.LIST);
        Map<String, String> operationlListValues = new HashMap<String, String>();
        operationlListValues.put("IMPORT", "IMPORT");
        operationlListValues.put("EXPORT", "EXPORT");
        operation.setListValues(operationlListValues);
        operation.setValueRequired(true);
        result.put("FtpAdapterJob_operation", operation);

        return result;
    }
}