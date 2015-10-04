import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import net.sf.vfsjfilechooser.VFSJFileChooser;
import net.sf.vfsjfilechooser.VFSJFileChooser.RETURN_TYPE;
import net.sf.vfsjfilechooser.VFSJFileChooser.SELECTION_MODE;
import net.sf.vfsjfilechooser.accessories.DefaultAccessoriesPanel;
import net.sf.vfsjfilechooser.utils.VFSUtils;

import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Ganesh
 *
 */
public class DownloadFrame extends FTPTest implements PropertyChangeListener {

	private JFrame frmDownloadManager;

	private JFileChooser fileChooser;

	private JTextArea  fileDestination;

	private JTextArea  serverFileLocation;

	private JButton btnBrowse;

	private JButton btnBrowseLocal;

	private JRadioButton rdbtnAscii;

	private DownloadService downloadFTP;

	private VFSJFileChooser vfsfileChooser;
	
	private FTPTest ftpObj;
	private JLabel lblNewLabel;
	private JLabel downloadPercentage;
	
	private String percentCompleted="";

	/**
	 * Create the application.
	 */
	public DownloadFrame() {
		initialize();
		frmDownloadManager.setVisible(true);
		frmDownloadManager.setLocationRelativeTo(null);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDownloadManager = new JFrame();
		frmDownloadManager.setTitle("Download Manager");
		frmDownloadManager.getContentPane().setBackground(SystemColor.inactiveCaption);
		frmDownloadManager.setBounds(100, 100, 593, 424);
		frmDownloadManager.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmDownloadManager.getContentPane().setLayout(null);

		JLabel lblDownloadFileLocation = new JLabel("File Location on Server :");
		lblDownloadFileLocation.setBounds(10, 24, 151, 29);
		frmDownloadManager.getContentPane().add(lblDownloadFileLocation);

		serverFileLocation = new JTextArea();
		serverFileLocation.setBounds(171, 26, 256, 23);
		frmDownloadManager.getContentPane().add(serverFileLocation);

		JLabel lblDestinationToSave = new JLabel("Destination to save file :");
		lblDestinationToSave.setBounds(10, 100, 143, 29);
		frmDownloadManager.getContentPane().add(lblDestinationToSave);

		fileDestination = new JTextArea();
		fileDestination.setBounds(171, 102, 261, 23);
		frmDownloadManager.getContentPane().add(fileDestination);

		JLabel lblSelectTransferMode = new JLabel("Select transfer  mode");
		lblSelectTransferMode.setBounds(23, 182, 130, 14);
		frmDownloadManager.getContentPane().add(lblSelectTransferMode);


		rdbtnAscii = new JRadioButton("ASCII");
		rdbtnAscii.setBounds(171, 178, 109, 23);
		frmDownloadManager.getContentPane().add(rdbtnAscii);

		JRadioButton rdbtnBinary = new JRadioButton("Binary");
		rdbtnBinary.setBounds(323, 178, 109, 23);
		frmDownloadManager.getContentPane().add(rdbtnBinary);


		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(rdbtnAscii);
		buttonGroup.add(rdbtnBinary);
		rdbtnBinary.setSelected(true);

		JButton btnNewButton = new JButton("Start Download");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				buttonStartDownloadClicked(e);

			}
		});
		btnNewButton.setBackground(Color.GRAY);
		btnNewButton.setBounds(218, 248, 172, 23);
		frmDownloadManager.getContentPane().add(btnNewButton);

		btnBrowse = new JButton("Browse Server");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				downloadPercentage.setText("");
				lblNewLabel.setVisible(false);

				//invoke this method get the remote directory listing using Apache Virtual File System
				vfsFileChooser(e);
		}
		});
		btnBrowse.setBounds(447, 24, 120, 29);
		frmDownloadManager.getContentPane().add(btnBrowse);

		btnBrowseLocal = new JButton("Browse Local");
		btnBrowseLocal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				downloadPercentage.setText("");
				lblNewLabel.setVisible(false);

				try {
					browseButtonActionListener(e);
				} catch (IOException eIO) {
					eIO.printStackTrace();
				}
			}
		});
		btnBrowseLocal.setBounds(447, 100, 120, 29);
		frmDownloadManager.getContentPane().add(btnBrowseLocal);
		
		lblNewLabel = new JLabel("Transfer Status:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel.setBounds(219, 302, 94, 35);
		frmDownloadManager.getContentPane().add(lblNewLabel);
		lblNewLabel.setVisible(false);
		
		
		downloadPercentage = new JLabel("abc");
		downloadPercentage.setFont(new Font("Tahoma", Font.BOLD, 15));
		downloadPercentage.setBounds(323, 300, 72, 35);
		frmDownloadManager.getContentPane().add(downloadPercentage);
		downloadPercentage.setText("");
		
	}
	
	
	
	/**
	 * Method called to get the file locations from server and local and invokes the upload service
	 * 
	 * @param e   Action event mouse click
	 */
	protected void buttonStartDownloadClicked(ActionEvent e) {
		String serverFilePath = serverFileLocation.getText();
		String downloadlocation = fileDestination.getText();
		
		if(downloadlocation.contains(".") == true){
			
		String transferType = "";
		

		if(rdbtnAscii.isSelected()){
			transferType = "ASCII";
		}
		else{
			transferType = "BINARY";
		}
		
		lblNewLabel.setVisible(true);
		
		downloadFTP = new DownloadService(serverFilePath,downloadlocation,transferType);
		

		//add a listener to track any change in this object's progress value in the background swing worker.
		downloadFTP.addPropertyChangeListener(this);
		downloadFTP.execute();
		
		}
		else{
			
			JOptionPane.showMessageDialog(null,
					"Please enter a filename with extension. ", "Message",
					JOptionPane.INFORMATION_MESSAGE);
			
			fileDestination.setText("");
		}
		
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();

			percentCompleted = String.valueOf(progress);
			downloadPercentage.setText(percentCompleted+"%");
		}
	}

	/**
	 * @param e The action event for which the method was called
	 */
	protected void vfsFileChooser(ActionEvent e) {

		if ((e.getSource().equals(btnBrowse))) {		

			ftpObj = new FTPTest();
			final String userName;
			final String passCode;
			userName = ftpObj.getUser();
			passCode = ftpObj.getPassword();
			String server = FTPTest.getServer();
			int port = FTPTest.getPort();
			String portString="";
			if((port!=21)&&(port!=990)){
				portString = ":"+String.valueOf(port).trim();
			}
			String URIBuilder = "ftp://"+userName+":"+passCode+"@"+server+""+portString+"/";				

			vfsfileChooser = new VFSJFileChooser(URIBuilder);

			vfsfileChooser.setAccessory(new DefaultAccessoriesPanel(vfsfileChooser));
			vfsfileChooser.setFileHidingEnabled(false);
			vfsfileChooser.setMultiSelectionEnabled(false);
			vfsfileChooser.setFileSelectionMode(SELECTION_MODE.FILES_ONLY);
			
			RETURN_TYPE selection = vfsfileChooser.showOpenDialog(null);

			if (selection == RETURN_TYPE.APPROVE)
			{
				final org.apache.commons.vfs.FileObject myFileObject = vfsfileChooser.getSelectedFile();

				//Getting the absolute path from the remote file object
				String remotefilePath = VFSUtils.getFriendlyName(myFileObject.toString());	
				String target = FTPTest.getServer();
				
				if(remotefilePath.contains("ftp://"+target)){
					remotefilePath = remotefilePath.replace("ftp://"+target, "/");
				}
				serverFileLocation.setText(remotefilePath);
			}

			else{
				serverFileLocation.setText("");
			}
			
			vfsfileChooser.setSelectedFile(null);
		}


	}


	public void browseButtonActionListener(java.awt.event.ActionEvent event) throws IOException{

		if (event.getSource().equals(btnBrowseLocal)) {
			fileChooser = new JFileChooser();
		}

		int userAction = fileChooser.showOpenDialog(null);

		if (userAction == JFileChooser.APPROVE_OPTION) {

			if(event.getSource().equals(btnBrowseLocal)){
				fileDestination.setText(fileChooser.getSelectedFile().getPath());
			}

		} else {
			if(event.getSource().equals(btnBrowseLocal)){       		
				fileDestination.setText("");}
		}

		//Initialise filechooser to null incase the user selects again
		fileChooser.setSelectedFile(null);
	}

}
