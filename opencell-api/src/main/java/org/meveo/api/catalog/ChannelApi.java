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

    /**
     * 
     * @param postData posted data to API containing channel's infos.
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
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
        if (postData.isDisabled() != null) {
            channel.setDisabled(postData.isDisabled());
        }

        channelService.create(channel);

        return channel;

    }

    /**
     * 
     * @param postData posted data to API containing channel's infos.
     * 
     * @throws MeveoApiException mevveo api exception
     * @throws BusinessException business exception
     */
    public Channel update(ChannelDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(postData);

        Channel channel = channelService.findByCode(postData.getCode());
        if (channel == null) {
            throw new EntityAlreadyExistsException(Channel.class, postData.getCode());
        }

        channel.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        channel.setDescription(postData.getDescription());

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

        ChannelDto ChannelDto = null;

        Channel Channel = channelService.findByCode(code);

        if (Channel == null) {
            throw new EntityDoesNotExistsException(Channel.class, code);
        }

        ChannelDto = new ChannelDto(Channel);

        return ChannelDto;

    }

    /**
     * 
     * @param code channel's code
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception
     */
    public void remove(String code) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        Channel Channel = channelService.findByCode(code);

        if (Channel == null) {
            throw new EntityDoesNotExistsException(Channel.class, code);
        }

        channelService.remove(Channel);

    }

    /**
     * 
     * @param postData posted data to API containing channel's infos
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception
     */
    public Channel createOrUpdate(ChannelDto postData) throws MeveoApiException, BusinessException {

        String code = postData.getCode();

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }
        if (channelService.findByCode(postData.getCode()) == null) {
            return create(postData);
        } else {
            return update(postData);
        }
    }

    /**
     * 
     * 
     * @return list of channels
     * @throws MeveoApiException meveo api exception
     */
    public List<ChannelDto> list() throws MeveoApiException {
        List<ChannelDto> ChannelDtos = new ArrayList<ChannelDto>();

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
