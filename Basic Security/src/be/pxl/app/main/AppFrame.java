package be.pxl.app.main;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class AppFrame extends JFrame {

	//Setting a fixed width and height
	private static final int WIDTH = 640;
	private static final int HEIGHT = 480;

	public JPanel pnl;
	
	public AppFrame() {
		//super();
		
		pnl = new JPanel();
		pnl.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		//pnl.setLayout(new BorderLayout());
		
		add(pnl);
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setTitle("Application");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
}
