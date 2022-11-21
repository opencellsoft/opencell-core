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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipOutputStream;

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
        List<String> filePaths = Arrays.asList("", customerDirIN, customerDirOUT, customerDirERR, customerDirWARN, customerDirKO, accountDirIN, accountDirOUT, accountDirERR, accountDirWARN, accountDirKO, subDirIN,
            subDirOUT, subDirERR, subDirWARN, catDirIN, catDirOUT, catDirKO, subDirKO, meterDirIN, meterDirOUT, meterDirKO, invoicePdfDir, invoiceXmlDir, jasperDir, priceplanVersionsDir);
        for (String custDirs : filePaths) {
            File subDir = new File(custDirs);
            if (!subDir.exists()) {
                subDir.mkdirs();
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

        if (folder.listFiles() != null && folder.listFiles().length > 0) {
            List<File> files = Arrays.asList(folder.listFiles());
            if (files != null) {
                for (File file : files) {
                    result.add(new FileDto(file));
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
        if(dir == null){
            throw new BusinessApiException("Invalid parameter, file or directory is null");
        }
        File dirFile = new File(getProviderRootDir()+File.separator+dir);
        Path path = dirFile.toPath();
        path= path.normalize();
        String prefix =  getProviderRootDir().replace("./","");
        if(!path.toString().contains(prefix)){
            throw new EntityDoesNotExistsException(FILE_DOES_NOT_EXISTS + dir);
        }
        return dir;
    }

    public void createDir(String dir) throws BusinessApiException {
        File file = new File(getProviderRootDir() + File.separator + normalizePath(dir));
        if (!file.exists()) {
            file.mkdirs();
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
        String normalizedDir = (isLocalDir(dir) ? "" : normalizePath(dir)) ;
        File file = new File(getProviderRootDir() + File.separator + normalizedDir);
        if (!StorageFactory.existsDirectory(file)) {
            throw new BusinessApiException("Directory does not exists: " + file.getPath());
        }
        File zipFile = new File(FilenameUtils.removeExtension(file.getParent() + File.separator + file.getName()) + ".zip");
        try (ZipOutputStream zos = new ZipOutputStream(Objects.requireNonNull(StorageFactory.getOutputStream(zipFile)))) {
            FileUtils.addDirToArchive(getProviderRootDir(), file.getPath(), zos);
            zos.flush();
            if(isLocalDir(dir))
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
        try (FileOutputStream fop = new FileOutputStream(file)){
//            if (!file.exists()) {
//                file.createNewFile();
//            }

            ;

            fop.write(data);
            fop.close();
            
            if (FilenameUtils.getExtension(file.getName()).equals("zip")) {
                // unzip
                // get parent dir
                String parentDir = file.getParent();
                FileUtils.unzipFile(parentDir, new FileInputStream(file));
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
     * @throws  MeveoApiException
     */
    public void uploadFileBase64(FileRequestDto postData) throws MeveoApiException {
        if (postData == null){
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
        FileOutputStream fop = null;
        try {

            File parent = file.getParentFile();
            if (parent == null) {
                throw new BusinessApiException("Invalid path : " + filepath);
            }

            parent.mkdirs();
            file.createNewFile();
            fop = new FileOutputStream(file);
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
        if (!FileUtils.isValidZip(file)) {
            suppressFile(filePath);
            throw new BusinessApiException("The zipped file is invalid ! ");
        }

        try(FileInputStream fileInputStream = new FileInputStream(file)) {
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

        if (file.exists()) {
            try {
                file.delete();
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

        if (file.exists()) {
            try {
                org.apache.commons.io.FileUtils.deleteDirectory(file);
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
        File file = new File(getProviderRootDir() + File.separator + normalizePath(filePath));
        if (!file.exists()) {
            throw new BusinessApiException(FILE_DOES_NOT_EXISTS + file.getPath());
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            response.setContentType(Files.probeContentType(file.toPath()));
            response.setContentLength((int) file.length());
            response.addHeader("Content-disposition", "attachment;filename=\"" + file.getName() + "\"");
            IOUtils.copy(fis, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new BusinessApiException("Error zipping file: " + file.getName() + ". " + e.getMessage());
        }
    }

}
