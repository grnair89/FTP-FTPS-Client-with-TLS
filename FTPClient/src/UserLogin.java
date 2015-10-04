import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


/**
 * UserLogin.java
 * 
 * This class generates a window for accepting the input related to login credentials from user.
 * 
 * @version 1.0
 * 
 * @author Ganesh Rajasekharan 
 * 
 */
public class UserLogin extends FTPTest {

	private JFrame frame;
	private JTextField userNameField;

	/**
	 * Create and initialize the application.
	 */
	public UserLogin() {
		initialize();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null); 
	}

	protected JFrame getUserFrame(){
		return this.frame;
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(SystemColor.inactiveCaption);
		frame.setBackground(Color.GRAY);
		frame.setBounds(100, 100, 593, 424);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel label = new JLabel("");
		label.setIcon(new ImageIcon(this.getClass().getResource("Images/user.png")));
		label.setBounds(49, 98, 128, 144);
		frame.getContentPane().add(label);

		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(198, 98, 70, 31);
		frame.getContentPane().add(lblUsername);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(198, 176, 70, 23);
		frame.getContentPane().add(lblPassword);

		userNameField = new JTextField();
		userNameField.setColumns(10);
		userNameField.setBounds(287, 95, 159, 31);
		frame.getContentPane().add(userNameField);


		final JPasswordField pwdField = new JPasswordField(20);
		lblPassword.setLabelFor(pwdField);


		pwdField.setColumns(10);
		pwdField.setBounds(287, 172, 159, 31);
		frame.getContentPane().add(pwdField);

		JButton btnLogin = new JButton("Login");

		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {


				if(userNameField.getText()!=null && pwdField.getPassword()!=null){
					try{
						frame.setVisible(false);

						String user = userNameField.getText().trim();
						char[] pwd =  pwdField.getPassword();
						String passwd = new String(pwd);

						FTPTest classObj = new FTPTest();

						classObj.setUser(user);
						classObj.setPassword(passwd);

						try {
							//SwingWorker Thread invoked to handle login process in the background
							classObj.execute();							
							frame.setVisible(false);							
							frame.dispose();


						} catch (Exception e1) {
							e1.printStackTrace();
						}

						 if(classObj.serverReplyCode.contains("434")){

							JOptionPane.showMessageDialog(frame,
									"Requested host unavailable",
									"Login Failed",
									JOptionPane.ERROR_MESSAGE);
							frame.setVisible(true);
						}

						else if(classObj.serverReplyCode.contains("501") || classObj.serverReplyCode.contains("530"))
						{
							JOptionPane.showMessageDialog(frame,
									"Incorrect username / password",
									"Login Failed",
									JOptionPane.ERROR_MESSAGE);
							userNameField.setText("");
							pwdField.setText("");
							frame.setVisible(true);
						}

					}
					catch(Exception exception){
						System.out.println(exception.getStackTrace());
					}

				}
			}
		});
		btnLogin.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnLogin.setBounds(287, 236, 70, 43);
		frame.getContentPane().add(btnLogin);

		JButton btnBack = new JButton("Back");

		//On back, dispose the frame and go back to previous one
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				LoginFrame obj = new LoginFrame();
				obj.getFrame().setVisible(true);

			}
		});
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnBack.setBounds(376, 236, 70, 43);
		frame.getContentPane().add(btnBack);
	}

}
