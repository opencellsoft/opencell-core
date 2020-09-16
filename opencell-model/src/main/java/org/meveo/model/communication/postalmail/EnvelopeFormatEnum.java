/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.model.communication.postalmail;

import java.util.List;

public enum EnvelopeFormatEnum {

    DL(110, 220), C7_C6(81, 162), C6(114, 162), C6_C5(114, 229), C5(162, 229), C4(229, 324), C3(324, 458), B6(125, 176), B5(176, 250), B4(250, 253), E3(280, 400);

    private int heightInMillimeters;
    private int widhtInMillimeter;

    private EnvelopeFormatEnum(int height, int width) {
        heightInMillimeters = height;
        widhtInMillimeter = width;
    }

    /*
     * if serie is null, lookup over all series A,B,C
     */
    public PaperFormatEnum getLargerContainingFormat(PaperSerieEnum serie) {
        return PaperFormatEnum.getLargerFormatSmallerOrEqualThan(heightInMillimeters, widhtInMillimeter, serie);
    }

    public static EnvelopeFormatEnum getSmallestContainingFormat(PaperFormatEnum paperFormat, int withFoldingNumber, int heightFoldingNumber,
            List<EnvelopeFormatEnum> excludedFormats) {
        EnvelopeFormatEnum result = null;
        if (withFoldingNumber <= 0) {
            withFoldingNumber = 1;
        }
        if (heightFoldingNumber <= 0) {
            heightFoldingNumber = 1;
        }
        int foldedPaperWidth = paperFormat.getWidhtInMillimeter() / withFoldingNumber;
        int foldedPaperHeight = paperFormat.getHeightInMillimeters() / heightFoldingNumber;
        int sizeMax = 0;
        for (EnvelopeFormatEnum format : EnvelopeFormatEnum.values()) {
            if (format.heightInMillimeters > foldedPaperHeight && format.widhtInMillimeter > foldedPaperWidth && (excludedFormats == null || !excludedFormats.contains(format))) {
                int size = format.heightInMillimeters * format.widhtInMillimeter;
                if (sizeMax == 0) {
                    sizeMax = size;
                }
                if (size <= sizeMax) {
                    result = format;
                    sizeMax = size;
                }
            }
        }
        return result;
    }

}
