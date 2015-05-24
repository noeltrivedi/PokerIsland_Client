package com.pokerisland;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class TabbedTables extends JFrame
{
	private JTabbedPane tabs;
	private ArrayList<TableGUI> tables;
	private Client client;
	
	public TabbedTables(Client client)
	{
		super("Poker Island");
		this.client = client;
		createGUI();
		tables = new ArrayList<TableGUI>();
		setResizable(false);		
		getContentPane().add(tabs, BorderLayout.CENTER);
		
	}
	
	private void createGUI()
	{
		this.setLayout(new BorderLayout());
		
		tabs = new JTabbedPane();
		
		fixSize();
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		//This occurs when the TabbedTables is closing
		//It will close every table
		addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
	        	int selection = JOptionPane.showConfirmDialog(TabbedTables.this, "Are you sure you want to close?\n"
							+ "This will leave all your tables!", "Confirmation", JOptionPane.YES_NO_OPTION);
				switch(selection)
				{
				case JOptionPane.YES_OPTION:
					setVisible(false);
					break;
				default:
					break;
				}
            }
        });
		
	}
	
	private void closeAllTables()
	{
		for(int i = tables.size() - 1; i >= 0; i--)
    	{
    		TableGUI table = tables.get(i);
    		table.sendCloseMessage();
    		client.removeTable(tables.get(i).getUniqueID());

    	}
    	
    	tables.clear();
    	tabs.removeAll();
	}
	
	@Override
	public void setVisible(boolean isVisible)
	{
		if(isVisible)
		{
			if(TableGUI.soundOn)
				client.playMusic();
		}
		else
		{
			//closing this frame
			
			closeAllTables();
			client.pauseMusic();
		}
		super.setVisible(isVisible);
	}
	
	private void fixSize()
	{

		int tabbedHeightOffset = 30;
		if(System.getProperty("os.name").equals("Mac OS X"))
			tabbedHeightOffset = 47;
		
		//Stole this code from TABLE GUI
		double SCALE_FACTOR = 1;
		if(Toolkit.getDefaultToolkit().getScreenSize().getWidth() < 1300.0 || Toolkit.getDefaultToolkit().getScreenSize().getHeight() < 800.0)
		{
			SCALE_FACTOR = 0.79; //The value of 0.79 changes the internal window size from 1300x800 to 1027x632
		}
		if(System.getProperty("os.name").equals("Mac OS X"))
			setSize(((int)(1300*SCALE_FACTOR))+21,((int)(800*SCALE_FACTOR))+22+tabbedHeightOffset);
		else if(System.getProperty("os.name").equals("Windows 7"))
			setSize(((int)(1300*SCALE_FACTOR)+6),((int)(800*SCALE_FACTOR))+28 + tabbedHeightOffset);
		else
			setSize(((int)(1300*SCALE_FACTOR)+6),((int)(800*SCALE_FACTOR))+28 + tabbedHeightOffset);
		
		//this sets the location of the JFrame to the center of the screen
		setLocation(((Toolkit.getDefaultToolkit().getScreenSize().width - getSize().width) / 2), 
				((Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 2)-15);
		
	}
	
	public void addTable(TableGUI table, String title)
	{
		tabs.add(title, table);
		tables.add(table);
		int location = tabs.indexOfComponent(table);
		if(location != -1)
		{
			tabs.setSelectedIndex(location);
			tabs.setTabComponentAt(location, new ExitTabButton(tabs, this));
		}
		if(isVisible() == false)
		{
			tabs.setVisible(true);
			this.setVisible(true);
		}
		table.triggerBuyIn();
	}
	
	@Override
	public void remove(int index)
	{
		
		//do our cleanup
		TableGUI table = (TableGUI)tabs.getComponentAt(index);
		table.sendCloseMessage();
			//tell the server we're folding
		tables.remove(table);
			//remove our reference to the table
		tabs.remove(index);
			//remove the actual tab
		client.removeTable(table);
			//remove the reference in the client
		
		if(tables.size() == 0)
		{
			//no more tables left so close this bad boy
			this.setVisible(false);
		}		
	}
	
	public void removeTable(TableGUI table)
	{
		int index = tabs.indexOfComponent(table);
		
		if(index != -1) //check this baby
		{
			tabs.remove(index);
			//remove the actual tab
	
			tables.remove(table);
				//remove our reference to the table
			
			client.removeTable(table);
				//remove the reference in the client
		}
		if(tables.size() == 0)
		{
			//no more tables left so close this bad boy
			this.setVisible(false);
		}	
	}
}
