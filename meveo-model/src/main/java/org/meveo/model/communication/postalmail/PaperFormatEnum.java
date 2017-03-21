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

import java.util.ArrayList;
import java.util.List;

public enum PaperFormatEnum {
	A0(PaperSerieEnum.A,841,1189),
	A1(PaperSerieEnum.A,594,841),
	A2(PaperSerieEnum.A,420,594),
	A3(PaperSerieEnum.A,297,420),
	A4(PaperSerieEnum.A,210,297),
	A5(PaperSerieEnum.A,148,210),
	A6(PaperSerieEnum.A,105,148),
	A7(PaperSerieEnum.A,74,105),
	A8(PaperSerieEnum.A,52,74),
	A9(PaperSerieEnum.A,37,52),
	A10(PaperSerieEnum.A,26,37),
	B0(PaperSerieEnum.B,1000,1414),
	B1(PaperSerieEnum.B,707,1000),
	B2(PaperSerieEnum.B,500,707),
	B3(PaperSerieEnum.B,353,500),
	B4(PaperSerieEnum.B,250,353),
	B5(PaperSerieEnum.B,176,250),
	B6(PaperSerieEnum.B,125,176),
	B7(PaperSerieEnum.B,88,125),
	B8(PaperSerieEnum.B,62,88),
	B9(PaperSerieEnum.B,44,62),
	B10(PaperSerieEnum.B,31,44),
	C0(PaperSerieEnum.C,917,1297),
	C1(PaperSerieEnum.C,648,917),
	C2(PaperSerieEnum.C,458,648),
	C3(PaperSerieEnum.C,324,458),
	C4(PaperSerieEnum.C,229,324),
	C5(PaperSerieEnum.C,162,229),
	C6(PaperSerieEnum.C,114,162),
	C7(PaperSerieEnum.C,81,114),
	C8(PaperSerieEnum.C,57,81),
	C9(PaperSerieEnum.C,40,57),
	C10(PaperSerieEnum.C,28,40);
	
	PaperSerieEnum serie;
	public int heightInMillimeters;
	public int widhtInMillimeter;
	
	private PaperFormatEnum(PaperSerieEnum serie,int height,int width){
		this.serie=serie;
		this.heightInMillimeters=height;
		this.widhtInMillimeter=width;
	}
	
	public static PaperFormatEnum getBySize(int height,int width){
		PaperFormatEnum result=null;
		for(PaperFormatEnum format:PaperFormatEnum.values()){
			if(format.heightInMillimeters==height && format.widhtInMillimeter==width){
				result=format;
				break;
			}
		}
		return result;
	}
	

	public static PaperFormatEnum getLargerFormatSmallerOrEqualThan(int height,int width,PaperSerieEnum serie){
		PaperFormatEnum result=null;
		int deltaMin=0;
		for(PaperFormatEnum format:PaperFormatEnum.values()){
			if((serie==null || format.serie==serie) && format.heightInMillimeters<=height && format.widhtInMillimeter<=width){
				int delta = (height-format.heightInMillimeters)+(width-format.widhtInMillimeter);
				if(delta>=deltaMin){
					result=format;
					deltaMin=delta;
				}
			}
		}
		return result;
	}
	
	public static List<PaperFormatEnum> getBySerie(PaperSerieEnum serie){
		List<PaperFormatEnum> result = new ArrayList<PaperFormatEnum>(11);
		for(PaperFormatEnum format:PaperFormatEnum.values()){
			if(format.serie==serie){
				result.add(format);
			}
		}
		return result;
	}
}
