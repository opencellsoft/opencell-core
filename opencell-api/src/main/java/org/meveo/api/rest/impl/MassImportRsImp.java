package org.meveo.api.rest.impl;

import org.meveo.api.MassImportApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.ImportFileTypeDto;
import org.meveo.api.rest.MassImportRs;
import org.meveo.api.rest.admin.impl.FileUploadForm;

import javax.inject.Inject;
import java.util.List;

public class MassImportRsImp extends BaseRs implements MassImportRs {

    @Inject
    private MassImportApi massImportApi;


    @Override
    public List<ImportFileTypeDto> uploadAndImport(FileUploadForm form) {
        return massImportApi.uploadAndImport(form);
    }

    @Override
    public List<ImportFileTypeDto> uploadMassFile(FileUploadForm form) {
        return massImportApi.uploadMassImportFile(form);
    }

    @Override
    public ActionStatus importMassFile(List<ImportFileTypeDto> filesType) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "Files import succeeded");

        try {
            massImportApi.importMassFile(filesType);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}
