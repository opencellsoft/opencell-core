package org.meveo.api.dto.document.sign;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A DTO class for a Request of a Signature procedure Member. 
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public class SignMemberRequestDto extends SignMemberDto { 
    
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    public SignMemberRequestDto () {
    }
    
    public SignMemberRequestDto (String user) {
        super(user);
    }
    
    /** The file objects. */
    private List<SignFileObjectRequestDto> fileObjects;

    /**
     * @return the fileObjects
     */
    public List<SignFileObjectRequestDto> getFileObjects() {
        return fileObjects;
    }

    /**
     * @param fileObjects the fileObjects to set
     */
    public void setFileObjects(List<SignFileObjectRequestDto> fileObjects) {
        this.fileObjects = fileObjects;
    }
    
   
}
