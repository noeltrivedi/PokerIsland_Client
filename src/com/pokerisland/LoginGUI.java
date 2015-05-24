package com.pokerisland;

import java.awt.CardLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.pokerisland.Utils;

public class LoginGUI extends JFrame
{
	public static final long serialVersionUID = 1;
	private JPanel cardPanel;
	private JTextField usernameTextField, usernameCreateTextField;
	private JPasswordField passwordTextField, passwordCreateTextField, passwordConfirmTextField;
	private JButton loginButton, loginAsGuestButton, createAccountButton, submitButton, cancelButton;
	private Client client;
	
	/**
	 * Creates a new, initially invisible {@code LoginGUI} with the specified {@code Client}
	 * <pre><ul>{@code public LoginGUI(Client client)}</ul></pre>
	 * @param client the {@code Client} that this {@code LoginGUI} will communicate with
	 */
	public LoginGUI(Client client)
	{
		super("Poker Island - Login");
		setSize(350,252);
		setResizable(false);
		
		this.client = client;

		//this sets the location of the JFrame to the center of the screen
		setLocation(((Toolkit.getDefaultToolkit().getScreenSize().width - getSize().width) / 2), 
				((Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 2)-15);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		instantiateVariablesAndCreateGUI();
		addActions();
		
		//setVisible(true);
	}
	/**
	 * Instantiates private variables and adds components to their appropriate panels
	 * <pre><ul>{@code public void instantiateVariablesAndCreateGUI()}</ul></pre>
	 */
	private void instantiateVariablesAndCreateGUI()
	{
		cardPanel = new JPanel(new CardLayout());

		JPanel usernamePanel = new JPanel();
		JPanel passwordPanel = new JPanel();
		JLabel usernameLabel = new JLabel("Username: ");
		JLabel passwordLabel = new JLabel("Password: ");
		usernameTextField = new JTextField(15);
		passwordTextField = new JPasswordField(15);
		usernamePanel.add(usernameLabel);
		usernamePanel.add(usernameTextField);
		passwordPanel.add(passwordLabel);
		passwordPanel.add(passwordTextField);
		
		JPanel loginButtonPanel = new JPanel();		
		JPanel orLabelPanel = new JPanel();
		JPanel loginAsGuestAndCreateAccountButtonPanel = new JPanel();
		loginButton = new JButton("Login");
		JLabel orLabel = new JLabel("OR");
		loginAsGuestButton = new JButton("Login as Guest");
		createAccountButton = new JButton("Create Account");
		loginButton.setFocusPainted(false);
		loginAsGuestButton.setFocusPainted(false);
		createAccountButton.setFocusPainted(false);
		loginButtonPanel.add(loginButton);
		orLabelPanel.add(orLabel);
		loginAsGuestAndCreateAccountButtonPanel.add(loginAsGuestButton);
		loginAsGuestAndCreateAccountButtonPanel.add(createAccountButton);
		
		JPanel panelLast = new JPanel();
		submitButton = new JButton("Submit");
		cancelButton = new JButton("Cancel");
		submitButton.setFocusPainted(false);
		cancelButton.setFocusPainted(false);
		panelLast.add(submitButton);
		panelLast.add(cancelButton);
		
		JLabel usernameCreateLabel = new JLabel("Create Username: ");
		JLabel passwordCreateLabel = new JLabel("Create Password: ");
		JLabel passwordConfirmLabel = new JLabel("Confirm Password: ");
		JPanel usernameCreatePanel = new JPanel();
		JPanel passwordCreatePanel = new JPanel();
		JPanel passwordConfirmPanel = new JPanel();
		usernameCreateTextField = new JTextField(15);
		passwordCreateTextField = new JPasswordField(15);
		passwordConfirmTextField = new JPasswordField(15);
		usernameCreatePanel.add(usernameCreateLabel);
		usernameCreatePanel.add(usernameCreateTextField);
		passwordCreatePanel.add(passwordCreateLabel);
		passwordCreatePanel.add(passwordCreateTextField);
		passwordConfirmPanel.add(passwordConfirmLabel);
		passwordConfirmPanel.add(passwordConfirmTextField);
		
		JLabel cardImageTopLabel = new JLabel(new ImageIcon(getClass().getResource("images/login header.jpg")));
		JLabel cardImageBottomLabel = new JLabel(new ImageIcon(getClass().getResource("images/login header.jpg")));
		
		//layout of the standard login panel
		JPanel topPanel = new JPanel();
		topPanel.setLayout(null);
		
		if(System.getProperty("os.name").equals("Mac OS X"))
		{
			cardImageBottomLabel.setBounds(0,0,350,50);
			cardImageTopLabel.setBounds(0,0,350,50);
			passwordPanel.setBounds(36,96,280,35);
			passwordConfirmPanel.setBounds(5,137,330,35);
		}
		else
		{
			cardImageBottomLabel.setBounds(0,-1,350,50);
			cardImageTopLabel.setBounds(0,-1,350,50);
			passwordPanel.setBounds(35,96,280,35);
			passwordConfirmPanel.setBounds(7,137,330,35);
		}
		usernamePanel.setBounds(35,58,280,35);
		
		loginButtonPanel.setBounds(34,132,280,35);
		orLabelPanel.setBounds(33,162,280,20);
		loginAsGuestAndCreateAccountButtonPanel.setBounds(32,182,287,35);
		
		topPanel.add(cardImageTopLabel);
		topPanel.add(usernamePanel);
		topPanel.add(passwordPanel);
		topPanel.add(loginButtonPanel);
		topPanel.add(orLabelPanel);
		topPanel.add(loginAsGuestAndCreateAccountButtonPanel);

		//layout of the create new user panel
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(null);
		
		usernameCreatePanel.setBounds(10,58,330,35);
		passwordCreatePanel.setBounds(11,97,330,35);
		panelLast.setBounds(30,182,287,35);
		
		bottomPanel.add(cardImageBottomLabel);
		bottomPanel.add(usernameCreatePanel);
		bottomPanel.add(passwordCreatePanel);
		bottomPanel.add(passwordConfirmPanel);
		bottomPanel.add(panelLast);
		
		cardPanel.add(topPanel, "top");
		cardPanel.add(bottomPanel, "bottom");
		
		//initially shows the standard login panel
		CardLayout cl = (CardLayout)(cardPanel.getLayout());
	    cl.show(cardPanel, "top");
	    
	    add(cardPanel);
	}
	
	public void accountSuccessfullyCreated()
	{
		JOptionPane.showMessageDialog(null, "Account successfully created!", "Success!", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void accountUnsuccessfullyCreated()
	{
		JOptionPane.showMessageDialog(null, "That username is already taken! \nPlease try again", "Failure!", JOptionPane.ERROR_MESSAGE);
	}
	/**
	 * Shows a dialog box explaining that login failed
	 * <pre><ul>{@code public void showLoginFailure()}</ul></pre>
	 */
	public void showLoginFailure()
	{
		JOptionPane.showMessageDialog(null, "Invalid Username and/or Password", "Login Error", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Gives buttons the appropriate actions
	 * <pre><ul>{@code public void addActions()}</ul></pre>
	 */
	private void addActions()
	{
		createAccountButton.addActionListener(new ActionListener() 
	    {
			public void actionPerformed(ActionEvent ae) 
        	{
        		CardLayout cl = (CardLayout)(cardPanel.getLayout());
        	    cl.show(cardPanel, "bottom");
        	}
        });
		cancelButton.addActionListener(new ActionListener() 
	    {
			public void actionPerformed(ActionEvent ae) 
        	{
        		CardLayout cl = (CardLayout)(cardPanel.getLayout());
        	    cl.show(cardPanel, "top");
        	}
        });
		loginButton.addActionListener(new LoginListener());
		usernameTextField.addActionListener(new MoveLoginListener(passwordTextField));
		passwordTextField.addActionListener(new LoginListener());
		loginAsGuestButton.addActionListener(new ActionListener() 
	    {
			public void actionPerformed(ActionEvent ae)
			{
				JOptionPane.showMessageDialog(null, "Welcome to Poker Island!\nYou are logging "
						+ "in as a guest. You will be able to play for 30 minutes as a trial. If"
						+ " you\nwould like to play for longer than that, please create an account"
						+ " from the Login window.", "Guest Information", JOptionPane.INFORMATION_MESSAGE);
				String message = "createGuest;";
				client.sendMessage(message);
			}
        });
		submitButton.addActionListener(new CreateAccountListener());
		passwordConfirmTextField.addActionListener(new CreateAccountListener());
	}
	
	private class MoveLoginListener implements ActionListener
	{
		private JTextField moveTo;
		public MoveLoginListener(JTextField moveTo)
		{
			this.moveTo = moveTo;
		}
		
		public void actionPerformed(ActionEvent ae)
		{
			moveTo.grabFocus();
		}
	}
	
	private class LoginListener implements ActionListener
	{
		public void actionPerformed(ActionEvent ae) 
		{
			String username = usernameTextField.getText();
			String password = Utils.sha256(String.copyValueOf(passwordTextField.getPassword()));
			
			if(!username.equals("") && passwordTextField.getPassword().length!=0)
			{
				client.sendMessage("login;" + username + ";" + password);
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Username and/or Password field cannot be blank!", "Error", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	private class CreateAccountListener implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
			String passwordOne = Utils.sha256(String.copyValueOf(passwordCreateTextField.getPassword()));
			String passwordTwo = Utils.sha256(String.copyValueOf(passwordConfirmTextField.getPassword()));
			String username = usernameCreateTextField.getText();
			if(Utils.checkUsernamePasswordValidity(username, passwordOne, passwordTwo))
			{
				String message = "createAccount;" + username + ";" + passwordTwo;
						
				if(!client.sendMessage(message))//sends the message to the client, and returns whether or not it was successfully sent
				{
					JOptionPane.showMessageDialog(null, "Error sending message to the server", "Error!", JOptionPane.WARNING_MESSAGE);

				}
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Please make sure your input matches our account requirements:"
						+ "\n\tUsername is longer than 2 characters"
						+ "\n\tUsername and password fields are not blank"
						+ "\n\tPassword fields match", "Error!", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	public void setVisible(boolean isVisible)
	{
		super.setVisible(isVisible);
		
		CardLayout cl = (CardLayout)(cardPanel.getLayout());
	    cl.show(cardPanel, "top");
	    
		usernameTextField.setText("");
		passwordTextField.setText("");
		usernameCreateTextField.setText("");
		passwordCreateTextField.setText("");
		passwordConfirmTextField.setText("");
		usernameTextField.grabFocus();
	}
	
	public static void main(String[] args)
	{
		new LoginGUI(new Client()).setVisible(true);
	}
}
