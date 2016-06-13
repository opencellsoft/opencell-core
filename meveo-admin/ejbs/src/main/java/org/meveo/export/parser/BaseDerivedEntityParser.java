package org.meveo.export.parser;

import java.lang.reflect.Field;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.service.base.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tonys on 13/06/2016.
 */
public abstract class BaseDerivedEntityParser {

    protected Logger log = LoggerFactory.getLogger(this.getClass());
    private Class<? extends IEntity> parentClass;
    private String fieldName;

    public BaseDerivedEntityParser() {
        if (this.getClass().isAnnotationPresent(DerivedEntityParser.class)) {
            DerivedEntityParser annotation = this.getClass().getAnnotation(DerivedEntityParser.class);
            this.parentClass = annotation.parentEntity();
            this.fieldName = annotation.fieldName();
        }
    }

    public boolean matches(Class<?> parentEntity, String fieldName) {
        return this.parentClass != null
            && this.parentClass.equals(parentEntity)
            && this.fieldName != null
            && this.fieldName.equals(fieldName);
    }

    public abstract void deriveEntities(IEntity entity, Field field, User currentUser);

}
