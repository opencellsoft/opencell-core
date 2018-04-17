package org.meveo.api.dto.response.finance;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

/**
 * The Class RunReportExtractDto.
 *
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.0
 */
public class RunReportExtractDto {

    /** The code. */
    @NotNull
    private String code;
    
    /** The params. */
    private Map<String, String> params = new HashMap<>();

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the params.
     *
     * @return the params
     */
    public Map<String, String> getParams() {
        return params;
    }

    /**
     * Sets the params.
     *
     * @param params the params
     */
    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
