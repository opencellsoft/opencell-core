package org.meveo.api.dto.response.finance;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

/**
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.0
 **/
public class RunReportExtractDto {

    @NotNull
    private String code;
    private Map<String, String> params = new HashMap<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
