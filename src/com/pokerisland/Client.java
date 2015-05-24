package com.pokerisland;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Hashtable;

import javax.swing.JOptionPane;

import com.pokerisland.*;

public class Client extends Thread
{	
	private OutputStreamWriter bw;
	private BufferedReader br;
	private Socket s;
	private Hashtable<Integer, TableGUI> tableGUIs; 

	//GUI
	private IPGUI ipGUI;
	private LoginGUI loginGUI;
	private LobbyGUI lobbyGUI;
	private GuestTimerGUI guestTimer;
	private TabbedTables tabbedTables;
	private String serverAddress;
	private Player player; //each client will have one player
	
	public static void main(String[] args)
	{
		new Client();
	}
	public Client() 
	{
		Utils.getFontName();
		
		initializeGUI();
		ipGUI.setVisible(true);
	}
	private void initializeGUI()
	{
		ipGUI = new IPGUI(this);
		lobbyGUI = new LobbyGUI(this);
		loginGUI = new LoginGUI(this);
		guestTimer = new GuestTimerGUI(this);
		tabbedTables = new TabbedTables(this);
		
	}
	public Player getPlayer()
	{
		return player;
	}
	public boolean getPlayerIsGuest()
	{
		return player.getIsGuest();
	}
	public String getPlayerUsername()
	{
		return player.getUsername();
	}
	public int getPlayerNetWorth()
	{
		return player.getNetWorth();
	}
	public void killGuestTimer()
	{
		guestTimer.setVisible(false);
	}
	public void setSoundCheckBoxesSelected(boolean isSelected)
	{
		for(int t : tableGUIs.keySet())
			tableGUIs.get(t).setSoundCheckBoxSelected(isSelected);
	}
	
	/**
	 * Connects to a Server at the specified IP Address
	 * @param ip The IP Address of the {@code Server}
	 */
	public void connectToServer(String ip)
	{
		serverAddress = ip;
		try
		{
			player = new Player("",0);
			
			InetSocketAddress inetAddress = new InetSocketAddress(serverAddress, Constants.serverPort);
				//create the adddress to connect to
			
			s = new Socket();
			
			s.connect(inetAddress, Constants.serverTimeout);
				//attempt to connect but only wait as long as the serverTimeout says (in millis)
			
			br = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
			bw = new OutputStreamWriter(
				    s.getOutputStream(), "UTF-8");

			this.tableGUIs = new Hashtable<Integer, TableGUI>();
			
			ipGUI.setVisible(false);
			lobbyGUI.setVisible(false);
			loginGUI.setVisible(true);
			this.start();
		}
		catch(SocketTimeoutException ste) //if the connection times out, invalid ip address
		{
			ste.printStackTrace();
			JOptionPane.showMessageDialog(null, "The IP Address you entered seems to be invalid."
					+ "\nPlease confirm that it was entered properly.", 
					"Error Connecting to Server!", JOptionPane.ERROR_MESSAGE);
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
			if(ioe.getMessage().equals("Connection refused: connect"))
			{
				ioe.printStackTrace();
				JOptionPane.showMessageDialog(null, "Could not connect to Server", 
						"Error Connecting to Server!", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				System.out.println("IOException in Client constructor: " + ioe.getMessage() );
			}
		}
	}
	
	/**
	 * Adds a Table to the client
	 * @param uniqueID Specifies the table
	 * @param blindLevel Specifies the Buy In
	 */
	public void addTable(int uniqueID, int blindLevel)
	{
        TableGUI table = new TableGUI(this, lobbyGUI, "Table " + uniqueID, uniqueID, blindLevel);
		tableGUIs.put(uniqueID, table);
		tabbedTables.addTable(table, "Table " + uniqueID);
	}
	
	public void pauseMusic()
	{
		lobbyGUI.pauseMusic();
	}
	public void playMusic()
	{
		lobbyGUI.playMusic();
	}
	
	/**
	 * Removes the specified table
	 * @param uniqueID The ID Of the Table to remove
	 */
	public void removeTable(int uniqueID)
	{
		TableGUI table = tableGUIs.get(uniqueID);
		
		tabbedTables.removeTable(table);
		tableGUIs.remove(uniqueID);
	}
	public boolean atTable(int uniqueID)
	{
		if(tableGUIs.get(uniqueID) == null)
			return false;
		else
			return true;
	}
	public void removeTable(TableGUI table)
	{
		removeTable(table.getUniqueID());
	}
	
	public void withdrawMoney(int money)
	{
		player.setNetWorth(player.getNetWorth() - money);
		lobbyGUI.updateNetWorthLabel();
	}
	
	public void depositMoney(int money)
	{
		player.setNetWorth(player.getNetWorth() + money);
		lobbyGUI.updateNetWorthLabel();
		sendMessage("updateDB;" + player.getUsername() + ";" + player.getNetWorth());
	}
	
	/**
	 * Hides all the GUIs and exits the program
	 */
	public void closingTime()
	{
		tabbedTables.setVisible(false);
		
		lobbyGUI.setVisible(false);
		loginGUI.setVisible(false);
		ipGUI.setVisible(false);
		player.logout();
		System.exit(0);
	}
	
	/**
	 * Logs out the player, returns to the login screen, and alerts the server that this player has logged out
	 */
	public void logout()
	{
		loginGUI.setVisible(true);
		lobbyGUI.setVisible(false);
		guestTimer.setVisible(false);
		tabbedTables.setVisible(false);
		sendMessage("logout;" + getPlayerUsername() + ";" + getPlayerNetWorth());		
	}
	
	public boolean isAtTables()
	{
		return tabbedTables.isVisible();
	}
	
	public boolean sendMessage(String message)
	{
		if(s != null && !s.isClosed())
		{
			try 
			{
				bw.write(message+"\n");
				bw.flush();
			} 
			catch(SocketException se)
			{
				System.out.println("Disconnected from Server");
				System.exit(0);
			}
			catch (IOException e) {
				//e.printStackTrace();
				System.exit(0);
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	public void run()
	{
		//creates all the readers, writers, etc
		if(br != null && s != null && bw != null)
		{
			try 
			{
				while (true) 
				{
					String line = Client.this.br.readLine();
					this.parse(line);
					//sendMessage(s.getInetAddress().getHostAddress() + ":" + s.getPort() + " got the button click");
				}
			} 
			catch (IOException ioe) 
			{
				if(ioe.getMessage().equals("Connection reset"))
				{
					JOptionPane.showMessageDialog(null, "Connection to Server lost", "No Connection", JOptionPane.ERROR_MESSAGE);
					System.exit(1);
				}
				else
					System.out.println("IOException in Client Thread: " + ioe.getMessage());
			}
			finally
			{
				try
				{
					if(s != null)
						s.close();
					if(br != null)
						br.close();
					if(bw != null)
						bw.close();
				}
				catch(IOException ioe)
				{
					System.out.println("IOE Closing Sockets and Readers: " + ioe.getMessage());
				}//close catch ioe
			}//close finally
		}//close if all readers and writers are not null
	}//close run
	private void parseLoginMessage(String[] info)
	{
		if(info[1].equals("success"))
		{
			player.setUsername(info[2]);
			player.setNetWorth(Integer.parseInt(info[3]));
			player.setIsGuest(false);
			loginGUI.setVisible(false);
			lobbyGUI.setVisible(true);
		}
		else if(info[1].equals("failure"))
		{
			loginGUI.showLoginFailure();
		}
	}
	private void parseTableRefreshMessage(String[] info)
	{
		lobbyGUI.addTableRow(new Object[]{" Table "+info[1], info[2], info[3]});
	}
	private void parseCreateAccountMessage(String[] info)
	{
		//this message tag means an account was successfully created
		if(info[1].equals("failed"))
		{
			loginGUI.accountUnsuccessfullyCreated();
		}
		else
		{
			player = new Player(info[1], Constants.startingNetWorth);
			loginGUI.accountSuccessfullyCreated();
			loginGUI.setVisible(false);
			lobbyGUI.setVisible(true);
		}
	}
		
	private void parseLoginGuestMessage(String[] info)
	{
		//logging in as a guest
		//info[1] is guest username
		//info[2] is starting net worth
		player = new Player(info[1], Integer.parseInt(info[2]));
		player.setIsGuest(true);
		loginGUI.setVisible(false);
		lobbyGUI.setVisible(true);
		guestTimer.setVisible(true); //create the timer 
	}
	public void parse(String message)
	{
		String[] info = message.split(";");
		//format is tableGUI;tableID;tableGuifunctionName;tableGUIfunctionParams
		if (info[0].equals("handHistory"))
		{
			String username = getPlayerUsername(); 
			boolean exists = false; 
			try
			{
				File temp = File.createTempFile(username, ".hh"); 
				exists = temp.exists(); 
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			if (exists == true)
			{
				try
				{
				    String filename= "handhistory\\" + username+".hh";
				    FileWriter fw = new FileWriter(filename,true);
				    fw.write(info[1]+'\n');
				    fw.close();
				}
				catch(IOException ioe)
				{
				    System.err.println("IOException: " + ioe.getMessage());
				}
			}
			else if (exists != true)
			{
				PrintWriter writer = null;
				try 
				{
					writer = new PrintWriter(username+".hh");
					writer.write(info[1]+'\n'); 
				} 
				catch (FileNotFoundException e) 
				{
					e.printStackTrace();
				}
				writer.close();
			}
		}
		else if (info[0].equals("tableGUI"))
		{
			Integer tableID = Integer.parseInt(info[1]); 
			if (info[2].equals("addPlayer"))
			{
				int pos = Integer.parseInt(info[3]); 
				String username = info[4];
				int amt = Integer.parseInt(info[5]); 
				TableGUI table = tableGUIs.get(tableID);
				if(table != null)
					table.addPlayer(pos, username, amt);
			}
			else if (info[2].equals("removePlayer"))
			{
				int pos = Integer.parseInt(info[3]); 
				TableGUI table = tableGUIs.get(tableID);
				if(table != null)
					table.removePlayer(pos);
			}
			else if (info[2].equals("putInChips"))
			{
				int pos = Integer.parseInt(info[3]); 
				int amt = Integer.parseInt(info[4]); 
				TableGUI table = tableGUIs.get(tableID);
				if(table != null)
					table.putInChips(pos, amt); 
			}
			else if (info[2].equals("takeOutChips"))
			{
				TableGUI table = tableGUIs.get(tableID);
				if(table != null)
					table.takeOutChips(Integer.parseInt(info[3]), Integer.parseInt(info[4])); 
			}
			else if (info[2].equals("setPotAmount"))
			{
				int amt = Integer.parseInt(info[3]); 
				TableGUI table = tableGUIs.get(tableID);
				if(table != null)
					table.setPotAmount(amt);
			}
			else if (info[2].equals("setPlayerAction"))
			{
				int pos = Integer.parseInt(info[3]); 
				String theMessage = info[4]; 
				TableGUI table = tableGUIs.get(tableID);
				if(table != null)
					table.setPlayerAction(pos, theMessage);
			}
			else if(info[2].equals("showFlopCards"))
			{
				TableGUI table = tableGUIs.get(tableID);
				if(table != null)
					table.showFlopCards(Integer.parseInt(info[3]), Integer.parseInt(info[4]),
						Integer.parseInt(info[5]), Integer.parseInt(info[6]), Integer.parseInt(info[7]),
						Integer.parseInt(info[8]));
			}
			else if(info[2].equals("showTurnCard"))
			{
				TableGUI table = tableGUIs.get(tableID);
				if(table != null)
					table.showTurnCard(Integer.parseInt(info[3]), Integer.parseInt(info[4]));
			}
			else if(info[2].equals("showRiverCard"))
			{
				TableGUI table = tableGUIs.get(tableID);
				if(table != null)
					table.showRiverCard(Integer.parseInt(info[3]), Integer.parseInt(info[4]));
			}
			else if(info[2].equals("clearBoard"))
			{
				TableGUI table = tableGUIs.get(tableID);
				if(table != null)
					table.clearBoard();
			}
			else if(info[2].equals("hidePlayerCards"))
			{
				TableGUI table = tableGUIs.get(tableID);
				if(table != null)
					table.hidePlayerCards(Integer.parseInt(info[3]));
			}
			else if(info[2].equals("setPlayerCardsToBackSide"))
			{
				TableGUI table = tableGUIs.get(tableID);
				if(table != null)
					table.setPlayerCardsToBackSide(Integer.parseInt(info[3]));
			}
			else if(info[2].equals("setPlayerCards"))
			{
				int pos = Integer.parseInt(info[3]);
				TableGUI table = tableGUIs.get(tableID);
				if(table != null)
					table.setPlayerCards(pos, Integer.parseInt(info[4]), Integer.parseInt(info[5]),
						Integer.parseInt(info[6]),Integer.parseInt(info[7]));
			}
			else if (info[2].equals("setPlayerStackSize"))
			{
				int pos = Integer.parseInt(info[3]); 
				int stack = Integer.parseInt(info[4]); 
				TableGUI table = tableGUIs.get(tableID);
				if(table != null)
					table.setPlayerStackSize(pos, stack);
			}
			else if(info[2].equals("setCallAmount"))
			{
				TableGUI table = tableGUIs.get(tableID);
				if(table != null)
					table.setCallAmount(Integer.parseInt(info[3]));
			}
			else if (info[2].equals("setMinimumBetAmount"))
			{
				int amt = Integer.parseInt(info[3]); 
				TableGUI table = tableGUIs.get(tableID);
				if(table != null)
					table.setMinimumBetAmount(amt);
			}
			else if (info[2].equals("setMaximumBetAmount"))
			{
				int amt = Integer.parseInt(info[3]); 
				TableGUI table = tableGUIs.get(tableID);
				if(table != null)
					table.setMaximumBetAmount(amt);
			}
			else if (info[2].equals("setDealerButtonPosition"))
			{
				int pos = Integer.parseInt(info[3]); 
				TableGUI table = tableGUIs.get(tableID);
				if(table != null)
					table.setDealerButtonPosition(pos); 
			}
			else if (info[2].equals("setBigBlindButtonPosition"))
			{
				int amt = Integer.parseInt(info[3]); 
				TableGUI table = tableGUIs.get(tableID);
				if(table != null)
					table.setBigBlindButtonPosition(amt); 
			}
			else if (info[2].equals("setSmallBlindButtonPosition"))
			{
				int amt = Integer.parseInt(info[3]); 
				TableGUI table = tableGUIs.get(tableID);
				if(table != null)
					table.setSmallBlindButtonPosition(amt);
			}
			else if(info[2].equals("setButtonsVisible"))
			{
				boolean betIsOnTable = false;
				boolean isVisible = false;
				if(info[3].equals("true"))
					isVisible = true;
				if(info[4].equals("true"))
					betIsOnTable = true;
					
				TableGUI table = tableGUIs.get(tableID);
				if(table != null)
					table.setButtonsVisible(isVisible, betIsOnTable);
			}
		}
		if (info[0].equals("quickplay"))
		{
			if (info.length == 1)
			{
				JOptionPane.showMessageDialog(null, "Unable to find any tables matching your specifications", "No Matching Tables", JOptionPane.ERROR_MESSAGE);
			}
            for (int i = 1; i < info.length; i+=2)
			{
                addTable(Integer.parseInt(info[i]), Integer.parseInt(info[i+1]));
			}
		}
		if(info.length == 4)
		{
			if(info[0].equals("login"))
			{
				parseLoginMessage(info);
			}
			//this message tag means the user hit refresh in LobbyGUI and we're updating the open tables that they see
			else if(info[0].equals("refreshLobbyTables"))
			{
				parseTableRefreshMessage(info);
			}
			else if(info[0].equals("createGuestAccount"))
			{
				parseCreateGuestAccountMessage(info);
			}
		}
		else if(info.length == 3)//all messages with 3 pieces of information
		{
			if(info[0].equals("createAccount"))
			{
				parseCreateAccountMessage(info);
			}
			else if(info[0].equals("loginGuest"))
			{
				parseLoginGuestMessage(info);
			}
		}
		else if(info.length == 2)
		{
			if(info[0].equals("netWorth"))
			{
				depositMoney(Integer.parseInt(info[1]));
			}
		}
		else if(info.length == 1)
		{
			if(info[0].equals("serverClosing"))
			{
				sendMessage("logout;" + getPlayerUsername() + ";" + getPlayerNetWorth());
				//send the logout message
			}
		}
	}
	private void parseCreateGuestAccountMessage(String[] info)
	{
		if(info[1].equals("failed"))
		{
			//account creation failed
			loginGUI.showLoginFailure();
		}
		else
		{
			//valid
			player.setUsername(info[1]);
			player.setNetWorth(Integer.parseInt(info[3]));
			player.setIsGuest(false);
			lobbyGUI.refresh();
			killGuestTimer();
		}
	}
}//close class Client
