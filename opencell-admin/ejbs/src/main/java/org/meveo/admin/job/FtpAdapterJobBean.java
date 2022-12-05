/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.job;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.regex.Pattern;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

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
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.FtpOperationEnum;
import org.meveo.model.jobs.FtpTransferredFile;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.FtpTransferredFileService;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

/**
 * The Class FtpAdapterJobBean.
 *
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 */
@Stateless
public class FtpAdapterJobBean {

    /**
     * the Job result report msg length
     */
    private static final int REPORT_MSG_LENGTH = 255;

    /**
     * The log.
     */
    @Inject
    private Logger log;

    /**
     * The ftp imported file service.
     */
    @Inject
    private FtpTransferredFileService ftpTransferredFileService;

    /**
     * The job execution service.
     */
    @Inject
    private JobExecutionService jobExecutionService;

    /**
     * The file pattern.
     */
    private Pattern filePattern;

    /**
     * The fs manager.
     */
    private FileSystemManager fsManager = null;

    /**
     * The opts.
     */
    private FileSystemOptions opts = null;

    /**
     * The src.
     */
    private FileObject src = null;

    /**
     * param Factory
     */
    @Inject
    private ParamBeanFactory paramBeanFactory;

    /**
     * actual currentOperation : IMPORT or EXPORT
     */
    private FtpOperationEnum currentOperation;

    /**
     * Execute.
     *
     * @param result             the result
     * @param jobInstance        the job instance
     * @param localDirectory     the dist directory
     * @param remoteServer       the remote server
     * @param remotePort         the remote port
     * @param removeOriginalFile the remove distant file
     * @param remoteDirectory    the ftp input directory
     * @param extention          the extention
     * @param ftpUsername        the ftp username
     * @param ftpPassword        the ftp password
     * @param ftpProtocol        the ftp protocol
     * @param operation          the transfer operation: IMPORT or EXPORT
     */
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance, String localDirectory, String remoteServer, int remotePort, boolean removeOriginalFile,
            String remoteDirectory, String extention, String ftpUsername, String ftpPassword, String ftpProtocol, String operation) {
        log.debug("start ftpClient...");

        String ftpAddress = ftpProtocol.toLowerCase() + "://" + ftpUsername + ":" + ftpPassword + "@" + remoteServer + ":" + remotePort + remoteDirectory;
        log.debug("ftpAddress:{}", ftpAddress);
        String locaAddress = "file://" + Paths.get(localDirectory).toAbsolutePath().toString();
        log.debug("locaAddress:{}", locaAddress);

        int cpOk = 0, cpKo = 0, cpAll = 0, cpWarn = 0;
        String fromDirURL, toDirURL;
        FileObject[] children;

        if ("IMPORT".equalsIgnoreCase(operation)) {
            fromDirURL = ftpAddress;
            toDirURL = locaAddress;
        } else if ("EXPORT".equalsIgnoreCase(operation)) {
            fromDirURL = locaAddress;
            toDirURL = ftpAddress;
        } else {
            throw new RuntimeException("Operation " + operation + " not supported : Only IMPORT or EXPORT");
        }
        this.currentOperation = FtpOperationEnum.valueOf(operation);

        try {
            initialize(ftpUsername, ftpPassword, extention, "SFTP".equalsIgnoreCase(ftpProtocol));

            try {
                createToDirIfNotExist(locaAddress, ftpAddress);
            } catch (FileSystemException ex) {
                result.setReport(StringUtils.truncate(ex.getMessage(), REPORT_MSG_LENGTH, true));
                throw new RuntimeException("Error creating new folder at " + (isOperationImport() ? locaAddress : ftpAddress), ex);
            }

            FileObject fromDir;
            try {
                fromDir = fsManager.resolveFile(fromDirURL, opts);
                if (isOperationImport()) {
                    log.debug("SFTP connection successfully established to {}", ftpAddress);
                }
            } catch (FileSystemException ex) {
                result.setReport(StringUtils.truncate(ex.getMessage(), REPORT_MSG_LENGTH, true));
                throw new RuntimeException("SFTP error parsing path " + fromDirURL, ex);
            }

            try {
                children = fromDir.getChildren();
            } catch (FileSystemException ex) {
                result.setReport(StringUtils.truncate(ex.getMessage(), REPORT_MSG_LENGTH, true));
                throw new RuntimeException("Error collecting directory listing of " + fromDir, ex);
            }

            for (FileObject fromFile : children) {
                if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                    break;
                }
                try {
                    String fileName = fromFile.getName().getBaseName();
                    String relativePath = File.separatorChar + fileName;
                    if (fromFile.getType() != FileType.FILE) {
                        log.debug("Ignoring non-file {}", fromFile.getName());
                        continue;
                    }
                    log.debug("Examining origin file {}", fileName);
                    log.debug("patern:{}", filePattern.matcher(fileName));
                    if (!filePattern.matcher(fileName).matches()) {
                        log.debug("Filename does not match, skipping file :{}", fileName);
                        continue;
                    }
                    if (checkFileAlreadyCopied(fromFile, remoteServer, remotePort, fileName, remoteDirectory)) {
                        continue;
                    }

                    String toFileURL = toDirURL + relativePath;
                    String standardToFilepath = localDirectory + relativePath;
                    log.debug("Standard local path is {}", standardToFilepath);
                    FileObject toFile = fsManager.resolveFile(toFileURL);
                    log.debug("Resolved local file name: {}", toFile.getName());

                    if (!toFile.getParent().exists()) {
                        toFile.getParent().createFolder();
                    }

                    log.debug("Retrieving file");
                    toFile.copyFrom(fromFile, new AllFileSelector());
                    log.debug("get file ok");
                    if (removeOriginalFile) {
                        log.debug("deleting remote file...");
                        fromFile.delete();
                        log.debug("remote file deleted");
                    }
                    cpOk++;
                    createTransferredFileHistory(fileName, new Date(fromFile.getContent().getLastModifiedTime()), fromFile.getContent().getSize(), remoteServer, remotePort,
                            remoteDirectory);
                } catch (Exception ex) {
                    log.error("Error getting file type for " + fromFile.getName(), ex);
                    cpKo++;
                    result.setReport(StringUtils.truncate(ex.getMessage(), REPORT_MSG_LENGTH, true));
                }
            }
            // Set src for cleanup in release()
            if (children != null && children.length > 0) {
                src = children[0];
            }

        } catch (Exception e) {
            log.error("", e);
        } finally {
            result.setNbItemsToProcess(cpAll);
            result.setNbItemsProcessedWithError(cpKo);
            result.setNbItemsProcessedWithWarning(cpWarn);
            result.setNbItemsCorrectlyProcessed(cpOk);
            release();
        }
    }

    /**
     * check if distnation directory exists, if not create it
     *
     * @param localAddress local address
     * @param ftpAddress remote address
     * @throws FileSystemException File system exception
     */
    private void createToDirIfNotExist(String localAddress, String ftpAddress) throws FileSystemException {
        //If import create local Dir if not exists
        if (isOperationImport()) {
            File localDirFile = new File(localAddress);
            if (!localDirFile.exists()) {
                localDirFile.mkdirs();
            }
        } else {
            //Create the remote dir in case of export
            FileObject remoteDirFile = fsManager.resolveFile(ftpAddress);
            if (!remoteDirFile.exists()) {
                remoteDirFile.createFolder();
            }
        }
    }

    /**
     * Check if the origin file hasn't been already copied
     *
     * @param fileObject origin file
     * @param remoteServer remote server
     * @param remotePort remote port
     * @param fileName file name
     * @param remoteDirectry ftp remote directory
     * @return
     * @throws FileSystemException File System Exception
     * @throws UnsupportedEncodingException Unsupported Encoding Exception
     * @throws NoSuchAlgorithmException No Such Algorithm Exception
     */
    private boolean checkFileAlreadyCopied(FileObject fileObject, String remoteServer, int remotePort, String fileName, String remoteDirectry)
            throws FileSystemException, UnsupportedEncodingException, NoSuchAlgorithmException {
        long size = fileObject.getContent().getSize();
        long lastModification = fileObject.getContent().getLastModifiedTime();
        String code = getCode(remoteServer, remotePort, fileName, remoteDirectry, size, new Date(lastModification));
        log.debug("code with sha:{}", code);
        FtpTransferredFile ftpTransferredFile = ftpTransferredFileService.findByCode(code);
        if (ftpTransferredFile != null) {
            log.debug("file is already transferred");
            return true;
        }
        return false;
    }

    /**
     * build a code as : SHA-256 ( uri+":"+size+ ":"+lastModified.getTime())
     *
     * @param host              the host
     * @param port              the port
     * @param fileName          the file name
     * @param remoteDirectory   the ftp remote directory
     * @param size              the size
     * @param lastModification  the last modification
     * @return the code
     * @throws NoSuchAlgorithmException     the no such algorithm exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    private String getCode(String host, int port, String fileName, String remoteDirectory, long size, Date lastModification)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String code = getUri(host, port, fileName, remoteDirectory) + ":" + size + ":" + lastModification.getTime() + ":" + this.currentOperation.name();
        log.debug("code:{}", code);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(code.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(hash);
    }

    /**
     * Gets the uri.
     *
     * @param host              the host
     * @param port              the port
     * @param fileName          the file name
     * @param ftpInputDirectory the ftp input directory
     * @return the uri
     */
    private String getUri(String host, int port, String fileName, String ftpInputDirectory) {
        return host + ":" + port + ftpInputDirectory + "/" + fileName;
    }

    /**
     * Creates the download directory localDir if it does not exist and makes a connection to the remote SFTP server.
     *
     * @param userName          the user name
     * @param password          the password
     * @param filePatternString the file pattern string
     * @param isSftp            the is sftp
     * @throws FileSystemException the file system exception
     */
    private void initialize(String userName, String password, String filePatternString, boolean isSftp) throws FileSystemException {
        try {
            fsManager = VFS.getManager();
        } catch (FileSystemException ex) {
            throw new RuntimeException("failed to get fsManager from VFS", ex);
        }

        UserAuthenticator auth = new StaticUserAuthenticator(null, userName, password);
        opts = getSftpOptions(isSftp);
        DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, auth);
        if ("false".equals(paramBeanFactory.getInstance().getProperty("ftpAdapter.useExtentionAsRegex", "false"))) {
            filePattern = Pattern.compile(".*" + filePatternString);
        } else {
            filePattern = Pattern.compile(filePatternString);
        }
    }

    /**
     * if the current operation is IMPORT
     * @return current operation == IMPORT
     */
    private boolean isOperationImport() {
        return FtpOperationEnum.IMPORT.equals(this.currentOperation);
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
     * Gets the sftp options.
     *
     * @param isSftp the is sftp
     * @return the sftp options
     * @throws FileSystemException the file system exception
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
     * Creates the imported file history.
     *
     * @param fileName          the file name
     * @param lastModification  the last modification
     * @param size              the size
     * @param remoteServer      the remote server
     * @param remotePort        the remote port
     * @param ftpInputDirectory the ftp input directory
     * @throws NoSuchAlgorithmException     the no such algorithm exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws BusinessException            the business exception
     */
    private void createTransferredFileHistory(String fileName, Date lastModification, Long size, String remoteServer, int remotePort, String ftpInputDirectory)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, BusinessException {
        FtpTransferredFile ftpTransferredFile = new FtpTransferredFile();
        ftpTransferredFile.setCode(getCode(remoteServer, remotePort, fileName, ftpInputDirectory, size, lastModification));
        ftpTransferredFile.setDescription(fileName);
        ftpTransferredFile.setLastModification(lastModification);
        ftpTransferredFile.setSize(size);
        ftpTransferredFile.setTransferDate(new Date());
        ftpTransferredFile.setUri(getUri(remoteServer, remotePort, fileName, ftpInputDirectory));
        ftpTransferredFile.setOperation(currentOperation);
        ftpTransferredFileService.create(ftpTransferredFile);
    }
}
