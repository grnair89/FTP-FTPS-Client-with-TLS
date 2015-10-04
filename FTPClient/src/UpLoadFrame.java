import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import net.sf.vfsjfilechooser.VFSJFileChooser;
import net.sf.vfsjfilechooser.VFSJFileChooser.RETURN_TYPE;
import net.sf.vfsjfilechooser.VFSJFileChooser.SELECTION_MODE;
import net.sf.vfsjfilechooser.accessories.DefaultAccessoriesPanel;
import net.sf.vfsjfilechooser.utils.VFSUtils;


/**
 * UpLoadFrame.java
 * 
 * This class display a window for the user to choose the upload file and the destination on the server to be saved.
 * 
 * @version 1.0
 * 
 * @author Ganesh Rajasekharan 
 * 
 */
public class UpLoadFrame extends FTPTest implements PropertyChangeListener {

	private JFrame frmUploadManager;

	private JFileChooser fileChooser;

	private JTextArea  serverCopyDestination;
	private JTextArea  localFileLocation;

	private JButton btnBrowse;
	private JButton btnBrowseRemote;

	private JRadioButton rdbtnAscii;

	private UploadService upService;

	private VFSJFileChooser vfsfileChooser;

	private FTPTest ftpObj;
	private String percentComnpleted = "";
	private JLabel percentCompletedLabel;
	
	private JLabel labelTransferStatus;

	/**
	 * Create the application.
	 */
	public UpLoadFrame() {
		initialize();
		frmUploadManager.setVisible(true);
		frmUploadManager.setLocationRelativeTo(null);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmUploadManager = new JFrame();
		frmUploadManager.setTitle("Upload Manager");
		frmUploadManager.getContentPane().setBackground(SystemColor.inactiveCaption);
		frmUploadManager.setBounds(100, 100, 593, 424);
		frmUploadManager.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmUploadManager.getContentPane().setLayout(null);
		frmUploadManager.setVisible(true);

		JLabel lblDownloadFileLocation = new JLabel("File Location on Local :");
		lblDownloadFileLocation.setBounds(10, 24, 130, 29);
		frmUploadManager.getContentPane().add(lblDownloadFileLocation);

		localFileLocation = new JTextArea();
		localFileLocation.setBounds(153, 26, 261, 23);
		frmUploadManager.getContentPane().add(localFileLocation);

		JLabel lblDestinationToSave = new JLabel("Upload Location :");
		lblDestinationToSave.setBounds(10, 100, 120, 29);
		frmUploadManager.getContentPane().add(lblDestinationToSave);

		serverCopyDestination = new JTextArea();
		serverCopyDestination.setBounds(153, 102, 261, 23);
		frmUploadManager.getContentPane().add(serverCopyDestination);

		JLabel lblSelectTransferMode = new JLabel("Select transfer  mode");
		lblSelectTransferMode.setBounds(10, 182, 130, 14);
		frmUploadManager.getContentPane().add(lblSelectTransferMode);


		rdbtnAscii = new JRadioButton("ASCII");
		rdbtnAscii.setBounds(153, 178, 109, 23);
		frmUploadManager.getContentPane().add(rdbtnAscii);

		JRadioButton rdbtnBinary = new JRadioButton("Binary");
		rdbtnBinary.setBounds(305, 178, 109, 23);
		frmUploadManager.getContentPane().add(rdbtnBinary);

		//Add both the transfer mode buttons to a button group object to toggle them one at a time
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(rdbtnAscii);
		buttonGroup.add(rdbtnBinary);
		rdbtnBinary.setSelected(true);

		JButton btnNewButton = new JButton("Start Upload");
		btnNewButton.addActionListener(new ActionListener() {

			//Call download method from class DownloadService on button click
			public void actionPerformed(ActionEvent e) {

				buttonStartUploadClicked(e);

			}
		});

		btnNewButton.setBackground(Color.GRAY);
		btnNewButton.setBounds(198, 230, 172, 23);
		frmUploadManager.getContentPane().add(btnNewButton);

		//Button for browsing local system files
		btnBrowse = new JButton("Browse Local");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

				
				labelTransferStatus.setVisible(false);
				percentCompletedLabel.setText("");
				
				//invoke this method get the remote directory listing using Apache Virtual File System
				//vfsFileChooser(e);
				try {
					localFileChooser(event);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnBrowse.setBounds(437, 24, 130, 29);
		frmUploadManager.getContentPane().add(btnBrowse);


		//Button for browsing remote server files
		btnBrowseRemote = new JButton("Browse Remote");
		btnBrowseRemote.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
				
				labelTransferStatus.setVisible(false);
				percentCompletedLabel.setText("");
				
				vfsFileChooser(event);
			}
		});
		btnBrowseRemote.setBounds(437, 100, 130, 29);
		frmUploadManager.getContentPane().add(btnBrowseRemote);



		labelTransferStatus = new JLabel("Transfer Status :");
		labelTransferStatus.setFont(new Font("Tahoma", Font.BOLD, 11));
		labelTransferStatus.setBounds(204, 289, 99, 29);
		frmUploadManager.getContentPane().add(labelTransferStatus);
		labelTransferStatus.setVisible(false);

		percentCompletedLabel = new JLabel("New label");
		percentCompletedLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		percentCompletedLabel.setBounds(313, 281, 68, 41);
		frmUploadManager.getContentPane().add(percentCompletedLabel);
		percentCompletedLabel.setText("");
	}

	/**
	 * Method called to get the file locations from server and local and invokes the upload service
	 * 
	 * @param e   Action event mouse click
	 */
	protected void buttonStartUploadClicked(ActionEvent e) {
		//show the transfer status label only when user click upload
		labelTransferStatus.setVisible(true);
		String localLocation = localFileLocation.getText();
		String remoteLocation = serverCopyDestination.getText();
		String transferType = "";

		if(rdbtnAscii.isSelected()){
			transferType = "ASCII";
		}
		else{
			transferType = "BINARY";
		}
		upService = new UploadService(remoteLocation, localLocation, transferType);



		//add a listener to watch out for change in this object's progress value in the background swing worker.
		upService.addPropertyChangeListener(this);
		upService.execute();
		
	}


	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 * 
	 * 
	 * Update the value of percentCompletedLabel when the property of progress changes in the background thread
	 * 
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();

			percentComnpleted = String.valueOf(progress);
			percentCompletedLabel.setText(percentComnpleted+"%");
		}
	}

	/**
	 * This method enables the user to browse the remote file system using  Apache VFS. 
	 * 
	 * @param e The action event for which the method was called
	 */
	protected void vfsFileChooser(ActionEvent e) {

		if ((e.getSource().equals(btnBrowseRemote))) {		

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
			
			//Build a URI for logging in and to display the remote file system's file chooser using vfs
			String URIBuilder = "ftp://"+userName+":"+passCode+"@"+server+""+portString+"/";

			vfsfileChooser = new VFSJFileChooser(URIBuilder);

			vfsfileChooser.setAccessory(new DefaultAccessoriesPanel(vfsfileChooser));
			vfsfileChooser.setFileHidingEnabled(false);
			vfsfileChooser.setMultiSelectionEnabled(false);
			vfsfileChooser.setFileSelectionMode(SELECTION_MODE.FILES_ONLY);

			RETURN_TYPE selection = vfsfileChooser.showOpenDialog(null);
			
			//if user selected a file by clicking open tab
			if (selection == RETURN_TYPE.APPROVE)
			{
				final org.apache.commons.vfs.FileObject myFileObject = vfsfileChooser.getSelectedFile();

				//Getting the absolute path from the remote file object
				String remotefilePath = VFSUtils.getFriendlyName(myFileObject.toString());

				String target = FTPTest.getServer();

				//formatting the path style
				if(remotefilePath.contains("ftp://"+target)){
					remotefilePath = remotefilePath.replace("ftp://"+target, "/");
				}
				serverCopyDestination.setText(remotefilePath);
			}

			else{
				serverCopyDestination.setText("");
			}

			vfsfileChooser.setSelectedFile(null);
		}


	}

	/**
	 * This method enable the user to browse the local file system and to choose a file.
	 * 
	 * @param event  The action event which triggered this method call
	 */
	public void localFileChooser(ActionEvent event) throws IOException{

		fileChooser = new JFileChooser();

		int userAction = fileChooser.showOpenDialog(null);

		if (userAction == JFileChooser.APPROVE_OPTION) {
			localFileLocation.setText(fileChooser.getSelectedFile().getPath());
		} 
		else {localFileLocation.setText("");}

		//Initialize file chooser to null if the user selects again
		fileChooser.setSelectedFile(null);
	}
}
