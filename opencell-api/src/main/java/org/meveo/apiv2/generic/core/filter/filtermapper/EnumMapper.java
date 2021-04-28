package org.meveo.apiv2.generic.core.filter.filtermapper;

import org.meveo.apiv2.generic.core.filter.FilterMapper;

import java.util.stream.Stream;

public class EnumMapper extends FilterMapper {
    private final Class clazz;
    public EnumMapper(String property, Object value, Class clazz) {
        super(property, value);
        this.clazz = clazz;
    }
    @Override
    public Object mapStrategy(Object valueEnum) {
        return Stream.of(clazz.getEnumConstants())
                .filter(enumConstant -> ((Enum) enumConstant).name().equalsIgnoreCase((String) valueEnum))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid argument :" + property));
    }
}
