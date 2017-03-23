package org.meveo.export;

import java.lang.reflect.Modifier;

import javax.persistence.Inheritance;

import org.meveo.model.IAuditable;
import org.meveo.model.IEntity;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Xstream converter for IEntity classes. Append class name when serialising an abstract or inheritance class' implementation and pass it along to ReflectionConverter converters
 * for the rest of work.
 * 
 * @author Andrius Karpavicius
 */
public class IEntityClassConverter extends ReflectionConverter {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private boolean preserveId;
    private MeveoUser currentUser;

    public IEntityClassConverter(Mapper mapper, ReflectionProvider reflectionProvider, boolean preserveId, MeveoUser currentUser) {
        super(mapper, reflectionProvider);
        this.preserveId = preserveId;
        this.currentUser = currentUser;
    }

    @SuppressWarnings({ "rawtypes" })
    @Override
    public boolean canConvert(Class clazz) {

        boolean willConvert = IEntity.class.isAssignableFrom(clazz);
        if (willConvert) {
            log.debug("Will be using " + this.getClass().getSimpleName() + " for " + clazz);
        }
        return willConvert;
    }

    /**
     * Append class name when serialising an abstract or inheritance class' implementation
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void marshal(Object original, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        Class clazz = original.getClass();
        if (Modifier.isAbstract(clazz.getModifiers()) || clazz.isAnnotationPresent(Inheritance.class) || clazz.getSuperclass().isAnnotationPresent(Inheritance.class)) {
            writer.addAttribute("class", original.getClass().getCanonicalName());
        }
        super.marshal(original, writer, context);
    }

    /**
     * Remove entity id value if not explicitly told to do import by id, update audit if entity is IAuditable
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        IEntity entity = (IEntity) super.unmarshal(reader, context);
        if (!preserveId) {
            entity.setId(null);
        }
        if (entity instanceof IAuditable) {
            ((IAuditable) entity).updateAudit(currentUser);
        }

        return entity;
    }
}