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
 * @author Edward P. Legaspi 7 Aug 2017
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.3.0
 */
public class FileUploadForm {

	@FormParam("uploadedFile")
	@PartType(MediaType.APPLICATION_OCTET_STREAM)
	private byte[] data;

	@FormParam("filename")
	@PartType(MediaType.TEXT_PLAIN)
	private String filename;

	@FormParam("fileFormat")
	@PartType(MediaType.TEXT_PLAIN)
	private String fileFormat;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	/**
	 * Gets the fileFormat
	 *
	 * @return the fileFormat
	 */
	public String getFileFormat() {
		return fileFormat;
	}

	/**
	 * Sets the fileFormat.
	 *
	 * @param fileFormat the new fileFormat
	 */
	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}
}
