package arenx.networktool;

import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

	private static Logger logger = LoggerFactory.getLogger(Utils.class);

	public static void sleep(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			logger.warn("interrupted while sleeping");
		}
	}

	public static String readableNetworkSpeed(long size) {
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B/s", "KB/s", "MB/s", "GB/s", "TB/s" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public static String readableSize(long size) {
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

}
