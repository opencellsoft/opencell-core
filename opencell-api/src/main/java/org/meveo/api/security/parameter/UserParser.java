package org.meveo.api.security.parameter;

import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.admin.User;

/**
 * This parser is used to retrieve the {@link User} object from the method parameters.
 * 
 * @author Tony Alejandro
 *
 */
public class UserParser extends SecureMethodParameterParser<User> {

    @Override
    public User getParameterValue(SecureMethodParameter parameter, Object[] values) throws InvalidParameterException, MissingParameterException {
        if (parameter == null) {
            return null;
        }

        Object parameterValue = values[parameter.index()];
        if (!(parameterValue instanceof User)) {
            throw new InvalidParameterException("Parameter received at index: " + parameter.index() + " is not a User instance.");
        }

        return (User) parameterValue;
    }
}
