package org.meveo.model.cpq.enums;

import org.apache.commons.lang3.math.Fraction;
import org.meveo.model.catalog.ColumnTypeEnum;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeValue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 
 * @author Tarik FAKHOURI.
 * @version 10.0
 */
public enum AttributeTypeEnum {

	/** No value to enter, a message entered during the configuration of the service (for example a secondary description) 
	 * is available to be used in the CPQ or in the estimate **/
	INFO {
		@Override
		public ColumnTypeEnum getColumnType(Boolean isRange) {
			return ColumnTypeEnum.String;
		}
	},
	
	/**  List of text values: Choice of a value from a predefined list **/
	LIST_TEXT {
		@Override
		public ColumnTypeEnum getColumnType(Boolean isRange) {
			return ColumnTypeEnum.String;
		}
	},
	/** List of multiple text value: choice of multiple values from a predefined list**/
	LIST_MULTIPLE_TEXT{
		@Override
		public ColumnTypeEnum getColumnType(Boolean isRange) {
			return ColumnTypeEnum.String;
		}
	}, // "; ; "
	/** List of numerical values: choice of a value among a list of numbers **/
	LIST_NUMERIC {
		@Override
		public ColumnTypeEnum getColumnType(Boolean isRange) {
			return ColumnTypeEnum.Double;
		}

		@Override
		public Object getValue(AttributeValue attributeValue) {
			return attributeValue.getDoubleValue();
		}
	},
	
	/** List of multiple numerical value: choice of a multiple values among a list of numbers **/
	LIST_MULTIPLE_NUMERIC{
		@Override
		public ColumnTypeEnum getColumnType(Boolean isRange) {
			return ColumnTypeEnum.Double;
		}
	}, // "; ; "
	
	/** Text value: Entering a text **/
	TEXT {
		@Override
		public ColumnTypeEnum getColumnType(Boolean isRange) {
			return ColumnTypeEnum.String;
		}
	},
	
	/** Numeric value: Entry of a number **/
	NUMERIC{
		@Override
		public ColumnTypeEnum getColumnType(Boolean isRange) {
			return isRange ? ColumnTypeEnum.Range_Numeric : ColumnTypeEnum.Double;
		}

		@Override
		public Object getValue(AttributeValue attributeValue) {
			return attributeValue.getDoubleValue();
		}
	},
	
	/** numeric with predefined decimale **/
	INTEGER{
		@Override
		public ColumnTypeEnum getColumnType(Boolean isRange) {
			return isRange ? ColumnTypeEnum.Range_Numeric : ColumnTypeEnum.Double;
		}
	},
	
	/** Date type**/
	DATE {
		@Override
		public ColumnTypeEnum getColumnType(Boolean isRange) {
			return ColumnTypeEnum.Range_Date;
		}

		@Override
		public Object getValue(AttributeValue attributeValue) {
			return attributeValue.getDateValue();
		}
	},
	
	/** choice of calendar of opencell's calendar**/
	CALENDAR{
		@Override
		public ColumnTypeEnum getColumnType(Boolean isRange) {
			return ColumnTypeEnum.Range_Date;
		}
	}, // To analyze
	
	/** Email format **/
	EMAIL {
		@Override
		public ColumnTypeEnum getColumnType(Boolean isRange) {
			return ColumnTypeEnum.String;
		}
	},
	
	/** phone number format **/
	PHONE {
		@Override
		public ColumnTypeEnum getColumnType(Boolean isRange) {
			return ColumnTypeEnum.String;
		}
	},
	
	/** display some of list of numerics **/
	TOTAL{
		@Override
		public ColumnTypeEnum getColumnType(Boolean isRange) {
			return isRange ? ColumnTypeEnum.Range_Numeric : ColumnTypeEnum.Double;
		}

		@Override
		public Object getValue(AttributeValue attributeValue) {
			List<Double> values = (List<Double>) attributeValue.getAssignedAttributeValue()
					.stream()
					.map(att -> ((AttributeValue) att).getAttribute().getAttributeType().getValue((AttributeValue) att))
					.collect(Collectors.toList());
			return values.stream().reduce(0.0, Double::sum);
		}

	},

    COUNT {
		@Override
		public ColumnTypeEnum getColumnType(Boolean isRange) {
			return isRange ? ColumnTypeEnum.Range_Numeric : ColumnTypeEnum.Double;
		}

		@Override
		public Object getValue(AttributeValue attributeValue) {
			Set<Double> values = (Set<Double>) attributeValue.getAssignedAttributeValue()
					.stream()
					.map(att -> ((AttributeValue) att).getAttribute().getAttributeType().getValue((AttributeValue) att))
					.collect(Collectors.toSet());
			return values.size();
		}
	};

	public abstract ColumnTypeEnum getColumnType(Boolean isRange);

	public Object  getValue(AttributeValue attributeValue) {
		return attributeValue.getStringValue();
	}
}
