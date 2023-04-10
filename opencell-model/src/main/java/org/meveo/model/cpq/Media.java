package org.meveo.model.cpq;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.cpq.enums.MediaTypeEnum;

/**
 * 
 * @author Tarik FAKHOURI.
 * @author Mbarek-Ay
 * @version 10.0
 *
 */
@Entity
@CustomFieldEntity(cftCodePrefix = "Media")
@Table(name = "cpq_media", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_media_seq")})
@NamedQuery(name = "Media.findByMediaName", query = "select m from Media m where m.mediaName=:mediaName")
public class Media extends BusinessCFEntity{

	public Media(Media copy) { 
		this.mediaName = copy.mediaName;
		this.label = copy.label;
		this.main = copy.main;
		this.mediaType = copy.mediaType;
		this.mediaPath = copy.mediaPath;
		this.code = copy.code;
		this.description = copy.description;
		this.cfValues = copy.cfValues;
		this.cfAccumulatedValues = copy.cfAccumulatedValues;
	}

	public Media() {
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

  
	
	/**
	 * short name of the media
	 */
	@Column(name = "media_name", length = 255, nullable = false)
	@Size(max = 255)
	@NotNull
	private String mediaName;
	
	/**
	 * label : short description for this type of attribute
	 */
	@Column(name = "label", length = 255, nullable = false)
	@NotNull
	private String label;
	
	/**
	 * flag indicate if the media is for current product or attribute of the product 
	 */ 
	@Type(type = "numeric_boolean")
	@Column(name = "main", nullable = false)
	@NotNull
	private boolean main;
	
	/**
	 * type of the media : 2 options available (image / video)
	 */
	@Column(name = "media_type", nullable = false)
	@NotNull
	@Enumerated(EnumType.STRING)
	private MediaTypeEnum mediaType;
	
	/**
	 * media path : current location for the media 
	 */
	@Column(name = "media_path")
	@Size(max = 255)
	private String mediaPath;

	 
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
	public Boolean getMain() {
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
	 * @param main the main to set
	 */
	public void setMain(boolean main) {
		this.main = main;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(label, main, mediaName, mediaPath, mediaType);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Media other = (Media) obj;
		return Objects.equals(label, other.label) && Objects.equals(main, other.main)
				&& Objects.equals(mediaName, other.mediaName) && Objects.equals(mediaPath, other.mediaPath)
				&& mediaType == other.mediaType;
		}
	
	

	
}
