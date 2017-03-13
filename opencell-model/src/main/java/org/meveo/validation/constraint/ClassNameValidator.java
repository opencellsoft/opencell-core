package org.meveo.validation.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ClassNameValidator implements ConstraintValidator<ClassName,String> {

	@Override
	public void initialize(ClassName arg0) {
	}

	@Override
	public boolean isValid(String className, ConstraintValidatorContext arg1) {
		boolean result=false;
		try {
			Class.forName(className);
			result=true;
		} catch(Exception e){}
		return result;
	}

}
