package org.meveo.model.mediation;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.meveo.model.EnableEntity;
import org.meveo.model.NotifiableEntity;

/**
 * Record ftp file status when uplod,download,rename,delete
 * @author Tyshan Shi
 *
 */
@NotifiableEntity
public class MeveoFtpFile extends EnableEntity implements Serializable{

	private static final long serialVersionUID = -6610759225502996091L;
	
	private String filename;
	private Long size;
	private Date lastModified;
	private ActionEnum action;
	private static Calendar calendar;
	static{
		calendar=Calendar.getInstance();
	}
	public MeveoFtpFile(String filename,Long size){
		this.filename=filename;
		this.size=size;
	}
	public MeveoFtpFile(String filename,Long size,Long lastModified){
		this(filename,size);
		calendar.setTimeInMillis(lastModified);
		this.lastModified=calendar.getTime();
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	public ActionEnum getAction() {
		return action;
	}
	public void setAction(ActionEnum action) {
		this.action = action;
	}
	@Override
	public String toString() {
		return "MeveoFtpFile [filename=" + filename + ", size=" + size + ", lastModified=" + lastModified + ", action="
				+ action.getLabel() + "]";
	}

}
