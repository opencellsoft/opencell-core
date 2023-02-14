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

package org.meveo.api.dto.tunnel;

import io.swagger.v3.oas.annotations.media.Schema;
import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.tunnel.CustomEnum;
import org.meveo.model.tunnel.CustomStyle;
import org.meveo.model.tunnel.HypertextSection;

import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * @author mohamed CHAOUKI
 */
@XmlRootElement(name = "CustomStyle")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomStyleDto extends BusinessEntityDto {

    /**
     * serial version uid.
     */
    private static final long serialVersionUID = 475958204852585405L;

    private String logo;

    private String favIcon;

    private String font;

    private String backgroundColor;

    private String textColor;

    private Map<String, String> backgroundImage;

    private String primaryColor;

    private String secondaryColor;

    private String css;

    @XmlElementWrapper(name = "hypertextSections")
    @XmlElement(name = "hypertextSections")
    @Schema(description = "list of hypertext sections")
    private List<HypertextSectionDto> hypertextSections;

    private CustomEnum type;

    public CustomStyleDto() {
    }

    public CustomStyleDto(String logo,
                          String favIcon,
                          String font,
                          String backgroundColor,
                          String textColor,
                          Map<String, String> backgroundImage,
                          String primaryColor,
                          String secondaryColor,
                          String css,
                          List<HypertextSectionDto> hypertextSections,
                          CustomEnum type) {
        this.logo = logo;
        this.favIcon = favIcon;
        this.font = font;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.backgroundImage = backgroundImage;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.css = css;
        this.hypertextSections = hypertextSections;
        this.type = type;
    }

    public CustomStyleDto(CustomStyle customStyle) {
        code = customStyle.getCode();
        id = customStyle.getId();

        if (customStyle.getLogo() != null) {
            logo = customStyle.getLogo();
        }
        if (customStyle.getFavIcon() != null) {
            favIcon = customStyle.getFavIcon();
        }
        if (customStyle.getFont() != null) {
            font = customStyle.getFont();
        }
        if (customStyle.getBackgroundColor() != null) {
            backgroundColor = customStyle.getBackgroundColor();
        }
        if (customStyle.getTextColor() != null) {
            textColor = customStyle.getTextColor();
        }
        if (customStyle.getBackgroundImage() != null) {
            backgroundImage = customStyle.getBackgroundImage();
        }
        if (customStyle.getPrimaryColor() != null) {
            primaryColor = customStyle.getPrimaryColor();
        }
        if (customStyle.getSecondaryColor() != null) {
            secondaryColor = customStyle.getSecondaryColor();
        }
        if (customStyle.getCss() != null) {
            css = customStyle.getCss();
        }
        if (customStyle.getType() != null) {
            type = customStyle.getType();
        }
        if (customStyle.getHypertextSections() != null) {
            for (HypertextSection section: customStyle.getHypertextSections()) {
                hypertextSections.add(new HypertextSectionDto(section));
            }
        }
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getFavIcon() {
        return favIcon;
    }

    public void setFavIcon(String favIcon) {
        this.favIcon = favIcon;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public Map<String, String> getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Map<String, String> backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(String secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public CustomEnum getType() {
        return type;
    }

    public void setType(CustomEnum type) {
        this.type = type;
    }

    public List<HypertextSectionDto> getHypertextSections() {
        return hypertextSections;
    }

    public void setHypertextSections(List<HypertextSectionDto> hypertextSections) {
        this.hypertextSections = hypertextSections;
    }
}
