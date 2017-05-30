package org.meveo.admin.job;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.regex.Pattern;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.UserAuthenticator;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.local.LocalFile;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
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
    
	private File localDirFile;
	private Pattern filePattern;
	private FileSystemManager fsManager = null;
	private FileSystemOptions opts = null;
	private FileObject sftpFile;
	private FileObject src = null;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void execute(JobExecutionResultImpl result, JobInstance jobInstance, String distDirectory, String remoteServer, int remotePort, boolean removeDistantFile, String ftpInputDirectory, String extention, String ftpUsername, String ftpPassword, String ftpProtocol) {
		log.debug("start ftpClient...");

		int cpOk = 0, cpKo = 0, cpAll = 0, cpWarn = 0;
		FileObject[] children;

		try {
			initialize(distDirectory, ftpUsername, ftpPassword, extention, "SFTP".equalsIgnoreCase(ftpProtocol));
			String ftpAddress = ftpProtocol.toLowerCase() + "://" + ftpUsername + ":" + ftpPassword + "@" + remoteServer + ":" + remotePort + ftpInputDirectory;
			log.debug("ftpAddress:" + ftpAddress);
			try {
				sftpFile = fsManager.resolveFile(ftpAddress, opts);
				log.debug("SFTP connection successfully established to " + ftpAddress);
			} catch (FileSystemException ex) {
				result.setReport(StringUtils.truncate(ex.getMessage(), 255, true));
				throw new RuntimeException("SFTP error parsing path " + ftpInputDirectory, ex);
			}

			try {
				children = sftpFile.getChildren();
			} catch (FileSystemException ex) {
				result.setReport(StringUtils.truncate(ex.getMessage(), 255, true));
				throw new RuntimeException("Error collecting directory listing of " + ftpInputDirectory, ex);
			}

			for (FileObject fileObject : children) {
				try {
					String fileName = fileObject.getName().getBaseName();
					String relativePath = File.separatorChar + fileName;
					if (fileObject.getType() != FileType.FILE) {
						log.debug("Ignoring non-file " + fileObject.getName());
						continue;
					}
					log.debug("Examining remote file " + fileName);
					log.debug("patern:" + filePattern.matcher(fileName));
					if (!filePattern.matcher(fileName).matches()) {
						log.debug("Filename does not match, skipping file :" + fileName);
						continue;
					}
					long size = fileObject.getContent().getSize();
					long lastModification = fileObject.getContent().getLastModifiedTime();
					String code = getCode(remoteServer, remotePort, fileName, ftpInputDirectory, size,new Date( lastModification));
					log.debug("code with sha:"+code);
					FtpImportedFile ftpImportedFile = ftpImportedFileService.findByCode(code);
					if (ftpImportedFile != null) {
						log.debug("file already imported");
						continue;
					}
					String localUrl = "file://" + distDirectory + relativePath;
					String standardPath = distDirectory + relativePath;
					log.debug("Standard local path is " + standardPath);
					LocalFile localFile = (LocalFile) fsManager.resolveFile(localUrl);
					log.debug("Resolved local file name: " + localFile.getName());

					if (!localFile.getParent().exists()) {
						localFile.getParent().createFolder();
					}


					log.debug("Retrieving file");
					localFile.copyFrom(fileObject, new AllFileSelector());
					log.debug("get file ok");
					if (removeDistantFile) {
						log.debug("deleting remote file...");
						fileObject.delete();
						log.debug("remote file deleted");
					}

					cpOk++;
					createImportedFileHistory(fileName, new Date(lastModification), size, remoteServer, remotePort, ftpInputDirectory);

				} catch (Exception ex) {
					log.error("Error getting file type for " + fileObject.getName(), ex);
					cpKo++;
					result.setReport(StringUtils.truncate(ex.getMessage(), 255, true));
				}
			}
			// Set src for cleanup in release()
			if (children != null && children.length > 0) {
				src = children[0];
			}

		} catch (Exception e) {
			log.error("", e);
		} finally {
			result.setDone(true);
			result.setNbItemsToProcess(cpAll);
			result.setNbItemsProcessedWithError(cpKo);
			result.setNbItemsProcessedWithWarning(cpWarn);
			result.setNbItemsCorrectlyProcessed(cpOk);
			release();
		}
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
		return host + ":" + port  + ftpInputDirectory + "/" + fileName;
	}

	/**
	 * Creates the download directory localDir if it does not exist and makes a
	 * connection to the remote SFTP server.
	 * 
	 * @throws FileSystemException
	 * 
	 */
	private void initialize(String localDir, String userName, String password, String filePatternString, boolean isSftp) throws FileSystemException {
		localDirFile = new File(localDir);
		if (!localDirFile.exists()) {
			localDirFile.mkdirs();
		}
		try {
			fsManager = VFS.getManager();
		} catch (FileSystemException ex) {
			throw new RuntimeException("failed to get fsManager from VFS", ex);
		}

		UserAuthenticator auth = new StaticUserAuthenticator(null, userName, password);
		opts = getSftpOptions(isSftp);
		try {
			DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, auth);
		} catch (FileSystemException ex) {
			throw new RuntimeException("setUserAuthenticator failed", ex);
		}
		if ("false".equals(ParamBean.getInstance().getProperty("ftpAdapter.useExtentionAsRegex", "false"))) {
			filePattern = Pattern.compile(".*" + filePatternString);
		} else {
			filePattern = Pattern.compile(filePatternString);
		}

	}

	/**
	 * Release system resources, close connection to the filesystem.
	 */
	private void release() {
		FileSystem fs = null;
		if (src != null) {
			fs = src.getFileSystem();
			fsManager.closeFileSystem(fs);
		}
	}

	/**
	 * 
	 * @param isSftp
	 * @return
	 * @throws FileSystemException
	 */
	private FileSystemOptions getSftpOptions(boolean isSftp) throws FileSystemException {
		FileSystemOptions opts = new FileSystemOptions();
		if (isSftp) {
			SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");
			SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);
			SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000);
		}
		return opts;
	}

	/**
	 * 
	 * @param fileName
	 * @param lastModification
	 * @param size
	 * @param remoteServer
	 * @param remotePort
	 * @param ftpInputDirectory
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws BusinessException 
	 */
	private void createImportedFileHistory(String fileName, Date lastModification, Long size, String remoteServer, int remotePort, String ftpInputDirectory) throws NoSuchAlgorithmException, UnsupportedEncodingException, BusinessException {
		FtpImportedFile ftpImportedFile = new FtpImportedFile();
		ftpImportedFile.setCode(getCode(remoteServer, remotePort, fileName, ftpInputDirectory, size, lastModification));
		ftpImportedFile.setDescription(fileName);
		ftpImportedFile.setLastModification(lastModification);
		ftpImportedFile.setSize(size);
		ftpImportedFile.setImportDate(new Date());
		ftpImportedFile.setUri(getUri(remoteServer, remotePort, fileName, ftpInputDirectory));
		ftpImportedFileService.create(ftpImportedFile);
	}
}
