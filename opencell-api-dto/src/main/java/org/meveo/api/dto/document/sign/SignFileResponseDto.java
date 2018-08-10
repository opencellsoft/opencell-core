package org.meveo.api.dto.document.sign;

import org.meveo.api.dto.response.BaseResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO Class for a File informations : response from Yousign document.
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public class SignFileResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L; 
    
    /**
     * Instantiates a new sign file response dto.
     */
    public SignFileResponseDto () {
    }
    
    /**
     * Instantiates a new sign file response dto.
     *
     * @param id the id
     * @param content the content
     */
    public SignFileResponseDto (String id, byte[] content) {
        this.id = id;
        this.content = content;
    }
    
    /** The id. */
    private String id;
    
    /** The name. */
    private String name;
    
    /** The description. */
    private String description;
    
    /** The content. */
    private byte[] content;
    
    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }
    
    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the id.
     *
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Sets the name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Sets the description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the content.
     *
     * @return the content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Sets the content.
     *
     * @param content the content to set
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

}
