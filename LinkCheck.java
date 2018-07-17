package linkCheck;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class LinkCheck {
	final static String version = "0.9.1";

	public static void main(String[] args) {

		LinkCheckGUI mygui = new LinkCheckGUI("LinkCheck v" + version);

		ActionListener clickRun = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ButtonAction actionOnClick = new ButtonAction(mygui);
				actionOnClick.start();
			}
		};
		
		KeyListener enterPressed = new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					ButtonAction actionOnClick = new ButtonAction(mygui);
					actionOnClick.start();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			
		};
		
		mygui.statusBar.setEditable(true);
		mygui.runButton.addActionListener(clickRun);
		mygui.statusBar.addKeyListener(enterPressed);
	
	}
}