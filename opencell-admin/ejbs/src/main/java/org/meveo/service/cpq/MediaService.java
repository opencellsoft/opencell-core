package org.meveo.service.cpq;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.model.cpq.Media;
import org.meveo.service.base.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tarik FAKHOURI.
 * @version 10.0
 * 
 * media service implementation.
 */

@Stateless
public class MediaService extends
		PersistenceService<Media> {

	private final static Logger LOG = LoggerFactory.getLogger(MediaService.class);
	
	public Media findByProductAndMediaName(String productCode, String mediaName) {
		try {
			return (Media) this.getEntityManager().createNamedQuery("Media.findByProductAndMediaName").setParameter("productCode", productCode).setParameter("mediaName", mediaName).getSingleResult();
		}catch(NoResultException e) {
			LOG.warn("no Media found for Product code : "+ productCode +" and media name : " + mediaName);
			return null;
		}
		
	}
}