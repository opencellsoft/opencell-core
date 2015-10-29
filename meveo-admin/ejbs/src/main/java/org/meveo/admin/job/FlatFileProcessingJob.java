package org.meveo.admin.job;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelectInfo;
import org.apache.commons.vfs.FileSelector;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.impl.StandardFileSystemManager;
import org.apache.commons.vfs.provider.sftp.SftpFileSystemConfigBuilder;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.admin.util.security.Sha1Encrypt;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.mediation.ImportedFile;
import org.meveo.service.job.Job;
import org.meveo.service.medina.impl.ImportedFileService;

@Startup
@Singleton
public class FlatFileProcessingJob extends Job {

	@Inject
	private FlatFileProcessingJobBean flatFileProcessingJobBean;

	@Inject
	private ResourceBundle resourceMessages;
	@Inject
	private ImportedFileService importedFileService;
	
	@Override
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobInstance jobInstance, User currentUser) {
		super.execute(jobInstance, currentUser);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	protected void execute(JobExecutionResultImpl result, JobInstance jobInstance, User currentUser) throws BusinessException {
		try {
			String mappingConf = null;
			String inputDir = null;
			String scriptInstanceFlowCode = null;
			String fileNameExtension = null;
			String recordVariableName = null;
			String originFilename = null;
			String formatTransfo= null;
			Map<String, Object> initContext = new HashMap<String, Object>();
			
			
			String fileAccess=null;
			String distantServer=null;
			long distantPort=0L;
			String removeDistantFile=null;
			String ftpInputDirectory=null;
			String ftpUsername=null;
			String ftpPassword=null;
			try {
				recordVariableName = (String) jobInstance.getCFValue("FlatFileProcessingJob_recordVariableName");
				originFilename = (String) jobInstance.getCFValue("FlatFileProcessingJob_originFilename");			
				CustomFieldInstance variablesCFI = jobInstance.getCustomFields().get("FlatFileProcessingJob_variables");
				if (variablesCFI != null) {
					initContext = variablesCFI.getMapValue();
				}
				mappingConf = (String) jobInstance.getCFValue("FlatFileProcessingJob_mappingConf");
				inputDir = ParamBean.getInstance().getProperty("providers.rootDir", "/tmp/meveo/") + File.separator + currentUser.getProvider().getCode() + ((String)jobInstance.getCFValue("FlatFileProcessingJob_inputDir")).replaceAll("\\..", "");
				fileNameExtension = (String) jobInstance.getCFValue("FlatFileProcessingJob_fileNameExtension");
				scriptInstanceFlowCode = (String) jobInstance.getCFValue("FlatFileProcessingJob_scriptsFlow");
				formatTransfo = (String) jobInstance.getCFValue("FlatFileProcessingJob_formatTransfo");
				
				fileAccess=(String)jobInstance.getCFValue("FlatFileProcessingJob_fileAccess");
				distantServer=(String)jobInstance.getCFValue("FlatFileProcessingJob_distantServer");
				distantPort=(Long)jobInstance.getCFValue("FlatFileProcessingJob_distantPort");
				removeDistantFile=(String)jobInstance.getCFValue("FlatFileProcessingJob_removeDistantFile");
				ftpInputDirectory=(String)jobInstance.getCFValue("FlatFileProcessingJob_ftpInputDirectory");
				ftpUsername=(String)jobInstance.getCFValue("FlatFileProcessingJob_ftpUsername");
				ftpPassword=(String)jobInstance.getCFValue("FlatFileProcessingJob_ftpPassword");

			} catch (Exception e) {
				log.warn("Cant get customFields for " + jobInstance.getJobTemplate(),e);
			}

			ArrayList<String> fileExtensions = new ArrayList<String>();
			fileExtensions.add(fileNameExtension);

			File f = new File(inputDir);
			if (!f.exists()) {
				log.debug("inputDir {} not exist",inputDir);
				f.mkdirs();
				log.debug("inputDir {} creation ok",inputDir);
			}
			
			//ftp & sftp
			Map<String,ImportedFile> importedFiles=null;
			File[] files=null;
			if(!"local".equalsIgnoreCase(fileAccess)){
				log.debug("connect to {}",fileAccess);
				importedFiles=connect2FTP(fileAccess,inputDir,ftpInputDirectory,distantServer,ftpUsername,ftpPassword,distantPort,fileNameExtension,currentUser);
			}
			files = FileUtils.getFilesForParsing(inputDir, fileExtensions);
			if (files == null || files.length == 0) {
				log.debug("there no file in {} with extension {}",inputDir,fileExtensions);
				return;
			}
			log.debug("found files {}",files.length);
	        for (File file : files) {
	        	
	        	//local file verified by hash
	        	ImportedFile localImportedFile=null;
	        	if("local".equalsIgnoreCase(fileAccess)){
	        		localImportedFile=new ImportedFile(file.getAbsolutePath(),file.length(),file.lastModified());
	        		String code=Sha1Encrypt.encodePassword(localImportedFile.getOriginHash(),Sha1Encrypt.SHA224);
	        		localImportedFile.setCode(code);
	        		localImportedFile.setDescription(file.getName());
	        		List<ImportedFile> localExisteds=importedFileService.findByCodeLike(code, currentUser.getProvider());
	        		if(localExisteds!=null&&localExisteds.size()>0){
	        			log.debug("local file {} has imported",file.getAbsolutePath());
	        			continue;
	        		}
	        		importedFileService.create(localImportedFile, currentUser,currentUser.getProvider());
	        	}
	        	flatFileProcessingJobBean.execute(result, inputDir, currentUser, file, mappingConf,scriptInstanceFlowCode,recordVariableName,initContext,originFilename,formatTransfo);
	        	//ftp or sftp file, parse, create 
	        	if(!"local".equalsIgnoreCase(fileAccess)){
	        		ImportedFile importedFile=importedFiles.get(file.getName());
	        		log.debug("try to create importedFile {}",importedFile);
	        		log.debug("delete distant file {}",removeDistantFile);
	        		if(importedFile!=null){
	        			importedFileService.create(importedFile, currentUser, currentUser.getProvider());
		        		if("true".equalsIgnoreCase(removeDistantFile)){
		        			log.debug("start to remove file {}",removeDistantFile);
		        			removeFtpfile(importedFile,fileAccess,distantServer,ftpUsername,ftpPassword,distantPort,ftpInputDirectory,fileNameExtension);
		        		}
	        		}
	        	}
	        }

		} catch (Exception e) {
			log.error("Failed to run mediation", e);
			result.registerError(e.getMessage());
		}
	}

	@Override
	public JobCategoryEnum getJobCategory() {
		return JobCategoryEnum.MEDIATION;
	}

	@Override
	public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

		CustomFieldTemplate inputDirectoryCF = new CustomFieldTemplate();
		inputDirectoryCF.setCode("FlatFileProcessingJob_inputDir");
		inputDirectoryCF.setAccountLevel(AccountLevelEnum.TIMER);
		inputDirectoryCF.setActive(true);
		inputDirectoryCF.setDescription(resourceMessages.getString("flatFile.inputDir"));
		inputDirectoryCF.setFieldType(CustomFieldTypeEnum.STRING);
		inputDirectoryCF.setDefaultValue(null);
		inputDirectoryCF.setValueRequired(true);
		result.put("FlatFileProcessingJob_inputDir", inputDirectoryCF);

		CustomFieldTemplate fileNameExtensionCF = new CustomFieldTemplate();
		fileNameExtensionCF.setCode("FlatFileProcessingJob_fileNameExtension");
		fileNameExtensionCF.setAccountLevel(AccountLevelEnum.TIMER);
		fileNameExtensionCF.setActive(true);
		fileNameExtensionCF.setDescription(resourceMessages.getString("flatFile.fileNameExtension"));
		fileNameExtensionCF.setFieldType(CustomFieldTypeEnum.STRING);
		fileNameExtensionCF.setDefaultValue("csv");
		fileNameExtensionCF.setValueRequired(true);
		result.put("FlatFileProcessingJob_fileNameExtension", fileNameExtensionCF);

		CustomFieldTemplate mappingConf = new CustomFieldTemplate();
		mappingConf.setCode("FlatFileProcessingJob_mappingConf");
		mappingConf.setAccountLevel(AccountLevelEnum.TIMER);
		mappingConf.setActive(true);
		mappingConf.setDescription(resourceMessages.getString("flatFile.mappingConf"));
		mappingConf.setFieldType(CustomFieldTypeEnum.TEXT_AREA);
		mappingConf.setDefaultValue("");
		mappingConf.setValueRequired(true);
		result.put("FlatFileProcessingJob_mappingConf", mappingConf);

		CustomFieldTemplate ss = new CustomFieldTemplate();
		ss.setCode("FlatFileProcessingJob_scriptsFlow");
		ss.setAccountLevel(AccountLevelEnum.TIMER);
		ss.setActive(true);
		ss.setDescription(resourceMessages.getString("flatFile.scriptsFlow"));
		ss.setFieldType(CustomFieldTypeEnum.STRING);
		ss.setDefaultValue(null);
		ss.setValueRequired(true);
		result.put("FlatFileProcessingJob_scriptsFlow", ss);

		CustomFieldTemplate variablesCF = new CustomFieldTemplate();
		variablesCF.setCode("FlatFileProcessingJob_variables");
		variablesCF.setAccountLevel(AccountLevelEnum.TIMER);
		variablesCF.setActive(true);
		variablesCF.setDescription("Init and finalize variables");
		variablesCF.setFieldType(CustomFieldTypeEnum.STRING);
		variablesCF.setStorageType(CustomFieldStorageTypeEnum.MAP);
		variablesCF.setValueRequired(false);
		result.put("FlatFileProcessingJob_variables", variablesCF);

		CustomFieldTemplate recordVariableName = new CustomFieldTemplate();
		recordVariableName.setCode("FlatFileProcessingJob_recordVariableName");
		recordVariableName.setAccountLevel(AccountLevelEnum.TIMER);
		recordVariableName.setActive(true);
		recordVariableName.setDefaultValue("record");
		recordVariableName.setDescription("Record variable name");
		recordVariableName.setFieldType(CustomFieldTypeEnum.STRING);
		recordVariableName.setValueRequired(true);
		result.put("FlatFileProcessingJob_recordVariableName", recordVariableName);

		CustomFieldTemplate originFilename = new CustomFieldTemplate();
		originFilename.setCode("FlatFileProcessingJob_originFilename");
		originFilename.setAccountLevel(AccountLevelEnum.TIMER);
		originFilename.setActive(true);
		originFilename.setDefaultValue("origin_filename");
		originFilename.setDescription("Filename variable name");
		originFilename.setFieldType(CustomFieldTypeEnum.STRING);
		originFilename.setValueRequired(false);
		result.put("FlatFileProcessingJob_originFilename", originFilename);

		CustomFieldTemplate formatTransfo = new CustomFieldTemplate();
		formatTransfo.setCode("FlatFileProcessingJob_formatTransfo");
		formatTransfo.setAccountLevel(AccountLevelEnum.TIMER);
		formatTransfo.setActive(true);
		formatTransfo.setDefaultValue("None");
		formatTransfo.setDescription("Format transformation");
		formatTransfo.setFieldType(CustomFieldTypeEnum.LIST);
		formatTransfo.setValueRequired(false);
		Map<String,String> listValues = new HashMap<String,String>();
		listValues.put("None","Aucune");
		listValues.put("Xlsx_to_Csv","Excel cvs");
		formatTransfo.setListValues(listValues);
		result.put("FlatFileProcessingJob_formatTransfo", formatTransfo);
//		
		CustomFieldTemplate fileAccess = new CustomFieldTemplate();
		fileAccess.setCode("FlatFileProcessingJob_fileAccess");
		fileAccess.setAccountLevel(AccountLevelEnum.TIMER);
		fileAccess.setActive(true);
		fileAccess.setDefaultValue("LOCAL");
		fileAccess.setDescription(resourceMessages.getString("flatFile.fileAccess"));
		fileAccess.setFieldType(CustomFieldTypeEnum.LIST);
		fileAccess.setValueRequired(true);
		Map<String,String> fileAccessListValues = new HashMap<String,String>();
		fileAccessListValues.put("LOCAL","Local");
		fileAccessListValues.put("FTP","FTP");
		fileAccessListValues.put("SFTP","SFTP");
		fileAccess.setListValues(fileAccessListValues);
		result.put("FlatFileProcessingJob_fileAccess", fileAccess);
		
		CustomFieldTemplate distantServer = new CustomFieldTemplate();
		distantServer.setCode("FlatFileProcessingJob_distantServer");
		distantServer.setAccountLevel(AccountLevelEnum.TIMER);
		distantServer.setActive(true);
		distantServer.setDescription(resourceMessages.getString("flatFile.distantServer"));
		distantServer.setFieldType(CustomFieldTypeEnum.STRING);
		distantServer.setValueRequired(false);
		result.put("FlatFileProcessingJob_distantServer", distantServer);
		
		CustomFieldTemplate distantPort = new CustomFieldTemplate();
		distantPort.setCode("FlatFileProcessingJob_distantPort");
		distantPort.setAccountLevel(AccountLevelEnum.TIMER);
		distantPort.setActive(true);
		distantPort.setDescription(resourceMessages.getString("flatFile.distantPort"));
		distantPort.setFieldType(CustomFieldTypeEnum.LONG);
		distantPort.setValueRequired(false);
		result.put("FlatFileProcessingJob_distantPort", distantPort);
		
		CustomFieldTemplate removeDistantFile = new CustomFieldTemplate();
		removeDistantFile.setCode("FlatFileProcessingJob_removeDistantFile");
		removeDistantFile.setAccountLevel(AccountLevelEnum.TIMER);
		removeDistantFile.setActive(true);
		removeDistantFile.setDescription(resourceMessages.getString("flatFile.removeDistantFile"));
		removeDistantFile.setFieldType(CustomFieldTypeEnum.LIST);
		Map<String,String> removeDistantFileListValues = new HashMap<String,String>();
		removeDistantFileListValues.put("TRUE","True");
		removeDistantFileListValues.put("FALSE","False");
		removeDistantFile.setListValues(removeDistantFileListValues);
		removeDistantFile.setValueRequired(false);
		result.put("FlatFileProcessingJob_removeDistantFile", removeDistantFile);
		
		CustomFieldTemplate ftpInputDirectory = new CustomFieldTemplate();
		ftpInputDirectory.setCode("FlatFileProcessingJob_ftpInputDirectory");
		ftpInputDirectory.setAccountLevel(AccountLevelEnum.TIMER);
		ftpInputDirectory.setActive(true);
		ftpInputDirectory.setDescription(resourceMessages.getString("flatFile.ftpInputDirectory"));
		ftpInputDirectory.setFieldType(CustomFieldTypeEnum.STRING);
		ftpInputDirectory.setValueRequired(false);
		result.put("FlatFileProcessingJob_ftpInputDirectory", ftpInputDirectory);
		
		CustomFieldTemplate ftpUsername = new CustomFieldTemplate();
		ftpUsername.setCode("FlatFileProcessingJob_ftpUsername");
		ftpUsername.setAccountLevel(AccountLevelEnum.TIMER);
		ftpUsername.setActive(true);
		ftpUsername.setDescription(resourceMessages.getString("flatFile.ftpUsername"));
		ftpUsername.setFieldType(CustomFieldTypeEnum.STRING);
		ftpUsername.setValueRequired(false);
		result.put("FlatFileProcessingJob_ftpUsername", ftpUsername);
		
		CustomFieldTemplate ftpPassword = new CustomFieldTemplate();
		ftpPassword.setCode("FlatFileProcessingJob_ftpPassword");
		ftpPassword.setAccountLevel(AccountLevelEnum.TIMER);
		ftpPassword.setActive(true);
		ftpPassword.setDescription(resourceMessages.getString("flatFile.ftpPassword"));
		ftpPassword.setFieldType(CustomFieldTypeEnum.STRING);
		ftpPassword.setValueRequired(false);
		result.put("FlatFileProcessingJob_ftpPassword", ftpPassword);
		
		return result;
	}
	private static FileSystemOptions getSftpOptions(boolean isSftp) throws FileSystemException{
		FileSystemOptions opts = new FileSystemOptions();
		if(isSftp){
			SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");
			SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);
			SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000);
		}
		return opts;
	}

	private Map<String,ImportedFile> connect2FTP(String fileAccess,String inputDir,String ftpInputDirectory,String distantServer,String ftpUsername,String ftpPassword,long distantPort,final String ftpFileExtension,User currentUser) throws IOException {
		
		Map<String,ImportedFile> result=new HashMap<String,ImportedFile>();
		if(ftpInputDirectory==null){
			ftpInputDirectory="/";
		}else{
			ftpInputDirectory=ftpInputDirectory.startsWith("/")?ftpInputDirectory:"/"+ftpInputDirectory;
		}

		final String ftpAddress=String.format("%s://%s:%s@%s:%s%s", fileAccess.toLowerCase(),ftpUsername,ftpPassword,distantServer,distantPort,ftpInputDirectory);
		String ftpUri=String.format("%s://%s:%s%s", fileAccess.toLowerCase(),distantServer,distantPort,ftpInputDirectory);
		log.debug("ftp uri {}",ftpUri);
		StandardFileSystemManager manager = null;
		try{
			manager = new StandardFileSystemManager();
			manager.init();
			FileObject fileObject=manager.resolveFile(ftpAddress,getSftpOptions("sftp".equalsIgnoreCase(fileAccess)));
			FileObject[] fileObjects = null;
			if(ftpFileExtension!=null){
				fileObjects=fileObject.findFiles(new FileSelector() {
					@Override
					public boolean traverseDescendents(FileSelectInfo info) throws Exception {
						return true;
					}
					@Override
					public boolean includeFile(FileSelectInfo info) throws Exception {
						return info.getDepth()==1&&ftpFileExtension.equalsIgnoreCase(info.getFile().getName().getExtension());
					}
				});
			}else{
				fileObjects=fileObject.getChildren();
			}

			ImportedFile importedFile=null;
			if(fileObjects!=null){
				for (FileObject o : fileObjects) {
					if (o.getType() == FileType.FILE) {
						String fileName=o.getName().getBaseName();
						FileContent c = o.getContent();
						long lastModified = c.getLastModifiedTime();
						long size = c.getSize();
						importedFile=new ImportedFile(((ftpUri.endsWith("/")?ftpUri:ftpUri+"/")+fileName),size,lastModified);
						String code=Sha1Encrypt.encodePassword(importedFile.getOriginHash(),Sha1Encrypt.SHA224);
						importedFile.setCode(code);
						importedFile.setDescription(o.getName().getBaseName());

						List<ImportedFile> files=importedFileService.findByCodeLike(code, currentUser.getProvider());
						if(files!=null&&files.size()>0){
							log.debug("has imported file {}",importedFile);
							continue;
						}
						log.debug("add importedFile {}",importedFile);
						
						File localFile = new File(fileName);
						if(inputDir==null){
							inputDir="/";
						}else{
							inputDir=inputDir.startsWith("/")?inputDir:"/"+inputDir;
							inputDir=inputDir.endsWith("/")?inputDir:inputDir+"/";
						}
						String outputFileName=String.format("%s%s", inputDir,localFile);
						log.debug("copy file to {}",outputFileName);
						FileOutputStream out = new FileOutputStream(outputFileName);
						IOUtils.copy(c.getInputStream(), out);
						result.put(fileName, importedFile);
					}
				}
			}
		}catch(Exception e){
			log.error("Error when read ftp file from {} ",ftpInputDirectory,e);
		}finally {
			if(manager!=null){
				try{
					manager.close();
				}catch(Exception e){}
			}
		}
		return result;
	}
	private void removeFtpfile(ImportedFile importedFile, String fileAccess, String distantServer, String ftpUsername,
			String ftpPassword, long distantPort,String ftpInputDirectory,final String ftpFileExtension) {
		final String ftpAddress=String.format("%s://%s:%s@%s:%s/%s", fileAccess.toLowerCase(),ftpUsername,ftpPassword,distantServer,distantPort,ftpInputDirectory);
		String ftpUri=String.format("%s://%s:%s%s", fileAccess.toLowerCase(),distantServer,distantPort,ftpInputDirectory);
		log.debug("ftp address {}",ftpAddress);
		StandardFileSystemManager manager = null;
		try{
			manager = new StandardFileSystemManager();
			manager.init();
			FileObject fileObject=manager.resolveFile(ftpAddress,getSftpOptions("sftp".equalsIgnoreCase(fileAccess)));
			FileObject[] fileObjects = null;
			if(ftpFileExtension!=null){
				fileObjects=fileObject.findFiles(new FileSelector() {
					@Override
					public boolean traverseDescendents(FileSelectInfo info) throws Exception {
						return true;
					}
					@Override
					public boolean includeFile(FileSelectInfo info) throws Exception {
						return info.getDepth()==1&&ftpFileExtension.equalsIgnoreCase(info.getFile().getName().getExtension());
					}
				});
			}else{
				fileObjects=fileObject.getChildren();
			}
			log.debug("found files {}",fileObjects!=null?fileObjects.length:0);
			if(fileObjects!=null){
				for (FileObject o : fileObjects) {
					log.debug("found {}",o.getName().getBaseName());
					if (o.getType() == FileType.FILE) {
						String fileName=o.getName().getBaseName();
						log.debug("found ftp file {}",fileName);
						FileContent c = o.getContent();
						long lastModified = c.getLastModifiedTime();
						long size = c.getSize();
						String originHash=String.format("%s:%d:%d",ftpUri.endsWith("/")?ftpUri+fileName:ftpUri+"/"+fileName,size,lastModified );
						log.debug("imported origin {}",importedFile.getOriginHash());
						String code=Sha1Encrypt.encodePassword(originHash, Sha1Encrypt.SHA224);
						log.debug("count code {}",code);
						if(code.equals(importedFile.getCode())){
							log.debug("found equal code");
							boolean result=o.delete();
							log.debug("deleted ftp file {}",result);
							break;
						}
					}
				}
			}
		}catch(Exception e){
			log.error("Error when delete a file {} from server ",importedFile.getUri(),e);
		}finally{
			if(manager!=null){
				try{
					((DefaultFileSystemManager) manager).close();
				}catch(Exception e){}
			}
		}
	}
}
