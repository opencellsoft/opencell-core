package org.meveo.api.rest.impl;

import org.meveo.api.MassImportApi;
import org.meveo.api.dto.ImportFileTypeDto;
import org.meveo.api.rest.MassImportRs;
import org.meveo.api.rest.admin.impl.FileImportForm;

import javax.inject.Inject;
import java.util.List;

public class MassImportRsImp extends BaseRs implements MassImportRs {

    @Inject
    private MassImportApi massImportApi;


    @Override
    public List<ImportFileTypeDto> uploadAndImport(FileImportForm form) {
        return massImportApi.uploadAndImport(form);
    }

}
