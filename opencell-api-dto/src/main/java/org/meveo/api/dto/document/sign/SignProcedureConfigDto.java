package org.meveo.api.dto.document.sign;

import java.util.List;
import java.util.Map;

import org.meveo.api.dto.BaseEntityDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *  DTO used basically to configure email notification, based on some events , kind of procedure.started ... 
 *  
 *  @author Said Ramli
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public class SignProcedureConfigDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L; 
    
    /** The email. */
    private Map<String, List<SignEventEmailDto>> email;

    /**
     * Gets the email.
     *
     * @return the email
     */
    public Map<String, List<SignEventEmailDto>> getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param email the email to set
     */
    public void setEmail(Map<String, List<SignEventEmailDto>> email) {
        this.email = email;
    }

}
