package com.pokerisland;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;

import com.pokerisland.Constants;

public class BuyInGUI extends JDialog
{
	private static final long serialVersionUID = 1;
    private int minimumBuyIn, maximumBuyIn, tableNumber, blindLevel; 
	private JLabel available, buyInAmountLabel; 
	private JSlider buyInAmountSlider; 
	private JButton okButton, cancelButton, minButton, maxButton; 
	private Client client;

    public BuyInGUI(Client client, int tableNumber, int blindLevel)
	{
		this.setTitle("Buy In");
		this.tableNumber = tableNumber;
		setSize(500, 200); 
		setLocation(((Toolkit.getDefaultToolkit().getScreenSize().width - getSize().width) / 2), 
				((Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 2)-15);
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel"); 
		JPanel bottomHolder = new JPanel(); 
		this.client = client;
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we)
			{
				BuyInGUI.this.client.removeTable(BuyInGUI.this.tableNumber);
				setVisible(false);
			}
		});
		minButton = new JButton("Min"); 
		maxButton = new JButton("Max"); 

		JPanel overallHolder = new JPanel(new BorderLayout()); 
		available = new JLabel("Available " + Constants.moneyName + " are: " + 1);
		JPanel topHolder = new JPanel(); 
		topHolder.add(available); 
		overallHolder.add(topHolder, BorderLayout.NORTH); 

		JLabel input = new JLabel("Buy In Amount: "); 
		buyInAmountLabel = new JLabel(40*blindLevel + ""); 

		buyInAmountSlider = new JSlider(JSlider.HORIZONTAL); 

        this.blindLevel = blindLevel;
        minimumBuyIn = 40*blindLevel; 
        maximumBuyIn = 5000; //temp value

		buyInAmountSlider.setMajorTickSpacing(maximumBuyIn-minimumBuyIn);
		buyInAmountSlider.setPaintTicks(true);
		buyInAmountSlider.setPaintLabels(true);
		

		buyInAmountSlider.setMinimum(minimumBuyIn);
		buyInAmountSlider.setMaximum(maximumBuyIn);

		JPanel middleTop = new JPanel(); 
		middleTop.add(input); 
		middleTop.add(buyInAmountLabel); 
		
		JPanel middleMid = new JPanel(); 
		middleMid.add(minButton); 
		middleMid.add(buyInAmountSlider); 
		middleMid.add(maxButton); 
		
		bottomHolder.add(okButton); 
		bottomHolder.add(cancelButton); 
		
		setActions();
		
		JPanel middle = new JPanel(new BorderLayout()); 
		
		middle.add(middleTop, BorderLayout.NORTH); 
		middle.add(middleMid, BorderLayout.CENTER); 

		overallHolder.add(middle, BorderLayout.CENTER);
		overallHolder.add(bottomHolder, BorderLayout.SOUTH); 

		add(overallHolder); 
		
	}
	public void setVisible(boolean isVisible)
	{
		minimumBuyIn = 40*blindLevel;
		if (client.getPlayerNetWorth() > 40*blindLevel)
		{
			maximumBuyIn = Math.min((100*blindLevel), client.getPlayerNetWorth());
		}
		else
		{
			maximumBuyIn = 100*blindLevel;
		}
		buyInAmountSlider.setMinimum(minimumBuyIn);
		buyInAmountSlider.setMaximum(maximumBuyIn);
		buyInAmountSlider.setValue(minimumBuyIn);
		
		buyInAmountSlider.setMajorTickSpacing((maximumBuyIn-minimumBuyIn));
		buyInAmountSlider.setPaintTicks(true);
		buyInAmountSlider.setPaintLabels(true);

		buyInAmountSlider.setLabelTable(buyInAmountSlider.createStandardLabels(maximumBuyIn - minimumBuyIn));
		
		setModal(true);
		super.setVisible(isVisible);
	}

	private void setActions()
	{
		minButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				buyInAmountSlider.setValue(minimumBuyIn); 
			}
		}); 
		maxButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				buyInAmountSlider.setValue(maximumBuyIn); 
			}
		});
		buyInAmountSlider.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(ChangeEvent ce) 
			{
				buyInAmountLabel.setText(""+buyInAmountSlider.getValue()); 
				if (buyInAmountSlider.getValue() > client.getPlayerNetWorth())
				{
					okButton.setEnabled(false);
				}
				else
				{
					okButton.setEnabled(true);
				}
			}
		});
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				client.removeTable(tableNumber);
				setVisible(false);
			}
		});
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String result = buyInAmountLabel.getText(); 
				client.sendMessage("table;"+tableNumber+";joins;"+result);
				client.withdrawMoney(Integer.parseInt(result));
				setVisible(false);
			}
		});
	}
	
	public void setNetWorth(int num)
	{
		available.setText("You have "+num+" "+Constants.moneyName+" in your account"); 
	}/*
	/**
	 * Assigns this BuyInGUI to a specific table number, so that when the user presses "OK", it joins that table
	 * @param tableNumber the Table ID to join
	 
	public void setTableNumber(int tableNumber)
	{
		this.tableNumber = tableNumber;
	}*/
	public void setMinimumBuyIn(int buyInAmount)
	{
		minimumBuyIn = buyInAmount; 
		buyInAmountSlider.setMinimum(minimumBuyIn);
	}

	public void setMaximumBuyIn(int buyInAmount)
	{
		maximumBuyIn = buyInAmount; 
		buyInAmountSlider.setMaximum(maximumBuyIn); 
	}

	public static void main(String[] args)
	{
        BuyInGUI gui = new BuyInGUI(null, 0, 69); 
		gui.setVisible(true);
	}
}
