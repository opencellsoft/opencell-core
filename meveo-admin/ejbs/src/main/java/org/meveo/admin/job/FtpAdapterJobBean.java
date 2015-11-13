package org.meveo.admin.job;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.FtpImportedFile;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.FtpImportedFileService;
import org.slf4j.Logger;

@Stateless
public class FtpAdapterJobBean {

	@Inject
	private Logger log;

	@Inject
	private FtpImportedFileService ftpImportedFileService;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void execute(JobExecutionResultImpl result, JobInstance jobInstance, User currentUser, String distDirectory, String remoteServer, int remotePort, boolean removeDistantFile, String ftpInputDirectory, String extention, String ftpUsername, String ftpPassword,String ftpProtocol) {
		log.debug("start ftpClient...");
		FTPClient ftpClient = new FTPClient();
		OutputStream outputStream = null;
		int reply,cpOk=0,cpKo=0,cpAll=0,cpWarn=0;
		try {			
			ftpClient.connect(remoteServer, remotePort);
			ftpClient.login(ftpUsername, ftpPassword);
			reply = ftpClient.getReplyCode();
			log.debug("reply from server :" + reply);
			if (!FTPReply.isPositiveCompletion(reply)) {
				log.debug("isPositiveCompletion:false");
				ftpClient.disconnect();
				log.debug("end !");
				return;
			}
			File tmp = new File(distDirectory);
			if(!tmp.exists()){
				tmp.mkdirs();
			}
			ftpClient.changeWorkingDirectory(ftpInputDirectory);
			String[] listNames = ftpClient.listNames();
			cpAll = listNames.length;
			log.debug("nb remote files : " + cpAll);
			for (String fileName : listNames) {
				log.debug("fileName : " + fileName);
				try {
					if(extention == null){
						log.debug("extension is null");
						continue;
					}
					if (!fileName.endsWith(extention) && !"*".equals(extention)) {
						log.debug("extension ignored");
						continue;
					}
					FTPFile ftpFile = ftpClient.mlistFile(fileName);
					if(ftpFile == null){
						log.debug("mlistFile return null");
						continue;
					}
					if (FTPFile.FILE_TYPE != ftpFile.getType()) {
						log.debug("file type ignored");
						continue;
					}
					long size = ftpFile.getSize();
					log.debug("size : " + size);
					Date lastModification =  null;
	
					if(ftpFile.getTimestamp() == null){
						lastModification =  new Date(0);
						log.debug("can't retrieve lastModification from server, 'the epoch' is used ");
					}else{
						lastModification = ftpFile.getTimestamp().getTime();
						log.debug("lastModification : " + lastModification);
					}					
					String code = getCode(remoteServer, remotePort, fileName, ftpInputDirectory, size, lastModification);
					log.debug("code with sha:"+code);
					FtpImportedFile ftpImportedFile = ftpImportedFileService.findByCode(code, currentUser.getProvider());
					if (ftpImportedFile != null) {
						log.debug("file already imported");
						continue;
					}
					File localFile = new File(distDirectory + File.separator + fileName);
					outputStream = new FileOutputStream(localFile);
					boolean isRetrieved = ftpClient.retrieveFile(fileName, outputStream);
					if (isRetrieved) {
						log.debug("local file successfully created");
						ftpImportedFile = new FtpImportedFile();
						ftpImportedFile.setCode(code);
						ftpImportedFile.setDescription(fileName);
						ftpImportedFile.setLastModification(lastModification);
						ftpImportedFile.setSize(size);
						ftpImportedFile.setImportDate(new Date());
						ftpImportedFile.setUri(getUri(remoteServer, remotePort, fileName, ftpInputDirectory));
						ftpImportedFile.setProvider(currentUser.getProvider());
						ftpImportedFileService.create(ftpImportedFile, currentUser);
						log.debug("ftpImportedFile persisted");
						if (removeDistantFile) {
							log.debug("deleting remote file ...");
							ftpClient.deleteFile(fileName);
							log.debug("deleting remote file done");
						}
						cpOk++;
					}else{
						log.warn("cant retrieve file");
						cpWarn++;
					}
				} catch (Exception e) {
					cpKo++;
					log.error("Exception on file iteration", e);
				}
			}
			ftpClient.logout();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
				if (ftpClient != null && ftpClient.isConnected()) {
					ftpClient.disconnect();
				}
			} catch (IOException ioe) {
			}
		}
		result.setDone(true);
		result.setNbItemsToProcess(cpAll);
		result.setNbItemsProcessedWithError(cpKo);
		result.setNbItemsProcessedWithWarning(cpWarn);
		result.setNbItemsCorrectlyProcessed(cpOk);		
	}

	/**
	 * build a code as : SHA-256 ( uri+":"+size+ ":"+lastModified.getTime())
	 * 
	 * @param host
	 * @param port
	 * @param fileName
	 * @param size
	 * @param lastModification
	 * @return
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 */
	private String getCode(String host, int port, String fileName, String ftpInputDirectory, long size, Date lastModification) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String code = getUri(host, port, fileName, ftpInputDirectory) + ":" + size + ":" + lastModification.getTime();
		log.debug("code:" + code);
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(code.getBytes("UTF-8"));
		return Base64.encodeBase64String(hash);
	}

	/**
	 * 
	 * @param host
	 * @param port
	 * @param fileName
	 * @param ftpInputDirectory
	 * @return
	 */
	private String getUri(String host, int port, String fileName, String ftpInputDirectory) {
		return host + ":" + port + "/" + ftpInputDirectory + "/" + fileName;
	}
}
