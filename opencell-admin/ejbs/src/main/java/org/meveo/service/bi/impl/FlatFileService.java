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
package org.meveo.service.bi.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.FileFormat;
import org.meveo.model.bi.FileStatusEnum;
import org.meveo.model.bi.FlatFile;
import org.meveo.service.base.BusinessService;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Flat file service
 *
 * @author Abdellatif BARI
 * @since 7.3.0
 */

@Stateless
public class FlatFileService extends BusinessService<FlatFile> {

    /**
     * Create the flat file
     *
     * @param fileOriginalName   the file original name
     * @param fileCurrentName    the file current name
     * @param currentDirectory   the current directory
     * @param fileFormat         the file format
     * @param errorMessage       the error message
     * @param status             the status
     * @param flatFileJobCode    the flat file job code
     * @param processingAttempts the processing attempts
     * @param linesInSuccess     the lines in success
     * @param linesInError       the lines in error
     * @return the created flat file
     * @throws BusinessException the business exception
     */
    public FlatFile create(String fileOriginalName, String fileCurrentName, String currentDirectory, FileFormat fileFormat, String errorMessage, FileStatusEnum status,
                           String flatFileJobCode, Integer processingAttempts, Integer linesInSuccess, Integer linesInError) throws BusinessException {
        FlatFile flatFile = new FlatFile();
        flatFile.setFileOriginalName(fileOriginalName);
        flatFile.setFileCurrentName(fileCurrentName);
        flatFile.setCurrentDirectory(currentDirectory);
        flatFile.setFileFormat(fileFormat);
        flatFile.setErrorMessage(errorMessage);
        flatFile.setStatus(status);
        flatFile.setLinesInSuccess(linesInSuccess);
        flatFile.setLinesInError(linesInError);
        flatFile.setProcessingAttempts(processingAttempts);
        flatFile.setFlatFileJobCode(flatFileJobCode);
        create(flatFile);
        String code = (fileFormat != null ? fileFormat.getCode() : "CODE") + flatFile.getId();
        flatFile.setCode(code);
        return flatFile;
    }

    /**
     * Update the flat file
     *
     * @param flatFile     the flat file
     * @param errorMessage the error message
     * @param status       the status
     * @throws BusinessException the business exception
     */
    public void update(FlatFile flatFile, String errorMessage, FileStatusEnum status) throws BusinessException {
        flatFile.setErrorMessage(errorMessage);
        flatFile.setStatus(status);
        update(flatFile);
    }

    /**
     * Find the flat file by its current directory and name.
     *
     * @param currentDirectory the current directory
     * @param currentName      the current name
     * @return the flat file
     */
    public FlatFile find(String currentDirectory, String currentName) {
        Query query = getEntityManager().createQuery("SELECT ff from FlatFile ff where ff.currentDirectory=:currentDirectory and ff.fileCurrentName=:currentName");
        query.setParameter("currentDirectory", currentDirectory);
        query.setParameter("currentName", currentName);
        try {
            return (FlatFile) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            return null;
        }
    }

    /**
     * Find the flat files list by their name.
     *
     * @param fileOriginalName the file original name
     * @return the flat files list
     */
    public List<FlatFile> findByFileOriginalName(String fileOriginalName) {
        TypedQuery<FlatFile> query = getEntityManager().createNamedQuery("FlatFile.findByOriginalName", FlatFile.class);
        query.setParameter("fileOriginalName", fileOriginalName.toLowerCase());
        return query.getResultList();
    }

    /**
     * Get flat file by file name.
     *
     * @param fileName the file name
     * @return the flat file
     */
    public FlatFile getFlatFileByFileName(String fileName) {
        Pattern p = Pattern.compile("\\d+\\_");
        Matcher m = p.matcher(fileName);
        FlatFile flatFile = null;
        while (m.find()) {
            String fileFormatCode = fileName.substring(0, fileName.indexOf(m.group()));
            Long flatFileId = Long.valueOf(m.group().split("_")[0]);
            String flatFileCode = fileFormatCode + flatFileId;
            flatFile = findById(flatFileId);
            if (flatFile != null && flatFile.getCode().equalsIgnoreCase(flatFileCode)) {
                return flatFile;
            }
        }
        return null;
    }
}