/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.dto.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.UserAccount;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class UserAccountIdCodeDto.
 *
 * @author trabeh
 */
@XmlRootElement(name = "UserAccountIdCodeDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAccountIdCodeDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1332916104721562009L;

    /** The UserAccount id. */
    @Schema(description = "User account id")
    private Long id;

    /** The UserAccount code. */
    @Schema(description = "User account code")
    private String code;

    /**
     * Instantiates a new UserAccountIdCodeDto dto.
     */
    public UserAccountIdCodeDto() {

    }

    public UserAccountIdCodeDto(UserAccount ua) {
        id = ua.getId();
        code = ua.getCode();
    }

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
        this.code = code;;
    }
    
    @Override
    public String toString() {
        return "UserAccountIdCodeDto [id=" + getId() + ", code=" + getCode() + "]";
    }
}