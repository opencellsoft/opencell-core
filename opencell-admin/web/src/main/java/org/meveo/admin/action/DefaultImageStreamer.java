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

package org.meveo.admin.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.meveo.admin.util.ModuleUtil;
import org.meveo.admin.web.servlet.PictureServlet;
import org.meveo.model.crm.Provider;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.util.ApplicationProvider;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * 
 *         Streams image for p:graphicImage use in image uploader.
 **/
@Named
@ApplicationScoped
public class DefaultImageStreamer {

    @Inject
    private Logger log;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;
    
    @Inject
    private FacesContext facesContext;

    public String getDefaultImage(String groupName) {
        if (groupName.equals("offerCategory")) {
            return PictureServlet.DEFAULT_OFFER_CAT_IMAGE;
        } else if (groupName.equals("offer")) {
            return PictureServlet.DEFAULT_OFFER_IMAGE;
        } else if (groupName.equals("service")) {
            return PictureServlet.DEFAULT_SERVICE_IMAGE;
        } else if (groupName.equals("product")) {
            return PictureServlet.DEFAULT_PRODUCT_IMAGE;
        } else if (groupName.equals("reportExtract")) {
            return PictureServlet.DEFAULT_REPORT_EXTRACT_IMAGE;
        } 

        return "offer";
    }

    public StreamedContent getImage() {
        DefaultStreamedContent streamedFile = new DefaultStreamedContent();

        if (facesContext.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the HTML. Return a stub StreamedContent so
            // that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            String fileName = facesContext.getExternalContext().getRequestParameterMap().get("fileName");
            // String providerCode = context.getExternalContext().getRequestParameterMap().get("providerCode");
            String groupName = facesContext.getExternalContext().getRequestParameterMap().get("pictureGroupName");

            String imagePath = ModuleUtil.getPicturePath(currentUser.getProviderCode(), groupName) + File.separator + fileName;
            try {
                streamedFile = new DefaultStreamedContent(new FileInputStream(imagePath));
            } catch (FileNotFoundException | NullPointerException e) {
                log.debug("failed loading image={}", imagePath);
                String ext = FilenameUtils.getExtension(fileName);
                imagePath = ModuleUtil.getPicturePath(currentUser.getProviderCode(), groupName) + File.separator + getDefaultImage(groupName);
                try {
                    streamedFile = new DefaultStreamedContent(new FileInputStream(imagePath), "image/" + ext);
                } catch (FileNotFoundException e1) {
                    log.error("no group default image, loading no image default...");
                    streamedFile = new DefaultStreamedContent(getClass().getClassLoader().getResourceAsStream("img/no_picture.png"), "image/png");
                }
            }
        }

        return streamedFile;
    }

}
