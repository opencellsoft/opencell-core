package org.meveo.admin.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.parsers.FileParserBeanio;
import org.meveo.commons.parsers.FileParserFlatworm;
import org.meveo.commons.parsers.IFileParser;
import org.meveo.commons.parsers.RecordContext;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.FileFormat;
import org.meveo.model.bi.FileStatusEnum;
import org.meveo.model.bi.FlatFile;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.FileFormatService;
import org.meveo.service.bi.impl.FlatFileService;
import org.slf4j.Logger;

/**
 * Flat file validator
 *
 * @author Abdellatif BARI
 * @since 7.3.0
 */
@Stateless
public class FlatFileValidator {

    /**
     * The log.
     */
    @Inject
    private Logger log;

    @Inject
    private FileFormatService fileFormatService;

    @Inject
    private FlatFileService flatFileService;

    /**
     * The Constant DATETIME_FORMAT.
     */
    private static final String DATETIME_FORMAT = "dd_MM_yyyy-HHmmss";

    /**
     * Get property value. Return a default value if value was not set previously.
     *
     * @param key          Property key
     * @param defaultValue Default value
     * @return Value of property, or a default value if it is not set yet
     */
    private int getProperty(String key, Integer defaultValue) {
        Integer result = null;
        Properties properties = ParamBean.getInstance().getProperties();
        if (properties.containsKey(key)) {
            try {
                result = Integer.parseInt(properties.getProperty(key));
            } catch (Exception ex) {
                if (defaultValue != null) {
                    result = defaultValue;
                }
            }
        } else if (defaultValue != null) {
            result = defaultValue;
        }
        return result;
    }

    /**
     * @return the bad lines limit in file
     */
    public int getBadLinesLimit() {
        return getProperty("meveo.badLinesLimitInFile", 100);
    }

    /**
     * Gets the file parser from the configuration template.
     *
     * @param configurationTemplate the configuration template
     * @return the file parser , beanIO or Flatworm.
     * @throws BusinessException
     */
    private IFileParser getFileParser(String configurationTemplate) throws BusinessException {
        IFileParser fileParser = null;
        if (!StringUtils.isBlank(configurationTemplate)) {
            if (configurationTemplate.indexOf("<beanio") >= 0) {
                fileParser = new FileParserBeanio();
            }
            if (configurationTemplate.indexOf("<file-format>") >= 0) {
                fileParser = new FileParserFlatworm();
            }
            if (fileParser == null) {
                throw new BusinessException("Check your mapping discriptor, only flatworm or beanio are allowed");
            }
        }
        return fileParser;
    }

    /**
     * Move file.
     *
     * @param file        the file
     * @param flatFile    the flat file
     * @param destination the destination
     */
    private void moveFile(File file, FlatFile flatFile, String destination) {
        if (file != null && flatFile != null && !StringUtils.isBlank(destination)) {
            String destName = flatFile.getFileName();
            if ((new File(destination + File.separator + flatFile.getFileName())).exists()) {
                destName += "_COPY_" + DateUtils.formatDateWithPattern(new Date(), DATETIME_FORMAT);
            }
            if (!StringUtils.isBlank(flatFile.getCode())) {
                destName = flatFile.getCode() + "_" + destName;
            }
            FileUtils.moveFile(destination, file, destName);
        }
    }

    /**
     * Get absolute path
     *
     * @param path directory path
     * @return absolute path
     */
    private String getDirectory(String path) {
        //StringBuilder reportDir = new StringBuilder(ParamBean.getInstance().getChrootDir(provider.getCode()));
        StringBuilder reportDir = new StringBuilder(ParamBean.getInstance().getChrootDir(""));
        reportDir.append(File.separator).append(path);
        return reportDir.toString();
    }

    /**
     * Get input directory
     *
     * @param fileFormat        the file format
     * @param newInputDirectory the new input directory
     * @return the new input directory if it is provided else the return the fileFormat input directory
     * @throws BusinessException the business exception
     */
    private String getInputDirectory(FileFormat fileFormat, String newInputDirectory) throws BusinessException {
        String inputDirectory = newInputDirectory;
        if (StringUtils.isBlank(inputDirectory)) {
            if (fileFormat != null && !StringUtils.isBlank(fileFormat.getInputDirectory())) {
                inputDirectory = getDirectory(fileFormat.getInputDirectory());
            }
        }
        if (StringUtils.isBlank(inputDirectory)) {
            throw new BusinessException("The input directory is missing");
        }
        return inputDirectory;
    }

    /**
     * Get reject directory
     *
     * @param fileFormat        the file format
     * @param newInputDirectory the new input directory
     * @return the reject directory
     */
    private String getRejectDirectory(FileFormat fileFormat, String newInputDirectory) {
        String rejectDirectory = null;
        if (StringUtils.isBlank(newInputDirectory)) {
            if (fileFormat != null && !StringUtils.isBlank(fileFormat.getRejectDirectory())) {
                rejectDirectory = getDirectory(fileFormat.getRejectDirectory());
            }
        }
        if (StringUtils.isBlank(rejectDirectory)) {
            rejectDirectory = getInputDirectory(fileFormat, newInputDirectory);
            if (rejectDirectory.endsWith(File.separator)) {
                rejectDirectory = rejectDirectory.substring(0, rejectDirectory.lastIndexOf(File.separator));
            }
            //rejectDirectory = rejectDirectory.substring(0, rejectDirectory.lastIndexOf(File.separator)) + File.separator + "reject";
            rejectDirectory = rejectDirectory + File.separator + "reject";
        }
        return rejectDirectory;
    }

    /**
     * Move the file in input directory or reject directory
     *
     * @param file            the file
     * @param flatFile        the flat file
     * @param inputDirectory  the input directory
     * @param rejectDirectory the reject directory
     */
    private void moveFile(File file, FlatFile flatFile, String inputDirectory, String rejectDirectory) {

        if (file != null && flatFile != null && !StringUtils.isBlank(inputDirectory)) {
            if (flatFile.getStatus() == FileStatusEnum.BAD_FORMED) {
                log.info("the file {} is bad formed", flatFile.getFileName());
                moveFile(file, flatFile, rejectDirectory);
            } else {
                log.info("the file {} is well formed", flatFile.getFileName());
                if (!new File(inputDirectory).exists()) {
                    new File(inputDirectory).mkdirs();
                }
                moveFile(file, flatFile, inputDirectory);
            }
        }
    }

    /**
     * Validate Record and Field of File.
     *
     * @param fileParser the file parser
     * @return the errors list.
     * @throws Exception the exception
     */
    private List<String> validate(IFileParser fileParser) throws Exception {
        List<String> errors = new ArrayList<>();

        if (fileParser != null) {
            int badLinesLimit = getBadLinesLimit();

            long linesCounter = 0;
                RecordContext recordContext = null;
            while (true) {
                linesCounter++;
                try {
                    recordContext = fileParser.getNextRecord();
                    if (recordContext == null) {
                        break;
                    }
                    log.trace("record line content:{}", recordContext.getLineContent());
                    if (recordContext.getRecord() == null) {
                        throw recordContext.getRejectReason();
                    }
                } catch (Throwable e) {
                    String erreur = (recordContext == null || recordContext.getRejectReason() == null) ? e.getMessage() : recordContext.getRejectReason().getMessage();
                    log.warn("record on error :" + erreur);
                    errors.add("line=" + linesCounter + ": " + erreur);
                    if (errors.size() >= badLinesLimit) {
                        break;
                    }
                }
            }
            if (linesCounter == 0) {
                errors.add("file is empty");
            }
        }
        return errors;
    }

    /**
     * Validate the file by its format
     *
     * @param file           the file
     * @param fileName       the file name
     * @param fileFormat     the file format
     * @param inputDirectory the input directory
     * @return errors if the file is not valid
     * @throws BusinessException
     */
    private StringBuilder validate(File file, String fileName, FileFormat fileFormat, String inputDirectory) throws BusinessException {

        if (!StringUtils.isBlank(inputDirectory)) {
            File[] files = new File(inputDirectory).listFiles();
            for (File currentFile : files) {
                if (currentFile != null && currentFile.getName().startsWith(fileFormat.getCode()) && currentFile.getName().endsWith(fileName)) {
                    throw new BusinessException("The file " + fileName + " is already exist");
                }
            }
        }

        StringBuilder errors = new StringBuilder();

        // validation with configurationTemplate ==> beanIO or flatWorm
        String configurationTemplate = fileFormat.getConfigurationTemplate();
        if (!StringUtils.isBlank(configurationTemplate)) {
            try {

                IFileParser fileParser = getFileParser(configurationTemplate);
                if (fileParser != null) {
                    fileParser.setDataFile(file);
                    fileParser.setMappingDescriptor(configurationTemplate);
                    String recordName = fileFormat.getRecordName();
                    if (StringUtils.isBlank(recordName)) {
                        throw new Exception("The record name is required");
                    }
                    fileParser.setDataName(recordName);
                    fileParser.parsing();

                    List<String> errorsList = validate(fileParser);
                    if (errorsList != null && !errorsList.isEmpty()) {
                        errors.append(String.join(",", errorsList));
                    }
                }
            } catch (Exception e) {
                log.error("Failed to process Record file {}", fileName, e);
                errors.append(e.getMessage());
            }
        } else { // Default validation (with extension).
            if (fileFormat.getFileTypes() != null && !fileFormat.getFileTypes().isEmpty() && !fileFormat.getFileTypes()
                    .contains(FilenameUtils.getExtension(fileName.toLowerCase()))) {
                log.info("The file type is invalid");
                errors.append("The file type is invalid");
            }
        }
        return errors;

    }

    /**
     * Validate and log the file by its format
     *
     * @param file            the file
     * @param fileFormat      the faile format
     * @param fileName        the file name
     * @param inputDirectory  the input directory
     * @param rejectDirectory the reject directory
     * @return the flat file
     * @throws BusinessException the business exception
     */
    private FlatFile validateAndLogFile(File file, FileFormat fileFormat, String fileName, String inputDirectory, String rejectDirectory) throws BusinessException {

        //Validate the input file
        StringBuilder errors = validate(file, fileName, fileFormat, inputDirectory);

        //Log in database the input file.
        FlatFile flatFile = flatFileService
                .create(fileName, fileFormat, errors.toString(), errors.length() > 0 ? FileStatusEnum.BAD_FORMED : FileStatusEnum.WELL_FORMED, null, null, null, null);

        //Move the file to the corresponding directory
        moveFile(file, flatFile, inputDirectory, rejectDirectory);

        return flatFile;
    }

    /**
     * Validate and log the file by its format
     *
     * @param file              the file
     * @param fileName          the file name
     * @param fileFormatCode    the faile format code
     * @param newInputDirectory the new input directory
     * @return the flat file
     * @throws BusinessException the business exception
     */
    public void validateAndLogFile(File file, String fileName, String fileFormatCode, String newInputDirectory) throws BusinessException {

        if (!StringUtils.isBlank(fileFormatCode)) {
            FileFormat fileFormat = fileFormatService.findByCode(fileFormatCode);
            if (fileFormat == null) {
                log.error("The file format " + fileFormatCode + " is not found");
                throw new BusinessException("The file format " + fileFormatCode + " is not found");
            }
            String inputDirectory = getInputDirectory(fileFormat, newInputDirectory);
            String rejectDirectory = getRejectDirectory(fileFormat, newInputDirectory);
            validateAndLogFile(file, fileFormat, fileName, inputDirectory, rejectDirectory);
        }
    }

    /**
     * @param files             the files list
     * @param fileFormatCode    the file format code
     * @param newInputDirectory the new input directory
     * @return the messages
     * @throws BusinessException the business exception
     */
    public Map<String, String> validateAndLogFiles(File[] files, String fileFormatCode, String newInputDirectory) throws BusinessException {
        Map<String, String> messages = new HashMap<>();

        if (!StringUtils.isBlank(fileFormatCode)) {
            FileFormat fileFormat = fileFormatService.findByCode(fileFormatCode);
            String inputDirectory = getInputDirectory(fileFormat, newInputDirectory);
            String rejectDirectory = getRejectDirectory(fileFormat, newInputDirectory);
            if (fileFormat == null) {
                log.error("The file format " + fileFormatCode + " is not found");
                throw new BusinessException("The file format " + fileFormatCode + " is not found");
            }

            StringBuilder errorMessage = new StringBuilder();
            StringBuilder successMessage = new StringBuilder();

            for (File file : files) {
                FlatFile flatFile = validateAndLogFile(file, fileFormat, file.getName(), inputDirectory, rejectDirectory);
                if (flatFile != null && flatFile.getStatus() == FileStatusEnum.WELL_FORMED) {
                    successMessage.append(" ").append(file.getName()).append(" ");
                }
                if (flatFile != null && flatFile.getStatus() == FileStatusEnum.BAD_FORMED) {
                    errorMessage.append(" ").append(file.getName()).append(" ");
                }
            }

            if (successMessage.length() > 0) {
                messages.put("success", "the files " + successMessage.toString() + " are uploaded to " + inputDirectory);
            }
            if (errorMessage.length() > 0) {
                messages.put("error", "the files " + successMessage.toString() + " are uploaded to " + rejectDirectory);
            }
        }
        return messages;
    }

}