package linkCheck;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LinkCheck {
	final static String version = "0.9";

	public static void main(String[] args) {

		LinkCheckGUI mygui = new LinkCheckGUI();

		ActionListener clickRun = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ButtonAction actionOnClick = new ButtonAction(mygui);
				actionOnClick.start();
			}
		};

		mygui.statusBar.setEditable(true);
		mygui.runButton.addActionListener(clickRun);
	}
}
