package org.meveo.model.catalog;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author Edward P. Legaspi
 */
public enum LifeCycleStatusEnum {
	IN_STUDY("IN_STUDY", "enum.LifeCycleStatusEnum.IN_STUDY"), IN_DESIGN("IN_DESIGN", "enum.LifeCycleStatusEnum.IN_DESIGN"), IN_TEST("IN_TEST", "enum.LifeCycleStatusEnum.IN_TEST"), ACTIVE(
			"ACTIVE", "enum.LifeCycleStatusEnum.ACTIVE"), LAUNCHED("LAUNCHED", "enum.LifeCycleStatusEnum.LAUNCHED"), RETIRED("RETIRED", "enum.LifeCycleStatusEnum.RETIRED"), OBSOLETE(
			"OBSOLETE", "enum.LifeCycleStatusEnum.OBSOLETE"), REJECTED("REJECTED", "enum.LifeCycleStatusEnum.REJECTED");

	private String value;
	private String label;

	private LifeCycleStatusEnum(String value, String label) {
		this.value = value;
		this.label = label;
	}

	@Override
	public String toString() {
		return value;
	}

	public String getValue() {
		return this.value;
	}

	public boolean canTransitionFrom(LifeCycleStatusEnum currentStatus) {
		if (this == currentStatus) {
			return true;
		}

		switch (this) {
		case IN_STUDY: {
			if (currentStatus == null) {
				return true;
			}

			return false;
		}

		case IN_DESIGN: {
			if (currentStatus == null || currentStatus == IN_STUDY || currentStatus == IN_TEST) {
				return true;
			}

			return false;
		}

		case IN_TEST: {
			if (currentStatus == null || currentStatus == IN_DESIGN) {
				return true;
			}

			return false;
		}

		case ACTIVE: {
			if (currentStatus == null || currentStatus == IN_TEST) {
				return true;
			}

			return false;
		}

		case LAUNCHED: {
			if (currentStatus == null || currentStatus == ACTIVE) {
				return true;
			}

			return false;
		}

		case RETIRED: {
			if (currentStatus == ACTIVE || currentStatus == LAUNCHED) {
				return true;
			}

			return false;
		}

		case OBSOLETE: {
			if (currentStatus == RETIRED) {
				return true;
			}

			return false;
		}

		case REJECTED: {
			if (currentStatus == IN_TEST) {
				return true;
			}

			return false;
		}

		default: {
			return false;
		}
		}
	}

	@SuppressWarnings("static-access")
	public static Set<LifeCycleStatusEnum> transitionableStatues(LifeCycleStatusEnum lifeCycleStatusEnum) {

		if (lifeCycleStatusEnum == null) {
			return EnumSet.of(LifeCycleStatusEnum.IN_STUDY, LifeCycleStatusEnum.IN_DESIGN, LifeCycleStatusEnum.IN_TEST, LifeCycleStatusEnum.ACTIVE, LifeCycleStatusEnum.LAUNCHED);
		}

		switch (lifeCycleStatusEnum) {
		case IN_STUDY: {
			return EnumSet.of(LifeCycleStatusEnum.IN_DESIGN);
		}

		case IN_DESIGN: {
			return EnumSet.of(LifeCycleStatusEnum.IN_TEST);
		}

		case IN_TEST: {
			return EnumSet.of(LifeCycleStatusEnum.IN_DESIGN, LifeCycleStatusEnum.ACTIVE, LifeCycleStatusEnum.REJECTED);
		}

		case ACTIVE: {
			return EnumSet.of(LifeCycleStatusEnum.LAUNCHED, LifeCycleStatusEnum.RETIRED);
		}

		case LAUNCHED: {
			return EnumSet.of(LifeCycleStatusEnum.RETIRED);
		}

		case RETIRED: {
			return EnumSet.of(LifeCycleStatusEnum.OBSOLETE);
		}

		case OBSOLETE: {
			return null;
		}

		case REJECTED: {
			return null;
		}

		default: {
			return EnumSet.noneOf(LifeCycleStatusEnum.class);
		}
		}
	}

	public static LifeCycleStatusEnum find(String value) {
		for (LifeCycleStatusEnum LifeCycleStatusEnum : values()) {
			if (LifeCycleStatusEnum.value.equals(value)) {
				return LifeCycleStatusEnum;
			}
		}

		return null;
	}

	public String getLabel() {
		return label;
	}

}
