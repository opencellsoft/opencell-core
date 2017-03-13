package org.meveo.service.script.product;

import java.io.Serializable;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 **/
@Singleton
@Startup
public class ProductModelScriptService implements Serializable {

	private static final long serialVersionUID = -2580475102375024245L;

	@Inject
	private ScriptInstanceService scriptInstanceService;

}