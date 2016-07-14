package org.meveo.admin.action.catalog;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.catalog.Channel;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.ChannelService;
import org.omnifaces.cdi.ViewScoped;

/**
 * @author Edward P. Legaspi
 */
@Named
@ViewScoped
public class ChannelBean extends BaseBean<Channel> {

	private static final long serialVersionUID = -7840171299348011926L;

	@Inject
	protected ChannelService channelService;

	public ChannelBean() {
		super(Channel.class);
	}

	@Override
	protected IPersistenceService<Channel> getPersistenceService() {
		return channelService;
	}

}
