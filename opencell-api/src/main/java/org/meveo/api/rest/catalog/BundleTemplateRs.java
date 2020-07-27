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

package org.meveo.api.rest.catalog;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.catalog.BundleTemplateDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author abdelmounaim akadid
 **/
@Path("/catalog/bundleTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface BundleTemplateRs extends IBaseRs {

    /**
     * Create bundleTemplate
     * 
     * @param postData bundleTemplate information
     * @return Response of the create bundleTemplate
     */
    @POST
    @Path("/")
    public Response createBundleTemplate(BundleTemplateDto postData);

    /**
     * Update bundleTemplate
     * 
     * @param postData bundleTemplate information
     * @return Response of the update bundleTemplate
     */
    @PUT
    @Path("/")
    public Response updateBundleTemplate(BundleTemplateDto postData);
   
    /**
     * Create or update bundleTemplate
     * 
     * @param postData bundleTemplate information
     * @return Response of the create or update bundleTemplate
     */
    @POST
    @Path("/createOrUpdate")
    public Response createOrUpdateBundleTemplate(BundleTemplateDto postData);
}
