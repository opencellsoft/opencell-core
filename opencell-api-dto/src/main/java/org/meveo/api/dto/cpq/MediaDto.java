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
	
	private String offerCode;
	
	private String productCode;
	
	private String serviceTemplateCode;
	
	private String attributeCode;
	@NotNull
	private String mediaName;
	@NotNull
	private String label;
	@NotNull
	private boolean main;
	@NotNull
	private MediaTypeEnum mediaType;
	private String mediaPath;
	
	public MediaDto() {
		
	}
	
	public MediaDto(Media media) {
		this.id=media.getId();
		this.productCode = media.getProduct()!=null? media.getProduct().getCode():null;
		this.serviceTemplateCode = media.getServiceTemplate()!=null?media.getServiceTemplate().getCode():null;
		this.offerCode=media.getOffer()!=null?media.getOffer().getCode():null;
		this.mediaName = media.getMediaName();
		this.label = media.getLabel();
		this.main = media.getMain();
		this.mediaPath = media.getMediaPath();
	}
	
	/**
	 * @return the productCode
	 */
	public String getProductCode() {
		return productCode;
	}
	/**
	 * @param productCode the productCode to set
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	/**
	 * @return the serviceTemplateCode
	 */
	public String getServiceTemplateCode() {
		return serviceTemplateCode;
	}
	/**
	 * @param serviceTemplateCode the serviceTemplateCode to set
	 */
	public void setServiceTemplateCode(String serviceTemplateCode) {
		this.serviceTemplateCode = serviceTemplateCode;
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
	public boolean isMain() {
		return main;
	}
	/**
	 * @param main the main to set
	 */
	public void setMain(boolean main) {
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
	 * @return the offerCode
	 */
	public String getOfferCode() {
		return offerCode;
	}

	/**
	 * @param offerCode the offerCode to set
	 */
	public void setOfferCode(String offerCode) {
		this.offerCode = offerCode;
	}

	/**
	 * @return the attributeCode
	 */
	public String getAttributeCode() {
		return attributeCode;
	}

	/**
	 * @param attributeCode the attributeCode to set
	 */
	public void setAttributeCode(String attributeCode) {
		this.attributeCode = attributeCode;
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
