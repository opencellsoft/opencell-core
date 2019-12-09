package org.meveo.api;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.meveo.common.DefaultTypeAdapter;

@XmlJavaTypeAdapter(DefaultTypeAdapter.class)
public interface ApiErrorCodeEnum {
}
