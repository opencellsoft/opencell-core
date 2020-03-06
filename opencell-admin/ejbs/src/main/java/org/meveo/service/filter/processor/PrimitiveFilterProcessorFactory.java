/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.filter.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.meveo.model.filter.PrimitiveFilterCondition;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrimitiveFilterProcessorFactory {

	private Logger logger = LoggerFactory.getLogger(PrimitiveFilterProcessorFactory.class);

	private List<PrimitiveFilterProcessor> processors;
	private PrimitiveFilterProcessor defaultProcessor;

	// Private constructor. Prevents instantiation from other classes.
	private PrimitiveFilterProcessorFactory() {
	}

	/**
	 * Initializes PrimitiveFilterProcessorFactory singleton.
	 * 
	 * {@link PrimitiveFilterProcessorFactoryHolder} is loaded on the first execution of
	 * {@link PrimitiveFilterProcessorFactory#getInstance()} or the first access to
	 * {@link PrimitiveFilterProcessorFactoryHolder#INSTANCE}.
	 */
	private static class PrimitiveFilterProcessorFactoryHolder {
		private static final PrimitiveFilterProcessorFactory INSTANCE = new PrimitiveFilterProcessorFactory();
	}

	public static PrimitiveFilterProcessorFactory getInstance() {
		return PrimitiveFilterProcessorFactoryHolder.INSTANCE;
	}

	public PrimitiveFilterProcessor getProcessor(PrimitiveFilterCondition condition) {
		PrimitiveFilterProcessor processor = null;
		if (processors == null) {
			initializeProcessors();
		}
		for (PrimitiveFilterProcessor primitiveFilterProcessor : processors) {
			if (primitiveFilterProcessor.canProccessCondition(condition)) {
				processor = primitiveFilterProcessor;
				break;
			}
		}
		if(processor == null){
			// assign the default processor
			processor = defaultProcessor;
		}
		return processor;
	}

	private void initializeProcessors() {
		logger.info("Initializing PrimitiveFilterProcessors");
		processors = new ArrayList<>();
		Reflections reflections = new Reflections("org.meveo.service.filter.processor");
		Set<Class<? extends PrimitiveFilterProcessor>> processorClasses = reflections.getSubTypesOf(PrimitiveFilterProcessor.class);
		PrimitiveFilterProcessor processor = null;
		for (Class<? extends PrimitiveFilterProcessor> processorClass : processorClasses) {
			try {
				processor = processorClass.newInstance();
				if (processor.getClass().equals(StringProcessor.class)) {
					// StringProcessor is the default processor
					defaultProcessor = processor;
				} else {
					processors.add(processor);
				}
			} catch (InstantiationException | IllegalAccessException e) {
				logger.warn("Failed to instantiate class: " + processorClass.getSimpleName(), e);
			}
		}
		logger.info("PrimitiveFilterProcessors initialization complete.  Found " + processors.size() + " processors.");
	}

}
