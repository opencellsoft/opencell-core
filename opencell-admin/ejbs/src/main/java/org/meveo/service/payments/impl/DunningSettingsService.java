package org.meveo.service.payments.impl;
import javax.ejb.Stateless;

import org.meveo.model.dunning.DunningSettings;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.PersistenceService;

/**
 * Service implementation to manage DunningCollectionManagement entity.
 * It extends {@link PersistenceService} class
 * 
 * @author Mbarek-Ay
 * @version 11.0
 *
 */
@Stateless
public class DunningSettingsService extends BusinessService<DunningSettings> {

}
