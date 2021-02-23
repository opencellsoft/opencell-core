package org.meveo.api.dto.cpq;

import javax.validation.constraints.NotNull;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.cpq.Media;
import org.meveo.model.cpq.enums.MediaTypeEnum;

/**
 * 
 * @author Tarik FAKHOURI.
 * @version 10.0
 *
 */
public class MediaDto extends BaseEntityDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String code;

	private String description;
 
	@NotNull
	private String mediaName;
	@NotNull
	private String label;
	@NotNull
	private Boolean main;
	@NotNull
	private MediaTypeEnum mediaType;
	private String mediaPath;
	
	public MediaDto() {
		
	}
	
	public MediaDto(Media media) {
		this.id=media.getId(); 
		this.code=media.getCode();
		this.description=media.getCode();
		this.mediaName = media.getMediaName();
		this.label = media.getLabel();
		this.main = media.getMain();
		this.mediaPath = media.getMediaPath();
		this.mediaType = media.getMediaType();
	}
	
	 
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the main
	 */
	public Boolean getMain() {
		return main;
	}

	/**
	 * @return the mediaName
	 */
	public String getMediaName() {
		return mediaName;
	}
	/**
	 * @param mediaName the mediaName to set
	 */
	public void setMediaName(String mediaName) {
		this.mediaName = mediaName;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return the main
	 */
	public Boolean isMain() {
		return main;
	}
	/**
	 * @param main the main to set
	 */
	public void setMain(Boolean main) {
		this.main = main;
	}
	/**
	 * @return the mediaType
	 */
	public MediaTypeEnum getMediaType() {
		return mediaType;
	}
	/**
	 * @param mediaType the mediaType to set
	 */
	public void setMediaType(MediaTypeEnum mediaType) {
		this.mediaType = mediaType;
	}
	/**
	 * @return the mediaPath
	 */
	public String getMediaPath() {
		return mediaPath;
	}
	/**
	 * @param mediaPath the mediaPath to set
	 */
	public void setMediaPath(String mediaPath) {
		this.mediaPath = mediaPath;
	}

	  
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	
	
}
