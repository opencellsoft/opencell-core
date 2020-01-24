package org.meveo.api.rest.custom.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Link;

import org.meveo.api.custom.CustomTableApi;
import org.meveo.api.dto.custom.UnitaryCustomTableDataDto;
import org.meveo.api.dto.custom.UnitaryCustomTableDataResource;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.custom.UnitaryCustomTableRS;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.apiv2.models.Resource;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class UnitaryCustomTableRsImpl extends BaseRs implements UnitaryCustomTableRS {

    private static final String BASE_NAME = "/unitaryCustomTable/";

    @Inject
    private CustomTableApi customTableApi;

    @Override
    public Resource create(UnitaryCustomTableDataDto dto) {
        customTableApi.create(dto);
        return new UnitaryCustomTableDataResource(dto, asHeatoeas(dto.getCustomTableCode(), dto.getValue().getId()));
    }

    @Override
    public Resource update(UnitaryCustomTableDataDto dto) {
        customTableApi.update(dto);
        return new UnitaryCustomTableDataResource(dto, asHeatoeas(dto.getCustomTableCode(), dto.getValue().getId()));
    }

    @Override
    public Resource remove(String tableName, Long id) {
        customTableApi.remove(tableName, id);
        return getDeleteResource(tableName, id);
    }

    @Override
    public Resource enable(String tableName, Long id) {
        customTableApi.enableOrDisble(tableName, id, true);
        return getResource(tableName, id);
    }

    @Override
    public Resource disable(String tableName, Long id) {
        customTableApi.enableOrDisble(tableName, id, false);
        return getResource(tableName, id);
    }

    List<Link> asHeatoeas(String tableName, Long id) {
        return Arrays.asList(Link.fromUri(BASE_NAME+"{tableName}/{id}").rel("remove").type("DELETE").build(tableName, id),
                Link.fromUri(BASE_NAME+"{tableName}/{id}/enable").rel("enable").type("POST").build(tableName, id),
                Link.fromUri(BASE_NAME+"{tableName}/{id}/disable").rel("disable").type("POST").build(tableName, id),
                Link.fromUri(BASE_NAME).rel("create").title("create a row using a request body").type("POST").build(),
                Link.fromUri(BASE_NAME).rel("update").title("update a row by giving the columns and values in the request body ").type("POST").build());
    }

    private Resource getDeleteResource(String tableName, Long id) {
        return new Resource() {
            @Nullable
            @Override
            public Long getId() {
                return id;
            }

            @Nullable
            @Override
            public List<Link> getLinks() {
                return Collections.singletonList(Link.fromUri(BASE_NAME).rel("POST").title("create").type("application/json").build());
            }
        };
    }

    private Resource getResource(String tableName, Long id) {
        return new Resource() {
            @Nullable
            @Override
            public Long getId() {
                return id;
            }

            @Nullable
            @Override
            public List<Link> getLinks() {
                return asHeatoeas(tableName, id);
            }
        };
    }
}
