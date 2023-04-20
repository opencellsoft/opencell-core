package org.meveo.apiv2.ordering.resource.mappers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.apiv2.billing.ImmutableInvoiceValidationRuleDto;
import org.meveo.apiv2.billing.impl.InvoiceValidationRuleMapper;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceValidationRule;
import org.meveo.model.scripts.ScriptInstance;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceValidationRuleMapperTest {

    private Random random = new Random();

    protected Logger log = LoggerFactory.getLogger(getClass());

    private InvoiceValidationRuleMapper invoiceValidationRuleMapper = new InvoiceValidationRuleMapper();

    @Test
    public void should_Build_InvoiceValidationRule_From_InvoiceValidationRuleDto() throws Exception {

        // Given
        ImmutableInvoiceValidationRuleDto invoiceValidationRuleDto  = instantiateRandomObject(ImmutableInvoiceValidationRuleDto.class, true);

        // When
        InvoiceValidationRule invoiceValidationRule = invoiceValidationRuleMapper.toEntity(invoiceValidationRuleDto);

        // Then
        assertDtoAndEntityAreEqual(invoiceValidationRuleDto, invoiceValidationRule);

    }

    @Test
    public void should_Update_InvoiceValidationRule_From_InvoiceValidationRuleDto() throws Exception {

        // Given
        ImmutableInvoiceValidationRuleDto invoiceValidationRuleDto  = instantiateRandomObject(ImmutableInvoiceValidationRuleDto.class, true);

        // When
        InvoiceValidationRule invoiceValidationRule = invoiceValidationRuleMapper.toEntity(invoiceValidationRuleDto, new InvoiceValidationRule(), new InvoiceType(), new ScriptInstance());

        // Then
        assertDtoAndEntityAreEqual(invoiceValidationRuleDto, invoiceValidationRule);

    }

    private static void assertDtoAndEntityAreEqual(ImmutableInvoiceValidationRuleDto invoiceValidationRuleDto, InvoiceValidationRule invoiceValidationRule) {
        assertThat(invoiceValidationRule.getType().toString()).isEqualTo(invoiceValidationRuleDto.getType());
        assertThat(invoiceValidationRule.getPriority()).isEqualTo(invoiceValidationRuleDto.getPriority());
        assertThat(invoiceValidationRule.getFailStatus()).isEqualTo(invoiceValidationRuleDto.getFailStatus());
        assertThat(invoiceValidationRule.getCode()).isEqualTo(invoiceValidationRuleDto.getCode());
        assertThat(invoiceValidationRule.getDescription()).isEqualTo(invoiceValidationRuleDto.getDescription());
    }

    public <T> T instantiateRandomObject(Class<T> clazz, boolean onlyBasicFields) throws Exception {
        T instance = null;
        if(Resource.class.isAssignableFrom(clazz)) {
            Method builderMethod = clazz.getMethod("builder");
            Object builder = builderMethod.invoke(null);
            final Class builderClass = builder.getClass();
            final Method build = builderClass.getMethod("build");

            for (Field field : clazz.getDeclaredFields()) {
                final String name = field.getName();
                if (!java.lang.reflect.Modifier.isStatic(field.getModifiers()) && !Collection.class.isAssignableFrom(field.getType())) {

                    Method accessor = builderClass.getMethod(name, field.getType());

                    Object value;
                    if (accessor.getName().equals("type")) {
                        value = "SCRIPT";
                    } else {
                        value = getRandomValueForField(field, onlyBasicFields);
                    }
                    accessor.invoke(builder, value);
                }
            }
            instance =  (T)build.invoke(builder);
        } else {
            final Constructor<T> constructor = clazz.getConstructor();
            if(constructor!=null) {
                Object[] cargs = new Object[constructor.getParameterCount()];
                instance = constructor.newInstance(cargs);
                for (Field field : clazz.getDeclaredFields()) {
                    if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                        field.setAccessible(true);
                        Object value = getRandomValueForField(field, onlyBasicFields);
                        field.set(instance, value);
                    }
                }
            } else {
                log.warn("WARNING: NO CONSTRUCTOR FOR "+clazz);
            }
        }
        return instance;
    }

    private Object getRandomValueForField(Field field, boolean onlyBasicFields) throws Exception {
        Class<?> type = field.getType();
        if (type.isEnum()) {
            Object[] enumValues = type.getEnumConstants();
            return enumValues[random.nextInt(enumValues.length)];
        } else if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
            return random.nextInt();
        } else if (type.equals(Long.TYPE) || type.equals(Long.class)) {
            return random.nextLong();
        } else if (type.equals(Double.TYPE) || type.equals(Double.class)) {
            return random.nextDouble();
        } else if (type.equals(Float.TYPE) || type.equals(Float.class)) {
            return random.nextFloat();
        } else if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
            return random.nextBoolean();
        } else if (type.equals(String.class)) {
            return UUID.randomUUID().toString();
        } else if (type.equals(BigInteger.class)) {
            return BigInteger.valueOf(random.nextInt());
        } else if (type.equals(BigDecimal.class)) {
            return BigDecimal.valueOf(random.nextInt());
        } else if (type.equals(Date.class)) {
            return new Date();
        } else if(!onlyBasicFields) {
            return instantiateRandomObject(type, onlyBasicFields);
        }
        return null;
    }

}
