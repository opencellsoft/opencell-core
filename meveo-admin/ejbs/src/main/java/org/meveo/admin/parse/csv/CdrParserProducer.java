package org.meveo.admin.parse.csv;

import java.util.Set;

import javax.ejb.Stateless;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.medina.impl.CSVCDRParser;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class CdrParserProducer {

	@Inject
	private Logger log;

	@Inject
	private BeanManager beanManager;

	/**
	 * The default parser.
	 */
	@Inject
	private MEVEOCdrParser meveoCdrParser;

	@SuppressWarnings("unchecked")
	public CSVCDRParser getParser() throws BusinessException {
		Set<Bean<?>> parsers = beanManager.getBeans(CSVCDRParser.class, new AnnotationLiteral<CdrParser>() {
			private static final long serialVersionUID = 1149660610296393946L;
		});

		if (parsers.size() > 1) {
			log.error("Multiple custom csv parsers encountered.");
			throw new BusinessException("Multiple custom csv parsers encountered.");
		} else if (parsers.size() == 1) {
			Bean<CSVCDRParser> bean = (Bean<CSVCDRParser>) parsers.toArray()[0];
			log.debug("Found custom cdr parser={}", bean.getBeanClass());
			try {
				CSVCDRParser parser = (CSVCDRParser) bean.getBeanClass().newInstance();
				return parser;
			} catch (InstantiationException | IllegalAccessException e) {
				throw new BusinessException("Cannot instantiate custom cdr parser class="
						+ bean.getBeanClass().getName() + ".");
			}
		} else {
			log.debug("Use default cdr parser={}", meveoCdrParser.getClass());
			return meveoCdrParser;
		}
	}
}
