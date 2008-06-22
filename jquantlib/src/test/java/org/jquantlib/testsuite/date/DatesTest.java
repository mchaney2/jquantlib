/*
 Copyright (C) 2008 Srinivas Hasti

 This source code is release under the BSD License.
 
 This file is part of JQuantLib, a free-software/open-source library
 for financial quantitative analysts and developers - http://jquantlib.org/

 JQuantLib is free software: you can redistribute it and/or modify it
 under the terms of the JQuantLib license.  You should have received a
 copy of the license along with this program; if not, please email
 <jquant-devel@lists.sourceforge.net>. The license is also available online at
 <http://www.jquantlib.org/index.php/LICENSE.TXT>.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the license for more details.
 
 JQuantLib is based on QuantLib. http://quantlib.org/
 When applicable, the original copyright notice follows this notice.
 */

package org.jquantlib.testsuite.date;

import static org.junit.Assert.fail;

import org.jquantlib.time.IMM;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;
import org.jquantlib.time.Weekday;
import org.jquantlib.util.Date;
import org.jquantlib.util.DateFactory;
import org.jquantlib.util.DateParser;
import org.jquantlib.util.Month;
import org.jquantlib.util.StopClock;
import org.junit.Test;

/**
 * Test various Dates
 * 
 * @author Srinivas Hasti
 * 
 */
public class DatesTest {

    static private final String IMMcodes[] = { "F0", "G0", "H0", "J0", "K0", "M0", "N0", "Q0", "U0", "V0", "X0", "Z0",
            "F1", "G1", "H1", "J1", "K1", "M1", "N1", "Q1", "U1", "V1", "X1", "Z1", "F2", "G2", "H2", "J2", "K2", "M2",
            "N2", "Q2", "U2", "V2", "X2", "Z2", "F3", "G3", "H3", "J3", "K3", "M3", "N3", "Q3", "U3", "V3", "X3", "Z3",
            "F4", "G4", "H4", "J4", "K4", "M4", "N4", "Q4", "U4", "V4", "X4", "Z4", "F5", "G5", "H5", "J5", "K5", "M5",
            "N5", "Q5", "U5", "V5", "X5", "Z5", "F6", "G6", "H6", "J6", "K6", "M6", "N6", "Q6", "U6", "V6", "X6", "Z6",
            "F7", "G7", "H7", "J7", "K7", "M7", "N7", "Q7", "U7", "V7", "X7", "Z7", "F8", "G8", "H8", "J8", "K8", "M8",
            "N8", "Q8", "U8", "V8", "X8", "Z8", "F9", "G9", "H9", "J9", "K9", "M9", "N9", "Q9", "U9", "V9", "X9", "Z9" };

    public DatesTest() {
		System.out.println("\n\n::::: "+this.getClass().getSimpleName()+" :::::");
    }
    
    @Test
    public void immDates() {
        System.out.println("Testing imm dates. It may take several minutes when Cobertura reports are generated!!!");

        Date counter = DateFactory.getFactory().getMinDate();

        // 10 years of futures must not exceed Date::maxDate
        Period period = new Period(-10, TimeUnit.YEARS);
        Date last = DateFactory.getFactory().getMaxDate().adjust(period);

        IMM iMM = IMM.getDefaultIMM();

        StopClock clock = new StopClock();
        clock.startClock();
        while (counter.le(last)) {

            Date immDate = iMM.nextDate(counter, false);
            
            // check that imm is greater than counter
            if (immDate.le(counter))
            	fail("\n  " + immDate.getWeekday() + " " + immDate + " is not greater than " + counter.getWeekday() + " " + counter);

            // check that imm is an IMM date
            if (!iMM.isIMMdate(immDate, false))
            	fail("\n  " + immDate.getWeekday() + " " + immDate + " is not an IMM date (calculated from " + counter.getWeekday() + " " + counter + ")");

            // check that imm is <= to the next IMM date in the main cycle
            if (immDate.gt(iMM.nextDate(counter, true)))
            	fail("\n  " + immDate.getWeekday() + " " + immDate + " is not less than or equal to the next future in the main cycle " + IMM.getDefaultIMM().nextDate(counter, true));

            //
            // COMMENTED AT SOURCE QuantLib 0.8.1
            //
            // // check that if counter is an IMM date, then imm==counter
            // if (IMM::isIMMdate(counter, false) && (imm!=counter))
            // fail("\n "
            // + counter.weekday() + " " + counter
            // + " is already an IMM date, while nextIMM() returns "
            // + IMM.getDefaultIMM().weekday() + " " + imm);

            // check that for every date IMMdate is the inverse of IMMcode
            if (!iMM.date(iMM.code(immDate), counter).equals(immDate))
            	fail("\n  " + iMM.code(immDate) + " at calendar day " + counter + " is not the IMM code matching " + immDate);

            // check that for every date the 120 IMM codes refer to future dates
            for (int i = 0; i < 40; ++i) {
            	if (iMM.date(IMMcodes[i], counter).lt(counter))
            		fail("\n  " + iMM.date(IMMcodes[i], counter) + " is wrong for " + IMMcodes[i] + " at reference date " + counter);
            }

            counter.increment();
        }
        clock.stopClock();
        clock.log();
    }

    @Test
    public void consistencyCheck() {

        System.out.println("Testing dates...");

        int dyold = DateFactory.getFactory().getMinDate().getDayOfYear();
        int dold = DateFactory.getFactory().getMinDate().getDayOfMonth();
        int mold = DateFactory.getFactory().getMinDate().getMonth();
        int yold = DateFactory.getFactory().getMinDate().getYear();
        Weekday wdold = DateFactory.getFactory().getMinDate().getWeekday();

        Date minDate = DateFactory.getFactory().getMinDate().increment(1);
        Date maxDate = DateFactory.getFactory().getMaxDate();

        StopClock clock = new StopClock();
        clock.startClock();
        for (Date t = minDate; t.le(maxDate); t.increment()) {
            int dy = t.getDayOfYear();
            int d = t.getDayOfMonth();
            int m = t.getMonth();
            int y = t.getYear();
            Weekday wd = t.getWeekday();

            // check if skipping any date
            if (!((dy == dyold + 1) 
            		|| (dy == 1 && dyold == 365 && !DateFactory.getFactory().isLeap(yold)) 
            		|| (dy == 1 && dyold == 366 && DateFactory.getFactory().isLeap(yold))))
            	fail("wrong day of year increment: \n" 
            			+ "    date: " + t + "\n" 
            			+ "    day of year: " + dy + "\n"
                        + "    previous:    " + dyold);

            dyold = dy;

            if (!((d == dold + 1 && m == mold && y == yold) || (d == 1 && m == mold + 1 && y == yold) || (d == 1 && m == 1 && y == yold + 1)) )
            	fail("wrong day,month,year increment: \n" 
            			+ "    date: " + t + "\n" 
            			+ "    day,month,year: " + d + "," + m + "," + y + "\n" 
            			+ "    previous:       " + dold + "," + mold + "," + yold);

            dold = d;
            mold = m;
            yold = y;

            // check month definition
            if ((m < 1 || m > 12))
            	fail("invalid month: \n" + "    date:  " + t + "\n" + "    month: " + m);

            // check day definition
            if ((d < 1))
            	fail("invalid day of month: \n" + "    date:  " + t + "\n" + "    day: " + d);

            if (!((m == 1 && d <= 31)
                    || (m == 2 && d <= 28) || (m == 2 && d == 29 && DateFactory.getFactory().isLeap(y))
                    || (m == 3 && d <= 31) || (m == 4 && d <= 30) || (m == 5 && d <= 31) || (m == 6 && d <= 30)
                    || (m == 7 && d <= 31) || (m == 8 && d <= 31) || (m == 9 && d <= 30) || (m == 10 && d <= 31)
                    || (m == 11 && d <= 30) || (m == 12 && d <= 31)))
            fail("invalid day of month: \n" + "    date:  " + t + "\n" + "    day: " + d);

            // check weekday definition
            if (!((wd.toInteger() == wdold.toInteger() + 1) || (wd.toInteger() == 1 && wdold.toInteger() == 7)))
            	fail("invalid weekday: \n" 
            			+ "    date:  " + t + "\n" 
            			+ "    weekday:  " + wd + "\n"
            			+ "    previous: " + wdold);
            wdold = wd;
        }
        clock.stopClock();
        clock.log();

    }

    @Test
    public void isoDates() {
        System.out.println("Testing ISO dates...");
        StopClock clock = new StopClock();
        clock.startClock();
        String input_date = "2006-01-15";
        Date d = DateParser.parseISO(input_date);
        if ((d.getDayOfMonth() != 15) || (d.getMonth() != Month.JANUARY.toInteger()) || (d.getYear() != 2006))
        	fail("Iso date failed\n" 
        			+ " input date:    " + input_date + "\n" 
        			+ " day of month:  " + d.getDayOfMonth() + "\n" 
        			+ " month:         " + d.getMonth() + "\n" 
        			+ " year:          " + d.getYear());
        clock.stopClock();
        clock.log();
    }

}
