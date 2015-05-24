package com.pokerisland;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.pokerisland.*;

public class IPGUI extends JFrame
{
	private static final long serialVersionUID = 1L;

	private Client client;
	
	//GUI components
	private JLabel ipLabel;
	private JTextField ipTextField;

	private JButton submitButton;

	private JLabel ipTitle;
	
	IPGUI(Client client)
	{
		super("Poker Island - Connect");
		this.client = client;
		
		instantiateComponents();
		createGUI();
		addListeners();
	
	}
	
	private void instantiateComponents()
	{
		ipTitle = new JLabel("Please Enter the Server's IP Address");
		
		ipLabel = new JLabel("Server IP: ");
		ipTextField = new JTextField(15);
		ipTextField.setText(Constants.defaultIP);
		
		
		submitButton = new JButton("Submit");
	}
	
	private void createGUI()
	{
		setSize(300,150);
		setResizable(false);
		JPanel window = new JPanel();
		
		window.add(ipTitle, BorderLayout.NORTH);
		
		JPanel ipPanel = new JPanel();
		ipPanel.add(ipLabel, BorderLayout.WEST);
		ipPanel.add(ipTextField, BorderLayout.CENTER);
		window.add(ipPanel, BorderLayout.CENTER);
		
		window.add(submitButton, BorderLayout.SOUTH);
		
		add(window);
		
		setLocation(((Toolkit.getDefaultToolkit().getScreenSize().width - getSize().width) / 2), 
				((Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 2)-15);
		
	}
	
	private void addListeners()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		ipTextField.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				client.connectToServer(ipTextField.getText());
			}
		});
		//TODO fix this: when you type in the wrong IP and press "Submit", the program freezes 
		submitButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				client.connectToServer(ipTextField.getText());
			}
			
		});
	}
}
