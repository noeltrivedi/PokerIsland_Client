package com.pokerisland;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class ExitTabButton extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTabbedPane pane;
	private TabbedTables tabbedTables;
	
	ExitTabButton(JTabbedPane pane, TabbedTables tabbedTables)
	{
		this.pane = pane;
		this.tabbedTables = tabbedTables;
		
		createGUI();
	}
	
	private void createGUI()
	{
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setOpaque(false);
		
		@SuppressWarnings("serial")
		JLabel tabLabel = new JLabel()
		{
			public String getText()
			{
				int location = pane.indexOfTabComponent(ExitTabButton.this);
				if(location != -1)
				{
					return pane.getTitleAt(location);
				}
				else
				{
					return "Error!";
				}
			}
		};
		
		add(tabLabel);
		tabLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		JButton exitButton = new ExitButton();
		add(exitButton);
		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
	}
	
	private class ExitButton extends JButton
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ExitButton() 
		{
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("Exit this game!");
            //Make the button looks the same for all Laf's
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e)
				{
					int location = pane.indexOfTabComponent(ExitTabButton.this);
					if(location != -1)
					{
						int selection = JOptionPane.showConfirmDialog(tabbedTables, "Are you sure you want to leave?\n"
								+ "This will fold your hand and cash out.", "Confirmation", JOptionPane.YES_NO_OPTION);
						switch(selection)
						{
						case JOptionPane.YES_OPTION:
							tabbedTables.remove(location);
							break;
						default:
							break;
						}
					}
				}
            	
            });
        }
		
		protected void paintComponent(Graphics g) 
		{
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            //shift the image for pressed buttons
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);
            if (getModel().isRollover()) {
                g2.setColor(Color.MAGENTA);
            }
            int delta = 6;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
	}
}
