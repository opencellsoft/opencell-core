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
package org.meveo.admin.action.admin;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.jboss.seam.international.status.Messages;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.FlatFileValidator;
import org.meveo.admin.util.DirectoriesConstants;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.FileFormat;
import org.meveo.model.admin.User;
import org.meveo.model.shared.DateUtils;
import org.meveo.security.AccessScopeEnum;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.FileFormatService;
import org.meveo.util.view.PageAccessHandler;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standard backing bean for {@link User} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 *
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.0.0
 */
@Named
@ViewScoped
public class FilesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Logger. */
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    protected Messages messages;

    @Inject
    private FileFormatService fileFormatService;

    @Inject
    private FlatFileValidator flatFileValidator;

    @Inject
    protected ParamBeanFactory paramBeanFactory;

    @Inject
    protected Conversation conversation;

    @Inject
    private PageAccessHandler pageAccessHandler;

    private String providerFilePath;
    private String selectedFolder;
    private boolean currentDirEmpty;
    private String selectedFileName;
    private String newFilename;
    private String directoryName;
    private List<File> fileList;
    private UploadedFile file;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

    private boolean autoUnzipped;

    final private String ZIP_FILE_EXTENSION = ".zip";

    private FileFormat fileFormat;

    @PostConstruct
    public void init() {
        this.providerFilePath = paramBeanFactory.getInstance().getChrootDir(currentUser.getProviderCode());
        if (conversation.isTransient()) {
            conversation.begin();
            createMissingDirectories();
            setSelectedFolder(null);
        }
    }

    public String getFilePath() {
        return providerFilePath;

    }

    private String getFilePath(String name) {
        String result = getFilePath() + File.separator + name;
        if (selectedFolder != null) {
            result = getFilePath() + File.separator + selectedFolder + File.separator + name;
        }
        return result;
    }

    public void createMissingDirectories() {
        log.info("createMissingDirectories() * ");

        String importDir = getFilePath() + File.separator + DirectoriesConstants.IMPORTS_ROOT_FOLDER + File.separator + "customers" + File.separator;
        String customerDirIN = importDir + DirectoriesConstants.INPUT_SUBFOLDER;
        String customerDirOUT = importDir + DirectoriesConstants.OUTPUT_SUBFOLDER;
        String customerDirERR = importDir + DirectoriesConstants.ERRORS_SUBFOLDER;
        String customerDirWARN = importDir + DirectoriesConstants.WARNINGS_SUBFOLDER;
        String customerDirKO = importDir + DirectoriesConstants.REJECT_SUBFOLDER;
        importDir = getFilePath() + File.separator + DirectoriesConstants.IMPORTS_ROOT_FOLDER + File.separator + "accounts" + File.separator;
        String accountDirIN = importDir + DirectoriesConstants.INPUT_SUBFOLDER;
        String accountDirOUT = importDir + DirectoriesConstants.OUTPUT_SUBFOLDER;
        String accountDirERR = importDir + DirectoriesConstants.ERRORS_SUBFOLDER;
        String accountDirWARN = importDir + DirectoriesConstants.WARNINGS_SUBFOLDER;
        String accountDirKO = importDir + DirectoriesConstants.REJECT_SUBFOLDER;
        importDir = getFilePath() + File.separator + DirectoriesConstants.IMPORTS_ROOT_FOLDER + File.separator + "subscriptions" + File.separator;
        String subDirIN = importDir + DirectoriesConstants.INPUT_SUBFOLDER;
        String subDirOUT = importDir + DirectoriesConstants.OUTPUT_SUBFOLDER;
        String subDirERR = importDir + DirectoriesConstants.ERRORS_SUBFOLDER;
        String subDirWARN = importDir + DirectoriesConstants.WARNINGS_SUBFOLDER;
        String subDirKO = importDir + DirectoriesConstants.REJECT_SUBFOLDER;
        importDir = getFilePath() + File.separator + DirectoriesConstants.IMPORTS_ROOT_FOLDER + File.separator + "catalog" + File.separator;
        String catDirIN = importDir + DirectoriesConstants.INPUT_SUBFOLDER;
        String catDirOUT = importDir + DirectoriesConstants.OUTPUT_SUBFOLDER;
        String catDirKO = importDir + DirectoriesConstants.REJECT_SUBFOLDER;
        importDir = getFilePath() + File.separator + DirectoriesConstants.IMPORTS_ROOT_FOLDER + File.separator + "metering" + File.separator;
        String meterDirIN = importDir + DirectoriesConstants.INPUT_SUBFOLDER;
        String meterDirOUT = importDir + DirectoriesConstants.OUTPUT_SUBFOLDER;
        String meterDirKO = importDir + DirectoriesConstants.REJECT_SUBFOLDER;
        String invoicePdfDir = getFilePath() + File.separator + DirectoriesConstants.INVOICES_ROOT_FOLDER + File.separator + "pdf";
        String invoiceXmlDir = getFilePath() + File.separator + DirectoriesConstants.INVOICES_ROOT_FOLDER + File.separator + "xml";
        String jasperDir = getFilePath() + File.separator + DirectoriesConstants.JASPER_ROOT_FOLDER;
        String priceplanVersionsDir = getFilePath() + File.separator + DirectoriesConstants.IMPORTS_ROOT_FOLDER + File.separator + "priceplan_versions";
        importDir = getFilePath() + File.separator + DirectoriesConstants.IMPORTS_ROOT_FOLDER + File.separator + "cdr" + File.separator + "flatFile" + File.separator;
        String cdrFlatFileDirIn = importDir + DirectoriesConstants.INPUT_SUBFOLDER;
        String cdrFlatFileDirOut = importDir + DirectoriesConstants.OUTPUT_SUBFOLDER;
        List<String> filePaths = Arrays.asList("", customerDirIN, customerDirOUT, customerDirERR, customerDirWARN, customerDirKO, accountDirIN, accountDirOUT, accountDirERR, accountDirWARN, accountDirKO, subDirIN,
            subDirOUT, subDirERR, subDirWARN, catDirIN, catDirOUT, catDirKO, subDirKO, meterDirIN, meterDirOUT, meterDirKO, invoicePdfDir, invoiceXmlDir, jasperDir, priceplanVersionsDir, cdrFlatFileDirIn, cdrFlatFileDirOut);
        for (String custDirs : filePaths) {
            File subDir = new File(custDirs);
            if (!subDir.exists()) {
                subDir.mkdirs();
            }
        }
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
        log.info("set file to" + file.getFileName());
    }

    public void deleteSelectedFile() {
        String folder = getFilePath() + File.separator + (this.selectedFolder == null ? "" : this.selectedFolder);
        log.info("delete file" + folder + File.separator + selectedFileName);
        File file = new File(folder + File.separator + selectedFileName);
        if (file.exists()) {
            file.delete();
        }
        this.selectedFileName = null;
        buildFileList();
    }

    public StreamedContent getSelectedFile() {
        StreamedContent result = null;
        try {
            String folder = getFilePath() + File.separator + (this.selectedFolder == null ? "" : this.selectedFolder);
            result = new DefaultStreamedContent(new FileInputStream(new File(folder + File.separator + selectedFileName)), null, selectedFileName);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            log.error("error generated while getting seleceted file", e);
        }
        return result;
    }

    public String getSelectedFolder() {
        return selectedFolder;
    }

    public boolean hasSelectedFolder() {
        return !StringUtils.isBlank(selectedFolder);
    }

    public void setSelectedFolder(String selectedFolder) {
        setSelectedFileName(null);
        if (selectedFolder == null) {
            log.debug("setSelectedFolder to null");
            this.selectedFolder = null;
        } else if ("..".equals(selectedFolder)) {
            if (this.selectedFolder.lastIndexOf(File.separator) > 0) {
                log.debug("setSelectedFolder to parent " + this.selectedFolder + " -> " + this.selectedFolder.substring(0, this.selectedFolder.lastIndexOf(File.separator)));
                this.selectedFolder = this.selectedFolder.substring(0, this.selectedFolder.lastIndexOf(File.separator));
            } else {
                this.selectedFolder = null;
            }
        } else {
            log.debug("setSelectedFolder " + selectedFolder);
            if (this.selectedFolder == null) {
                this.selectedFolder = selectedFolder;
            } else {
                this.selectedFolder += File.separator + selectedFolder;
            }
        }
        buildFileList();
    }

    private void buildFileList() {
        String folder = getFilePath() + File.separator + (this.selectedFolder == null ? "" : this.selectedFolder);
        File file = new File(folder);
        log.debug("getFileList " + folder);

        File[] files = file.listFiles();

        fileList = files == null ? new ArrayList<File>() : new ArrayList<File>(Arrays.asList(files));
        currentDirEmpty = !StringUtils.isBlank(this.selectedFolder) && fileList.size() == 0;
    }

    public String getFileType(String fileName) {
        if (fileName != null && fileName.endsWith(".zip")) {
            return "zip";
        }
        return "text";
    }

    public String getLastModified(File file) {
        return sdf.format(new Date(file.lastModified()));
    }

    public String getSelectedFileName() {
        return selectedFileName;
    }

    public void setSelectedFileName(String selectedFileName) {
        log.debug("setSelectedFileName " + selectedFileName);
        this.selectedFileName = selectedFileName;
    }

    public String getNewFilename() {
        return newFilename;
    }

    public void setNewFilename(String newFilename) {
        this.newFilename = newFilename;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public boolean isCurrentDirEmpty() {
        return currentDirEmpty;
    }

    public List<File> getFileList() {
        return fileList;
    }

    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        String fileName = file.getFileName();

        log.debug("upload file={},autoUnziped {}", fileName, autoUnzipped);
        // FIXME: use resource bundle
        File tempDirectory = null;
        try {
            String folderPath = null;
            String filePath = null;

            if (fileFormat != null) {
                folderPath = getFilePath() + File.separator + "temp" + DateUtils.formatDateWithPattern(new Date(), "dd_MM_yyyy-HHmmss");
                tempDirectory = new File(folderPath);
                if (!tempDirectory.exists()) {
                    tempDirectory.mkdirs();
                }
                filePath = folderPath + File.separator + fileName;
            } else {
                folderPath = getFilePath("");
                filePath = getFilePath(fileName);
            }

            InputStream fileInputStream = file.getInputstream();
            if (this.isAutoUnzipped()) {
                if (!fileName.endsWith(ZIP_FILE_EXTENSION)) {
                    messages.info(fileName + " isn't a valid zip file!");
                    copyFile(filePath, fileInputStream);
                } else {
                    copyUnZippedFile(folderPath, fileInputStream);
                }
            } else {
                copyFile(filePath, fileInputStream);
            }
            if (fileFormat != null) {
                File[] files = tempDirectory.listFiles();
                Map<String, String> messagesValidation = flatFileValidator.validateAndLogFiles(files, fileFormat.getCode(), getFilePath(""));
                tempDirectory.delete();
                buildFileList();

                if (messagesValidation != null && !messagesValidation.isEmpty()) {
                    if (messagesValidation.containsKey("success")) {
                        messages.info(messagesValidation.get("success"));
                    }
                    if (messagesValidation.containsKey("error")) {
                        messages.error(messagesValidation.get("error"));
                    }
                }

            } else {
                messages.info(fileName + " is uploaded to " + ((selectedFolder != null) ? selectedFolder : "Home"));
            }
        } catch (BusinessException e) {
            log.error("Failed to upload a file {}", fileName, e);
            messages.error(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to upload a file {}", fileName, e);
            messages.error("Error while uploading " + fileName);
        } finally {
            if (tempDirectory != null && tempDirectory.isDirectory()) {
                tempDirectory.delete();
            }
        }
    }

    public void upload(ActionEvent event) {
        if (file != null) {
            log.debug("upload file={}", file);
            try {
                String filePath = getFilePath(FilenameUtils.getName(file.getFileName()));
                copyFile(filePath, file.getInputstream());

                messages.info(file.getFileName() + " is uploaded to " + ((selectedFolder != null) ? selectedFolder : "Home"));
            } catch (IOException e) {
                log.error("Failed to upload a file {}", file.getFileName(), e);
                messages.error("Error while uploading " + file.getFileName());
            }
        } else {
            log.info("upload file is null");

        }
    }

    public void createDirectory() {
        if (!StringUtils.isBlank(directoryName)) {
            String filePath = getFilePath(directoryName);
            File newDir = new File(filePath);
            if (!newDir.exists()) {
                if (newDir.mkdir()) {
                    buildFileList();
                    directoryName = "";
                }
            }
        }
    }

    public void deleteDirectory() {
        log.debug("deleteDirectory:" + selectedFolder);
        if (currentDirEmpty) {
            String filePath = getFilePath("");
            File currentDir = new File(filePath);
            if (currentDir.exists() && currentDir.isDirectory()) {
                if (currentDir.delete()) {
                    setSelectedFolder("..");
                    createMissingDirectories();
                    buildFileList();
                }
            }
        }
    }

    public void renameFile() {
        if (!StringUtils.isBlank(selectedFileName) && !StringUtils.isBlank(newFilename)) {
            String filePath = getFilePath(selectedFileName);
            String newFilePath = getFilePath(newFilename);
            File currentFile = new File(filePath);
            File newFile = new File(newFilePath);
            if (currentFile.exists() && currentFile.isFile() && !newFile.exists()) {
                if (currentFile.renameTo(newFile)) {
                    buildFileList();
                    selectedFileName = newFilename;
                    newFilename = "";
                }
            }
        }
    }

    public StreamedContent getDownloadZipFile() {
        String filename = selectedFolder == null ? "meveo-fileexplore" : selectedFolder.substring(selectedFolder.lastIndexOf(File.separator) + 1);
        String sourceFolder = getFilePath() + (selectedFolder == null ? "" : selectedFolder);
        try {
            byte[] filedata = FileUtils.createZipFile(sourceFolder);
            InputStream is = new ByteArrayInputStream(filedata);
            return new DefaultStreamedContent(is, "application/octet-stream", filename + ".zip");
        } catch (Exception e) {
            log.debug("Failed to zip a file", e);
        }
        return null;
    }

    private void copyUnZippedFile(String filePath, InputStream in) {
        try {
            FileUtils.unzipFile(filePath, in);
            buildFileList();
        } catch (Exception e) {
            log.debug("error when upload zip file for new UI", e);
        }
    }

    /**
     * @param filePath file path
     * @param in input stream
     */
    public void copyFile(String filePath, InputStream in) {
        try (OutputStream out = new FileOutputStream(new File(filePath))) {
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
            log.debug("New file created!");
            buildFileList();
        } catch (IOException e) {
            log.error("Failed saving file. ", e);
        }
    }

    public boolean isAutoUnzipped() {
        return autoUnzipped;
    }

    public void setAutoUnzipped(boolean autoUnzipped) {
        this.autoUnzipped = autoUnzipped;
    }

    /**
     * Gets the fileFormat
     *
     * @return the fileFormat
     */
    public FileFormat getFileFormat() {
        return fileFormat;
    }

    /**
     * Sets the fileFormat.
     *
     * @param fileFormat the new fileFormat
     */
    public void setFileFormat(FileFormat fileFormat) {
        this.fileFormat = fileFormat;
    }

    /**
     * Get a list of file formats
     *
     * @return A list of file formats
     */
    public List<FileFormat> getFileFormatList() {
        List<FileFormat> fileFormats = fileFormatService.list();
        return fileFormats;
    }

    public void handleFileFormatChange(ValueChangeEvent event) {
        this.fileFormat = (FileFormat) event.getNewValue();
    }

    /**
     * Determine if current user can modify a current entity
     * 
     * @return True if user has suficient permissions to modify a current entity
     */
    public boolean canUserUpdateEntity() {
        return pageAccessHandler.isCurrentURLAccesible(AccessScopeEnum.UPDATE.getHttpMethod());
    }

    /**
     * Determine if current user can delete a current entity
     * 
     * @return True if user has sufficient permissions to delete a current entity
     */
    public boolean canUserDeleteEntity() {
        return pageAccessHandler.isCurrentURLAccesible(AccessScopeEnum.DELETE.getHttpMethod());
    }
}