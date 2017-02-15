package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.ChannelDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.Channel;
import org.meveo.service.catalog.impl.ChannelService;

@Stateless
public class ChannelApi extends BaseApi {

    @Inject
    private ChannelService channelService;

    /**
     * 
     * @param postData

     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void create(ChannelDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();
        

        

        if (channelService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(Channel.class, postData.getCode());
        } else {

            Channel channel = new Channel();
            channel.setCode(postData.getCode());
            channel.setDescription(postData.getDescription());
            channelService.create(channel);

        }
    }

    /**
     * 
     * @param postData

     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void update(ChannelDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();
        

        

        Channel channel = channelService.findByCode(postData.getCode());

        if (channel == null) {
            throw new EntityAlreadyExistsException(Channel.class, postData.getCode());
        } else {

            channel.setDescription(postData.getDescription());
            channelService.update(channel);

        }
    }

    /**
     * 
     * @param code
     * @param provider
     * @return
     * @throws MeveoApiException
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
     * @param code
     * @param provider
     * @throws MeveoApiException
     * @throws BusinessException 
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
     * @param postData

     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void createOrUpdate(ChannelDto postData) throws MeveoApiException, BusinessException {

        String code = postData.getCode();

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        if (channelService.findByCode(code) == null) {
            create(postData);
        } else {
            update(postData);
        }
    }

    /**
     * 
     * 
     * @return
     * @throws MeveoApiException
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
     * @param ChannelId

     * @return
     * @throws MeveoApiException
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
