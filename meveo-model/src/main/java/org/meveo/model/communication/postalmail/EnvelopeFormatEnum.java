/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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
