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

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.admin.FileFormatDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.FileFormat;
import org.meveo.model.admin.FileType;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.FileFormatService;
import org.meveo.service.admin.impl.FileTypeService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * File format API
 *
 * @author Abdellatif BARI
 * @since 8.0.0
 */
@Stateless
public class FileFormatApi extends BaseCrudApi<FileFormat, FileFormatDto> {

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
    private FileFormat dtoToFileFormat(FileFormatDto fileFormatDto, FileFormat fileFormat) {
        boolean isNew = fileFormat.getId() == null;
        if (isNew) {
            fileFormat.setCode(fileFormatDto.getCode());
        }
        if (fileFormatDto.getConfigurationTemplate() != null) {
            fileFormat.setConfigurationTemplate(StringUtils.isEmpty(fileFormatDto.getConfigurationTemplate()) ? null : fileFormatDto.getConfigurationTemplate());
        }

        if (fileFormatDto.getDescription() != null) {
            fileFormat.setDescription(StringUtils.isEmpty(fileFormatDto.getDescription()) ? null : fileFormatDto.getDescription());
        }

        if (fileFormatDto.getFileNamePattern() != null) {
            fileFormat.setFileNamePattern(StringUtils.isEmpty(fileFormatDto.getFileNamePattern()) ? null : fileFormatDto.getFileNamePattern());
        }

        if (fileFormatDto.isFileNameUniqueness() != null) {
            fileFormat.setFileNameUniqueness(fileFormatDto.isFileNameUniqueness());
        }
        
        if (fileFormatDto.getRecordName() != null) {
            fileFormat.setRecordName(StringUtils.isEmpty(fileFormatDto.getRecordName()) ? null : fileFormatDto.getRecordName());
        }

        if (fileFormatDto.getJobCode() != null) {
            fileFormat.setJobCode(StringUtils.isEmpty(fileFormatDto.getJobCode()) ? null : fileFormatDto.getJobCode());
        }

        if (fileFormatDto.getArchiveDirectory() != null) {
            fileFormat.setArchiveDirectory(StringUtils.isEmpty(fileFormatDto.getArchiveDirectory()) ? null : fileFormatDto.getArchiveDirectory());
        }
        if (fileFormatDto.getInputDirectory() != null) {
            fileFormat.setInputDirectory(StringUtils.isEmpty(fileFormatDto.getInputDirectory()) ? null : fileFormatDto.getInputDirectory());
        }
        if (fileFormatDto.getOutputDirectory() != null) {
            fileFormat.setOutputDirectory(StringUtils.isEmpty(fileFormatDto.getOutputDirectory()) ? null : fileFormatDto.getOutputDirectory());
        }
        if (fileFormatDto.getRejectDirectory() != null) {
            fileFormat.setRejectDirectory(StringUtils.isEmpty(fileFormatDto.getRejectDirectory()) ? null : fileFormatDto.getRejectDirectory());
        }

        if (fileFormatDto.getFileTypes() != null && !fileFormatDto.getFileTypes().isEmpty()) {
            List<FileType> fileTypes = new ArrayList<>();
            for (String fileTypeCode : fileFormatDto.getFileTypes()) {
                FileType fileType = fileTypeService.findByCode(fileTypeCode);
                if (fileType == null) {
                    fileType = new FileType();
                    fileType.setCode(fileTypeCode);
                    fileTypeService.create(fileType);
                }
                fileTypes.add(fileType);
            }
            fileFormat.setFileTypes(fileTypes);
        }
        return fileFormat;
    }

    /**
     * Create a file format.
     *
     * @param fileFormatDto the file format Dto
     */
    public FileFormat create(FileFormatDto fileFormatDto) {

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

        FileFormat entity = fileFormatService.findByCode(fileFormatDto.getCode());

        if (entity != null) {
            throw new EntityAlreadyExistsException(FileFormat.class, fileFormatDto.getCode());
        }

        entity = new FileFormat();

        fileFormatService.create(dtoToFileFormat(fileFormatDto, entity));

        return entity;
    }

    /**
     * Update a file format.
     *
     * @param fileFormatDto the file format Dto
     */
    public FileFormat update(FileFormatDto fileFormatDto) {

        if (StringUtils.isBlank(fileFormatDto.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        FileFormat entity = fileFormatService.findByCode(fileFormatDto.getCode());
        if (entity == null) {
            throw new EntityDoesNotExistsException(FileFormat.class, fileFormatDto.getCode());
        }

        entity = fileFormatService.update(dtoToFileFormat(fileFormatDto, entity));

        return entity;
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

    @Override
    protected BiFunction<FileFormat, CustomFieldsDto, FileFormatDto> getEntityToDtoFunction() {
        return FileFormatDto::new;
    }
}