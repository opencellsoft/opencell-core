package org.meveo.api;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.meveo.common.DefaultTypeAdapter;

/**
 * This class will be included in the upcoming OC versions , hence should be removed from here , later
 */
@XmlJavaTypeAdapter(DefaultTypeAdapter.class)
public interface ApiErrorCodeEnum {
}
