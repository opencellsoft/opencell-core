package org.meveo.api.sequence;

import javax.ejb.Stateless;

import org.meveo.api.dto.sequence.GenericSequenceDto;
import org.meveo.model.sequence.GenericSequence;

/**
 * Class for converting the GenericSequence dto to entity and vice versa.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Stateless
public class GenericSequenceApi {

	public static GenericSequence toGenericSequence(GenericSequenceDto source, GenericSequence target) {
		if (target == null) {
			target = new GenericSequence();
		}
		target.setPrefix(source.getPrefix());
		target.setSequenceSize(source.getSequenceSize());

		return target;
	}

	public static GenericSequenceDto fromGenericSequence(GenericSequence source) {
		GenericSequenceDto target = new GenericSequenceDto();

		target.setPrefix(source.getPrefix());
		target.setSequenceSize(source.getSequenceSize());
		if (source.getCurrentSequenceNb() != null) {
			target.setCurrentSequenceNb(source.getCurrentSequenceNb());
		}

		return target;
	}

}
