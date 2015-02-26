package org.meveo.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;

import org.meveo.util.MeveoJpa;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class EmService {

	@Inject
	@MeveoJpa
	private EntityManager em;

	public List<String> getEntities() {
		List<String> result = new ArrayList<String>();
		final Metamodel mm = em.getMetamodel();
		for (final ManagedType<?> managedType : mm.getManagedTypes()) {
			result.add(managedType.getJavaType().getName());
		}

		return result;
	}

}
