package org.meveo.commons.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnumBuilder {

	private static final Logger log = LoggerFactory.getLogger(EnumBuilder.class);

	/**
	 * MAP_EXTENSIBLE_ENUMs is initialized by Opencell extensible enum values.
	 * Other values could be added from outside for custom needs, by a Singleton & Startup bean for example :
	 *
	 * @Startup
	 * @Singleton
	 * public class CustomBean implements Serializable {
	 * @Inject
	 * private Logger log;
	 *
	 * @PostConstruct
	 * public void init() {
	 * EnumBuilder.put(JobCategoryEnum.class, Arrays.stream(CustomJobCategoryEnum.values()).collect(Collectors.toMap(item -> item.name(), item -> item)));
	 * }
	 * }
	 *
	 */
	private static Map<String, Map<String, Object>> MAP_EXTENSIBLE_ENUMs = new HashMap<>();

	static {
		put(JobCategoryEnum.class, Arrays.stream(MeveoJobCategoryEnum.values())
				.collect(Collectors.toMap(item -> item.name(), item -> item)));
	}

	public static void put(Class enumType, Map<String, Object> newItems) {
		log.debug("adding newItems={} to enumType={}", newItems, enumType);
		final String enumName = enumType.getName();
		Map<String, Object> enumItems = MAP_EXTENSIBLE_ENUMs.get(enumName);
		if (enumItems == null) {
			enumItems = new HashMap<>();
		}
		enumItems.putAll(newItems);
		MAP_EXTENSIBLE_ENUMs.put(enumName, enumItems);
	}

	public static void put(Class enumType, String key, Object value) {
		final String enumName = enumType.getName();
		Map<String, Object> enumItems = MAP_EXTENSIBLE_ENUMs.get(enumName);
		if (enumItems == null) {
			enumItems = new HashMap<>();
		}
		enumItems.put(key, value);
		MAP_EXTENSIBLE_ENUMs.put(enumName, enumItems);
	}

	public static Object[] values(String enumName) {
		log.debug("values of enumName={}", enumName);
		Map<String, Object> enumItems = MAP_EXTENSIBLE_ENUMs.get(enumName);
		if (enumItems == null) {
			log.error("no enum found for enumName={} MAP_EXTENSIBLE_ENUMs={}", enumName, MAP_EXTENSIBLE_ENUMs);
			return null;
		}
		return enumItems.values().toArray();
	}

	public static Object build(String code, Class enumType) {
		log.debug("building Enum code={} for enumType={}", code, enumType);
		final String enumName = enumType.getName();
		Map<String, Object> enumItems = MAP_EXTENSIBLE_ENUMs.get(enumName);
		if (enumItems == null) {
			log.error("no enum found for enumType={} and code={}, MAP_EXTENSIBLE_ENUMs={}", enumType, code,
					MAP_EXTENSIBLE_ENUMs);
			return null;
		}
		return enumItems.get(code);
	}

	public static List<Object> values(Class enumType) {
		Map<String, Object> enumItems = MAP_EXTENSIBLE_ENUMs.get(enumType.getName());
		if (MapUtils.isNotEmpty(enumItems)) {
			return new ArrayList<>(enumItems.values());
		}
		return null;
	}
	
}