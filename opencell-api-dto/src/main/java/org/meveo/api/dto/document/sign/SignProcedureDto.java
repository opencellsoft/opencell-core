package org.meveo.api.dto.document.sign;

import java.util.List;

import org.meveo.api.dto.BaseEntityDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 
 * A Dto holding inputs required to create a document signature procedure on Yousign platform side
 * 
 * @author Said Ramli 
 */ 
@JsonIgnoreProperties (ignoreUnknown = true)
public class SignProcedureDto  extends BaseEntityDto { 
    
    /** The Constant serialVersionUID. */ 
    private static final long serialVersionUID = 1L; 
    
    /** The id. */
    private String id;
    
    /** The name. */
    private String name;
    
    /** The description. */
    private String description;
    
    /** The start. */
    private boolean start;
    
    /** The members. */
    private List<SignMemberRequestDto> members;
    
    /** The config. */
    private SignProcedureConfigDto config;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the start
     */
    public boolean isStart() {
        return start;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param start the start to set
     */
    public void setStart(boolean start) {
        this.start = start;
    }

    /**
     * @return the members
     */
    public List<SignMemberRequestDto> getMembers() {
        return members;
    }

    /**
     * @param members the members to set
     */
    public void setMembers(List<SignMemberRequestDto> members) {
        this.members = members;
    }

    /**
     * @return the config
     */
    public SignProcedureConfigDto getConfig() {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(SignProcedureConfigDto config) {
        this.config = config;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

}
