package linkCheck;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

public class LinkCheckGUI extends JFrame {
	JTextField statusBar;
	JTextField progressBar;
	JTextField progressBarRight;
	JEditorPane resultsField;
	ScrollPane scrolling;
	String resultsHTML;
	JButton runButton;
	JButton stopButton;
	Checkbox recursiveBox;
	Checkbox onlyErrorsBox;
	
	// keep an ArrayList of the links that work. This speeds up the recursive crawling immensely. By keeping this list in the GUI it is kept from program start to end.
	public ArrayList<String> goodLinks;

	private static final long serialVersionUID = 1L;

	LinkCheckGUI(String text) {
		goodLinks = new ArrayList<String>();
		JFrame window = new JFrame(text);
		
		// java - get screen size using the Toolkit class
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		window.setSize(screenSize.width / 2, screenSize.height / 2);
		window.setLocationRelativeTo(null);
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// DEBUG System.out.println("Closed");
				e.getWindow().dispose();
			}
		});

		// Top panel contains the status bar and the control panel (run and stop button and
		// recursive checkbox)
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

		statusBar = new JTextField();
		statusBar.setEditable(false);

		// Control panel 
		runButton = new JButton("run");
		stopButton = new JButton("stop");
		stopButton.setEnabled(false); // Stop button will only be enabled, when crawler is running
		
		JPanel checkBoxesPanel = new JPanel();
		checkBoxesPanel.setLayout(new BoxLayout(checkBoxesPanel, BoxLayout.Y_AXIS));
		recursiveBox = new Checkbox("recursive Search");
		onlyErrorsBox = new Checkbox("show only pages with errors");
		
		checkBoxesPanel.add(recursiveBox);
		checkBoxesPanel.add(onlyErrorsBox);

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		controlPanel.add(runButton);
		controlPanel.add(stopButton);
		
		controlPanel.add(checkBoxesPanel);

		topPanel.add(statusBar);
		topPanel.add(controlPanel);

		progressBar = new JTextField();
		progressBar.setEditable(false);
		progressBarRight = new JTextField();
		progressBarRight.setEditable(false);
		
		JPanel progressBars = new JPanel();
		progressBars.setLayout(new BoxLayout(progressBars, BoxLayout.X_AXIS));
		progressBars.add(progressBar);
		progressBars.add(Box.createHorizontalGlue());
		progressBars.add(progressBarRight);
		
		
		// Add the top panel bar at the top
		window.add(topPanel, BorderLayout.NORTH);

		// Create a JEditorPane and wrap it in a ScrollPane
		resultsField = new JEditorPane();
		resultsField.setEditorKit(new HTMLEditorKit());
		resultsField.setEditable(false);
			
		scrolling = new ScrollPane();
		scrolling.add(resultsField);
		window.add(scrolling);

		// Add the progress bar at the bottom
		window.add(progressBars, BorderLayout.SOUTH);
		
		
		// add a HyperlinkListener to enable clicking on the URLs in the results and open them in a browser
				resultsField.addHyperlinkListener(new HyperlinkListener() {
			        @Override
			        public void hyperlinkUpdate(HyperlinkEvent e) {
						if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) { // When one hyperlink is clicked
							if (Desktop.isDesktopSupported()) {	// check if java.awt.Desktop is supported
								try {
									Desktop.getDesktop().browse(e.getURL().toURI()); // try to open the URL in the System's default browser
								} catch (IOException e1) {
									progressBar.setText("An error occured.");
									e1.printStackTrace();
								} catch (URISyntaxException e1) {
									e1.printStackTrace();
									progressBar.setText("Not a valid hyperlink");
								}
							}
			            }
			        }
			    });
		

		window.setVisible(true);
	}

	public void addResultsText(String text) {
		resultsHTML += text;
		// System.out.println(resultsHTML);
		writeResultsSafely(resultsHTML);

	}

	public void addResultsText(String url, Hashtable<String, Integer> brokenLinks,
			Hashtable<String, Redirect> redirectLinks) {
		String resultsText = "<h2>" + StringFormatter.urlToHyperlink(url) + "</h2>";

		// list broken links

		if (brokenLinks.isEmpty()) {
			resultsText += "<h3 color=\"#008000\"> No broken Links </h3>";
		} else {

			resultsText += "<h3 color=\"#ff0000\"> broken Links: </h3>";
			Set<String> errorURLs = brokenLinks.keySet();
			for (String singleURL : errorURLs) {
				resultsText += brokenLinks.get(singleURL) + " " + singleURL + "<br>";
			}
		}

		// list redirects

		if (redirectLinks.isEmpty()) {
			resultsText += "<h3 color=\"#008000\"> No redirects </h3>";
		} else {
			resultsText += "<h3 color=\"#daad00\"> redirects: </h3>";
			Set<String> redirectURLs = redirectLinks.keySet();
			
			for (String singleURL : redirectURLs) {
				resultsText += redirectLinks.get(singleURL).getCode() + " " + singleURL + " ---> " + redirectLinks.get(singleURL).getDestination() + "<br>";
			}
		}

		resultsText += "<hr>";
		addResultsText(resultsText);
	}

	public void addResultsText(String url, ErrorsAndRedirects errorsRedirects) {
		if (errorsRedirects != null) {
			addResultsText(url, errorsRedirects.getErrorPages(), errorsRedirects.getRedirectPages());
		}
	}

	public void initialiseResults() {
		resultsHTML = "<HTML> <h1>Results</h1>";
		writeResultsSafely(resultsHTML);

	}

	public void displayText(String text) {
		resultsHTML = "<HTML>" + text;
		writeResultsSafely(resultsHTML);
	}

	public void writeStatusSafely(String text) {
		Runnable r = new Runnable() {
			public void run() {
				try {
					statusBar.setText(text);
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
		};

		SwingUtilities.invokeLater(r);
	}

	public void writeResultsSafely(String text) {
		Runnable r = new Runnable() {
			public void run() {
				try {
					resultsField.setText(text);
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
		};

		SwingUtilities.invokeLater(r);
	}

	public void writeProgressSafely(String newProgress) {

		Runnable r = new Runnable() {
			public void run() {
				try {
					progressBar.setText(newProgress);
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
		};

		SwingUtilities.invokeLater(r);
	}
	public void writeProgressRightSafely(String newProgress) {

		Runnable r = new Runnable() {
			public void run() {
				try {
					progressBarRight.setText(newProgress);
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
		};

		SwingUtilities.invokeLater(r);
	}
}