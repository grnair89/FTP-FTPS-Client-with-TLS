import java.io.IOException;
import java.net.SocketException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;


/**
 * FTPClient.java
 * 
 * This class contains utility methods for creating an FTP connection and also to login to the server. 
 * 
 * @version 1.0
 * 
 * @author Ganesh Rajasekharan 
 * 
 * 
 */
public class FTPTest extends SwingWorker<String, Object> {
	
	private static String server;	
	private static int port=21;
	
	private static String user="";
	private static String password="";
	
	private String transferStatus="";
    protected String serverReplyCode="";
	
	private static boolean ftpsChecked;
	
	protected FTPClient ftpObj;
	protected FTPSClient ftpsObj;
	
	//Getters and setters for members
	/**
	 * @return the transferStatus
	 */
	public String getTransferStatus() {
		return transferStatus;
	}

	/**
	 * @param transferStatus the transferStatus to set
	 */
	public void setTransferStatus(String transferStatus) {
		this.transferStatus = transferStatus;
	}


	/**
	 * @return the ftpsChecked
	 */
	protected static boolean isFtpsChecked() {
		return ftpsChecked;
	}

	
	/**
	 * @param ftpsChecked the ftpsChecked to set
	 */
	protected static void setFtpsChecked(boolean ftpsChecked) {
		FTPTest.ftpsChecked = ftpsChecked;
	}

	/**
	 * @return the server
	 */
	protected static String getServer() {
		return server;
	}


	/**
	 * @return the port
	 */
	protected static int getPort() {
		return port;
	}
	
	
	
	/**
	 * @return the statusCode
	 */
	protected String getserverReplyCode() {
		return this.serverReplyCode;
	}

	
	/**
	 * @param server the server to set
	 */
	protected static void setServer(String server) {
		FTPTest.server = server;
	}


	/**
	 * @param port the port to set
	 */
	protected static void setPort(int port) {
		FTPTest.port = port;
	}
	
	
	/**
	 * @param statusCode the statusCode to set
	 */
	protected void setserverReplyCode(String statusCode) {
		this.serverReplyCode = statusCode;
	}


	/**
	 * @return the user
	 */
	protected String getUser() {
		return user;
	}


	/**
	 * @param user the user to set
	 */
	protected void setUser(String user) {
		FTPTest.user = user;
	}


	/**
	 * @return the password
	 */
	protected String getPassword() {
		return password;
	}


	/**
	 * @param password the password to set
	 */
	protected void setPassword(String password) {
		FTPTest.password = password;
	}
	
	/**
	 * Constructor  Creates a connection if the socket is unavailable
	 * 
	 */
	public FTPTest(){
		
		ftpObj = new FTPClient();
		if(!ftpObj.isConnected()){		
			connectFTP(server,port);
		}

	}
	
	
	protected String showServerReply(FTPClient ftpClient) {
		
		String storeMessage="SERVER: ";
		String[] replies = ftpClient.getReplyStrings();
		if (replies != null && replies.length > 0) {
			for (String aReply : replies) {
				storeMessage +=" "+ aReply;
			}
		}
		
		return storeMessage;
	}


	/**
	 * This method creates a socket connection to the server.
	 * 
	 * @param server   host name/IP of the server
	 * @param port 	   port number (default 21 for FTP and 990 for FTPS)
	 * 
	 */
	public void connectFTP(String server, int port) {
		
		try {
			
			//check if the user selected ftps
			if(ftpsChecked){
				
			//use implicit FTPS with TLS.	
			ftpsObj = new FTPSClient(true);
			
			//check if the security certificate provided by the server is accepted
			ftpsObj.setTrustManager(TrustManagerUtils.getValidateServerCertificateTrustManager());
            ftpObj = ftpsObj;
			}
                
			ftpObj.connect(server,port);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if(ftpObj==null){
				JOptionPane.showMessageDialog(null,
						"No active server..",
						"Login Failed. Please try again.",
						JOptionPane.ERROR_MESSAGE);
			}
			
		}
		
	}
	
	public void loginFTP(String username, String password){
		String message = "";
		try {
			boolean isLoggedin = ftpObj.login(username,password);
			if(isLoggedin){
				this.serverReplyCode = ftpObj.getReplyString();
			}
			
			message = showServerReply(ftpObj);
			
			setserverReplyCode(message); 

		} catch (IOException e) {
			System.out.println("Login error");
			e.printStackTrace();
			if(e.getMessage().contains("Connection is not open")){
				setserverReplyCode("434"); 
			}
		}
		ftpObj.enterLocalPassiveMode();
	
	}


	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#doInBackground()
	 * 
	 * Swing Worker Thread to handle login so that the Event Dispatch Thread is not waiting for it to complete login.
	 * Helps to keep the UI from freezing.
	 * 
	 */
	protected String doInBackground() throws Exception {
	
		loginFTP(user, password);
		return this.serverReplyCode;
	}
	
	
	/**
	 * 
	 * Handle the messages to be displayed after login attempt by the worker thread
	 */
	protected void done(){
		
		if(getserverReplyCode().contains("230")){
			JOptionPane.showMessageDialog(null,
					"Successful login to server!");
			new TransferSelectionFrame();

		}

		else if(getserverReplyCode().contains("434")){

			JOptionPane.showMessageDialog(null,
					"Requested host unavailable",
					"Login Failed",
					JOptionPane.ERROR_MESSAGE);
			
			new UserLogin();
		}

		else if(getserverReplyCode().contains("501") || getserverReplyCode().contains("530"))
		{
			JOptionPane.showMessageDialog(null,
					"Incorrect username / password",
					"Login Failed",
					JOptionPane.ERROR_MESSAGE);
			
		   new UserLogin();
			
		}
		
	}
	

}
