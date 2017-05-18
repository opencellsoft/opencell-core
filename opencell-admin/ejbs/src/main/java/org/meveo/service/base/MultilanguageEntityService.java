package org.meveo.service.base;

import org.meveo.model.BusinessEntity;

/**
 * Class is used to identify that a persistence service deals with multilanguage entities. See CatMessagesService. An alternative would be to use annotations
 * 
 * @author Andrius Karpavicius
 */
public abstract class MultilanguageEntityService<T extends BusinessEntity> extends BusinessService<T> {
}