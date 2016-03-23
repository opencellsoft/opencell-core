/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.communication.postalmail;

import java.util.List;

public enum EnvelopeFormatEnum {

	DL(110,220),
	C7_C6(81,162),
	C6(114,162),
	C6_C5(114,229),
	C5(162,229),
	C4(229,324),
	C3(324,458),
	B6(125,176),
	B5(176,250),
	B4(250,253),
	E3(280,400);
	
	private int heightInMillimeters;
	private int widhtInMillimeter;
	
	private EnvelopeFormatEnum(int height,int width){
		heightInMillimeters=height;
		widhtInMillimeter=width;
	}
	
	/*
	 * if serie is null, lookup over all series A,B,C
	 */
	public PaperFormatEnum getLargerContainingFormat(PaperSerieEnum serie){
		return PaperFormatEnum.getLargerFormatSmallerOrEqualThan(heightInMillimeters, widhtInMillimeter, serie);
	}
	
	public static EnvelopeFormatEnum getSmallestContainingFormat(PaperFormatEnum paperFormat,int withFoldingNumber,int heightFoldingNumber,List<EnvelopeFormatEnum> excludedFormats){
		EnvelopeFormatEnum result=null;
		if(withFoldingNumber<=0){
			withFoldingNumber=1;
		}
		if(heightFoldingNumber<=0){
			heightFoldingNumber=1;
		}
		int foldedPaperWidth=paperFormat.widhtInMillimeter/withFoldingNumber;
		int foldedPaperHeight=paperFormat.heightInMillimeters/heightFoldingNumber;
		int sizeMax=0;
		for(EnvelopeFormatEnum format:EnvelopeFormatEnum.values()){
			if(format.heightInMillimeters>foldedPaperHeight && format.widhtInMillimeter>foldedPaperWidth && (excludedFormats==null || !excludedFormats.contains(format))){
				int size = format.heightInMillimeters*format.widhtInMillimeter;
				if(sizeMax==0){
					sizeMax=size;
				}
				if(size<=sizeMax){
					result=format;
					sizeMax=size;
				}
			}
		}
		return result;
	}

}
