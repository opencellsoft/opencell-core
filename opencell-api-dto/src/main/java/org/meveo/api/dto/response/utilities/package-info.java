
@XmlJavaTypeAdapter(value = DateTimeAdapter.class, type = Date.class)
package org.meveo.api.dto.response.utilities;

import java.util.Date;

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.meveo.api.jaxb.DateTimeAdapter;
