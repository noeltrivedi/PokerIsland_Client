package com.pokerisland;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URISyntaxException;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.swing.Timer;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
//import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.pokerisland.Constants;
import com.pokerisland.Utils;

public class LobbyGUI extends JFrame
{
	private final int refreshTime = 1000;
	
	private JTable table;
	private DefaultTableModel tableModel;
	private JButton createTableButton, quickplayButton, joinTableButton, accountSettingsButton, logoutButton;
	//private JCheckBox handHistoryCheckBox;
	private JLabel usernameLabel, netWorthLabel;
	private int cap1, cap2, cap3, windowWidth, currentRefreshTimer = Constants.lobbyResfreshInterval;
	private Client client;
	private Quickplay quickplay;
	private Timer timer;
	private static int selectedRow;
	private MediaPlayer mediaPlayer;
	public static final long serialVersionUID = 1;
	/**
	 * Creates a new, initially invisible {@code LobbyGUI}
	 * <pre><ul>{@code public LoginGUI(Client client)}</ul></pre>
	 * @param client the {@code Client} that this {@code LobbyGUI} will communicate with
	 */
	public LobbyGUI(Client client)
	{
		super("Poker Island - Lobby");
		this.client = client;
		if(System.getProperty("os.name").equals("Mac OS X"))
			windowWidth = 354;
		else
			windowWidth = 360;
		setSize(windowWidth,439);
		
		//this sets the location of the JFrame to the center of the screen
		setLocation(((Toolkit.getDefaultToolkit().getScreenSize().width - getSize().width) / 2), 
			((Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 2)-15);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		cap1 = 1;
		cap2 = 4;
		cap3 = 6;
		
		instantiateComponents();
		createGUI();
		addActions();
		new MusicThread().start();
	}
	
	public void updateNetWorthLabel()
	{
		netWorthLabel.setText(Utils.titleCase(Constants.moneyName) + ": " + client.getPlayerNetWorth());
	}
	
	/**
	 * Instantiates the private variables of the LoginGUI class
	 * <pre><ul>{@code public void instantiateComponents()}</ul></pre>
	 */
	public void instantiateComponents()
	{
		DefaultTableCellRenderer centeredCellRenderer = new DefaultTableCellRenderer() 
		{
			private static final long serialVersionUID = 1;

			public Component getTableCellRendererComponent(JTable table, Object
                value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                //setForeground(Color.blue);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
                return this;
            }
        };
        DefaultTableCellRenderer leftAlignedCellRenderer = new DefaultTableCellRenderer() 
		{
			private static final long serialVersionUID = 1;

			public Component getTableCellRendererComponent(JTable table, Object
                value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                //setForeground(Color.blue);
                setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
                return this;
            }
        };
		String[] columnNames = {"Table Name","Buy-in","Capacity"};
		Object[][] data = {{" Table 1", "$5",cap1+"/3"},{" Table 2", "$15",cap2+"/9"},{" Table 3", "$50",cap3+"/6"}};
		tableModel = new DefaultTableModel(data, columnNames) {
			private static final long serialVersionUID = 1;

			@Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		};

		table = new JTable(tableModel);
		table.getTableHeader().setReorderingAllowed(false);
		table.getColumnModel().getColumn(0).setCellRenderer(leftAlignedCellRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centeredCellRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centeredCellRenderer);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(115);
        table.getColumnModel().getColumn(2).setPreferredWidth(115);
		table.setRowMargin(15);
        table.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    joinTable();
                }
            }
        });

		for(int i = 0; i < table.getRowCount(); i++)
		{
			table.setRowHeight(i, 30);
		}
		//set focus to first row
		table.requestFocus();
		table.changeSelection(0,0,false, false);
		
		usernameLabel = new JLabel("Logged in as: username");
		netWorthLabel = new JLabel("Earnings:       $"+"69");
		
		createTableButton = new JButton("Create Table");
		quickplayButton = new JButton("Quickplay");
		joinTableButton = new JButton("Join Table");
		accountSettingsButton = new JButton("Account Settings");
		logoutButton = new JButton("Logout");
		createTableButton.setFocusPainted(false);
		quickplayButton.setFocusPainted(false);
		joinTableButton.setFocusPainted(false);
		accountSettingsButton.setFocusPainted(false);
		logoutButton.setFocusPainted(false);
		
		quickplay = new Quickplay(client);
		
		//handHistoryCheckBox = new JCheckBox("Save Hand History");
		//instantiate and start refresh timer
		ActionListener decrementTimer = new ActionListener()
		{
			public void actionPerformed(ActionEvent ae) 
			{
				if (currentRefreshTimer == 0)
				{
					refresh();
					//reset time
					currentRefreshTimer = Constants.lobbyResfreshInterval;
				}
				currentRefreshTimer--;
			}

		};
		timer = new Timer(refreshTime, decrementTimer);
	}
	/**
	 * Adds components to their appropriate panels
	 * <pre><ul>{@code public void configureGUI()}</ul></pre>
	 */
	public void createGUI()
	{
		setLayout(null);
		setResizable(false);
		usernameLabel.setBounds(14,15,190,20);
		netWorthLabel.setBounds(14,43,190,20);
		if(System.getProperty("os.name").equals("Mac OS X"))
		{
			accountSettingsButton.setBounds(197, 40, (int)accountSettingsButton.getPreferredSize().getWidth(),
				(int)accountSettingsButton.getPreferredSize().getHeight());
			logoutButton.setBounds(260, 12, (int)logoutButton.getPreferredSize().getWidth(),
				(int)logoutButton.getPreferredSize().getHeight());
		}
		else
		{	
			accountSettingsButton.setBounds(210, 40, (int)accountSettingsButton.getPreferredSize().getWidth(),
					(int)accountSettingsButton.getPreferredSize().getHeight());
			logoutButton.setBounds(268, 12, (int)logoutButton.getPreferredSize().getWidth(),
					(int)logoutButton.getPreferredSize().getHeight());
		}
		
		table.setBounds(13,105,328,229);
		
		JLabel header = new JLabel(new ImageIcon(getClass().getResource("images/lobby table header.png")));
		header.setBounds(13, 77, 328, 28);
		
		table.setBorder(BorderFactory.createLineBorder(Color.black));
		
		JPanel topButtonPanel = new JPanel();
		topButtonPanel.setBounds(0, 338, windowWidth, 41);
		//TODO topButtonPanel.add(createTableButton);
		topButtonPanel.add(quickplayButton);
		//topButtonPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		
		JPanel bottomButtonPanel = new JPanel();
		bottomButtonPanel.setBounds(0, 373, windowWidth, 41);
		//bottomButtonPanel.add(handHistoryCheckBox);
		bottomButtonPanel.add(joinTableButton);
		//bottomButtonPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		add(usernameLabel);
		add(netWorthLabel);
		add(logoutButton);
		add(topButtonPanel);
		add(bottomButtonPanel);
		add(accountSettingsButton);
		add(table);
		add(header);
		
		//add(new JScrollPane(table), BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
            {
				int selection = JOptionPane.showConfirmDialog(LobbyGUI.this, "Are you sure you want to leave Poker Island?\n"
						+ "This will leave all your tables!", "Confirmation", JOptionPane.YES_NO_OPTION);
				switch(selection)
				{
				case JOptionPane.YES_OPTION:
					client.closingTime();
					break;
				default:
					break;
				}
            	
            }
		});
	}
	/**
	 * Removes all the rows from the LobbyGUI table
	 * <pre><ul>{@code public void removeAllTables()}</ul></pre>
	 */
	public void removeAllTables()
	{
		tableModel.setRowCount(0);
	}
	/**
	 * Adds a row at the last position of the LobbyGUI table
	 * <pre><ul>{@code public void addTableRow(Object[] columns)}</ul></pre>
	 * @param columns an array representing the {@code Object} (usually a {@code String}) to be contained in each column of the row
	 */
	public void addTableRow(Object[] columns)
	{
		tableModel.addRow(columns);
		table.setRowMargin(15);
		table.setRowHeight(table.getRowCount()-1, 30);
		if(table.getRowCount() == selectedRow+1)
			table.addRowSelectionInterval(selectedRow, selectedRow);
	}

	public void setVisible(boolean isVisible)
	{
		if(isVisible)
		{
			refresh();
			//start timer
			startRefreshTimer();
		}
		else
		{
			//stop timer
			stopRefreshTimer();
		}
		super.setVisible(isVisible);
	}
	
	/**
	 * Refreshes the LoginGUI and displays the new information
	 */
	public void refresh()
	{
		usernameLabel.setText("Logged in as: "+client.getPlayerUsername());
		updateNetWorthLabel();

		//refresh the Tables, this would be the same as pressing the (deprecated) refresh button
		selectedRow = table.getSelectedRow();
		removeAllTables();
		client.sendMessage("getTables");
	}
	/**
	 * Adds {@code ActionListener} events to the {@code LobbyGUI} buttons
	 * <pre><ul>{@code public void addActions()}</ul></pre>
	 */
	public void addActions()
	{
		logoutButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int selection = JOptionPane.showConfirmDialog(LobbyGUI.this, "Are you sure you want to logout?\n"
						+ "This will leave all your tables!", "Confirmation", JOptionPane.YES_NO_OPTION);
				switch(selection)
				{
				case JOptionPane.YES_OPTION:
					client.logout();
					break;
				default:
					break;
				}
			}
		});
		joinTableButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				joinTable();
			}
		});
		
		quickplayButton.addActionListener(new ActionListener() 
		{
            public void actionPerformed(ActionEvent e)
            {
                quickplay.setVisible(true);
            }
        });
		createTableButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				//create a table //TODO
			}
		});
		accountSettingsButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(client.isAtTables())
				{
					JOptionPane.showMessageDialog(null, "You cannot edit your account while at a table!"
							+ "\nPlease leave your tables to access the Account Settings menu.", "Error!",JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					createAccountSettingsMenu(); 
				}
			}
		});
		//double click on row listener
		table.addMouseListener(new MouseAdapter()
		{
		    public void mouseClicked(MouseEvent e)
		    {
		        if(e.getClickCount()==2)
		        {
		        	joinTable();
		        }
		    }
		});
		//must have row selected for join table button to be enabled
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionModel listSelectionModel = table.getSelectionModel();
		listSelectionModel.addListSelectionListener(new ListSelectionListener() 
		{
			public void valueChanged(ListSelectionEvent e) 
			{ 
				ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				joinTableButton.setEnabled(!lsm.isSelectionEmpty());
			}
		});
	}
	
	private void createAccountSettingsMenu()
	{
		if (client.getPlayerIsGuest())
		{
			JTextField usernameField = new JTextField();
			usernameField.setText(client.getPlayerUsername());
			JTextField password = new JPasswordField();
			JTextField confirmPassword = new JPasswordField();
			Object[] message = {new JLabel("You're playing as a guest."), new JLabel("Create an account to reap the"), new JLabel("benefits of a registered user."), new JLabel(""), 
					"Username:          ", usernameField, "Password:          ", password,"Confirm Password:          ", confirmPassword};
			int option = JOptionPane.showConfirmDialog(null, message, "Account Settings", JOptionPane.OK_CANCEL_OPTION);
			if (option == JOptionPane.OK_OPTION) 
			{
				String passwordOne = Utils.sha256(password.getText());
				String passwordTwo = Utils.sha256(confirmPassword.getText());
				String username = usernameField.getText();
				if(Utils.checkUsernamePasswordValidity(username, passwordOne, passwordTwo))
				{
					client.sendMessage("createGuestAccount;" + username + ";" + passwordOne + ";" + client.getPlayerNetWorth());
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Please make sure your input matches our account requirements:"
							+ "\n\tUsername is longer than 5 characters"
							+ "\n\tUsername does not contain the word \"Guest\""
							+ "\n\tUsername and password fields are not blank"
							+ "\n\tPassword fields match", "Error!", JOptionPane.WARNING_MESSAGE);
				}
			}
		}
		else
		{
			JTextField password = new JPasswordField();
			JTextField confirmPassword = new JPasswordField();
			Object[] message = {"New Password:          ", password,"Confirm Password:          ", confirmPassword};

			int option = JOptionPane.showConfirmDialog(null, message, "Account Settings", JOptionPane.OK_CANCEL_OPTION);
			if (option == JOptionPane.OK_OPTION) {
			    if (!password.getText().equals(null) && !confirmPassword.getText().equals(null) && confirmPassword.getText().equals(password.getText())) {
			    	String newPassword = Utils.sha256(confirmPassword.getText());
					String username = client.getPlayerUsername();
					client.sendMessage("changePassword;"+username+";"+newPassword);
			    } else {
			    	JOptionPane.showMessageDialog(null, "Invalid input", "Password Change Error", JOptionPane.ERROR_MESSAGE);
			    }
			}
		}
	}
	
	public void joinTable()
	{
		String capacity = ((String) table.getModel().getValueAt(table.getSelectedRow(), 2));
		String tableNumber = (String) table.getModel().getValueAt(table.getSelectedRow(), 0);
		tableNumber = tableNumber.substring(tableNumber.lastIndexOf(' ')+1, tableNumber.length());
		if(capacity.substring(capacity.indexOf('/')+1).equals(capacity.substring(0,capacity.indexOf('/'))))//if table is full
		{
			JOptionPane.showMessageDialog(null, 
					"The table you are trying to join is full.\nPlease join a different table", "Table Full",
					JOptionPane.OK_OPTION);
		}
		else if(client.atTable(Integer.parseInt(tableNumber)))
		{
			JOptionPane.showMessageDialog(null, 
					"You are already sitting at this table!", "Already Joined",
					JOptionPane.OK_OPTION);
		}
		else
		{
			String blindLevel = (String)table.getModel().getValueAt(table.getSelectedRow(), 1);
			blindLevel = blindLevel.substring(blindLevel.lastIndexOf(' ')+1);
			client.addTable(Integer.parseInt(tableNumber), Integer.parseInt(blindLevel));
		}
	}
	public void startRefreshTimer ()
	{
		timer.start();
	}
	public void stopRefreshTimer ()
	{
		timer.stop();
	}
	private class MusicThread extends Thread
	{
		public void run()
		{
			new javafx.embed.swing.JFXPanel();
		    try 
		    {
				mediaPlayer = new MediaPlayer(new Media(getClass().getResource("sounds/music.mp3").toURI().toString()));
			} 
		    catch (URISyntaxException e) 
		    {
				System.err.println("URISyntaxException: "+e.getMessage());
			}
		}
	}
	public void pauseMusic()
	{
		mediaPlayer.pause();
	}
	public void playMusic()
	{
		mediaPlayer.play();
	}
	public static void main(String[] args)
	{
		new LobbyGUI(new Client()).setVisible(true);
	}
}