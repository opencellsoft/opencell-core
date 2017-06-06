package org.meveo.service.catalog.impl;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.shared.DateUtils;

public class GenericProductOfferingServiceTest {

    @Test
    public void testUpdateValidityOfVersion() {

        String today = DateUtils.formatDateWithPattern(new Date(), DateUtils.DATE_PATTERN);

        // matched period validity from, matched period validity to
        String[] matchedPeriod = new String[] { null, null };
        String[][] checkDates = new String[][] { //
                { "2017-05-01", "2017-06-01", null, "2017-05-01" }, //
                { "2017-05-01", null, null, "2017-05-01" }, //
                { null, "2017-06-01", "2017-06-01", null }, //
                { null, null, today, today } // invalidated

        };

        validateMatchedPeriods(matchedPeriod, checkDates);

        matchedPeriod = new String[] { "2017-05-01", "2017-06-01" };
        checkDates = new String[][] { // offer validity from, offer validity to, updated matched period validity from, updated
                                      // matched period validity to

                { null, "2017-05-01", "2017-05-01", "2017-06-01" }, // unchanged
                { null, "2017-05-02", "2017-05-02", "2017-06-01" }, //
                { null, "2017-06-01", "2017-05-01", "2017-05-01" }, // invalidated
                { null, "2017-06-02", "2017-05-01", "2017-05-01" }, // invalidated
                { null, null, "2017-05-01", "2017-05-01" }, // invalidated

                { "2017-04-01", "2017-05-01", "2017-05-01", "2017-06-01" }, // unchanged
                { "2017-04-01", "2017-05-02", "2017-05-02", "2017-06-01" }, //
                { "2017-04-01", "2017-06-01", "2017-05-01", "2017-05-01" }, // invalidated
                { "2017-04-01", "2017-06-02", "2017-05-01", "2017-05-01" }, // invalidated
                { "2017-04-01", null, "2017-05-01", "2017-05-01" }, // invalidated

                { "2017-05-01", "2017-05-02", "2017-05-02", "2017-06-01" }, //
                { "2017-05-01", "2017-06-01", "2017-05-01", "2017-05-01" }, // invalidated
                { "2017-05-01", "2017-06-02", "2017-05-01", "2017-05-01" }, // invalidated
                { "2017-05-01", null, "2017-05-01", "2017-05-01" }, // invalidated

                { "2017-05-02", "2017-05-20", "2017-05-01", "2017-05-01" }, // invalidated
                { "2017-05-02", "2017-06-01", "2017-05-01", "2017-05-02" }, //
                { "2017-05-02", "2017-06-02", "2017-05-01", "2017-05-02" }, //
                { "2017-05-02", null, "2017-05-01", "2017-05-02" }, //

                { "2017-06-01", "2017-06-02", "2017-05-01", "2017-06-01" }, // unchanged
                { "2017-06-01", null, "2017-05-01", "2017-06-01" }, // unchanged

        };

        validateMatchedPeriods(matchedPeriod, checkDates);

        matchedPeriod = new String[] { "2017-05-01", null };
        checkDates = new String[][] { // offer validity from, offer validity to, updated matched period validity from, updated
                                      // matched period validity to

                { null, "2017-05-01", "2017-05-01", null }, // unchanged
                { null, "2017-05-02", "2017-05-02", null }, //
                { null, null, "2017-05-01", "2017-05-01" }, // invalidated

                { "2017-04-01", "2017-05-01", "2017-05-01", null }, // unchanged
                { "2017-04-01", "2017-05-02", "2017-05-02", null }, //
                { "2017-04-01", null, "2017-05-01", "2017-05-01" }, // invalidated

                { "2017-05-01", "2017-05-02", "2017-05-02", null }, //
                { "2017-05-01", null, "2017-05-01", "2017-05-01" }, // invalidated

                { "2017-05-02", "2017-05-20", "2017-05-01", "2017-05-02" }, //
                { "2017-05-02", null, "2017-05-01", "2017-05-02" }, //
        };

        validateMatchedPeriods(matchedPeriod, checkDates);

        matchedPeriod = new String[] { null, "2017-06-01" };
        checkDates = new String[][] { // offer validity from, offer validity to, updated matched period validity from, updated
                                      // matched period validity to

                { null, "2017-05-20", "2017-05-20", "2017-06-01" }, //
                { null, "2017-06-01", today, today }, // invalidated
                { null, "2017-06-02", today, today }, // invalidated
                { null, null, today, today }, // invalidated

                { "2017-05-01", "2017-05-20", "2017-05-20", "2017-06-01" }, //
                { "2017-05-01", "2017-06-01", null, "2017-05-01" }, //
                { "2017-05-01", "2017-06-02", null, "2017-05-01" }, //
                { "2017-05-01", null, null, "2017-05-01" }, //

                { "2017-06-01", "2017-06-02", null, "2017-06-01" }, // unchanged
                { "2017-06-01", null, null, "2017-06-01" }, // unchanged
        };

        validateMatchedPeriods(matchedPeriod, checkDates);
    }

    private void validateMatchedPeriods(String[] matchedPeriod, String[][] checkDates) {

        OfferTemplate matchedVersion = new OfferTemplate();

        GenericProductOfferingService<ProductOffering> poService = new GenericProductOfferingService<ProductOffering>();

        for (String[] dates : checkDates) {

            DatePeriod validity = new DatePeriod(matchedPeriod[0], matchedPeriod[1], DateUtils.DATE_PATTERN);
            matchedVersion.setValidity(validity);

            DatePeriod offeringValidity = new DatePeriod(dates[0], dates[1], DateUtils.DATE_PATTERN);

            boolean isPeriodInvalid = poService.isMatchedPeriodInvalid(validity, offeringValidity.getFrom(), offeringValidity.getTo());

            Assert.assertEquals(matchedPeriod[0] + " - " + matchedPeriod[1] + " / " + offeringValidity.toString(DateUtils.DATE_PATTERN),
                StringUtils.compare(dates[2], dates[3]) == 0, isPeriodInvalid);

            poService.updateValidityOfVersion(matchedVersion, offeringValidity);

            Assert.assertEquals(matchedPeriod[0] + " - " + matchedPeriod[1] + " / " + offeringValidity.toString(DateUtils.DATE_PATTERN), dates[2] + " / " + dates[3],
                (matchedVersion.getValidity().getFrom() != null ? DateUtils.formatDateWithPattern(matchedVersion.getValidity().getFrom(), DateUtils.DATE_PATTERN) : null) + " / "
                        + (matchedVersion.getValidity().getTo() != null ? DateUtils.formatDateWithPattern(matchedVersion.getValidity().getTo(), DateUtils.DATE_PATTERN) : null));
        }
    }
}
