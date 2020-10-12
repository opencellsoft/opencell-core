package org.meveo.apiv2.generic.security.parser;

import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.config.SecureMethodParameterConfig;
import org.meveo.api.security.parameter.SecureMethodParameterParser;
import org.meveo.apiv2.generic.services.PersistenceServiceHelper;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IEntity;

import java.util.Collections;
import java.util.List;

/**
 * Secure method parameter parser that extends {@link SecureMethodParameterParser} and which
 * is using the Generic API requested entity's Id to load the secured entity to check
 *
 * @author Mounir Boukayoua
 * @since 10.X
 */
public class IdParser extends SecureMethodParameterParser<BusinessEntity> {

    @Override
    public List<BusinessEntity> getParameterValue(SecureMethodParameterConfig parameterConfig, Object[] values) throws InvalidParameterException, MissingParameterException {
        if (parameterConfig == null) {
            return null;
        }

        // retrieve the id from the invoked method params
        Long id = (Long) values[parameterConfig.getIndex()];
        if (id == null) {
            // TODO how to handle when entity to filter can not be resolved because it is null - is it an error?
            return null;
            // throw new MissingParameterException("code parameterConfig is an empty value");
        }
        Class<? extends BusinessEntity> entityClass = parameterConfig.getEntityClass();

        // load the entity to check by it's Id.
        IEntity entity = PersistenceServiceHelper.getPersistenceService(entityClass).findById(id);
        return Collections.singletonList((BusinessEntity) entity);
    }

}
