package org.meveo.apiv2.securityDeposit.securityDepositSettings.impl;

import org.meveo.apiv2.generic.services.PersistenceServiceHelper;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.apiv2.securityDeposit.SecurityDepositSettings;
import org.meveo.apiv2.securityDeposit.securityDepositSettings.SecurityDepositSettingsResource;

import javax.ws.rs.core.Response;
import java.net.URI;

public class SecurityDepositSettingsResourceImpl implements SecurityDepositSettingsResource {

    private SecurityDepositSettingsMapper securityDepositSettingsMapper = new SecurityDepositSettingsMapper();
    @Override public Response create(SecurityDepositSettings input) {
        PersistenceServiceHelper.getPersistenceService(SecurityDepositSettings.class)
                .create(securityDepositSettingsMapper.toEntity(input));
        final URI result = LinkGenerator
                .getUriBuilderFromResource(SecurityDepositSettingsResource.class, input.getId()).build();
        return Response.created(result).build();
    }

    @Override public Response update(Long id, SecurityDepositSettings securityDepositSettings) {

        org.meveo.model.securityDeposit.SecurityDepositSettings securityDepositSettingsModel = (org.meveo.model.securityDeposit.SecurityDepositSettings) PersistenceServiceHelper.getPersistenceService(SecurityDepositSettings.class).
        findById(id);

        final URI result = LinkGenerator
                .getUriBuilderFromResource(SecurityDepositSettingsResource.class, securityDepositSettingsModel.getId()).build();
        return Response.accepted(result).build();
    }
}
