package com.pokerisland;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import com.pokerisland.Constants;

public class GuestTimerGUI extends JDialog
{
	private static final long serialVersionUID = 1;
	Timer timer;
	JLabel secondsLabel;
	JLabel minutesLabel;
	
	Client client;
	
	int seconds = 0;
	int minutes = Constants.guestStartingMinutes;
	
	boolean debug = false;
	
	public static void main(String[] args)
	{
		new GuestTimerGUI(new Client()).setVisible(true);
	}
	
	GuestTimerGUI(Client client)
	{
		setTitle("Guest Timer");
		this.client = client;
		createTimer();
		createGUI();
	}
	
	private void createGUI()
	{
		setSize(200,90);
		setLayout(null);
		secondsLabel = new JLabel("00");
		JLabel colon = new JLabel(":");
		minutesLabel = new JLabel("" + Constants.guestStartingMinutes);
		
		Font font = new Font(Constants.fontName, Font.BOLD, 24);
		minutesLabel.setFont(font);
		secondsLabel.setFont(font);
		colon.setFont(font);
		
		JLabel timeRemainingLabel = new JLabel("Time Remaining:", SwingConstants.CENTER);
		timeRemainingLabel.setFont(new Font(Constants.fontName, Font.PLAIN, 20));
		timeRemainingLabel.setBounds(2, 2, 194, 35);
		
		JPanel timerPanel = new JPanel();
		timerPanel.setLayout(new FlowLayout());
		timerPanel.add(minutesLabel);
		timerPanel.add(colon);
		timerPanel.add(secondsLabel);
		timerPanel.setBounds(0, 27, 194, 40);
		add(timeRemainingLabel);
		add(timerPanel);
		
		setResizable(false);
		
		if(debug)
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		else
			setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}
	
	private void createTimer()
	{
		ActionListener updateTimer = new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				seconds--;
				if(seconds == -1)
				{
					minutes--;
					seconds = 60-1; //60 seconds in a minute minus 1
				}
				
				updateText();

				if(minutes == 0 && seconds == 0)
				{
					timerDone();
					timer.stop();
				}
			}
		};
		
		timer = new Timer(1000, updateTimer);
		timer.setInitialDelay(0);
	}
	
	private void timerDone()
	{
		JOptionPane.showMessageDialog(null, "We're sorry but you've run out of time! "
				+ "\nWe hope you've enjoyed your stay on Poker Island", "Time's up!", JOptionPane.INFORMATION_MESSAGE);
		client.closingTime();
	}
	
	private void updateText()
	{
		if(seconds < 10)
			secondsLabel.setText("0" + seconds);
		else
			secondsLabel.setText("" + seconds);
		
		minutesLabel.setText("" + minutes);
	}
	
	private void resetData()
	{
		seconds = 0;
		minutes = Constants.guestStartingMinutes;
	}
	
	public void setVisible(boolean visible)
	{
		resetData();
		updateText();
		timer.start();
		super.setVisible(visible);
	}
	
}
