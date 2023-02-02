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

package org.meveo.api.rest.admin.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.admin.FilesApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.admin.FileRequestDto;
import org.meveo.api.dto.response.admin.GetFilesResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.admin.FilesRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.bi.FlatFile;

/**
 * @author Edward P. Legaspi
 * @author Youssef IZEM
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.3.0
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class FilesRsImpl extends BaseRs implements FilesRs {

    @Inject
    private FilesApi filesApi;

    @Override
    public GetFilesResponseDto listFiles() {
        GetFilesResponseDto result = new GetFilesResponseDto();

        try {
            result.setFiles(filesApi.listFiles(null));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetFilesResponseDto listFiles(String dir) {
        GetFilesResponseDto result = new GetFilesResponseDto();

        try {
            result.setFiles(filesApi.listFiles(dir));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createDir(String dir) {
        ActionStatus result = new ActionStatus();

        try {
            filesApi.createDir(dir);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus zipFile(String filePath) {
        ActionStatus result = new ActionStatus();

        try {
            filesApi.zipFile(filePath);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus zipDir(String dir) {
        ActionStatus result = new ActionStatus();

        try {
            filesApi.zipDir(dir);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus suppressFile(String filePathAsParam, String filePath) {
        ActionStatus result = new ActionStatus();

        try {
            if (filePathAsParam != null) {
                filesApi.suppressFile(filePathAsParam);
            }
            else {
                filesApi.suppressFile(filePath);
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus suppressDir(String dir) {
        ActionStatus result = new ActionStatus();

        try {
            filesApi.suppressDir(dir);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus uploadFile(FileUploadForm form) {
        ActionStatus result = new ActionStatus();

        try {
            FlatFile flatFile = filesApi.uploadFile(form.getData(), form.getFilename(), form.getFileFormat());

            if (flatFile != null) {
                result.setEntityId(flatFile.getId());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus uploadFileBase64(FileRequestDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            filesApi.uploadFileBase64(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus uploadZippedFileBase64(FileRequestDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            filesApi.uploadFileBase64(postData);
            filesApi.unzipFile(postData.getFilepath(), true);
            filesApi.suppressFile(postData.getFilepath());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus downloadFile(String file) {
        ActionStatus result = new ActionStatus();

        try {
            filesApi.downloadFile(file, httpServletResponse);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus moveFileOrDirectory(String srcPath, String destPath) {
        ActionStatus result = new ActionStatus();

        try {
            filesApi.moveFileOrDirectory(srcPath, destPath);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

}
