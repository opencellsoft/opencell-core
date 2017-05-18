package org.meveo.audit.logging;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.audit.logging.dto.AuditEvent;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class MetadataHandler {

	@Inject
	private MetaDataProvider metaDataProvider;

	public AuditEvent addSignature(AuditEvent event) {
		event.setActor(metaDataProvider.getActor());
		event.setClientIp(metaDataProvider.getOrigin());

		return event;
	}

}
