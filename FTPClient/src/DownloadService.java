import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JOptionPane;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;


/**
 * UserLoginFrame.java
 * 
 * This class contains fields for accepting the input related to login credentials from user.
 * 
 * @version 1.0
 * 
 * @author Ganesh Rajasekharan 
 * 
 */
public class DownloadService extends FTPTest {

	private String dwnloadStatus;
	private String serverFilePath;
	private String downloadFilePath;
	private String transferType;

	private static final int BUFFER_SIZE = 4096;

	private int percentCompleted = 0;

	private OutputStream outputStream;;

	/**
	 * Constructor for this class
	 * 
	 * @param transferType 
	 * @param user
	 * @param password
	 */
	public DownloadService(String remotePath, String downloadFilePath, String transferType){

		this.serverFilePath = remotePath;
		this.downloadFilePath = downloadFilePath;
		this.transferType = transferType;
	}


	/**
	 * @return the dwnloadStatus
	 */
	protected String getDwnloadStatus() {
		return dwnloadStatus;
	}

	/**
	 * @param dwnloadStatus the dwnloadStatus to set
	 */
	protected void setDwnloadStatus(String dwnloadStatus) {
		this.dwnloadStatus = dwnloadStatus;
	}


	//run a thread to handle download in the background
	protected String doInBackground() throws IOException{

		this.dwnloadStatus = downloadFTP(serverFilePath, downloadFilePath, transferType);


		String convertslash = serverFilePath.replace("\\", "\\\\");
		File downLoadfile = new File(convertslash); 
		String filename = downLoadfile.getName();

		//extract the file path from the absolute file path
		convertslash = convertslash.replaceAll("(.*\\/).*", "$1");

		//open an input stream to read from the file to be downloaded

		ftpObj.changeWorkingDirectory(convertslash);
		InputStream inputStream = ftpObj.retrieveFileStream(downLoadfile.getName());

		//FileInputStream fileinputStream = new FileInputStream(inputStream);


		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = -1;
		long totalBytesRead = 0;


		FTPFile remoteFile = ftpObj.mlistFile(serverFilePath);
		long fileSize = remoteFile.getSize(); 

		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
			totalBytesRead += bytesRead;
			percentCompleted = (int) ((totalBytesRead * 100) / fileSize);
			setProgress(percentCompleted);
		}

		inputStream.close();
		outputStream.close();


		if(percentCompleted == 100){

			JOptionPane.showMessageDialog(null,
					"File: "+filename+" has been downloaded successfully.", "Message",
					JOptionPane.INFORMATION_MESSAGE);

		}

		return this.dwnloadStatus;
	}

	/**
	 * 
	 * This method is executed when the worked thread finishes execution when the download is complete to display status.
	 */
	protected void done() {

		if(percentCompleted!=100){

			JOptionPane.showMessageDialog(null,
					"Download failed..Please try again",
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * 
	 * This method retrieves a file from the server location to the local path specified by the user.
	 * 
	 * 
	 * @param serverFilePath
	 * @param downloadFilePath
	 * @param transferType
	 * @return dwnloadStatus
	 */
	public String downloadFTP(String serverFilePath, String downloadFilePath, String transferType){

		try{

			boolean login = ftpObj.login(this.getUser(),this.getPassword());

			if(login){

				ftpObj.enterLocalPassiveMode();

				if(transferType.matches("BINARY")){
					ftpObj.setFileType(FTP.BINARY_FILE_TYPE);
				}
				else{
					ftpObj.setFileType(FTP.ASCII_FILE_TYPE);
				}

				String convertslash = downloadFilePath.replace("\\", "\\\\");
				File downloadFile = new File(convertslash);

				//open a stream to write to the local file
				outputStream = new FileOutputStream(downloadFile);
			}

		} catch (IOException e) {
			System.out.println("Error connecting to server: " + e.getMessage());
			e.printStackTrace();
		} 
		return this.dwnloadStatus;		
	}

}
