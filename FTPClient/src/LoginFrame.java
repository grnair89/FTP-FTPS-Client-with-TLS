import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JRadioButton;


/**
 * LoginFrame.java
 * 
 * This class contains fields for accepting the input related to server from 
 * user and also the protocol for making the connection. 
 * 
 * @version 1.0
 * 
 * @author Ganesh Rajasekharan 
 * 
 */

public class LoginFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * @return the frame
	 */
	public JFrame getFrame() {
		return frmFtpLogin;
	}

	/**
	 * @param frame the frame to set
	 */
	public void setFrame(JFrame frame) {
		this.frmFtpLogin = frame;
	}

	/**
	 * 
	 */
	
	private JFrame frmFtpLogin;
	private JTextField serverTextField;
	private JTextField portTextField;
	
	private JRadioButton ftpButton;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame window = new LoginFrame();
					window.frmFtpLogin.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LoginFrame() {
		initialize();		
		frmFtpLogin.setLocationRelativeTo(null); 
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmFtpLogin = new JFrame();
		frmFtpLogin.setTitle("FTP Login");
		frmFtpLogin.getContentPane().setBackground(SystemColor.inactiveCaption);
		frmFtpLogin.setBackground(Color.GRAY);
		frmFtpLogin.setBounds(100, 100, 593, 424);
		frmFtpLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmFtpLogin.getContentPane().setLayout(null);
		
		JLabel lblServerIp = new JLabel("Server IP");
		lblServerIp.setBounds(238, 95, 56, 35);
		frmFtpLogin.getContentPane().add(lblServerIp);
		
		serverTextField = new JTextField();
		serverTextField.setBounds(325, 95, 187, 35);
		frmFtpLogin.getContentPane().add(serverTextField);
		serverTextField.setColumns(10);
		
		JLabel lblPortNumber = new JLabel("Port Number");
		lblPortNumber.setBounds(238, 159, 77, 20);
		frmFtpLogin.getContentPane().add(lblPortNumber);
		
		portTextField = new JTextField();
		portTextField.setBounds(325, 152, 95, 35);
		frmFtpLogin.getContentPane().add(portTextField);
		portTextField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setBackground(Color.GRAY);
		lblNewLabel_1.setIcon(new ImageIcon(this.getClass().getResource("Images/login1.png")));
		lblNewLabel_1.setBounds(0, 41, 208, 322);
		frmFtpLogin.getContentPane().add(lblNewLabel_1);
		
		JButton btnLogin = new JButton("Next");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try{
					if((serverTextField.getText()!=null)&&(portTextField.getText()!=null)){
						String text = serverTextField.getText().trim();
						String port = portTextField.getText();
						int portTyped = Integer.parseInt(port.trim());
						
						//validate data which user entered.
						if((((text.equalsIgnoreCase("localhost"))==false) && (checkIP(text))==false) || (text.isEmpty())){

							JOptionPane.showMessageDialog(frmFtpLogin,
								    "Server's IP is in invalid format.",
								    "Login error",
								    JOptionPane.ERROR_MESSAGE);
						}						
						
						else{
							
							FTPTest.setServer(text);
							FTPTest.setPort(portTyped);
							
							if(ftpButton.isSelected()){
								FTPTest.setFtpsChecked(false);								
							}
							else {FTPTest.setFtpsChecked(true);}
							
							frmFtpLogin.setVisible(false);	
							//create a new frame to handle the login process
							new UserLogin();														
						}
					
					}
				}catch(Exception excep){
					
				}
				
			}
		});
		btnLogin.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnLogin.setBounds(325, 284, 89, 43);
		frmFtpLogin.getContentPane().add(btnLogin);
		
		JButton btnCancel = new JButton("Clear");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				serverTextField.setText("");
				portTextField.setText("");
				
			}
		});
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnCancel.setBounds(423, 284, 89, 43);
		frmFtpLogin.getContentPane().add(btnCancel);
		
		JLabel lblProtocol = new JLabel("Protocol");
		lblProtocol.setBounds(238, 215, 56, 14);
		frmFtpLogin.getContentPane().add(lblProtocol);
		
		ftpButton = new JRadioButton("FTP");
		ftpButton.setBounds(325, 215, 70, 28);
		frmFtpLogin.getContentPane().add(ftpButton);
		
		JRadioButton ftpsButton = new JRadioButton("FTPS");
		ftpsButton.setBounds(442, 215, 70, 28);
		frmFtpLogin.getContentPane().add(ftpsButton);
		
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(ftpButton);
		buttonGroup.add(ftpsButton);
		ftpButton.setSelected(true);

		
	}
	
	
	/**
	 * Check for a valid IP
	 * 
	 * @param ip    The aIP address entered by the user.
	 */
	
	public boolean checkIP (String address) {
	    try {
	        if (address == null || address.isEmpty()) {
	            return false;
	        }

	        String[] splits = address.split( "\\." );
	        if ( splits.length != 4 ) {
	            return false;
	        }

	        for ( String part : splits ) {
	            int value = Integer.parseInt(part);
	            if (value < 0 || value > 255) {
	                return false;
	            }
	        }
	        if(address.endsWith(".")) {
	                return false;
	        }

	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	}

}
