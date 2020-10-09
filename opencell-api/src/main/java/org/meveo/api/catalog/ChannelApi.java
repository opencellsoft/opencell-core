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

package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.catalog.ChannelDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.Channel;
import org.meveo.service.catalog.impl.ChannelService;

@Stateless
public class ChannelApi extends BaseCrudApi<Channel, ChannelDto> {

    @Inject
    private ChannelService channelService;

    @Override
    public Channel create(ChannelDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(postData);

        if (channelService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(Channel.class, postData.getCode());
        }

        Channel channel = new Channel();
        channel.setCode(postData.getCode());
        channel.setDescription(postData.getDescription());
        if(postData.getLanguageDescriptions() != null) {
            channel.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));
        }
        if (postData.isDisabled() != null) {
            channel.setDisabled(postData.isDisabled());
        }

        channelService.create(channel);

        return channel;

    }

    @Override
    public Channel update(ChannelDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(postData);

        Channel channel = channelService.findByCode(postData.getCode());
        if (channel == null) {
            throw new EntityDoesNotExistsException(Channel.class, postData.getCode());
        }

        channel.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        channel.setDescription(postData.getDescription());
        if(postData.getLanguageDescriptions() != null) {
            channel.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));
        }

        channel = channelService.update(channel);
        return channel;
    }

    /**
     * 
     * @param code channel's code
     * @return found channel
     * @throws MeveoApiException meveo api exception.
     */
    public ChannelDto find(String code) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        ChannelDto ChannelDto;

        Channel Channel = channelService.findByCode(code);

        if (Channel == null) {
            throw new EntityDoesNotExistsException(Channel.class, code);
        }

        ChannelDto = new ChannelDto(Channel);

        return ChannelDto;

    }

    /**
     * 
     * 
     * @return list of channels
     * @throws MeveoApiException meveo api exception
     */
    public List<ChannelDto> list() throws MeveoApiException {
        List<ChannelDto> ChannelDtos = new ArrayList<>();

        List<Channel> channels = channelService.list();
        if (channels != null && !channels.isEmpty()) {
            for (Channel Channel : channels) {
                ChannelDto ChannelDto = new ChannelDto(Channel);
                ChannelDtos.add(ChannelDto);
            }
        }

        return ChannelDtos;
    }

    /**
     * Allows to get All or only the active channel list
     * 
     * @return list of channels
     * @throws MeveoApiException meveo api exception
     */
    public List<ChannelDto> list(Boolean active) throws MeveoApiException {
        List<ChannelDto> ChannelDtos = new ArrayList<>();

        List<Channel> channels = channelService.list(active);
        if (channels != null && !channels.isEmpty()) {
            for (Channel channel : channels) {
                ChannelDto ChannelDto = new ChannelDto();
                ChannelDto.setCode(channel.getCode());
                ChannelDto.setDescription(channel.getDescriptionAndCode());
                ChannelDtos.add(ChannelDto);
            }
        }

        return ChannelDtos;
    }

    /**
     * 
     * @param ChannelId channel's id
     * 
     * @return channel for given id
     * @throws MeveoApiException meveo api exception.
     */
    public ChannelDto findById(String ChannelId) throws MeveoApiException {
        ChannelDto ChannelDto = null;

        if (!StringUtils.isBlank(ChannelId)) {
            try {
                long id = Integer.parseInt(ChannelId);
                Channel Channel = channelService.findById(id);
                if (Channel == null) {
                    throw new EntityDoesNotExistsException(Channel.class, id);
                }
                ChannelDto = new ChannelDto(Channel);

            } catch (NumberFormatException nfe) {
                throw new MeveoApiException("Passed ChannelId is invalid.");
            }

        }

        return ChannelDto;
    }

}
