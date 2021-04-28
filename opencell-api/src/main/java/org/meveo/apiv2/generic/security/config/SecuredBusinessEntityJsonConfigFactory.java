package org.meveo.apiv2.generic.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.meveo.api.security.config.*;
import org.meveo.apiv2.generic.security.parser.IdParser;
import org.meveo.commons.utils.StringUtils;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.interceptor.InvocationContext;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Class factory responsible to build and get secured entities configurations based on Json definition
 * (see config file secured-entities-config.json)
 *
 * @author Mounir Boukayoua
 * @since 10.X
 */
@Singleton
@JsonConfigFactory
public class SecuredBusinessEntityJsonConfigFactory implements SecuredBusinessEntityConfigFactory {

    @Inject
    private Logger log;

    /**
     * Map to store loaded SE configs from json file and stored with
     * key using format entityName.apiMethod
     */
    private Map<String, SecuredBusinessEntityConfig> configMap = new HashMap<>();

    /**
     * Get the Json based config corresponding to the entity name
     * and API method name retrieved form method invocation context
     * @param context API method invocation context
     * @return an instance of {@link SecuredBusinessEntityJsonConfig}
     */
    @Override
    public SecuredBusinessEntityConfig get(InvocationContext context) {
        Object firstParam = context.getParameters()[0];
        Class<?> entityClass;
        if (firstParam instanceof Class<?>) {
            entityClass = (Class<?>) firstParam;
        } else {
            entityClass = firstParam.getClass();
        }

        String methodName = context.getMethod().getName();
        String keyConfig = entityClass.getSimpleName() + "." + methodName;
        SecuredBusinessEntityConfig config = configMap.get(keyConfig);

        if (config == null) {
            log.warn("No Secured entities configuration found for entity.method={}", keyConfig);
        } else {
            // fill fields that were not set with defautl values
            fillConfigDefaultValues(config);
        }

        log.debug("Secured entities configuration for entity.method={} is found: {}", keyConfig, config);
        return config;
    }

    /**
     * Fill the empty config parameters with the default values as defined
     * for this factory
     * @param config secured business entities configuration instance
     */
    private void fillConfigDefaultValues(SecuredBusinessEntityConfig config) {

        // fill default values for SecuredMethodConfig.SecureMethodParameterConfig[]
        SecuredMethodConfig securedMethodConfig = config.getSecuredMethodConfig();
        if (securedMethodConfig != null && securedMethodConfig.getValidate() != null) {
            for (SecureMethodParameterConfig parameterConfig : securedMethodConfig.getValidate()) {
                //the default value for index is 1 instead of 0
                parameterConfig.setIndex(parameterConfig.getIndex() != -1. ? parameterConfig.getIndex() : 1);
                //the default value for the parser is IdParser instead of CodeParser
                parameterConfig.setParser(parameterConfig.getParser() != null ? parameterConfig.getParser() : IdParser.class);
            }
        }
        // fill default values for filterResultsConfig.getPropertyToFilter
        FilterResultsConfig filterResultsConfig = config.getFilterResultsConfig();
        if(filterResultsConfig != null && StringUtils.isBlank(filterResultsConfig.getPropertyToFilter())) {
            filterResultsConfig.setPropertyToFilter("entityList");
        }
    }

    /**
     * Load secured business entities configurations from file secured-entities-config.json
     */
    @PostConstruct
    private void laodConfigs() {
        log.debug("Loading secured business entities configurations from file secured-entities-config.json...");
        try {
            InputStream configInputStream = this.getClass().getClassLoader()
                    .getResourceAsStream("secured-entities-config.json");
            // Set Class deserializer on the mapper
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(Class.class, new ClassDeserializer());
            mapper.registerModule(module);

            SecuredBusinessEntityJsonConfig[] sbeConfigsArray = mapper.readValue(configInputStream,
                    SecuredBusinessEntityJsonConfig[].class);

            int entitiesCount = 0;
            for (SecuredBusinessEntityJsonConfig securedBusinessEntityJsonConfig : sbeConfigsArray) {
                String entityName = securedBusinessEntityJsonConfig.getEntityName();

                SecuredBusinessEntityConfigWrapper[] configWrappers = securedBusinessEntityJsonConfig.getConfigs();
                if (configWrappers != null) {
                    for(SecuredBusinessEntityConfigWrapper configWrapper : configWrappers) {
                        String[] methodNames = configWrapper.getMethod().split("\\|");

                        for (String methodName : methodNames) {
                            configMap.put(entityName+"."+methodName, configWrapper.getSecuredBusinessEntityConfig());
                        }
                    }
                }
                entitiesCount++;
            }
            log.debug("{} configurations for {} secured entities found and loaded with success.", configMap.size(), entitiesCount);

        } catch (Exception e){
            throw new RuntimeException("Can't load secured business entities from file " +
                    "secured-entities-config.json", e);
        }
    }
}
