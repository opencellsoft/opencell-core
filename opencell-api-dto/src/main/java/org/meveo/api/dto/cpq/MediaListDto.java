package org.meveo.api.dto.cpq;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Tarik FAKHOURI
 * @version 10.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class MediaListDto {


    /** The list size. */
    private int listSize;
	
    /** list of media **/
    private List<MediaDto> medias = new ArrayList<>();

	/**
	 * @return the listSize
	 */
	public int getListSize() {
		return medias.size();
	}

	/**
	 * @param listSize the listSize to set
	 */
	public void setListSize(int listSize) {
		this.listSize = listSize;
	}

	/**
	 * @return the medias
	 */
	public List<MediaDto> getMedias() {
		return medias;
	}

	/**
	 * @param medias the medias to set
	 */
	public void setMedias(List<MediaDto> medias) {
		this.medias = medias;
	}

	
	
	
}
