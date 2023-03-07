package org.meveo.api.dto.account;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CustomerToTransfertOperationDto implements Serializable {

    
    private String code;
    
    private Long id;


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getCode() {
        return code;
    }


    public void setCode(String code) {
        this.code = code;
    }
}
