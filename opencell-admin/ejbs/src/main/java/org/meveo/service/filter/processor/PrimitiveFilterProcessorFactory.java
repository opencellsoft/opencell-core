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
