package org.meveo.api.dto.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class AccessesDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AccessesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1614784156576503978L;

    /** The access. */
    private List<AccessDto> access = new ArrayList<>();

    /**
     * Gets the access.
     *
     * @return the access
     */
    public List<AccessDto> getAccess() {
        return access;
    }

    /**
     * Sets the access.
     *
     * @param access the new access
     */
    public void setAccess(List<AccessDto> access) {
        this.access = access;
    }

    @Override
    public String toString() {
        return "AccessesDto [access=" + access + "]";
    }
}