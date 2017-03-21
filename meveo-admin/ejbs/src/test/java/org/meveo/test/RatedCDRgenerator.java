package org.meveo.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Date;

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
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
		System.out.println("Time :" + (System.currentTimeMillis() - startTime));

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
