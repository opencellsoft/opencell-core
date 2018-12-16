package org.meveo.jpa.event;

import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.jpa.event.internal.core.JpaPostDeleteEventListener;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.service.index.ElasticClient;

/**
 * JPA delete event listener. Flushes pending changes to Elastic Search.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.3
 */
public class DeleteEventListener extends JpaPostDeleteEventListener {

	private static final long serialVersionUID = 4290464068190662604L;

	@Override
	public void onPostDelete(PostDeleteEvent event) {
		super.onPostDelete(event);

		ElasticClient elasticClient = (ElasticClient) EjbUtils.getServiceInterface("ElasticClient");
		elasticClient.flushChanges();
	}

}