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

package org.meveo.admin.action.frontend;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.crm.Provider;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.util.ApplicationProvider;

/**
 * @author Edward P. Legaspi
 */
@WebServlet("/files/*")
public class FileServlet extends HttpServlet {

    private static final long serialVersionUID = -7865816094143438213L;

    @Inject
    @ApplicationProvider
    private Provider appProvider;

    @Inject
    @CurrentUser
    private MeveoUser currentUser;

    /** paramBeanFactory. */
    @Inject
    private ParamBeanFactory paramBeanFactory;

    /** base path.*/
    private String basePath;

    /**
     * Initialize the servlet.
     * @throws ServletException servlet exception.
     * 
     */
    public void init() throws ServletException {

        // Get base path (path to get all resources from) as init parameter.
        this.basePath = paramBeanFactory.getInstance().getChrootDir(currentUser.getProviderCode());

        // Validate base path.
        if (this.basePath == null) {
            throw new ServletException("FileServlet property 'providers.rootDir' is required.");
        } else {
            File path = new File(this.basePath);
            if (!path.exists()) {
                throw new ServletException("FileServlet property 'providers.rootDir' value '" + this.basePath + "' does actually not exist in file system.");
            } else if (!path.isDirectory()) {
                throw new ServletException("FileServlet property 'providers.rootDir' value '" + this.basePath + "' is actually not a directory in file system.");
            } else if (!path.canRead()) {
                throw new ServletException("FileServlet property 'providers.rootDir' value '" + this.basePath + "' is actually not readable in file system.");
            }
        }
    }

    /**
     * Process HEAD request. This returns the same headers as GET request, but without content.
     * 
     */
    protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Process request without content.
        processRequest(request, response, false);
    }

    /**
     * Process GET request.
     * 
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Process request with content.
        processRequest(request, response, true);
    }

    /**
     * Process the actual request.
     * 
     * @param request The request to be processed.
     * @param response The response to be created.
     * @param content Whether the request body should be written (GET) or not (HEAD).
     * @throws IOException If something fails at I/O level.
     */
    private void processRequest(HttpServletRequest request, HttpServletResponse response, boolean content) throws IOException {
        // Validate the requested file
        // ------------------------------------------------------------

        // Get requested file by path info.
        String requestedFile = request.getPathInfo();

        // Check if file is actually supplied to the request URL.
        if (requestedFile == null) {
            // Do your thing if the file is not supplied to the request URL.
            // Throw an exception, or send 404, or show default/warning page, or just ignore
            // it.
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // URL-decode the file name (might contain spaces and on) and prepare file
        // object.
        File fileOrFolder = new File(basePath, URLDecoder.decode(requestedFile, "UTF-8"));

        // Check if file actually exists in filesystem.
        if (!fileOrFolder.exists()) {
            // Do your thing if the file appears to be non-existing.
            // Throw an exception, or send 404, or show default/warning page, or just ignore
            // it.
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if(!currentUser.isAuthenticated()) {//Not Authenticated
            //Only files in "media" Folder is Valid
            boolean isFolderOrFileValide = (requestedFile.length() > 7) && ("/media/".equalsIgnoreCase(requestedFile.substring(0, 7)));
            if (!isFolderOrFileValide || fileOrFolder.isDirectory()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        
        // Prepare some variables. The ETag is an unique identifier of the file.
        String fileName = fileOrFolder.getName();

        if (fileOrFolder.isDirectory()) {
            // zipped it
            ByteArrayOutputStream zipout = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(zipout);
            FileUtils.addDirToArchive(basePath, fileOrFolder.getPath(), zos);
            zos.close();

            ServletOutputStream outStream = response.getOutputStream();
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".zip");
            outStream.write(zipout.toByteArray());
            outStream.flush();
            outStream.close();
            zipout.close();
        } else {
            // file
            try(FileInputStream fis = new FileInputStream(fileOrFolder);) {
                response.setContentType("application/force-download");
                response.setContentLength((int) fileOrFolder.length());
                response.addHeader("Content-disposition", "attachment;filename=\"" + fileName + "\"");
                IOUtils.copy(fis, response.getOutputStream());
                response.flushBuffer();
            } catch (FileNotFoundException ex) {
                throw  ex;
            }
        }

    }

}
