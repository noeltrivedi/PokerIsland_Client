package com.pokerisland;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Quickplay extends JDialog
{
	private static final long serialVersionUID = 1;
	private JComboBox<String> tables, blindsBox, options;
	private JButton playButton;
	private Client client;
	public Quickplay (Client client)
	{
		setTitle("Quickplay"); 
		setResizable(false);
		setModal(true);
		this.client = client;
		JPanel overall = new JPanel(new BorderLayout()); 	
		JPanel centralHolder1 = new JPanel(); 
		JPanel centralHolder2 = new JPanel(); 
		JPanel centralHolder3 = new JPanel(); 
		JPanel centralHolder = new JPanel(); 
		
		JLabel tableLabel = new JLabel("Table Size"); 
		JLabel blindLabel = new JLabel("Blind Limit"); 
		JLabel numLabel =  new JLabel("Number of Tables to Join"); 
	
		String[] tableSizes = {"Heads Up (2)", "6-max (6)", "Full Ring (9)"}; 
		String[] blinds = {"1/2", "2/5", "5/10", "10/20"}; 
		String[] numberReturned = {"One", "Two", "Three"}; 
		
		tables = new JComboBox<String>(tableSizes);
		tables.setSelectedIndex(0); 
		blindsBox = new JComboBox<String>(blinds);
		blindsBox.setSelectedIndex(0); 
		options = new JComboBox<String>(numberReturned);
		options.setSelectedIndex(0); 
		
		
		centralHolder1.add(tableLabel); 
		centralHolder1.add(tables); 
		centralHolder2.add(blindLabel); 
		centralHolder2.add(blindsBox); 
		centralHolder3.add(numLabel); 
		centralHolder3.add(options); 
		
		centralHolder.add(centralHolder1); 
		centralHolder.add(centralHolder2);
		centralHolder.add(centralHolder3);
		
		playButton = new JButton("Play"); 
		playButton.setFocusPainted(false);
		
		setActions();
		
		centralHolder.add(playButton); 
		
		overall.add(centralHolder, BorderLayout.CENTER); 
		
		add(overall); 
		 
		
		setSize(270, 200); 
		setLocation(((Toolkit.getDefaultToolkit().getScreenSize().width - getSize().width) / 2), 
			((Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 2)-15);
	}
	public void setActions()
	{
		playButton.addActionListener(new ActionListener() 
		{
            public void actionPerformed(ActionEvent e)
            {
                //parsing
                String tableSize = tables.getSelectedItem().toString();
                tableSize = tableSize.substring(tableSize.indexOf("(") + 1);
                tableSize = tableSize.substring(0, tableSize.indexOf(")"));
                
                String blindDesired = blindsBox.getSelectedItem().toString();
                blindDesired = blindDesired.substring(blindDesired.lastIndexOf("/") + 1);
                
                String returnableNum = options.getSelectedItem().toString(); 
                if (returnableNum.equals("One"))
                	returnableNum = "1";
                if (returnableNum.equals("Two"))
                	returnableNum = "2";
                if (returnableNum.equals("Three"))
                	returnableNum = "3";
                if (returnableNum.equals("Four"))
                	returnableNum = "4";
                                
                client.sendMessage("joinQuickplay;"+tableSize+";"+blindDesired+";"+returnableNum);
                setVisible(false);
            }
        }); 
	}
	public static void main(String[] args)
	{
		new Quickplay(null).setVisible(true); 
	}
}
