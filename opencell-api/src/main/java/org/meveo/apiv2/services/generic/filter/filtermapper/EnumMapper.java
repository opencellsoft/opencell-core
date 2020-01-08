package org.meveo.apiv2.services.generic.filter.filtermapper;

        import org.meveo.apiv2.services.generic.filter.FilterMapper;

        import java.lang.reflect.Field;
        import java.util.List;
        import java.util.stream.Collectors;
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
