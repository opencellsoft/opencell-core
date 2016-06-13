package org.meveo.export.parser;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.meveo.model.IEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory that retrieves a {@link DerivedEntityParser} object based on the parentEntity and field name.
 */
@Singleton
public class DerivedEntityParserFactory {
    private Logger logger = LoggerFactory.getLogger(DerivedEntityParserFactory.class);

    private List<BaseDerivedEntityParser> derivedEntityParsers;

    @Inject
    private void initParsers(@Any Instance<BaseDerivedEntityParser> parsers) {
        if (derivedEntityParsers == null) {
            derivedEntityParsers = new ArrayList<>();
        }
        if (derivedEntityParsers.isEmpty()) {
            for (BaseDerivedEntityParser parser : parsers) {
                derivedEntityParsers.add(parser);
            }
        }
    }

    public BaseDerivedEntityParser getParser(final Class<? extends IEntity> parentEntity, final String fieldName) {
        BaseDerivedEntityParser parser = null;
        for (BaseDerivedEntityParser entityParser : derivedEntityParsers) {
            if (entityParser.matches(parentEntity, fieldName)) {
                parser = entityParser;
                break;
            }
        }
        return parser;
    }
}
