package org.meveo.api;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.meveo.common.DefaultTypeAdapter;

@XmlType
@XmlJavaTypeAdapter(DefaultTypeAdapter.class)
public interface ApiErrorCodeEnum {
}
