package org.meveo.api.rest.custom.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.custom.CustomTableApi;
import org.meveo.api.dto.custom.IdentityResponseDTO;
import org.meveo.api.dto.custom.UnitaryCustomTableDataDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.custom.UnitaryCustomTableRS;
import org.meveo.api.rest.impl.BaseRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class UnitaryCustomTableRsImpl extends BaseRs implements UnitaryCustomTableRS {

    @Inject
    private CustomTableApi customTableApi;

    @Override
    public IdentityResponseDTO create(UnitaryCustomTableDataDto dto) {
        customTableApi.create(dto);
        return new IdentityResponseDTO(dto.getValue().getId());
    }

    @Override
    public IdentityResponseDTO update(UnitaryCustomTableDataDto dto) {
        customTableApi.update(dto);
        return new IdentityResponseDTO(dto.getValue().getId());
    }

    @Override
    public IdentityResponseDTO remove(String tableName, Long id) {
        customTableApi.remove(tableName, id);
        return new IdentityResponseDTO(id);
    }

    @Override
    public IdentityResponseDTO enable(String tableName, Long id) {
        customTableApi.enableOrDisble(tableName, id, true);
        return new IdentityResponseDTO(id);
    }

    @Override
    public IdentityResponseDTO disable(String tableName, Long id) {
        customTableApi.enableOrDisble(tableName, id, false);
        return new IdentityResponseDTO(id);
    }



}
