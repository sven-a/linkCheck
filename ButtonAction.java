package linkCheck;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

import org.htmlparser.beans.LinkBean;

public class ButtonAction extends Thread {
	
	

	LinkCheckGUI mygui;
	// WebCrawler crawler;

	ButtonAction(LinkCheckGUI newGUI) {
		this.mygui = newGUI;
	}

	@Override
	public void run() {

		// deactivate input line and runButton, enable stopButton:
		mygui.runButton.setEnabled(false);
		mygui.statusBar.setEditable(false);
		mygui.stopButton.setEnabled(true);

		ActionListener stopListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Abbruch angefordert");
				interrupt();
				// mygui.stopButton.setEnabled(false);
			}

		};

		mygui.stopButton.addActionListener(stopListener);

		// read URL from statusBar

		String urlFromInput = StringFormatter.cleanURL(mygui.statusBar.getText());

		// When "recursive" is selected, identify all subpages

		try {
			if (mygui.recursiveBox.getState()) {
				ArrayList<String> crawlPages = new ArrayList<String>();
				// crawlPages = crawler.getAllSubPages(urlFromInput);
				crawlPages = getAllSubPages(urlFromInput);

				if (crawlPages.isEmpty()) {
					mygui.displayText("no subpages on " + urlFromInput);
				} else {

					mygui.displayText(crawlPages.size() + " subpages found on " + urlFromInput + "<br>");

					for (String singleURL : crawlPages) {
						mygui.addResultsText(singleURL + "<br>");
					}
				}

				// check the initial URL:
				// ErrorsAndRedirects errorsRedirects = crawler.checkPages(urlFromInput);
				ErrorsAndRedirects errorsRedirects = checkPages(urlFromInput);

				// Show results in window
				mygui.initialiseResults();
				mygui.addResultsText(urlFromInput, errorsRedirects);

				// check all links on all subpages

				for (String singleURL : crawlPages) {
					if (!interrupted()) {
						// errorsRedirects = crawler.checkPages(singleURL);
						errorsRedirects = checkPages(singleURL);
						mygui.addResultsText(singleURL, errorsRedirects);
					}
				}

			} else {
				// Code for the non-recursive search
				ErrorsAndRedirects errorsRedirects = null;
				// errorsRedirects = crawler.checkPages(urlFromInput);
				errorsRedirects = checkPages(urlFromInput);

				// Show results in window
				mygui.initialiseResults();
				mygui.addResultsText(urlFromInput, errorsRedirects);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		mygui.runButton.setEnabled(true);
		// Write the initially entered URL back into the statusBar
		mygui.writeStatusSafely(urlFromInput);
		mygui.statusBar.setEditable(true);
		mygui.stopButton.setEnabled(false);
	}

	public ArrayList<String> getAllSubPages(String urlToCrawl) {
		ArrayList<String> subPages = new ArrayList<String>();
		ArrayList<String> unCrawledPages = new ArrayList<String>();

		String currentURL;
		unCrawledPages.add(urlToCrawl);
		subPages.add(urlToCrawl);
		mygui.writeStatusSafely("Collecting subpages of " + urlToCrawl);

		while (!unCrawledPages.isEmpty()) {
			currentURL = unCrawledPages.get(0);

			if (!interrupted()) {

				try {
					URL[] urls = getLinks(currentURL);
					String[] urlStrings = StringFormatter.cleanURLArray(urls);
					for (String singleURL : urlStrings) {
						String URLString = singleURL;
						if (URLString.startsWith(currentURL) && (!subPages.contains(URLString))) {
							subPages.add(URLString);
							unCrawledPages.add(URLString);
							mygui.writeProgressSafely((subPages.size() - 1) + "Subpages found: ");
						}
					}
				} catch (Exception e) {
					System.out.println("Error with URL: " + currentURL);
					e.printStackTrace();
				}
			} else {
				interrupt();
				subPages = new ArrayList<String>();
				return subPages;
			}

			unCrawledPages.remove(currentURL);
		}

		subPages.remove(urlToCrawl);

		return subPages;
	}

	public ErrorsAndRedirects checkPages(String urlToCheck) throws InterruptedException {
		Hashtable<String, Integer> errorPages = new Hashtable<String, Integer>();
		Hashtable<String, String> redirectPages = new Hashtable<String, String>();

		try {

			// Use getLinks to return array of links from website specified at command-line

			URL[] urls = getLinks(urlToCheck);

			this.mygui.writeStatusSafely("Checking " + urls.length + " links on: " + urlToCheck + "\n\n");

			// Create an array of same size to store Response Codes
			int[] codes = new int[urls.length];

			// Iterate through the Urls
			for (int i = 0; i < urls.length; i++) {
				if (!interrupted()) {

					try {
						mygui.writeProgressSafely(i + " of " + urls.length);

						// Connect to the URL and add Response Code to Codes Array
						HttpURLConnection connect = (HttpURLConnection) urls[i].openConnection();
						codes[i] = connect.getResponseCode();
						if (codes[i] != 200) {
							// Redirects
							if (codes[i] == 301) {
								redirectPages.put(urls[i].toString(), getDestinationURL(urls[i]).toString());

								// Everything else
							} else {
								errorPages.put(urls[i].toString(), codes[i]);
							}

						}

						connect.disconnect();
						mygui.writeProgressSafely("");
					}

					catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					interrupt();
					return null;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return new ErrorsAndRedirects(errorPages, redirectPages);
	}

	public static URL[] getLinks(String url) {
		// Use LinkBean class from HTMLParser to get array of links from a page
		LinkBean lb = new LinkBean();
		lb.setURL(url);
		URL[] urls = lb.getLinks();
		return urls;
	}

	public static URL getDestinationURL(URL url) {
		// returns the destination URL in case of a redirect
		try {
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setInstanceFollowRedirects(false);
			con.connect();

			String Location = con.getHeaderField("Location");
			if (Location.startsWith("/")) {
				Location = url.getProtocol() + "://" + url.getHost() + Location;
			}
			//return getDestinationURL(new URL(Location));
			return new URL(Location);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}

}
