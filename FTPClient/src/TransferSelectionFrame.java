import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 * TransferSelectionFrame.java
 * 
 * This class displays a window for the user to make a selection between download and upload modes.
 * Releases all socket connections on window close.
 * 
 * @version 1.0
 * 
 * @author Ganesh Rajasekharan 
 * 
 */
public class TransferSelectionFrame extends FTPTest {

	private JFrame frmSelectTransferMode;

	/**
	 * Create the application.
	 */
	public TransferSelectionFrame() {
		initialize();
		frmSelectTransferMode.setVisible(true);
		frmSelectTransferMode.setLocationRelativeTo(null); 
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSelectTransferMode = new JFrame();
		frmSelectTransferMode.getContentPane().setFont(new Font("Tahoma", Font.BOLD, 13));
		frmSelectTransferMode.getContentPane().setBackground(SystemColor.inactiveCaption);
		frmSelectTransferMode.setTitle("Select Transfer Mode");
		frmSelectTransferMode.setBounds(100, 100, 593, 424);
		frmSelectTransferMode.getContentPane().setLayout(null);

		JButton btnNewButton = new JButton("Download Files");

		//if btnNewButton is clicked display download options
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				new DownloadFrame();
			}
		});
		btnNewButton.setBackground(SystemColor.activeCaption);
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 13));
		btnNewButton.setBounds(197, 101, 195, 66);
		frmSelectTransferMode.getContentPane().add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Upload Files");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				new UpLoadFrame();
			}
		});
		btnNewButton_1.setBackground(SystemColor.activeCaption);
		btnNewButton_1.setFont(new Font("Tahoma", Font.BOLD, 13));
		btnNewButton_1.setBounds(197, 237, 195, 66);
		frmSelectTransferMode.getContentPane().add(btnNewButton_1);


		frmSelectTransferMode.addWindowListener(new WindowAdapter() {

			/* (non-Javadoc)
			 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
			 * 
			 * Close the active connection on window close
			 */
			@Override
			public void windowClosing(WindowEvent e) {

				try {
					if (ftpObj.isConnected()) {
						ftpObj.logout();
						ftpObj.disconnect();
						
						JOptionPane.showMessageDialog(null,
								"FTP connection to the server has been closed.", "Message",
								JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (IOException exIO) {
					exIO.printStackTrace();
				}
				
				frmSelectTransferMode.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
		
	}
}
