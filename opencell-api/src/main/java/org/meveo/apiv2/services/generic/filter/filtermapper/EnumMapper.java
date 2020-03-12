/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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
