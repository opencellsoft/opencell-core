package org.meveo.service.billing.impl;

import javax.ejb.Stateless;
import javax.inject.Named;

import org.meveo.model.billing.ExchangeRate;
import org.meveo.service.base.PersistenceService;

@Stateless
@Named
public class ExchangeRateService extends PersistenceService<ExchangeRate> {

}
