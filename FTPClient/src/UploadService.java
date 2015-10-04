import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JOptionPane;

import org.apache.commons.net.ftp.FTP;

/**
 * UploadService.java
 * 
 * This class contains methods for uploading a file and tracking the upload status.
 * 
 * @version 1.0
 * 
 * @author Ganesh Rajasekharan 
 * 
 */

public class UploadService extends FTPTest  {

	private static final int buffersize = 8192;
	
	private int percentCompleted = 0;

	private String uploadStatus="";
	private String remotePath;
	private String localPath;
	private String transferType;	
	private String uploadFileName;

	private OutputStream outputStream;

	public UploadService(String remotePath, String localPath, String transferType){

		this.remotePath = remotePath;
		this.localPath = localPath;
		this.transferType = transferType;
	}
	//run a background thread to handle upload without freezing event dispatch thread
	protected String doInBackground() throws Exception {

		uploadFTP(remotePath,  localPath, transferType);

		String convertslash = localPath.replace("\\", "\\\\");
		File upLoadfile = new File(convertslash); 

		//open an input stream to read from the file to be uploaded
		try(FileInputStream inputStream = new FileInputStream(upLoadfile)){

			byte[] buffer = new byte[buffersize];
			
			long BytesReadSoFar = 0;

			int bytesRead = -1;

			long fileSize = upLoadfile.length();

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
				BytesReadSoFar += bytesRead;
				percentCompleted = (int) (BytesReadSoFar * 100 / fileSize);
				setProgress(percentCompleted);
			}
			
			if(percentCompleted == 100){

				JOptionPane.showMessageDialog(null,
						"File: "+uploadFileName+" has been uploaded successfully.", "Message",
						JOptionPane.INFORMATION_MESSAGE);				
			}

		}
		catch(IOException e){

			System.out.println(e.getMessage());
		}		
		finally{

			//close the output stream to the file to be uploaded.
			outputStream.close();

		}
		return uploadStatus;

	}

	protected void done() {
		
		if(percentCompleted!=100){
			
			JOptionPane.showMessageDialog(null,
					"Upload failed..Please try again",
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}

	}


	public String uploadFTP(String remotePath, String localPath, String transferType){

		try{

			ftpObj.login(this.getUser(),this.getPassword());

			ftpObj.enterLocalPassiveMode();

			if(transferType.matches("BINARY")){
				ftpObj.setFileType(FTP.BINARY_FILE_TYPE);

			}
			else{
				ftpObj.setFileType(FTP.ASCII_FILE_TYPE);
			}


			String convertslash = localPath.replace("\\", "\\\\");
			File upLoadfile = new File(convertslash); 
			
			
			//extract the file path from the absolute file path
			remotePath = remotePath.replaceAll("(.*\\/).*", "$1");
			
			//change working directory to path extracted
			ftpObj.changeWorkingDirectory(remotePath);
			
			//open an outputstream to write to the remote file
			outputStream = ftpObj.storeFileStream(upLoadfile.getName());
			
			uploadFileName = upLoadfile.getName();

		} catch (IOException e) {

			System.out.println("Error uploading file: " + e.getMessage());
			this.uploadStatus = "ERROR";				
			e.printStackTrace();
		} 
		
		return uploadStatus;		



	}
}