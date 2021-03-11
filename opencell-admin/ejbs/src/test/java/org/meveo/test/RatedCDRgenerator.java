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

package org.meveo.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RatedCDRgenerator implements Runnable {

	String fileName;
	long nbRecords, shift;
	long startTime;
	
	private static final Logger log = LoggerFactory.getLogger(RatedCDRgenerator.class);

	public RatedCDRgenerator(String fileName, long nbRecords, long shift,
			long time) {
		this.fileName = fileName;
		this.nbRecords = nbRecords;
		this.shift = shift;
		this.startTime = time;
	}

	@Override
	public void run() {
		long time = System.currentTimeMillis() + shift * 100;
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileOutputStream(fileName));
			StringBuffer sb = new StringBuffer();
			for (long i = 0; i < nbRecords; i++) {
				time += 100;
				long i3 = i % 3;
				sb.setLength(0);
				sb.append(new Date(time));
				sb.append(";MSISDN1;");
				sb.append(i % 500000L + ";");
				sb.append((i3 == 0 ? "SMS;" : (i3 == 1 ? "VOICE;" : "DATA;")));
				sb.append(Math.random() * 10);
				out.println(sb.toString());
			}
		} catch (FileNotFoundException e) {
			log.error("error = {}", e);
		} finally {
			if (out != null) {
				out.close();
			}
		}

	}

	public static void main(String[] args) {
		// generate 1 million CDR in 10 files of 100 000 records
		long nbCDR = 10000000L;
		long nbThread = Long.parseLong(args[0]);
		long time = System.currentTimeMillis();
		for (int i = 0; i < nbThread; i++) {
			RatedCDRgenerator generator = new RatedCDRgenerator("/tmp/ratedCDR"
					+ i + ".csv", nbCDR / nbThread, i * nbCDR / nbThread, time);
			Thread t = new Thread(generator);
			t.start();
		}
	}
}
