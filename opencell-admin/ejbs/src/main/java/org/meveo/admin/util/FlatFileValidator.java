package org.meveo.admin.util;

import org.apache.commons.io.FilenameUtils;
import org.meveo.admin.async.FlatFileAsyncListResponse;
import org.meveo.admin.async.FlatFileAsyncUnitResponse;
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
import java.util.Date;

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
     * The directory of the file management.
     */
    private final String FILE_MANAGEMENT_DIRECTORY = "fileManagement";

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
     */
    private void moveFile(String dest, File file, String name) {
        String destName = name;
        if ((new File(dest + File.separator + name)).exists()) {
            destName += "_COPY_" + DateUtils.formatDateWithPattern(new Date(), DATETIME_FORMAT);
        }
        FileUtils.moveFile(dest, file, destName);
    }

    private String getFileManagementDirectory(String subdirectory) {
        //StringBuilder reportDir = new StringBuilder(ParamBean.getInstance().getChrootDir(provider.getCode()));
        StringBuilder reportDir = new StringBuilder(ParamBean.getInstance().getChrootDir(""));
        reportDir.append(File.separator).append(subdirectory);
        return reportDir.toString();
    }

    /**
     * Validate the file by its format
     *
     * @param file           the file
     * @param fileName       the file name
     * @param fileFormatCode the faile format code
     * @param inputDirectory the input directory
     * @throws BusinessException the business exception
     */
    public void validateFileFormat(File file, String fileName, String fileFormatCode, String inputDirectory) throws BusinessException {

        if (!StringUtils.isBlank(fileFormatCode)) {

            FileFormat fileFormat = null;
            StringBuilder errors = new StringBuilder();

            fileFormat = fileFormatService.findByCode(fileFormatCode);

            if (fileFormat == null) {
                log.error("The file format " + fileFormatCode + " is not found");
                throw new BusinessException("The file format " + fileFormatCode + " is not found");
            } else {
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

                            FlatFileAsyncListResponse response = validate(fileParser);
                            long linesCounter = 0;
                            for (FlatFileAsyncUnitResponse flatFileAsyncResponse : response.getResponses()) {
                                linesCounter++;
                                if (!flatFileAsyncResponse.isSuccess()) {
                                    errors.append("line=" + flatFileAsyncResponse.getLineNumber() + ": " + flatFileAsyncResponse.getReason());
                                }
                            }
                            if (linesCounter == 0) {
                                errors.append("file is empty");
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
            }

            FlatFile flatFile = flatFileService.create(fileName, fileFormat, errors.toString(), errors.length() > 0 ? FileStatusEnum.BAD_FORMED : FileStatusEnum.WELL_FORMED);

            fileName = flatFile.getCode() + "_" + fileName;

            if (errors.length() > 0) {
                log.error("Failed to process Record file {}", fileName);
                if (file != null) {
                    moveFile(getFileManagementDirectory(fileFormatCode + File.separator + "KO"), file, fileName);
                }
            } else {
                log.info("file validation {} done.", fileName);
                if (file != null) {
                    if (!StringUtils.isBlank(inputDirectory) || !StringUtils.isBlank(fileFormat.getInputDirectory())) {
                        inputDirectory = !StringUtils.isBlank(inputDirectory) ? inputDirectory : getFileManagementDirectory(fileFormat.getInputDirectory());
                        if (!new File(inputDirectory).exists()) {
                            new File(inputDirectory).mkdirs();
                        }
                        moveFile(inputDirectory, file, fileName);
                    }
                }
            }
        }
    }

    /**
     * Validate Record and Field of File.
     *
     * @param fileParser the file parser
     * @return the validation response.
     * @throws Exception the exception
     */
    private FlatFileAsyncListResponse validate(IFileParser fileParser) throws Exception {
        FlatFileAsyncListResponse flatFileAsyncListResponse = new FlatFileAsyncListResponse();
        if (fileParser != null) {
            long linesCounter = 0;
            while (fileParser.hasNext()) {
                RecordContext recordContext = null;
                linesCounter++;
                FlatFileAsyncUnitResponse flatFileAsyncResponse = new FlatFileAsyncUnitResponse();
                flatFileAsyncResponse.setLineNumber(linesCounter);
                try {
                    recordContext = fileParser.getNextRecord();
                    flatFileAsyncResponse.setLineRecord(recordContext.getLineContent());
                    log.trace("record line content:{}", recordContext.getLineContent());
                    if (recordContext.getRecord() == null) {
                        throw new Exception(recordContext.getReason());
                    }
                    flatFileAsyncResponse.setSuccess(true);
                } catch (Throwable e) {
                    String erreur = (recordContext == null || recordContext.getReason() == null) ? e.getMessage() : recordContext.getReason();
                    log.warn("record on error :" + erreur);
                    flatFileAsyncResponse.setSuccess(false);
                    flatFileAsyncResponse.setReason(erreur);
                    //flatFileAsyncListResponse.getResponses().add(flatFileAsyncResponse);
                    //break;
                }
                flatFileAsyncListResponse.getResponses().add(flatFileAsyncResponse);
            }
        }
        return flatFileAsyncListResponse;
    }
}