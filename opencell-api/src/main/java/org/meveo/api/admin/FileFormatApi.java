package org.meveo.api.admin;

import org.apache.commons.beanutils.BeanUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.admin.FileFormatDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.FileFormat;
import org.meveo.model.admin.FileType;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.FileFormatService;
import org.meveo.service.admin.impl.FileTypeService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * File format API
 *
 * @author Abdellatif BARI
 * @since 8.0.0
 */
@Stateless
public class FileFormatApi extends BaseApi {

    @Inject
    private FileFormatService fileFormatService;

    @Inject
    private FileTypeService fileTypeService;

    /**
     * Convert dtp to FileFormat entity
     *
     * @param fileFormatDto the fileFormat Dto
     * @return the fileFormat entity
     */
    private FileFormat dtoToFileFormat(FileFormatDto fileFormatDto) {
        FileFormat fileFormat = null;
        if (fileFormatDto != null) {
            fileFormat = new FileFormat();
            try {
                BeanUtils.copyProperties(fileFormat, fileFormatDto);
                if (fileFormatDto.getFileTypes() != null && !fileFormatDto.getFileTypes().isEmpty()) {
                    List<FileType> fileTypes = new ArrayList<>();
                    for (String fileTypeCode : fileFormatDto.getFileTypes()) {
                        FileType fileType = fileTypeService.findByCode(fileTypeCode);
                        if (fileType == null) {
                            throw new BusinessApiException("Unrecognized file type with code " + fileTypeCode);
                        }
                        fileTypes.add(fileType);
                    }
                    fileFormat.setFileTypes(fileTypes);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("Problem on converting from dto to file format entity id={} !", fileFormatDto.getCode(), e);
            }
        }
        return fileFormat;
    }

    /**
     * Create a file format.
     *
     * @param fileFormatDto the file format Dto
     */
    public void create(FileFormatDto fileFormatDto) {

        if (StringUtils.isBlank(fileFormatDto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(fileFormatDto.getInputDirectory())) {
            missingParameters.add("inputDirectory");
        }
        if (fileFormatDto.getFileTypes() == null || fileFormatDto.getFileTypes().isEmpty()) {
            missingParameters.add("fileTypes");
        } else {
            for (int i = 0; i < fileFormatDto.getFileTypes().size(); i++) {
                String fileTypeCode = fileFormatDto.getFileTypes().get(i);
                if (StringUtils.isBlank(fileTypeCode)) {
                    missingParameters.add("fileTypes[" + i + "]");
                }
            }
        }

        handleMissingParameters();

        fileFormatService.create(dtoToFileFormat(fileFormatDto));
    }

    /**
     * Removes a file format based on it's code.
     *
     * @param code file format's code
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public void remove(String code) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        FileFormat fileFormat = fileFormatService.findByCode(code);
        if (fileFormat != null) {
            fileFormatService.remove(fileFormat);
        } else {
            throw new EntityDoesNotExistsException(Title.class, code);
        }
    }

}
