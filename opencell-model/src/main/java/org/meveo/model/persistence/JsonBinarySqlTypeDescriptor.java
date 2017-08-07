package org.meveo.model.persistence;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonBinarySqlTypeDescriptor extends AbstractJsonSqlTypeDescriptor {

    private static final long serialVersionUID = 1501788205699761204L;

    public static final JsonBinarySqlTypeDescriptor INSTANCE = new JsonBinarySqlTypeDescriptor();

    @Override
    public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
        return new BasicBinder<X>(javaTypeDescriptor, this) {
            @Override
            protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {  
                Logger log = LoggerFactory.getLogger(getClass());
                log.error("AKK statement class is {} {}", st.getClass(), st.getConnection().getClass());
                st.setObject(index, javaTypeDescriptor.unwrap(value, JsonNode.class, options), getSqlType());
            }

            @Override
            protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                Logger log = LoggerFactory.getLogger(getClass());
                log.error("AKK statement class is {} {}", st.getClass(), st.getConnection().getClass());
                
                st.setObject(name, javaTypeDescriptor.unwrap(value, JsonNode.class, options), getSqlType());
            }
        };
    }
}