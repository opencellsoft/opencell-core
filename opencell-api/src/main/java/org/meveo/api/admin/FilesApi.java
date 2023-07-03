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

package org.meveo.api.admin;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.meveo.admin.storage.StorageFactory;
import org.meveo.admin.util.FlatFileValidator;
import org.meveo.admin.util.DirectoriesConstants;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.admin.FileDto;
import org.meveo.api.dto.admin.FileRequestDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.bi.FlatFile;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipOutputStream;

import static java.lang.String.format;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

/**
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @author Youssef IZEM
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.4.0
 */
@Stateless
public class FilesApi extends BaseApi {

    public static final String FILE_DOES_NOT_EXISTS = "File does not exists: ";

    public static final String SOURCE_FILE_OR_FOLDER_DOES_NOT_EXISTS = "Source file or folder does not exist: ";

    public static final String DESTINATION_FILE_OR_FOLDER_ALREADY_EXISTS = "Destination file or folder already exists: ";
    @Inject
    private FlatFileValidator flatFileValidator;

    public String getProviderRootDir() {
        return paramBeanFactory.getDefaultChrootDir();
    }
    
    @PostConstruct
    public void init() {
    	createMissingDirectories();
    }

    private void createMissingDirectories() {
        log.info("createMissingDirectories() * ");

        String importDir = getProviderRootDir() + File.separator + DirectoriesConstants.IMPORTS_ROOT_FOLDER + File.separator + "customers" + File.separator;
        String customerDirIN = importDir + DirectoriesConstants.INPUT_SUBFOLDER;
        String customerDirOUT = importDir + DirectoriesConstants.OUTPUT_SUBFOLDER;
        String customerDirERR = importDir + DirectoriesConstants.ERRORS_SUBFOLDER;
        String customerDirWARN = importDir + DirectoriesConstants.WARNINGS_SUBFOLDER;
        String customerDirKO = importDir + DirectoriesConstants.REJECT_SUBFOLDER;
        importDir = getProviderRootDir() + File.separator + DirectoriesConstants.IMPORTS_ROOT_FOLDER + File.separator + "accounts" + File.separator;
        String accountDirIN = importDir + DirectoriesConstants.INPUT_SUBFOLDER;
        String accountDirOUT = importDir + DirectoriesConstants.OUTPUT_SUBFOLDER;
        String accountDirERR = importDir + DirectoriesConstants.ERRORS_SUBFOLDER;
        String accountDirWARN = importDir + DirectoriesConstants.WARNINGS_SUBFOLDER;
        String accountDirKO = importDir + DirectoriesConstants.REJECT_SUBFOLDER;
        importDir = getProviderRootDir() + File.separator + DirectoriesConstants.IMPORTS_ROOT_FOLDER + File.separator + "subscriptions" + File.separator;
        String subDirIN = importDir + DirectoriesConstants.INPUT_SUBFOLDER;
        String subDirOUT = importDir + DirectoriesConstants.OUTPUT_SUBFOLDER;
        String subDirERR = importDir + DirectoriesConstants.ERRORS_SUBFOLDER;
        String subDirWARN = importDir + DirectoriesConstants.WARNINGS_SUBFOLDER;
        String subDirKO = importDir + DirectoriesConstants.REJECT_SUBFOLDER;
        importDir = getProviderRootDir() + File.separator + DirectoriesConstants.IMPORTS_ROOT_FOLDER + File.separator + "catalog" + File.separator;
        String catDirIN = importDir + DirectoriesConstants.INPUT_SUBFOLDER;
        String catDirOUT = importDir + DirectoriesConstants.OUTPUT_SUBFOLDER;
        String catDirKO = importDir + DirectoriesConstants.REJECT_SUBFOLDER;
        importDir = getProviderRootDir() + File.separator + DirectoriesConstants.IMPORTS_ROOT_FOLDER + File.separator + "metering" + File.separator;
        String meterDirIN = importDir + DirectoriesConstants.INPUT_SUBFOLDER;
        String meterDirOUT = importDir + DirectoriesConstants.OUTPUT_SUBFOLDER;
        String meterDirKO = importDir + DirectoriesConstants.REJECT_SUBFOLDER;
        String invoicePdfDir = getProviderRootDir() + File.separator + DirectoriesConstants.INVOICES_ROOT_FOLDER + File.separator + "pdf";
        String invoiceXmlDir = getProviderRootDir() + File.separator + DirectoriesConstants.INVOICES_ROOT_FOLDER + File.separator + "xml";
        String jasperDir = getProviderRootDir() + File.separator + DirectoriesConstants.JASPER_ROOT_FOLDER;
        String priceplanVersionsDir = getProviderRootDir() + File.separator + DirectoriesConstants.IMPORTS_ROOT_FOLDER + File.separator + "priceplan_versions";
        importDir = getProviderRootDir() + File.separator + DirectoriesConstants.IMPORTS_ROOT_FOLDER + File.separator + "cdr" + File.separator + "flatFile" + File.separator;
        String cdrFlatFileDirIn = importDir + DirectoriesConstants.INPUT_SUBFOLDER;
        String cdrFlatFileDirOut = importDir + DirectoriesConstants.OUTPUT_SUBFOLDER;
        
        List<String> filePaths = Arrays.asList("", customerDirIN, customerDirOUT, customerDirERR, customerDirWARN, customerDirKO, accountDirIN, accountDirOUT, accountDirERR, accountDirWARN, accountDirKO, subDirIN,
            subDirOUT, subDirERR, subDirWARN, catDirIN, catDirOUT, catDirKO, subDirKO, meterDirIN, meterDirOUT, meterDirKO, invoicePdfDir, invoiceXmlDir, jasperDir, priceplanVersionsDir, cdrFlatFileDirIn, cdrFlatFileDirOut);
        for (String custDirs : filePaths) {
            File subDir = new File(custDirs);
            if (!StorageFactory.existsDirectory(subDir)) {
                StorageFactory.mkdirs(subDir);
            }
        }
    }

    public List<FileDto> listFiles(String dir) throws BusinessApiException {
        if (!StringUtils.isBlank(dir)) {
            dir = getProviderRootDir() + File.separator + normalizePath(dir);
        } else {
            dir = getProviderRootDir();
        }

        File folder = new File(dir);

        if (folder.isFile()) {
            throw new BusinessApiException("Path " + dir + " is a file.");
        }

        List<FileDto> result = new ArrayList<FileDto>();

        if (! StorageFactory.isS3Activated()) {
            if (folder.listFiles() != null && Objects.requireNonNull(folder.listFiles()).length > 0) {
                List<File> files = Arrays.asList(Objects.requireNonNull(folder.listFiles()));
                for (File file : files) {
                    result.add(new FileDto(file));
                }
            }
        }
        else {
            if (Objects.requireNonNull(StorageFactory.listSubFoldersAndFiles(folder)).size() > 0) {
                Map<String, Date> map = Objects.requireNonNull(StorageFactory.listSubFoldersAndFiles(folder));
                for (Map.Entry<String, Date> entry : map.entrySet()) {
                    result.add(new FileDto(entry.getKey(), entry.getValue()));
                }
            }
        }

        return result;
    }

    /**
     * Remove any directory above the provider directory root
     * @param dir
     * @return
     */
    private String normalizePath(String dir) {
        if (dir == null) {
            throw new BusinessApiException("Invalid parameter, file or directory is null");
        }
        File dirFile = new File(getProviderRootDir() + File.separator + dir);
        Path path = dirFile.toPath();
        path = path.normalize();
        String prefix = getProviderRootDir().replace("./", "");
        if (!path.toString().contains(prefix)) {
            throw new EntityDoesNotExistsException(FILE_DOES_NOT_EXISTS + dir);
        }
        return dir;
    }

    public void createDir(String dir) throws BusinessApiException {
        File file = new File(getProviderRootDir() + File.separator + normalizePath(dir));
        if (!StorageFactory.existsDirectory(file)) {
            StorageFactory.createDirectory(file);
        }
    }

    public void zipFile(String filePath) throws BusinessApiException {
        File file = new File(getProviderRootDir() + File.separator + normalizePath(filePath));
        if (!file.exists()) {
            throw new BusinessApiException(FILE_DOES_NOT_EXISTS + file.getPath());
        }

        try {
            FileUtils.archiveFile(file);
        } catch (IOException e) {
            throw new BusinessApiException("Error zipping file: " + file.getName() + ". " + e.getMessage());
        }
    }

    /**
     * @param dir directory to be zipped.
     * @throws BusinessApiException business exception.
     */
    public void zipDir(String dir) throws BusinessApiException {
        String normalizedDir = (isLocalDir(dir) ? "" : normalizePath(dir));
        File file = new File(getProviderRootDir() + File.separator + normalizedDir);
        if (!StorageFactory.existsDirectory(file)) {
            throw new BusinessApiException("Directory does not exists: " + file.getPath());
        }
        File zipFile = new File(FilenameUtils.removeExtension(file.getParent() + File.separator + file.getName()) + ".zip");
        try (ZipOutputStream zos = new ZipOutputStream(Objects.requireNonNull(StorageFactory.getOutputStream(zipFile)))) {
            FileUtils.addDirToArchive(getProviderRootDir(), file.getPath(), zos);
            zos.flush();
            if (isLocalDir(dir))
                Files.move(zipFile.toPath(), Paths.get(getProviderRootDir() + File.separator + zipFile.getName()), ATOMIC_MOVE);
        } catch (IOException e) {
            throw new BusinessApiException("Error zipping directory: " + file.getName() + ". " + e.getMessage());
        }
    }

    private boolean isLocalDir(String dir) {
        return dir.equals("./") || dir.equals("/.") || dir.equals(".");
    }

    /**
     * @param data       array of bytes as data uploaded
     * @param filename   file name
     * @param fileFormat file format
     * @return The created flat file record
     * @throws BusinessApiException business api exeption.
     */
    public FlatFile uploadFile(byte[] data, String filename, String fileFormat) throws BusinessApiException {
        File file = new File(getProviderRootDir() + File.separator + normalizePath(filename));
        try (OutputStream fop = StorageFactory.getOutputStream(file)){
//            if (!file.exists()) {
//                file.createNewFile();
//            }


            assert fop != null;
            fop.write(data);
            fop.close();
            
            if (FilenameUtils.getExtension(file.getName()).equals("zip")) {
                // unzip
                // get parent dir
                String parentDir = file.getParent();
                FileUtils.unzipFile(parentDir, StorageFactory.getInputStream(file));
            }

            if (!StringUtils.isBlank(fileFormat)) {
                return flatFileValidator.validateProcessFile(file, filename, fileFormat);
            }
            return null;

        } catch (Exception e) {
            throw new BusinessApiException("Error uploading file: " + filename + ". " + e.getMessage());
        }
    }

    /**
     * Allows to upload a base64 file
     *
     * @param postData contains filename and the base64 data to upload
     * @throws MeveoApiException
     */
    public void uploadFileBase64(FileRequestDto postData) throws MeveoApiException {
        if (postData == null) {
            throw new InvalidParameterException("Body request is empty");
        }
        if (StringUtils.isBlank(postData.getFilepath())) {
            missingParameters.add("filepath");
        }
        if (StringUtils.isBlank(postData.getContent())) {
            missingParameters.add("content");
        }

        handleMissingParametersAndValidate(postData);


        String filepath = getProviderRootDir() + File.separator + normalizePath(postData.getFilepath());
        File file = new File(filepath);
        OutputStream fop = null;
        try {

            File parent = file.getParentFile();
            if (parent == null) {
                throw new BusinessApiException("Invalid path : " + filepath);
            }
            StorageFactory.mkdirs(parent);
            StorageFactory.createNewFile(file);
            fop = StorageFactory.getOutputStream(file);
            assert fop != null;
            fop.write(Base64.decodeBase64(postData.getContent()));
            fop.flush();

        } catch (Exception e) {
            throw new BusinessApiException("Error uploading file: " + postData.getFilepath() + ". " + e.getMessage());
        } finally {
            IOUtils.closeQuietly(fop);
        }
    }

    /**
     * Allows to unzip a file
     *
     * @param filePath
     * @param deleteOnError
     * @throws MeveoApiException
     */
    public void unzipFile(String filePath, boolean deleteOnError) throws MeveoApiException {
        if (filePath == null || StringUtils.isBlank(filePath)) {
            throw new BusinessApiException("filePath is required ! ");
        }

        File file = new File(getProviderRootDir() + File.separator + normalizePath(filePath));
        if (!StorageFactory.isValidZip(file)) {
            suppressFile(filePath);
            throw new BusinessApiException("The zipped file is invalid ! ");
        }

        try(InputStream fileInputStream = StorageFactory.getInputStream(file)) {
            String parentDir = file.getParent();
            FileUtils.unzipFile(parentDir, fileInputStream);
        } catch (Exception e) {
            if (deleteOnError) {
                suppressFile(filePath);
            }
            throw new BusinessApiException("Error unziping file: " + filePath + ". " + e.getMessage());
        }
    }

    public void suppressFile(String filePath) throws BusinessApiException {
        String filename = getProviderRootDir() + File.separator + normalizePath(filePath);
        File file = new File(filename);

        if (StorageFactory.exists(file)) {
            try {
                StorageFactory.delete(file);
            } catch (Exception e) {
                throw new BusinessApiException("Error suppressing file: " + filename + ". " + e.getMessage());
            }
        } else {
            throw new BusinessApiException(FILE_DOES_NOT_EXISTS + filename);
        }
    }

    public void suppressDir(String dir) throws BusinessApiException {
        String filename = getProviderRootDir() + File.separator + normalizePath(dir);
        File file = new File(filename);

        if (StorageFactory.existsDirectory(file)) {
            try {
                StorageFactory.deleteDirectory(file);
            } catch (Exception e) {
                throw new BusinessApiException("Error suppressing file: " + filename + ". " + e.getMessage());
            }
        } else {
            throw new BusinessApiException("Directory does not exists: " + filename);
        }
    }

    /**
     * @param filePath file's path
     * @param response http servlet response.
     * @throws BusinessApiException business exception.
     */
    public void downloadFile(String filePath, HttpServletResponse response) throws BusinessApiException {
        File file = checkAndGetExistingFile(filePath);

        try (InputStream fis = StorageFactory.getInputStream(file)) {
            response.setContentType(Files.probeContentType(file.toPath()));
            response.setContentLength((int) StorageFactory.length(file));
            response.addHeader("Content-disposition", "attachment;filename=\"" + file.getName() + "\"");
            IOUtils.copy(fis, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new BusinessApiException("Error zipping file: " + file.getName() + ". " + e.getMessage());
        }
    }

    /**
     * Check existing file with two format (Java format and also SQL format) depends on the type of ReportExtract
     *
     * @param filePath File Path
     * @return The existing File {@link File}
     */
    public File checkAndGetExistingFile(String filePath) {

        File javaXMlFormatFile = (filePath.contains(getProviderRootDir().replace("\\", "/"))) ?
                new File(filePath) : new File(getProviderRootDir() + File.separator + normalizePath(filePath));
        if (StorageFactory.exists(javaXMlFormatFile)) {
            return javaXMlFormatFile;
        } else {
            String[] fileNameParts = filePath.split("\\.");
            if (fileNameParts.length > 2) {
                File sqlXMlFormatFile = new File((".").concat(filePath.split("\\.")[1] + "_" + format("%04d", 0) + "." + filePath.split("\\.")[2]));
                if (sqlXMlFormatFile.exists()) {
                    return sqlXMlFormatFile;
                } else {
                    throw new BusinessApiException(FILE_DOES_NOT_EXISTS + javaXMlFormatFile.getPath());
                }
            } else {
                throw new BusinessApiException(FILE_DOES_NOT_EXISTS + javaXMlFormatFile.getPath());
            }
        }
    }

    public void moveFileOrDirectory(String srcPath, String destPath) throws BusinessApiException {
        String srcName = getProviderRootDir() + File.separator + normalizePath(srcPath);
        File source = new File(srcName);

        String[] arrFileName = srcName.split("/");
        String nameFileOrDir = arrFileName[arrFileName.length - 1];
        String destName = getProviderRootDir() + File.separator + normalizePath(destPath) + File.separator + nameFileOrDir;
        File dest = new File(destName);

        if (StorageFactory.exists(source) || StorageFactory.existsDirectory(source)) {
            if (!StorageFactory.isDirectory(source)) {
                if (!StorageFactory.exists(dest)) {
                    if (!StorageFactory.existsDirectory(dest.getParentFile())) {
                        StorageFactory.mkdirs(dest.getParentFile());
                    }

                    try {
                        StorageFactory.moveFileOrObject(srcName, destName);
                    } catch (Exception e) {
                        throw new BusinessApiException("Error while moving file with source path: " + srcName
                                + " and destination path: " + destName + e.getMessage());
                    }
                }
                else {
                    throw new BusinessApiException(DESTINATION_FILE_OR_FOLDER_ALREADY_EXISTS + destName);
                }
            } else {
                if (!StorageFactory.existsDirectory(dest)) {
                    if (!StorageFactory.existsDirectory(dest.getParentFile())) {
                        StorageFactory.mkdirs(dest.getParentFile());
                    }

                    try {
                        StorageFactory.moveFileOrObject(srcName, destName);
                    } catch (Exception e) {
                        throw new BusinessApiException("Error while moving directory with source path: " + srcName
                                + " and destination path: " + destName + e.getMessage());
                    }
                }
                else {
                    throw new BusinessApiException(DESTINATION_FILE_OR_FOLDER_ALREADY_EXISTS + destName);
                }
            }
        }
        else {
            throw new BusinessApiException(SOURCE_FILE_OR_FOLDER_DOES_NOT_EXISTS + srcName);
        }
    }
}
