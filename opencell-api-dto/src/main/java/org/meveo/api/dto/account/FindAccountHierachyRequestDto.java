package org.meveo.api.dto.account;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class FindAccountHierachyRequestDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class FindAccountHierachyRequestDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 9110625442489443755L;

    /**
     * Possible values. CUST = 1; CA = 2; BA = 4; UA = 8;
     **/
    private int level;
    
    /** The valid level values. */
    public static List<Integer> VALID_LEVEL_VALUES = Arrays.asList(1, 2, 4, 8);
    
    /** The name. */
    private NameDto name;
    
    /** The address. */
    private AddressDto address;

    /**
     * Gets the level.
     *
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the level.
     *
     * @param level the new level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public NameDto getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(NameDto name) {
        this.name = name;
    }

    /**
     * Gets the address.
     *
     * @return the address
     */
    public AddressDto getAddress() {
        return address;
    }

    /**
     * Sets the address.
     *
     * @param address the new address
     */
    public void setAddress(AddressDto address) {
        this.address = address;
    }

}