package linkCheck;

import java.net.URL;

public class StringFormatter {

	static public String urlToHyperlink(String inputString) {
		return "<a href=" + inputString + "> " + inputString + "</a>";
	}

	public static String noSuffixHTML(String inputURL) {
		String resultURL = inputURL.trim();
		if (resultURL.endsWith("#")) {
			resultURL = resultURL.substring(0, resultURL.length() - 1);
		}
		if (resultURL.endsWith("/")) {
			resultURL = resultURL.substring(0, resultURL.length() - 1);
		}
		if (resultURL.endsWith(".html")) {
			resultURL = resultURL.substring(0, resultURL.length() - 5);
		}
		if (resultURL.endsWith(".htm")) {
			resultURL = resultURL.substring(0, resultURL.length() - 4);
		}

		return resultURL;
	}

	public static String cleanURL(String inputURL) {
		String resultURL = inputURL;
		if (resultURL.startsWith("www")) {
			resultURL = "https://" + resultURL;
		}
		resultURL = noSuffixHTML(resultURL);
		// resultURL.trim();

		return resultURL;
	}

	public static String[] cleanURLArray(URL[] urls) {
		String[] results = new String[urls.length];
		for (int i = 0; i < urls.length; i++) {
			results[i] = cleanURL(urls[i].toString());
		}
		return results;
	}

	public static String[] cleanURLArray(String[] urls) {
		String[] results = new String[urls.length];
		for (int i = 0; i < urls.length; i++) {
			results[i] = cleanURL(urls[i]);
		}
		return results;
	}

}