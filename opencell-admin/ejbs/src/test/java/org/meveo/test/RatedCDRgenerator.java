package org.meveo.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RatedCDRgenerator implements Runnable {

	String fileName;
	long nbRecords, shift;
	long startTime;

	public RatedCDRgenerator(String fileName, long nbRecords, long shift,
			long time) {
		this.fileName = fileName;
		this.nbRecords = nbRecords;
		this.shift = shift;
		this.startTime = time;
	}

	@Override
	public void run() {
		long time = shift * 100;
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileOutputStream(fileName));
			StringBuffer sb = new StringBuffer();
			for (long i = 0; i < nbRecords; i++) {
				time += 100;
				long i3 = i % 3;
				sb.setLength(0);
				sb.append(LocalDateTime.now().plus(time, ChronoUnit.MILLIS).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
				sb.append(";").append(Math.random() * 10);
				sb.append(";DEMO.CLASSIC;");
				sb.append(i % 500000L + ";");
				sb.append((i3 == 0 ? "SMS;" : (i3 == 1 ? "VOICE;" : "DATA;")));
				out.println(sb.toString());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}

	}

	public static void main(String[] args) {
		// generate 1 million CDR in 10 files of 100 000 records
		long nbCDR = 10000000L;
		long nbThread = 100L;
		long time = System.currentTimeMillis();
		for (int i = 0; i < nbThread; i++) {
			RatedCDRgenerator generator = new RatedCDRgenerator("C:/tmp/CDR/CDR_RANDOM_"
					+ i + ".csv", nbCDR / nbThread, i * nbCDR / nbThread, time);
			Thread t = new Thread(generator);
			t.start();
		}
	}
}
