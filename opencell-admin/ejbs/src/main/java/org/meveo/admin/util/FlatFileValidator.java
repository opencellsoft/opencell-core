package org.meveo.admin.util;

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

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

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
     * @param dest the destination
     * @param file the file
     * @param name the file name
     * @param name the file code
     */
    private void moveFile(String dest, File file, String name, String fileCode) {
        String destName = name;
        if ((new File(dest + File.separator + name)).exists()) {
            destName += "_COPY_" + DateUtils.formatDateWithPattern(new Date(), DATETIME_FORMAT);
        }
        if (!StringUtils.isBlank(fileCode)) {
            destName = fileCode + "_" + destName;
        }
        FileUtils.moveFile(dest, file, destName);
    }

    private String getFileManagementDirectory(String subdirectory) {
        //StringBuilder reportDir = new StringBuilder(ParamBean.getInstance().getChrootDir(provider.getCode()));
        StringBuilder reportDir = new StringBuilder(ParamBean.getInstance().getChrootDir(""));
        reportDir.append(File.separator).append(subdirectory);
        return reportDir.toString();
    }

    private void moveFile(File file, FileFormat fileFormat, String fileCode, String fileName, String inputDirectory, boolean isFileRejected) {

        String inputDir = StringUtils.isBlank(inputDirectory) ? getFileManagementDirectory(fileFormat.getInputDirectory()) : inputDirectory;
        if (file != null && !StringUtils.isBlank(inputDir)) {
            if (isFileRejected) {
                log.error("Failed to process Record file {}", fileName);
                String rejectDirectory = null;
                if (!StringUtils.isBlank(fileFormat.getRejectDirectory())) {
                    rejectDirectory = getFileManagementDirectory(fileFormat.getRejectDirectory());
                } else {
                    rejectDirectory = inputDir;
                    if (rejectDirectory.endsWith(File.separator)) {
                        rejectDirectory = rejectDirectory.substring(0, rejectDirectory.lastIndexOf(File.separator));
                    }
                    rejectDirectory = rejectDirectory.substring(0, rejectDirectory.lastIndexOf(File.separator)) + File.separator + "reject";
                }
                moveFile(rejectDirectory, file, fileName, fileCode);
            } else {
                log.info("file validation {} done.", fileName);
                if (!new File(inputDir).exists()) {
                    new File(inputDir).mkdirs();
                }
                moveFile(inputDir, file, fileName, fileCode);
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
            while (fileParser.hasNext()) {
                RecordContext recordContext = null;
                linesCounter++;
                try {
                    recordContext = fileParser.getNextRecord();
                    log.trace("record line content:{}", recordContext.getLineContent());
                    if (recordContext.getRecord() == null) {
                        throw new Exception(recordContext.getReason());
                    }
                } catch (Throwable e) {
                    String erreur = (recordContext == null || recordContext.getReason() == null) ? e.getMessage() : recordContext.getReason();
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

        String inputDir = StringUtils.isBlank(inputDirectory) ? getFileManagementDirectory(fileFormat.getInputDirectory()) : inputDirectory;
        if (!StringUtils.isBlank(inputDir)) {
            File[] files = new File(inputDir).listFiles();
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
            if (!FilenameUtils.getExtension(fileName.toLowerCase()).equals(fileFormat.getFileType())) {
                log.error("The file type is invalid");
                errors.append("The file type is invalid");
            }
        }
        return errors;

    }

    /**
     * Validate and log the file by its format
     *
     * @param file           the file
     * @param fileName       the file name
     * @param fileFormatCode the faile format code
     * @param inputDirectory the input directory
     * @throws BusinessException the business exception
     */
    public void validateAndLogFile(File file, String fileName, String fileFormatCode, String inputDirectory) throws BusinessException {

        if (file != null && !StringUtils.isBlank(fileFormatCode)) {

            FileFormat fileFormat = null;
            StringBuilder errors = new StringBuilder();

            fileFormat = fileFormatService.findByCode(fileFormatCode);

            if (fileFormat == null) {
                log.error("The file format " + fileFormatCode + " is not found");
                throw new BusinessException("The file format " + fileFormatCode + " is not found");
            } else {
                errors = validate(file, fileName, fileFormat, inputDirectory);
            }

            FlatFile flatFile = flatFileService.create(fileName, fileFormat, errors.toString(), errors.length() > 0 ? FileStatusEnum.BAD_FORMED : FileStatusEnum.WELL_FORMED);

            moveFile(file, fileFormat, flatFile.getCode(), fileName, inputDirectory, (errors.length() > 0));

        }
    }
}