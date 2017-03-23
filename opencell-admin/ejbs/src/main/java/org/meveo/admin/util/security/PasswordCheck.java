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
/* 
 * Name: 
 * 		PasswordCheck.java
 * Author: 
 * 		Jim Sloey - jsloey@justwild.us
 * Adaptations : 
 * 		Sebastien Michea - smichea@gmail.com
 * Requirements:
 * 		Java 1.4 or greater
 * Usage:
 *		Bundled usage: java -jar PasswordCheck.jar <password>
 *		Unbundled usage: java PasswordCheck <password>
 * History:
 * 		Created May 19, 2006 by Jim Sloey
 * 		Updated Oct 31, 2007 by Sebastien Michea
 * Derived from: 
 * 		Steve Moitozo's passwdmeter
 * 		See http://www.geekwisdom.com/dyn/passwdmeter
 * License:
 * 		Open Software License 2.1 or Academic Free License 2.1 
 * 		See http://www.opensource.org
 * Description:
 * 		Need a simple way to check the strength of a password?
 * 		To check in the HTML on the front end try Steve Moitozo's 
 * 		Javascript example at http://www.geekwisdom.com/dyn/passwdmeter
 * Source URL:
 * 		http://justwild.us/examples/password/
 */
package org.meveo.admin.util.security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.meveo.commons.utils.ParamBean;

public class PasswordCheck {

    // Rules variables
    private String PASSWORD_MIXED_CASE;
    private String PASSWORD_MIN_LENGTH;
    private String PASSWORD_NUMERIC;
    private String PASSWORD_SPECIAL;
    private String PASSWORD_STRENGTH;

    public PasswordCheck() {
        ParamBean param = ParamBean.getInstance("meveo.properties");

        PASSWORD_MIXED_CASE = param.getProperty("password.minMixedCase", "0");
        PASSWORD_MIN_LENGTH = param.getProperty("password.minLength", "8");
        PASSWORD_NUMERIC = param.getProperty("password.minNumeric", "0");
        PASSWORD_SPECIAL = param.getProperty("password.special", "0");
        PASSWORD_STRENGTH = param.getProperty("password.strength", "0");

    }

    public boolean checkPasswordStrength(String passwd) {
        int upper = 0, lower = 0, numbers = 0, special = 0, length = 0;
        int intScore = 0;
        String strVerdict = "none", strLog = "";
        Pattern p;
        Matcher m;
        if (passwd == null)
            return false;
        // PASSWORD LENGTH
        length = passwd.length();
        if (length < 5) // length 4 or less
        {
            intScore = (intScore + 3);
            strLog = strLog + "3 points for length (" + length + ")\n";
        } else if (length > 4 && passwd.length() < 8) // length between 5 and 7
        {
            intScore = (intScore + 6);
            strLog = strLog + "6 points for length (" + length + ")\n";
        } else if (length > 7 && passwd.length() < 16) // length between 8 and
        // 15
        {
            intScore = (intScore + 12);
            strLog = strLog + "12 points for length (" + length + ")\n";
        } else if (length > 15) // length 16 or more
        {
            intScore = (intScore + 18);
            strLog = strLog + "18 point for length (" + length + ")\n";
        }
        // LETTERS
        p = Pattern.compile(".??[a-z]");
        m = p.matcher(passwd);
        while (m.find()) // [verified] at least one lower case letter
        {
            lower += 1;
        }
        if (lower > 0) {
            intScore = (intScore + 1);
            strLog = strLog + "1 point for a lower case character\n";
        }
        p = Pattern.compile(".??[A-Z]");
        m = p.matcher(passwd);
        while (m.find()) // [verified] at least one upper case letter
        {
            upper += 1;
        }
        if (upper > 0) {
            intScore = (intScore + 5);
            strLog = strLog + "5 point for an upper case character\n";
        }
        // NUMBERS
        p = Pattern.compile(".??[0-9]");
        m = p.matcher(passwd);
        while (m.find()) // [verified] at least one number
        {
            numbers += 1;
        }
        if (numbers > 0) {
            intScore = (intScore + 5);
            strLog = strLog + "5 points for a number\n";
            if (numbers > 1) {
                intScore = (intScore + 2);
                strLog = strLog + "2 points for at least two numbers\n";
                if (numbers > 2) {
                    intScore = (intScore + 3);
                    strLog = strLog + "3 points for at least three numbers\n";
                }
            }
        }
        // SPECIAL CHAR
        p = Pattern.compile(".??[:,!,@,#,$,%,^,&,*,?,_,~]");
        m = p.matcher(passwd);
        while (m.find()) // [verified] at least one special character
        {
            special += 1;
        }
        if (special > 0) {
            intScore = (intScore + 5);
            strLog = strLog + "5 points for a special character\n";
            if (special > 1) {
                intScore += (intScore + 5);
                strLog = strLog + "5 points for at least two special characters\n";
            }
        }
        // COMBOS
        if (upper > 0 && lower > 0) // [verified] both upper and lower case
        {
            intScore = (intScore + 2);
            strLog = strLog + "2 combo points for upper and lower letters\n";
        }
        if ((upper > 0 || lower > 0) && numbers > 0) // [verified] both letters
        // and numbers
        {
            intScore = (intScore + 2);
            strLog = strLog + "2 combo points for letters and numbers\n";
        }
        if ((upper > 0 || lower > 0) && numbers > 0 && special > 0) // [verified]
        // letters,
        // numbers,
        // and
        // special
        // characters
        {
            intScore = (intScore + 2);
            strLog = strLog + "2 combo points for letters, numbers and special chars\n";
        }
        if (upper > 0 && lower > 0 && numbers > 0 && special > 0) // [verified]
        // upper,
        // lower,
        // numbers,
        // and special
        // characters
        {
            intScore = (intScore + 2);
            strLog = strLog + "2 combo points for upper and lower case letters, numbers and special chars\n";
        }
        if (intScore < 16) {
            strVerdict = "very weak";
        } else if (intScore > 15 && intScore < 25) {
            strVerdict = "weak";
        } else if (intScore > 24 && intScore < 35) {
            strVerdict = "mediocre";
        } else if (intScore > 34 && intScore < 45) {
            strVerdict = "strong";
        } else {
            strVerdict = "very strong";
        }
        System.out.println(strVerdict + " - " + intScore + "\n" + strLog);
        // Does it meet the password policy?
        try {
            int min = Integer.parseInt(PASSWORD_MIN_LENGTH);
            if (length < min)
                return false;
        } catch (Exception e) {
            ;
        } // undefined
        try {
            int num = Integer.parseInt(PASSWORD_NUMERIC);
            if (numbers < num)
                return false;
        } catch (Exception e) {
            ;
        } // undefined
        try {
            int mix = Integer.parseInt(PASSWORD_MIXED_CASE);
            if (upper < mix || lower < mix)
                return false;
        } catch (Exception e) {
            ;
        } // undefined
        try {
            int str = Integer.parseInt(PASSWORD_STRENGTH);
            if (intScore < str)
                return false;
        } catch (Exception e) {
            ;
        } // undefined
        try {
            int spec = Integer.parseInt(PASSWORD_SPECIAL);
            if (special < spec)
                return false;
        } catch (Exception e) {
            ;
        } // undefined
        return true;
    }

    @Override
    public String toString() {
        return String.format(
                "%1$s minimal length, %2$s minimal lower and upper characters, %3$s minimal digits, %4$s minimal special characters, %5$s minimal score",
                PASSWORD_MIN_LENGTH, PASSWORD_MIXED_CASE, PASSWORD_NUMERIC, PASSWORD_SPECIAL, PASSWORD_STRENGTH);
    }
}
