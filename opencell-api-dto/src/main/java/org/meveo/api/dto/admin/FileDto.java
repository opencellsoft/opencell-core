package org.meveo.api.dto.admin;

import java.io.File;
import java.util.Date;

public class FileDto {

	private String name;
	private boolean isDirectory;
	private Date lastModified;

	public FileDto() {

	}

	public FileDto(File file) {
		name = file.getName();
		isDirectory = file.isDirectory();
		lastModified = new Date(file.lastModified());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

}
