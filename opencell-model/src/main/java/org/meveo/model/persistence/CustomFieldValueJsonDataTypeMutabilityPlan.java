package org.meveo.model.persistence;

import java.io.Serializable;

import org.hibernate.SharedSessionContract;
import org.hibernate.type.descriptor.java.MutabilityPlan;
import org.meveo.model.crm.custom.CustomFieldValues;

public class CustomFieldValueJsonDataTypeMutabilityPlan implements MutabilityPlan<CustomFieldValues> {

    private static final long serialVersionUID = 6703003211970015069L;

    public static final CustomFieldValueJsonDataTypeMutabilityPlan INSTANCE = new CustomFieldValueJsonDataTypeMutabilityPlan();

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public CustomFieldValues deepCopy(CustomFieldValues value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Serializable disassemble(CustomFieldValues value, SharedSessionContract session) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CustomFieldValues assemble(Serializable cached, SharedSessionContract session) {
        // TODO Auto-generated method stub
        return null;
    }

}
