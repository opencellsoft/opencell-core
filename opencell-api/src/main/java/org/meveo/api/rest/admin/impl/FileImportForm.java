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

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

/**
 * @author Ilham Chafik
 */
public class FileImportForm {

    @FormParam("uploadedFile")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    private byte[] data;

    @FormParam("filename")
    @PartType(MediaType.TEXT_PLAIN)
    private String filename;

    @FormParam("filesToImport")
    @PartType(MediaType.TEXT_PLAIN)
    private String files;

    public FileImportForm() { }

    public FileImportForm(byte[] data, String filename, String files) {
        this.data = data;
        this.filename = filename;
        this.files = files;
    }

    public byte[] getData() { return data; }

    public void setData(byte[] data) { this.data = data; }

    public String getFilename() { return filename; }

    public void setFilename(String filename) { this.filename = filename; }

    public String getFiles() { return files; }

    public void setFiles(String files) { this.files = files; }
}
