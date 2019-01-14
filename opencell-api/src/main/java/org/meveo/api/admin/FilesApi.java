package org.meveo.api.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.admin.FileDto;
import org.meveo.api.dto.admin.FileRequestDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.StringUtils;

/**
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @author Youssef IZEM
 * @lastModifiedVersion 5.4
 * 
 */
@Stateless
public class FilesApi extends BaseApi {

    public String getProviderRootDir() {
        return paramBeanFactory.getChrootDir();
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
        File file = new File(getProviderRootDir() + File.separator + dir);
        if (!file.exists()) {
            throw new BusinessApiException("Directory does not exists: " + file.getPath());
        }

        try (FileOutputStream fos = new FileOutputStream(new File(FilenameUtils.removeExtension(file.getParent() + File.separator + file.getName()) + ".zip"));
                ZipOutputStream zos = new ZipOutputStream(fos)) {
            FileUtils.addDirToArchive(getProviderRootDir(), file.getPath(), zos);
            fos.flush();
        } catch (IOException e) {
            throw new BusinessApiException("Error zipping directory: " + file.getName() + ". " + e.getMessage());
        }
    }

    /**
     * @param data array of bytes as data uploaded
     * @param filename file name
     * @throws BusinessApiException business api exeption.
     */
    public void uploadFile(byte[] data, String filename) throws BusinessApiException {
        File file = new File(getProviderRootDir() + File.separator + filename);
        FileOutputStream fop = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            fop = new FileOutputStream(file);

            fop.write(data);
            fop.flush();

            if (FilenameUtils.getExtension(file.getName()).equals("zip")) {
                // unzip
                // get parent dir
                String parentDir = file.getParent();
                FileUtils.unzipFile(parentDir, new FileInputStream(file));
            }

        } catch (Exception e) {
            throw new BusinessApiException("Error uploading file: " + filename + ". " + e.getMessage());
        } finally {
            IOUtils.closeQuietly(fop);
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

        File file = new File(getProviderRootDir() + File.separator + postData.getFilepath());
        FileOutputStream fop = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

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
     * @param postData contains filename to unzip
     * @throws MeveoApiException
     */
    public void unzipFile(FileRequestDto postData) throws MeveoApiException {
        if (postData == null || StringUtils.isBlank(postData.getFilepath())) {
            missingParameters.add("filepath");
        }

        handleMissingParametersAndValidate(postData);

        File file = new File(getProviderRootDir() + File.separator + postData.getFilepath());
        try {
            if (FilenameUtils.getExtension(file.getName()).equals("zip")) {
                String parentDir = file.getParent();
                FileUtils.unzipFile(parentDir, new FileInputStream(file));
            }
        } catch (Exception e) {
            throw new BusinessApiException("Error unziping file: " + postData.getFilepath() + ". " + e.getMessage());
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
