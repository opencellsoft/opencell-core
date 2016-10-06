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
import org.meveo.model.admin.User;
import org.meveo.model.catalog.Channel;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.ChannelService;

@Stateless
public class ChannelApi extends BaseApi {

    @Inject
    private ChannelService channelService;

    /**
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void create(ChannelDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();
        

        Provider provider = currentUser.getProvider();

        if (channelService.findByCode(postData.getCode(), provider) != null) {
            throw new EntityAlreadyExistsException(Channel.class, postData.getCode());
        } else {

            Channel channel = new Channel();
            channel.setCode(postData.getCode());
            channel.setDescription(postData.getDescription());
            channelService.create(channel, currentUser);

        }
    }

    /**
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void update(ChannelDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();
        

        Provider provider = currentUser.getProvider();

        Channel channel = channelService.findByCode(postData.getCode(), provider);

        if (channel == null) {
            throw new EntityAlreadyExistsException(Channel.class, postData.getCode());
        } else {

            channel.setDescription(postData.getDescription());
            channelService.update(channel, currentUser);

        }
    }

    /**
     * 
     * @param code
     * @param provider
     * @return
     * @throws MeveoApiException
     */
    public ChannelDto find(String code, Provider provider) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        ChannelDto ChannelDto = null;

        Channel Channel = channelService.findByCode(code, provider);

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
    public void remove(String code, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        Channel Channel = channelService.findByCode(code, currentUser.getProvider());

        if (Channel == null) {
            throw new EntityDoesNotExistsException(Channel.class, code);
        }

        channelService.remove(Channel, currentUser);

    }

    /**
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void createOrUpdate(ChannelDto postData, User currentUser) throws MeveoApiException, BusinessException {

        String code = postData.getCode();

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        if (channelService.findByCode(code, currentUser.getProvider()) == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
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
     * @param currentUser
     * @return
     * @throws MeveoApiException
     */
    public ChannelDto findById(String ChannelId, User currentUser) throws MeveoApiException {
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
