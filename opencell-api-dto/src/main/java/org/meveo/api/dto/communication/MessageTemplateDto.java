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

package org.meveo.api.dto.communication;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.communication.MediaEnum;
import org.meveo.model.communication.MessageTemplate;
import org.meveo.model.communication.MessageTemplateTypeEnum;

/**
 * The Class MessageTemplateDto.
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 * @since Jul 10, 2016 9:18:59 PM
 */
@XmlRootElement(name = "MessageTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class MessageTemplateDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2370984261457651138L;

    /** The media. */
    private MediaEnum media = MediaEnum.EMAIL;
    
    /** The tag start delimiter. */
    private String tagStartDelimiter = "#{";
    
    /** The tag end delimiter. */
    private String tagEndDelimiter = "}";
    
    /** The start date. */
    private Date startDate;
    
    /** The end date. */
    private Date endDate;
    
    /** The type. */
    private MessageTemplateTypeEnum type;

    private String textContent;

    private List<TranslatedTextContentDto> translatedTextContent;

    /**
     * Instantiates a new message template dto.
     */
    public MessageTemplateDto() {
    }

    /**
     * Instantiates a new message template dto.
     *
     * @param messageTemplate the message template
     */
    public MessageTemplateDto(MessageTemplate messageTemplate) {
        super(messageTemplate);

        this.media = messageTemplate.getMedia();
        this.tagStartDelimiter = messageTemplate.getTagStartDelimiter();
        this.tagEndDelimiter = messageTemplate.getTagEndDelimiter();
        this.startDate = messageTemplate.getStartDate();
        this.endDate = messageTemplate.getEndDate();
        this.type = messageTemplate.getType();
    }

    /**
     * Gets the media.
     *
     * @return the media
     */
    public MediaEnum getMedia() {
        return media;
    }

    /**
     * Sets the media.
     *
     * @param media the new media
     */
    public void setMedia(MediaEnum media) {
        this.media = media;
    }

    /**
     * Gets the tag start delimiter.
     *
     * @return the tag start delimiter
     */
    public String getTagStartDelimiter() {
        return tagStartDelimiter;
    }

    /**
     * Sets the tag start delimiter.
     *
     * @param tagStartDelimiter the new tag start delimiter
     */
    public void setTagStartDelimiter(String tagStartDelimiter) {
        this.tagStartDelimiter = tagStartDelimiter;
    }

    /**
     * Gets the tag end delimiter.
     *
     * @return the tag end delimiter
     */
    public String getTagEndDelimiter() {
        return tagEndDelimiter;
    }

    /**
     * Sets the tag end delimiter.
     *
     * @param tagEndDelimiter the new tag end delimiter
     */
    public void setTagEndDelimiter(String tagEndDelimiter) {
        this.tagEndDelimiter = tagEndDelimiter;
    }

    /**
     * Gets the start date.
     *
     * @return the start date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date.
     *
     * @param startDate the new start date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date.
     *
     * @return the end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date.
     *
     * @param endDate the new end date
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public MessageTemplateTypeEnum getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(MessageTemplateTypeEnum type) {
        this.type = type;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public List<TranslatedTextContentDto> getTranslatedTextContent() {
        return translatedTextContent;
    }

    public void setTranslatedTextContent(List<TranslatedTextContentDto> translatedTextContent) {
        this.translatedTextContent = translatedTextContent;
    }
}