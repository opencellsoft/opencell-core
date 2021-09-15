package org.meveo.model.persistence;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.StringTypeDescriptor;
import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
import org.hibernate.type.descriptor.sql.LongVarcharTypeDescriptor;

/**
 * Large text type field mapping that adapts both for Oracle's Clob and Postgresql's Text type field based on a system parameter.<br/>
 * A value of -Dopencell.json.db.type=clob will implement Oracle's Clob and a missing or any other value will assume Postgresql Text implementation.
 * 
 * @author Andrius Karpavicius
 */
public class TextType extends AbstractSingleColumnStandardBasicType<String> {

    private static final long serialVersionUID = 7111697837104953467L;

    public static final TextType INSTANCE = new TextType();

    public TextType() {
        super(JsonType.IS_CLOB ? ClobTypeDescriptor.DEFAULT : LongVarcharTypeDescriptor.INSTANCE, StringTypeDescriptor.INSTANCE);
    }

    public String getName() {
        return "longText";
    }
}