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
import org.meveo.api.BaseApi;
import org.meveo.api.dto.admin.FileDto;
import org.meveo.api.dto.admin.FileRequestDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.bi.FlatFile;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
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

    @Inject
    private FlatFileValidator flatFileValidator;

    public String getProviderRootDir() {
        return paramBeanFactory.getDefaultChrootDir();
    }

    public List<FileDto> listFiles(String dir) throws BusinessApiException {
        if (!StringUtils.isBlank(dir)) {
            dir = getProviderRootDir() + File.separator + dir;
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

    public void createDir(String dir) throws BusinessApiException {
        File file = new File(getProviderRootDir() + File.separator + dir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public void zipFile(String filePath) throws BusinessApiException {
        File file = new File(getProviderRootDir() + File.separator + filePath);
        if (!file.exists()) {
            throw new BusinessApiException("File does not exists: " + file.getPath());
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
        File file = new File(getProviderRootDir() + File.separator + (isLocalDir(dir) ? "" : dir));
        if (!file.exists()) {
            throw new BusinessApiException("Directory does not exists: " + file.getPath());
        }
        File zipFile = new File(FilenameUtils.removeExtension(file.getParent() + File.separator + file.getName()) + ".zip");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
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
        File file = new File(getProviderRootDir() + File.separator + filename);
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
     * @throws MeveoApiException
     */
    public void uploadFileBase64(FileRequestDto postData) throws MeveoApiException {
        if (postData == null || StringUtils.isBlank(postData.getFilepath())) {
            missingParameters.add("filepath");
        }
        if (postData == null || StringUtils.isBlank(postData.getContent())) {
            missingParameters.add("content");
        }

        handleMissingParametersAndValidate(postData);

        String filepath = getProviderRootDir() + File.separator + postData.getFilepath();
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

        File file = new File(getProviderRootDir() + File.separator + filePath);
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
        String filename = getProviderRootDir() + File.separator + filePath;
        File file = new File(filename);

        if (file.exists()) {
            try {
                file.delete();
            } catch (Exception e) {
                throw new BusinessApiException("Error suppressing file: " + filename + ". " + e.getMessage());
            }
        } else {
            throw new BusinessApiException("File does not exists: " + filename);
        }
    }

    public void suppressDir(String dir) throws BusinessApiException {
        String filename = getProviderRootDir() + File.separator + dir;
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
        File file = new File(getProviderRootDir() + File.separator + filePath);
        if (!file.exists()) {
            throw new BusinessApiException("File does not exists: " + file.getPath());
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
