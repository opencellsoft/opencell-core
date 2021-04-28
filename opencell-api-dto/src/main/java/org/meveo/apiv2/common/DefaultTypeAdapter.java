package org.meveo.apiv2.common;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class DefaultTypeAdapter extends XmlAdapter<Object, Object> {
    public DefaultTypeAdapter() {
    }

    public Object unmarshal(Object v) {
        return v;
    }

    public Object marshal(Object v) {
        return v;
    }
}
