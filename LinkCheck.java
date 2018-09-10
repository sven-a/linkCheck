package linkCheck;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class LinkCheck {
	final static String version = "1.1";

	public static void main(String[] args) {

		LinkCheckGUI mygui = new LinkCheckGUI("LinkCheck v" + version);
		
		KeyListener enterPressed = new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					ButtonAction actionOnClick = new ButtonAction(mygui);
					actionOnClick.start();
				}
			}
		};
		
		mygui.statusBar.setEditable(true);
		mygui.statusBar.addKeyListener(enterPressed);
		
		
		ActionListener clickRun = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ButtonAction actionOnClick = new ButtonAction(mygui);
				actionOnClick.start();
			}
		};
		mygui.runButton.addActionListener(clickRun);
	}
}
