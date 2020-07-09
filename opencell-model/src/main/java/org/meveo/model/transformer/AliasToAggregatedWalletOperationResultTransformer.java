package org.meveo.model.transformer;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyChainedImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyFieldImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyMapImpl;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.property.access.spi.SetterMethodImpl;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;
import org.meveo.model.BaseEntity;
import org.meveo.model.crm.CustomFieldTemplate;

/**
 * Result transformer that allows to transform a result to
 * a user specified class which will be populated via setter
 * methods or fields matching the alias names.
 * <p/>
 * <pre>
 * List resultWithAliasedBean = s.createCriteria(Enrolment.class)
 * 			.createAlias("student", "st")
 * 			.createAlias("course", "co")
 * 			.setProjection( Projections.projectionList()
 * 					.add( Projections.property("co.description"), "courseDescription" )
 * 			)
 * 			.setResultTransformer( new AliasToBeanResultTransformer(StudentDTO.class) )
 * 			.list();
 * <p/>
 *  StudentDTO dto = (StudentDTO)resultWithAliasedBean.get(0);
 * 	</pre>
 *
 * @author max
 */
public class AliasToAggregatedWalletOperationResultTransformer extends AliasedTupleSubsetResultTransformer {

    // IMPL NOTE : due to the delayed population of setters (setters cached
    // 		for performance), we really cannot properly define equality for
    // 		this transformer

    private final Class resultClass;
    private boolean isInitialized;
    private String[] aliases;
    private Setter[] setters;


    public AliasToAggregatedWalletOperationResultTransformer(Class resultClass) {
        if (resultClass == null) {
            throw new IllegalArgumentException("resultClass cannot be null");
        }
        isInitialized = false;
        this.resultClass = resultClass;
    }

    @Override
    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return false;
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Object result;

        try {
            if (!isInitialized) {
                initialize(aliases);
            } else {
                check(aliases);
            }

            result = resultClass.newInstance();
            Setter setterCustomField = null;
            Map<String, Object> cfValues = new HashMap<>();
            for (int i = 0; i < aliases.length; i++) {
                SetterMethodImpl setter = (SetterMethodImpl) setters[i];
                if (setter != null) {
                    if (setter.getMethod().getParameterTypes()[0].isPrimitive()) {
                        setter.set(result, tuple[i], null);
                    } else {
                        if (setter.getMethodName().equals("setCfValues")) {
                            cfValues.put(aliases[i], tuple[i]);
                            setterCustomField = setter;
                        } else {
                            Object setterValue = getSetterValueAsObject(aliases[i], tuple[i]);
                            setter.set(result, setterValue, null);
                        }
                    }
                }
            }
            populateCustomField(result, setterCustomField, cfValues);
        } catch (InstantiationException e) {
            throw new RuntimeException("Could not instantiate resultclass: " + resultClass.getName(), e);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException("Could not instantiate resultclass: " + resultClass.getName(), e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not instantiate resultclass: " + resultClass.getName(), e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Could not instantiate resultclass: " + resultClass.getName(), e);
        }

        return result;
    }

    private void populateCustomField(Object result, Setter setterCustomField, Map<String, Object> cfValues) {
        setterCustomField.set(result, cfValues, null);
    }

    private Object getSetterValueAsObject(String field, Object value)
            throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Class classField = resultClass.getDeclaredField(field).getType();
        if (BaseEntity.class.isAssignableFrom(classField)) {
            BaseEntity valueObject = (BaseEntity) classField.getDeclaredConstructor(null).newInstance();
            valueObject.setId((Long) value);
            return valueObject;
        } else {
            if (value instanceof Double && classField.isAssignableFrom(BigDecimal.class)) {
                return new BigDecimal(value.toString());
            }
            return value;
        }
    }

    private void initialize(String[] aliases) {
        PropertyAccessStrategyChainedImpl propertyAccessStrategy = new PropertyAccessStrategyChainedImpl(PropertyAccessStrategyBasicImpl.INSTANCE,
                PropertyAccessStrategyFieldImpl.INSTANCE, PropertyAccessStrategyMapImpl.INSTANCE);
        this.aliases = new String[aliases.length];
        setters = new Setter[aliases.length];
        for (int i = 0; i < aliases.length; i++) {
            String alias = aliases[i];
            if (alias != null) {
                this.aliases[i] = alias;
                try {
                    setters[i] = propertyAccessStrategy.buildPropertyAccess(resultClass, alias).getSetter();
                } catch (Exception ignore) {
                    setters[i] = propertyAccessStrategy.buildPropertyAccess(resultClass, "cfValues").getSetter();
                }
            }
        }
        isInitialized = true;
    }

    private void check(String[] aliases) {
        if (!Arrays.equals(aliases, this.aliases)) {
            throw new IllegalStateException("aliases are different from what is cached; aliases=" + Arrays.asList(aliases) + " cached=" + Arrays.asList(this.aliases));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AliasToAggregatedWalletOperationResultTransformer that = (AliasToAggregatedWalletOperationResultTransformer) o;

        if (!resultClass.equals(that.resultClass)) {
            return false;
        }
        if (!Arrays.equals(aliases, that.aliases)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = resultClass.hashCode();
        result = 34 * result + (aliases != null ? Arrays.hashCode(aliases) : 0);
        return result;
    }
}
