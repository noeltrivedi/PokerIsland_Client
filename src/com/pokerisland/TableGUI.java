package com.pokerisland;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class TableGUI extends JPanel
{
	private static final long serialVersionUID = 1;
	/**
	 * a {@code Coordinate} array where each index in the array
	 * corresponds to the top left pixel of each player's {@code RoundRectPanel}
	 */
	private Coordinate[] playerLocations = {new Coordinate(507, 632),  //player 1
			new Coordinate(163, 632),  //player 2
			new Coordinate(5, 410),    //player 3
			new Coordinate(5, 204),    //player 4
			new Coordinate(335, 33),   //player 5
			new Coordinate(680, 33),   //player 6
			new Coordinate(1003, 204), //player 7
			new Coordinate(1003, 410), //player 8
			new Coordinate(851, 632)}; //player 9
	/**
	 * a {@code RoundRectPanel} array of each player's {@code RoundRectPanel}
	 */
	private RoundRectPanel[] playerPanels;
	/**
	 * a {@code JLabel} array of the five cards on the table
	 */
	private JLabel[] tableCards;
	/**
	 * a {@code JLabel} array of the text representing each player's most recent action
	 */
	private JLabel[] playerActions;
	/**
	 * a {@code JLabel} array of each player's first card in their hand
	 */
	private JLabel[] playerFirstCards;
	/**
	 * a {@code JLabel} array of each player's second card in their hand
	 */
	private JLabel[] playerSecondCards;
	/**
	 * a {@code JLabel} array of the title bars of all the players, which are shown on the RoundRectPanels
	 */
	private JLabel[] playerNameAndStackSizeLabels;
	/**
	 * {@code String[]} array of the names of all 9 players
	 */
	private String[] playerNames;
	/**
	 * {@code int[]} array of the number of current chips that each player has
	 */
	private int[] playerStackSizes;

	private Client client;
	private LobbyGUI lobbyGUI;
	/**
	 * <pre>
	 * an {@code ImageIcon} array of all 52 card images, 
	 * with the addition of the back side of the card
	 * 
	 * cards are stored as follows: [Rank][Suit]
	 * 
	 * for example, 
	 * cardIcons[Rank.ACE.value()][Suit.SPADE.value()] 
	 * will return the Ace of Spades 
	 * 
	 * cardIcons[0][0] will return the back of the card
	 * </pre>
	 */
	private ImageIcon[][] cardIcons;
	/**
	 * Default value for POKER_CHIP_WIDTH = 75
	 */
	private int POKER_CHIP_WIDTH = 75;
	/**
	 * Default value for RECT_WIDTH = 291
	 */
	private int RECT_WIDTH = 291;
	/**
	 * Default value for RECT_HEIGHT = 161
	 */
	private int RECT_HEIGHT = 161;
	/**
	 * Default value for NAME_VERTICAL_OFFSET = 80
	 */
	private int NAME_VERTICAL_OFFSET = 80;
	/**
	 * Default value for ACTION_VERTICAL_OFFSET = 128
	 */
	private int ACTION_VERTICAL_OFFSET = 128;
	/**
	 * Default value for CARD_VERTICAL_OFFSET = -32
	 */
	private int CARD_VERTICAL_OFFSET = -32;
	/**
	 * Default value for FIRST_CARD_HORIZONTAL_OFFSET = 58
	 */
	private int FIRST_CARD_HORIZONTAL_OFFSET = 58;
	/**
	 * Default value for SECOND_CARD_HORIZONTAL_OFFSET = 161
	 */
	private int SECOND_CARD_HORIZONTAL_OFFSET = 161;
	/**
	 * Default value for DEALER_BUTTON_HORIZONTAL_OFFSET = -10
	 */
	private int DEALER_BUTTON_HORIZONTAL_OFFSET = -10;
	/**
	 * Default value for DEALER_BUTTON_VERTICAL_OFFSET = -10
	 */
	private int DEALER_BUTTON_VERTICAL_OFFSET = -10;
	/**
	 * Default value for BLIND_BUTTON_HORIZONTAL_OFFSET = 244
	 */
	private int BLIND_BUTTON_HORIZONTAL_OFFSET = 244;
	/**
	 * Default value for BLIND_BUTTON_VERTICAL_OFFSET = -10
	 */
	private int BLIND_BUTTON_VERTICAL_OFFSET = -10;
	/**
	 * Default value for BLIND_DEALER_BUTTON_WIDTH = 57
	 */
	private int BLIND_DEALER_BUTTON_WIDTH = 57;
	/**
	 * Default value for CARD_WIDTH = 72
	 */
	private static final int CARD_WIDTH = 72;
	/**
	 * Default value for CARD_HEIGHT = 97
	 */
	private static final int CARD_HEIGHT = 97;
	/**
	 * Default value for DISTANCE_BETWEEN_TABLE_CARDS = 31
	 */
	private int DISTANCE_BETWEEN_TABLE_CARDS = 31;
	/**
	 * Default value for TOP_ACTION_BUTTON_VERTICAL_COORDINATE = 14
	 */
	private int TOP_ACTION_BUTTON_VERTICAL_COORDINATE = 14;
	/**
	 * Default value for ACTION_BUTTON_HORIZONTAL_COORDINATE = 83
	 */
	private int ACTION_BUTTON_HORIZONTAL_COORDINATE = 83;
	/**
	 * Default value for ACTION_BUTTON_WIDTH = 154
	 */
	private int ACTION_BUTTON_WIDTH = 154;
	/**
	 * Default value for ACTION_BUTTON_HEIGHT = 41
	 */
	private int ACTION_BUTTON_HEIGHT = 41;
	/**
	 * Changing SCALE_FACTOR will change the size of the TableGUI
	 * Do not change SCALE_FACTOR here; change it at the beginning of initializeVariables()
	 * The default size of the internal window (not inclusing the OS title bar/border) is 1300x800 
	 * So for example, changing SCALE_FACTOR to 2 would create a TableGUI of size 2600,1600
	 */
	private static double SCALE_FACTOR = 1;
	private RoundButton checkButton, callButton, foldButton, raiseButton, betButton;
	private JSlider betAmountSlider;
	private JLabel timerLabel, dealerButton, bigBlindButton, smallBlindButton, sliderCover, potLabel;
	private JLabel[] pokerChipArray;
	private JCheckBox soundCheckBox;
	private Clip[] cardSlideClipArray, chipSlideClipArray;
	private int time, potAmount, minimumBetAmount, maximumBetAmount, uniqueID, cardSlideSoundCounter, blindLevel;
	public static boolean soundOn = true;
	private Timer timer;
	protected BuyInGUI buyInGUI; 
	private static final boolean debug = false;

	private class ChipAnimationThread extends Thread
	{
		int seatedPlayerPosition, xAmountToIncrement, yAmountToIncrement;
		Coordinate startCoordinate;
		Coordinate endCoordinate;
		int numAnimationFrames;
		public ChipAnimationThread(int seatedPlayerPosition, boolean toTable)
		{
			this.seatedPlayerPosition = seatedPlayerPosition;
			if(toTable)
			{
				this.startCoordinate = new Coordinate(playerLocations[this.seatedPlayerPosition].getX()+(RECT_WIDTH/2)-(POKER_CHIP_WIDTH/2), 
						playerLocations[this.seatedPlayerPosition].getY()+(RECT_HEIGHT/2)-(POKER_CHIP_WIDTH/2));
				this.endCoordinate = new Coordinate((int)(SCALE_FACTOR*617), (int)(SCALE_FACTOR*260));
			}
			else
			{
				this.startCoordinate = new Coordinate((int)(SCALE_FACTOR*617), (int)(SCALE_FACTOR*260));
				this.endCoordinate = new Coordinate(playerLocations[this.seatedPlayerPosition].getX()+(RECT_WIDTH/2)-(POKER_CHIP_WIDTH/2), 
						playerLocations[this.seatedPlayerPosition].getY()+(RECT_HEIGHT/2)-(POKER_CHIP_WIDTH/2));
			}
			numAnimationFrames = 30;
			xAmountToIncrement = (endCoordinate.getX() - startCoordinate.getX())/numAnimationFrames;
			yAmountToIncrement = (endCoordinate.getY() - startCoordinate.getY())/numAnimationFrames;
		}
		public void run()
		{
			if(soundOn)
			{
				if(seatedPlayerPosition == 8)
				{
					chipSlideClipArray[2].setFramePosition(0);
					chipSlideClipArray[2].start();
				}
				else
				{
					chipSlideClipArray[seatedPlayerPosition/2].setFramePosition(0);
					chipSlideClipArray[seatedPlayerPosition/2].start();
				}
			}
			pokerChipArray[seatedPlayerPosition].setBounds(startCoordinate.getX(), startCoordinate.getY(), POKER_CHIP_WIDTH, POKER_CHIP_WIDTH);
			pokerChipArray[seatedPlayerPosition].setVisible(true);
			try
			{
				for(int i = 2; i < numAnimationFrames+2; i++)
				{
					sleep(30); //seconds = sleep# * numFrames
					pokerChipArray[seatedPlayerPosition].setBounds(startCoordinate.getX()+(i*xAmountToIncrement), 
							startCoordinate.getY()+(i*yAmountToIncrement), POKER_CHIP_WIDTH, POKER_CHIP_WIDTH);
				}
			}
			catch(InterruptedException ie)
			{
				System.out.println("InterruptedException in ChipAnimationThread: "+ie.getMessage());
			}
			pokerChipArray[seatedPlayerPosition].setVisible(false);
		}
	}
	private class CardShowDelayAndSoundThread extends Thread
	{
		int seatedPlayerPosition, firstCardRankValue, firstCardSuitValue, secondCardRankValue, secondCardSuitValue,
		thirdCardSuitValue, thirdCardRankValue, numCards;
		/**
		 * Creates a Thread that animates the showing of cards
		 * <pre><ul>{@code public CardShowDelayAndSoundThread(int seatedPlayerPosition, 
		 * 			       int firstCardRankValue,
		 * 			       int firstCardSuitValue,
		 * 			       int secondCardRankValue,
		 * 			       int secondCardSuitValue,
		 * 			       int thirdCardRankValue,
		 * 			       int thirdCardSuitValue)}<ul></pre>
		 * @param seatedPlayerPosition seat position of the player to animate; for flop set to 10; turn, 20; river, 30
		 * @param firstCardRankValue the Rank of the first card to be shown
		 * @param firstCardSuitValue the Suit of the first card to be shown
		 * @param secondCardRankValue the Rank of the second card to be shown; set to -1 if not applicable
		 * @param secondCardSuitValue the Suit of the second card to be shown; set to -1 if not applicable
		 * @param thirdCardRankValue the Rank of the third card to be shown; set to -1 if not applicable
		 * @param thirdCardSuitValue the Suit of the third card to be shown; set to -1 if not applicable
		 */
		public CardShowDelayAndSoundThread(int seatedPlayerPosition, int firstCardRankValue, 
				int firstCardSuitValue, int secondCardRankValue, int secondCardSuitValue,
				int thirdCardRankValue, int thirdCardSuitValue)
		{
			this.seatedPlayerPosition = seatedPlayerPosition;
			this.firstCardRankValue = firstCardRankValue;
			this.firstCardSuitValue = firstCardSuitValue;
			this.secondCardRankValue = secondCardRankValue;
			this.secondCardSuitValue = secondCardSuitValue;
			this.thirdCardRankValue = thirdCardRankValue;
			this.thirdCardSuitValue = thirdCardSuitValue;
			numCards = 1;
			if(this.secondCardRankValue != -1)
				numCards++;
			if(this.thirdCardRankValue != -1)
				numCards++;
		}
		public void run()
		{
			for(int i = 0; i < numCards; i++)
			{
				if(soundOn)
				{
					cardSlideClipArray[cardSlideSoundCounter].setFramePosition(0);
					cardSlideClipArray[cardSlideSoundCounter].start();
					if(cardSlideSoundCounter > 7)
						cardSlideSoundCounter = 0;
					else
						cardSlideSoundCounter++;
				}

				if(seatedPlayerPosition < 10) //if player cards
				{
					if(i == 0)
					{
						playerFirstCards[seatedPlayerPosition].setIcon(cardIcons[firstCardRankValue][firstCardSuitValue]);
						playerFirstCards[seatedPlayerPosition].setVisible(true);
					}
					else
					{
						playerSecondCards[seatedPlayerPosition].setIcon(cardIcons[secondCardRankValue][secondCardSuitValue]);
						playerSecondCards[seatedPlayerPosition].setVisible(true);
					}
				}
				else //else if table cards
				{
					if(seatedPlayerPosition == 10) //if flop
					{
						if(i == 0)
						{
							tableCards[0].setIcon(cardIcons[firstCardRankValue][firstCardSuitValue]);
							tableCards[0].setVisible(true);
						}
						else if(i == 1)
						{
							tableCards[1].setIcon(cardIcons[secondCardRankValue][secondCardSuitValue]);
							tableCards[1].setVisible(true);
						}
						else
						{
							tableCards[2].setIcon(cardIcons[thirdCardRankValue][thirdCardSuitValue]);
							tableCards[2].setVisible(true);
						}
					}
					else if(seatedPlayerPosition == 20) //if turn
					{
						tableCards[3].setIcon(cardIcons[firstCardRankValue][firstCardSuitValue]);
						tableCards[3].setVisible(true);
					}
					else if(seatedPlayerPosition == 30) //if river
					{
						tableCards[4].setIcon(cardIcons[firstCardRankValue][firstCardSuitValue]);
						tableCards[4].setVisible(true);
					}
				}
				if(i != numCards-1)
				{
					try
					{
						sleep(300);
					}
					catch(InterruptedException e)
					{
						System.out.println("InterruptedException in CardShowDelayAndSoundThread: "+e.getMessage());
					}
				}
			}
		}
	}
	private class Coordinate
	{
		private int x, y;
		public Coordinate(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
		public int getX()
		{
			return x;
		}
		public int getY()
		{
			return y;
		}
	}
	private class RoundButton extends JButton 
	{
		private static final long serialVersionUID = 1;
		public RoundButton(String text) 
		{
			super(text);

			//this causes the JButton not to paint the background
			setContentAreaFilled(false);
			setBorderPainted(false);

			//after you click on the button, there is not a box around the text
			setFocusPainted(false);
		}

		//paint the round background and label
		protected void paintComponent(Graphics g) 
		{
			if (getModel().isArmed()) 
			{
				//you might want to make the highlight color a property of the RoundButton class.
				g.setColor(Color.gray);
			} 
			else 
			{
				g.setColor(getBackground());
			}
			int arcWidth = (int)(42*SCALE_FACTOR);
			g.fillRoundRect(0, 0, ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, arcWidth, arcWidth);

			//this will paint the label and the focus rectangle
			super.paintComponent(g);
		}
	}
	private class RoundRectPanel extends JPanel
	{
		private static final long serialVersionUID = 1;
		private Color color;
		public RoundRectPanel(Color color)
		{
			super();
			this.color = color;
		}
		private AlphaComposite makeComposite(float alpha) 
		{
			int type = AlphaComposite.SRC_OVER;
			return(AlphaComposite.getInstance(type, alpha));
		}
		protected void paintComponent(Graphics g) 
		{
			Graphics2D g2d = (Graphics2D)g;
			g2d.setPaint(color);

			//if color equals yellow, make it less transparent
			if(color.equals(new Color(163,158,20)))
				g2d.setComposite(makeComposite((float).75));
			else
				g2d.setComposite(makeComposite((float).56));
			int arcWidth = (int)(94*SCALE_FACTOR);
			g2d.fillRoundRect(0, 0, RECT_WIDTH, RECT_HEIGHT, arcWidth, arcWidth);
			//if color equals white, yellow, or black, change the underline color
			if(color.equals(new Color(163,158,20)) || 
					color.equals(new Color(255,255,255)) ||
					color.equals(new Color(0,0,0)))
			{
				if(color.equals(new Color(163,158,20)))
					g2d.setPaint(new Color(76,74,9));
				else if(color.equals(new Color(255,255,255)))
					g2d.setPaint(new Color(58,58,58));
				else if(color.equals(new Color(0,0,0)))
					g2d.setPaint(new Color(114,114,114));
			}
			else
			{
				g2d.setPaint(color.darker());
			}
			g2d.setStroke(new BasicStroke(3));
			g2d.drawLine((int)(SCALE_FACTOR*40), (int)(SCALE_FACTOR*125), (int)(SCALE_FACTOR*253), (int)(SCALE_FACTOR*125));
		}
	}
	/**
	 * Adds player to the table at the designated 
	 * seat position with given username and with given number of chips
	 * <pre><ul>{@code public void addPlayer(int seatedPlayerPosition,
	 * 		   String username,
	 * 		   int buyInAmount)}</ul></pre>
	 * @param seatedPlayerPosition the seat position to seat the player at. Seat numbers range from 0 to 8.
	 * @param username the username of the player to be seated
	 * @param buyInAmount the amount of chips (stack size) of the player
	 */
	public void addPlayer(int seatedPlayerPosition, String username, int buyInAmount)
	{
		if(seatedPlayerPosition >= 0 && seatedPlayerPosition <=8)
		{
			playerPanels[seatedPlayerPosition].setVisible(true);
			playerNames[seatedPlayerPosition] = username;
			playerStackSizes[seatedPlayerPosition] = buyInAmount;
			String jpRegex = "[\\u3000-\\u303F]|[\\u3040-\\u309F]|[\\u30A0-\\u30FF]|[\\uFF00-\\uFFEF]|[\\u4E00-\\u9FAF]|[\\u2605-\\u2606]|[\\u2190-\\u2195]|\\u203B";
			String inRegex = "[\\u0900-\\u097F]";
			int inFontSize = 26;
			if(!System.getProperty("os.name").equals("Mac OS X"))
				inFontSize = 36;
			if(((Pattern.compile(jpRegex)).matcher(username)).find())
				playerNameAndStackSizeLabels[seatedPlayerPosition].setFont(new Font(Constants.jpFontName, Font.BOLD, (int)(SCALE_FACTOR*24)));
			else if((((Pattern.compile(inRegex)).matcher(username)).find()))
				playerNameAndStackSizeLabels[seatedPlayerPosition].setFont(new Font(Constants.inFontName, Font.BOLD, (int)(SCALE_FACTOR*inFontSize)));
			else if(((Pattern.compile(".*\\p{IsHangul}.*")).matcher(username)).find())
				playerNameAndStackSizeLabels[seatedPlayerPosition].setFont(new Font(Constants.knFontName, Font.BOLD, (int)(SCALE_FACTOR*26)));
			else
				playerNameAndStackSizeLabels[seatedPlayerPosition].setFont(new Font(Constants.fontName, Font.BOLD, (int)(SCALE_FACTOR*26)));
			playerNameAndStackSizeLabels[seatedPlayerPosition].setVisible(true);
			playerNameAndStackSizeLabels[seatedPlayerPosition].setText(username+": "+buyInAmount);
		}
		else
		{
			System.err.println("Error: Out-of-bounds access in TableGUI.addPlayer()");
		}
	}
	/**
	 * Removes player at the specified position from the table
	 * <pre><ul>{@code public void removePlayer(int seatedPlayerPosition)}</ul></pre>
	 * @param seatedPlayerPosition the seat position of the player to be removed
	 */
	public void removePlayer(int seatedPlayerPosition)
	{
		if(seatedPlayerPosition >= 0 && seatedPlayerPosition <=8)
		{
			playerPanels[seatedPlayerPosition].setVisible(false);
			playerNameAndStackSizeLabels[seatedPlayerPosition].setVisible(false);
			playerActions[seatedPlayerPosition].setVisible(false);
			playerFirstCards[seatedPlayerPosition].setVisible(false);
			playerSecondCards[seatedPlayerPosition].setVisible(false);
		}
		else
		{
			System.err.println("Error: Out-of-bounds access in TableGUI.removePlayer()");
		}
		/*called when a Player leaves the table
		shows JDialog prompting user to confirm that they intend to exit
		updates the Player's HashMap and GamePlay's HashMap
		sends message to Server to update users table in DB to reflect the player's new balance*/
	}
	/**
	 * Animates and transfers chips from the specified player's stack to the pot
	 * <pre><ul>{@code public void putInChips(int seatedPlayerPosition, int amountOfChips)}</ul></pre>
	 * @param seatedPlayerPosition the seat position of the player to animate from
	 * @param amountOfChips the amount of chips to be transfered
	 */
	public void putInChips(int seatedPlayerPosition, int amountOfChips)
	{
		if((seatedPlayerPosition >= 0) && (seatedPlayerPosition <= 8))
		{
			//animate chips to pot
			new ChipAnimationThread(seatedPlayerPosition, true).start();
			potAmount += amountOfChips;
			setPotAmount(potAmount);
		}
		else
			System.err.println("Error: Out-of-bounds access in TableGUI.putInChips()");
	}
	/**
	 * Animates and transfers chips from the pot to the specified player's stack
	 * <pre><ul>{@code public void takeOutChips(int seatedPlayerPosition, int amountOfChips)}</ul></pre>
	 * @param seatedPlayerPosition the seat position of the player to animate to
	 * @param amountOfChips the amount of chips to be transfered
	 */
	public void takeOutChips(int seatedPlayerPosition, int amountOfChips)
	{
		if((seatedPlayerPosition >= 0) && (seatedPlayerPosition <= 8))
		{
			//animate chips to pot
			new ChipAnimationThread(seatedPlayerPosition, false).start();
			potAmount -= amountOfChips;
			setPotAmount(potAmount);
		}
		else
			System.err.println("Error: Out-of-bounds access in TableGUI.putInChips()");
	}
	/**
	 * Sets the pot to show the given amount of chips
	 * <pre><ul>{@code public void setPotAmount(int potAmount)}</ul></pre>
	 * @param potAmount the amount that the pot will be set to
	 */
	public void setPotAmount(int potAmount)
	{
		potLabel.setText("Pot: "+potAmount);
		potLabel.setVisible(true);
	}
	/**
	 * Sets the specified player's last action to the specified text
	 * <pre><ul>{@code public void setPlayerAction(int seatedPlayerPosition, String action)}</ul></pre>
	 * @param seatedPlayerPosition the seat position of the player to set the action on
	 * @param action the {@code String} containing the action (Check, Bet, Fold, etc)
	 */
	public void setPlayerAction(int seatedPlayerPosition, String action)
	{
		if((seatedPlayerPosition >= 0) && (seatedPlayerPosition <= 8))
		{
			playerActions[seatedPlayerPosition].setText(action);
			playerActions[seatedPlayerPosition].setVisible(true);
		}
		else
			System.err.println("Error: Out-of-bounds access in TableGUI.setPlayerAction()");
	}
	/**
	 * Shows the flop cards (first, second, and third cards) on the table
	 * <pre><ul>{@code public void showFlopCards(int firstCardRankValue,
	 * 		       int firstCardSuitValue,
	 * 		       int secondCardRankValue, 
	 * 		       int secondCardSuitValue, 
	 * 		       int thirdCardRankValue, 
	 * 		       int thirdCardSuitValue)}</ul></pre>
	 * @param firstCardRankValue the int value of the Rank of the first card to be shown
	 * @param firstCardSuitValue the int value of the Suit of the first card to be shown
	 * @param secondCardRankValue the int value of the Rank of the second card to be shown
	 * @param secondCardSuitValue the int value of the Suit of the second card to be shown
	 * @param thirdCardRankValue the int value of the Rank of the third card to be shown
	 * @param thirdCardSuitValue the int value of the Suit of the third card to be shown
	 */
	public void showFlopCards(int firstCardRankValue, int firstCardSuitValue, 
			int secondCardRankValue, int secondCardSuitValue, int thirdCardRankValue, int thirdCardSuitValue)
	{
		new CardShowDelayAndSoundThread(10, firstCardRankValue, firstCardSuitValue,
				secondCardRankValue, secondCardSuitValue, thirdCardRankValue, thirdCardSuitValue).start();
	}
	/**
	 * Shows the turn card (the fourth card) on the table
	 * <pre><ul>{@code public void showTurnCard(int cardRank, int cardSuit)}</ul></pre>
	 * @param cardRankValue the int value of the Rank of the card to be shown
	 * @param cardSuitValue the int value of the Suit of the card to be shown
	 */
	public void showTurnCard(int cardRankValue, int cardSuitValue)
	{
		new CardShowDelayAndSoundThread(20, cardRankValue, cardSuitValue,-1,-1,-1,-1).start();
	}
	/**
	 * Shows the river card (the fifth card) on the table
	 * <pre><ul>{@code public void showRiverCard(int cardRankValue, int cardSuitValue)}</ul></pre>
	 * @param cardRankValue the int value of the Rank of the card to be shown
	 * @param cardSuitValue the int value of the Suit of the card to be shown
	 */
	public void showRiverCard(int cardRankValue, int cardSuitValue)
	{
		new CardShowDelayAndSoundThread(30, cardRankValue, cardSuitValue,-1,-1,-1,-1).start();
	}
	/**
	 * Removes all cards (flop, turn, and river cards) from the table
	 * <pre><ul>{@code public void clearBoard()}</ul></pre>
	 */
	public void clearBoard()
	{
		for(int i = 0; i < 5; i++)
		{
			tableCards[i].setVisible(false);
		}
	}
	/**
	 * Removes the cards from the specified seatedPlayerPosition so that they are no longer visible
	 * <pre><ul>{@code public void hidePlayerCards(int seatedPlayerPosition)}</ul></pre>
	 * @param seatedPlayerPosition the seat position of the player whose cards will be removed
	 */
	public void hidePlayerCards(int seatedPlayerPosition)
	{
		playerFirstCards[seatedPlayerPosition].setVisible(false);
		playerSecondCards[seatedPlayerPosition].setVisible(false);
	}
	/**
	 * Turns a player's cards to the back side so that you can no longer see the rank and suit of the card
	 * <pre><ul>{@code public void setPlayerCardsToBackSide(int seatedPlayerPosition)}</ul></pre>
	 * @param seatedPlayerPosition the seat position of the player whose cards will be turned over
	 */
	public void setPlayerCardsToBackSide(int seatedPlayerPosition)
	{
		if((seatedPlayerPosition >= 0) && (seatedPlayerPosition <= 8))
		{
			new CardShowDelayAndSoundThread(seatedPlayerPosition, 0, 0, 0, 0, -1, -1).start();
		}
		else
			System.err.println("Error: Out-of-bounds access in TableGUI.setPlayerCardsToBackSide()");
	}
	/**
	 * Sets the specified player's cards to the ranks and suits given
	 * <pre><ul>{@code public void setPlayerCards (int seatedPlayerPosition,
	 * 			int firstCardRankValue,
	 * 			int firstCardSuitValue,
	 *			int secondCardRankValue,
	 *			int secondCardSuitValue)}</ul></pre>
	 * @param seatedPlayerPosition the seat position of the player to set the cards of
	 * @param firstCardRankValue the int of the Rank of the first card to be shown
	 * @param firstCardSuitValue the int of the Suit of the first card to be shown
	 * @param secondCardRankValue the int of the Rank of the second card to be shown
	 * @param secondCardSuitValue the int of the Suit of the second card to be shown
	 */
	public void setPlayerCards (int seatedPlayerPosition, int firstCardRankValue, int firstCardSuitValue, 
			int secondCardRankValue, int secondCardSuitValue)
	{
		if((seatedPlayerPosition >= 0) && (seatedPlayerPosition <= 8))
		{
			new CardShowDelayAndSoundThread(seatedPlayerPosition, firstCardRankValue, firstCardSuitValue,
					secondCardRankValue, secondCardSuitValue, -1, -1).start();
		}
		else
			System.err.println("Error: Out-of-bounds access in TableGUI.setPlayerCards()");
	}
	/**
	 * Sets the amount of chips a player has 
	 * <pre><ul>{@code public void setPlayerStackSize(int seatedPlayerPosition, int stackSize)}</ul></pre>
	 * @param seatedPlayerPosition the seat position of the player to set the stack size of (can range from 0 to 8)
	 * @param stackSize the amount of chips that the player will have
	 */
	public void setPlayerStackSize(int seatedPlayerPosition, int stackSize)
	{
		if((seatedPlayerPosition >= 0) && (seatedPlayerPosition <= 8))
		{
			//animate chips to pot
			this.playerStackSizes[seatedPlayerPosition] = stackSize;
			this.playerNameAndStackSizeLabels[seatedPlayerPosition].setText(this.playerNames[seatedPlayerPosition]
					+": "+this.playerStackSizes[seatedPlayerPosition]);
		}
		else
			System.err.println("Error: Out-of-bounds access in TableGUI.setPlayerStackSize()");
	}
	/**
	 * Sets the number that will be shown next to the Call button
	 * <pre><ul>{@code public void setCallAmount(int callAmount)}</ul></pre>
	 * @param callAmount the number to set
	 */
	public void setCallAmount(int callAmount)
	{
		callButton.setText("Call "+callAmount);
	}
	/**
	 * Sets the minimum amount of chips that each player can bet
	 * <ul>{@code public void setMinimumBetAmount(int minimumBetAmount)}</ul>
	 * @param minimumBetAmount the minimum amount of chips that each player can bet
	 */
	public void setMinimumBetAmount(int minimumBetAmount)
	{
		this.minimumBetAmount = minimumBetAmount;
		betAmountSlider.setMinimum(minimumBetAmount);
		betAmountSlider.setValue(minimumBetAmount);
	}
	/**
	 * Sets the maximum amount of chips that the player using this instance of TableGUI is allowed to bet
	 * <pre><ul>{@code public void setMaximumBetAmount(int maximumBetAmount)}</ul></pre>
	 * @param maximumBetAmount the maximum amount of chips that a player is allowed to bet (should be set to their stack size)
	 */
	public void setMaximumBetAmount(int maximumBetAmount)
	{
		this.maximumBetAmount = maximumBetAmount;
		betAmountSlider.setMaximum(this.maximumBetAmount);
	}
	/**
	 * Puts the dealer button at the position of the specified player
	 * <pre><ul>{@code public void setDealerButtonPosition(int seatedPlayerPosition)}</ul></pre>
	 * @param seatedPlayerPosition the seat position of the player to add the dealer button
	 */
	public void setDealerButtonPosition(int seatedPlayerPosition)
	{
		if((seatedPlayerPosition >= 0) && (seatedPlayerPosition <= 8))
		{
			dealerButton.setBounds(playerLocations[seatedPlayerPosition].getX()+DEALER_BUTTON_HORIZONTAL_OFFSET,
					playerLocations[seatedPlayerPosition].getY()+DEALER_BUTTON_VERTICAL_OFFSET, 
					BLIND_DEALER_BUTTON_WIDTH, BLIND_DEALER_BUTTON_WIDTH);
			dealerButton.setVisible(true);
		}
		else
			System.err.println("Error: Out-of-bounds access in TableGUI.setDealerButtonPosition()");
	}
	/**
	 * Puts the big blind button at the position of the specified player
	 * <pre><ul>{@code public void setBigBlindButtonPosition(int seatedPlayerPosition)}</ul></pre>
	 * @param seatedPlayerPosition the seat position of the player to add the big blind button
	 */
	public void setBigBlindButtonPosition(int seatedPlayerPosition)
	{
		bigBlindButton.setBounds(playerLocations[seatedPlayerPosition].getX()+BLIND_BUTTON_HORIZONTAL_OFFSET,
				playerLocations[seatedPlayerPosition].getY()+BLIND_BUTTON_VERTICAL_OFFSET, 
				BLIND_DEALER_BUTTON_WIDTH, BLIND_DEALER_BUTTON_WIDTH);
		bigBlindButton.setVisible(true);
	}
	/**
	 * Puts the small blind button at the position of the specified player
	 * <pre><ul>{@code public void setSmallBlindButtonPosition(int seatedPlayerPosition)}</ul></pre>
	 * @param seatedPlayerPosition the seat position of the player to add the small blind button
	 */
	public void setSmallBlindButtonPosition(int seatedPlayerPosition)
	{
		smallBlindButton.setBounds(playerLocations[seatedPlayerPosition].getX()+BLIND_BUTTON_HORIZONTAL_OFFSET,
				playerLocations[seatedPlayerPosition].getY()+BLIND_BUTTON_VERTICAL_OFFSET, 
				BLIND_DEALER_BUTTON_WIDTH, BLIND_DEALER_BUTTON_WIDTH);
		smallBlindButton.setVisible(true);
	}
	/**
	 * Hides or shows the action buttons that allow a player to interact with the game
	 * <pre><ul>{@code public void setButtonsVisible(boolean isVisible, boolean betIsOnTable)}</ul></pre>
	 * @param isVisible true to make the buttons visible; false to hide them 
	 * @param betIsOnTable true to show call, raise, and fold; false to show check, bet, and fold
	 */
	public void setButtonsVisible(boolean isVisible, boolean betIsOnTable)
	{
		if(isVisible)
		{
			timer.start();
			foldButton.setVisible(true);
			betAmountSlider.setValue(minimumBetAmount);
			betAmountSlider.setVisible(true);
			sliderCover.setVisible(false);
			if(betIsOnTable)
			{
				callButton.setVisible(true);
				raiseButton.setVisible(true);
				checkButton.setVisible(false);
				betButton.setVisible(false);
			}
			else
			{
				checkButton.setVisible(true);
				betButton.setVisible(true);
				callButton.setVisible(false);
				raiseButton.setVisible(false);
			}
		}
		else
		{
			timer.stop();
			timerLabel.setText("   --");
			time = 30;
			sliderCover.setVisible(true);
			betAmountSlider.setVisible(false);
			foldButton.setVisible(false);
			callButton.setVisible(false);
			raiseButton.setVisible(false);
			checkButton.setVisible(false);
			betButton.setVisible(false);
		}
	}

	/**
	 * Activates the Buy In GUI associated with this table
	 */
	public void triggerBuyIn()
	{
		buyInGUI.setNetWorth(client.getPlayerNetWorth());
		buyInGUI.setVisible(true);
	}

	/**
	 * When a table closes, it will send this message to the server via the client
	 */
	public void sendCloseMessage()
	{
		client.sendMessage("game;"+uniqueID+";folds;gucci");
		client.sendMessage("table;"+uniqueID+";leaves;gucci");
	}
	public int getUniqueID()
	{
		return uniqueID;
	}
	public void setSoundCheckBoxSelected(boolean isSelected)
	{
		soundCheckBox.setSelected(isSelected);
	}
	private void initializeVariables()
	{
		//this code block scales the TableGUI window down if you're stuck in the early 2000s' and haven't bought a decent monitor
		if(Toolkit.getDefaultToolkit().getScreenSize().getWidth() < 1300.0 || Toolkit.getDefaultToolkit().getScreenSize().getHeight() < 800.0)
		{
			SCALE_FACTOR = 0.79; //The value of 0.79 changes the internal window size from 1300x800 to 1027x632
		}
		if(SCALE_FACTOR != 1) //only execute this block of code if the scale has been changed
		{
			RECT_WIDTH = (int)(RECT_WIDTH*SCALE_FACTOR);
			RECT_HEIGHT = (int)(RECT_HEIGHT*SCALE_FACTOR);
			BLIND_BUTTON_HORIZONTAL_OFFSET = (int)(RECT_WIDTH - (47*SCALE_FACTOR));
			BLIND_BUTTON_VERTICAL_OFFSET = (int)(SCALE_FACTOR*BLIND_BUTTON_VERTICAL_OFFSET);
			POKER_CHIP_WIDTH = (int)(SCALE_FACTOR*POKER_CHIP_WIDTH);

			double SCALE_FACTOR_INVERSE = 1-SCALE_FACTOR;
			if(SCALE_FACTOR_INVERSE < 0)
				SCALE_FACTOR_INVERSE *= -1;

			FIRST_CARD_HORIZONTAL_OFFSET = (int)((RECT_WIDTH-(2*CARD_WIDTH))/((2 + SCALE_FACTOR_INVERSE)));
			SECOND_CARD_HORIZONTAL_OFFSET = RECT_WIDTH-(CARD_WIDTH+FIRST_CARD_HORIZONTAL_OFFSET);
			DISTANCE_BETWEEN_TABLE_CARDS = (int)((((SCALE_FACTOR*1300.0)*0.55)-(5.0*CARD_WIDTH))/(11.4839));
			CARD_VERTICAL_OFFSET = ((int)(SCALE_FACTOR*CARD_VERTICAL_OFFSET));
			TOP_ACTION_BUTTON_VERTICAL_COORDINATE = (int)(TOP_ACTION_BUTTON_VERTICAL_COORDINATE*SCALE_FACTOR);
			ACTION_BUTTON_HORIZONTAL_COORDINATE = (int)(ACTION_BUTTON_HORIZONTAL_COORDINATE*SCALE_FACTOR);
			ACTION_BUTTON_WIDTH = (int)(ACTION_BUTTON_WIDTH*SCALE_FACTOR);
			ACTION_BUTTON_HEIGHT = (int)(ACTION_BUTTON_HEIGHT*SCALE_FACTOR);
			ACTION_VERTICAL_OFFSET = ((int)(SCALE_FACTOR*ACTION_VERTICAL_OFFSET));
			BLIND_DEALER_BUTTON_WIDTH = ((int)(SCALE_FACTOR*BLIND_DEALER_BUTTON_WIDTH));
			DEALER_BUTTON_HORIZONTAL_OFFSET = ((int)(SCALE_FACTOR*DEALER_BUTTON_HORIZONTAL_OFFSET));
			DEALER_BUTTON_VERTICAL_OFFSET = ((int)(SCALE_FACTOR*DEALER_BUTTON_VERTICAL_OFFSET));
			for(int i = 0; i < playerLocations.length; i++)
			{
				playerLocations[i].x *= SCALE_FACTOR;
				playerLocations[i].y *= SCALE_FACTOR;
			}
		}
		cardSlideSoundCounter = 0;
		soundCheckBox = new JCheckBox();
		soundCheckBox.setSelected(soundOn);
		soundCheckBox.setFocusPainted(false);
		soundCheckBox.setOpaque(false);
		maximumBetAmount = 5;
		potAmount = 0;
		timerLabel = new JLabel("00:30");
		timerLabel.setFont(new Font(Constants.fontName, Font.BOLD, (int)(32*SCALE_FACTOR)));
		time = 30;
		potLabel = new JLabel("Pot: 0", SwingConstants.CENTER);
		potLabel.setFont(new Font(Constants.fontName, Font.PLAIN, (int)(SCALE_FACTOR*24)));
		potLabel.setForeground(Color.BLACK);
		potLabel.setVisible(false);
		playerPanels = new RoundRectPanel[9];
		playerNames = new String[9];
		playerNameAndStackSizeLabels = new JLabel[9];
		playerActions = new JLabel[9];
		playerStackSizes = new int[9];
		playerFirstCards = new JLabel[9];
		playerSecondCards = new JLabel[9];
		tableCards = new JLabel[5];
		ImageIcon cardBack = new ImageIcon(getClass().getResource("card images/back.png"));
		cardIcons = new ImageIcon[14][4];
		for(int a = 0; a < Suit.size(); a++)
		{
			for(int b = 0; b < 14; b++)
			{
				if(b == 0)
				{
					cardIcons[b][a] = cardBack;
				}
				else
					cardIcons[b][a] = new ImageIcon(getClass().getResource("card images/"+a+b+".png"));
			}
		}
		checkButton = new RoundButton("Check");
		callButton = new RoundButton("Call");
		raiseButton = new RoundButton("Raise");
		foldButton = new RoundButton("Fold");
		betButton = new RoundButton("Bet");
		minimumBetAmount = 2;
		betAmountSlider = new JSlider(JSlider.VERTICAL);
		betAmountSlider.setOpaque(false);
		betAmountSlider.setMaximum(100);
		betAmountSlider.setValue(minimumBetAmount);
		betAmountSlider.setMinimum(minimumBetAmount);
		betAmountSlider.setPaintTicks(true);
		betAmountSlider.setMajorTickSpacing(20);
		betAmountSlider.setPaintTrack(false);
		betAmountSlider.setSnapToTicks(false);
		try
		{
			cardSlideClipArray = new Clip[9];
			chipSlideClipArray = new Clip[4];
			for(int i = 0; i < 9; i++)
			{
				cardSlideClipArray[i] = AudioSystem.getClip();
				cardSlideClipArray[i].open(AudioSystem.getAudioInputStream(getClass().getResource("sounds/cardSlide"+i+".wav")));
				if(i < 4)
				{
					chipSlideClipArray[i] = AudioSystem.getClip();
					chipSlideClipArray[i].open(AudioSystem.getAudioInputStream(getClass().getResource("sounds/chip slide"+i+".wav")));
				}
			}
		}
		catch(IOException ioe)
		{
			System.out.println("IOException in Battleship constructor: "+ioe.getMessage());
		}
		catch(UnsupportedAudioFileException uafe)
		{
			System.out.println("UnsupportedAudioFileException in Battleship constructor: "+uafe.getMessage());
		}
		catch(LineUnavailableException lue)
		{
			System.out.println("LineUnavailableException in Battleship constructor: "+lue.getMessage());
		}
		/**
		 * Subtracts one from the timer each time it is called; resets the time when called at 0
		 */
		ActionListener countdown = new ActionListener() {
			public void actionPerformed(ActionEvent evt) 
			{
				if(time < 0)
				{
					setButtonsVisible(false, false);
					client.sendMessage("game;"+uniqueID+";folds;gucci");
				}
				else
				{
					if(time > 9)
						timerLabel.setText("00:"+time);
					else timerLabel.setText("00:0"+time);
					time--;
				}
			}
		};
		timer = new Timer(1000, countdown);
		timer.setInitialDelay(0);

		buyInGUI = new BuyInGUI(client, uniqueID, blindLevel);
	}
	private void createGUI()
	{
		setLayout(null);
		JLabel backgroundLabel;
		if(SCALE_FACTOR != 1) //if scale factor is not 1, scale background image to scaled size
		{
			ImageIcon image = new ImageIcon(getClass().getResource("table images/island_background_sm.gif"));
			/*Image newImage = image.getImage();
			Image thirdImage = newImage.getScaledInstance((int)(1300*SCALE_FACTOR), 
					(int)(800*SCALE_FACTOR), Image.SCALE_SMOOTH);
			backgroundLabel = new JLabel(new ImageIcon(thirdImage));*/
			backgroundLabel = new JLabel(image);
			backgroundLabel.setBounds(0,0,(int)(1300*SCALE_FACTOR),(int)(800*SCALE_FACTOR));
		}
		else
		{
			ImageIcon image = new ImageIcon(getClass().getResource("table images/island_background.gif"));
			backgroundLabel = new JLabel(image);
			backgroundLabel.setBounds(0,0,1300,800);
		}

		potLabel.setBounds((int)(SCALE_FACTOR*568), (int)(SCALE_FACTOR*282), (int)(SCALE_FACTOR*173), (int)(SCALE_FACTOR*35));

		soundCheckBox.setBounds((int)(SCALE_FACTOR*32), (int)(SCALE_FACTOR*750), 70, 20);
		JLabel soundTextLabel = new JLabel(new ImageIcon(getClass().getResource("table images/sounds label.png")));
		soundTextLabel.setBounds((int)(SCALE_FACTOR*52), (int)(SCALE_FACTOR*751), 75, 19);		

		//create timer stuff
		JLabel timerTitle = new JLabel("Timer:");
		timerTitle.setFont(new Font(Constants.fontName, Font.BOLD, (int)(26*SCALE_FACTOR)));
		timerTitle.setBounds((int)((1300*SCALE_FACTOR)-(146*SCALE_FACTOR)), (int)(12*SCALE_FACTOR), 
				(int)(SCALE_FACTOR*100),(int)(SCALE_FACTOR*30));
		timerLabel.setBounds((int)((1300*SCALE_FACTOR)-(146*SCALE_FACTOR)), (int)(57*SCALE_FACTOR), 
				(int)(SCALE_FACTOR*150),(int)(SCALE_FACTOR*35));

		betAmountSlider.setBounds((int)(SCALE_FACTOR*7), (int)(SCALE_FACTOR*13),
				(int)(SCALE_FACTOR*40), (int)(SCALE_FACTOR*170));
		betAmountSlider.setForeground(Color.white);
		betAmountSlider.setVisible(false);

		//button creation code
		JButton[] buttonArray = {checkButton, betButton, foldButton, callButton, raiseButton};
		Color[] buttonColors = {new Color(191, 63, 84), new Color(30, 51, 135), new Color(255, 232, 59)};
		for(int i = 0; i < 5; i++)
		{
			buttonArray[i].setBounds(ACTION_BUTTON_HORIZONTAL_COORDINATE, TOP_ACTION_BUTTON_VERTICAL_COORDINATE+(((int)(SCALE_FACTOR*49))*(i%3)),
					ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT);
			buttonArray[i].setBackground(buttonColors[i%3]);
			buttonArray[i].setFont(new Font(Constants.fontName, Font.PLAIN, (int)(SCALE_FACTOR*18)));
			if((i%3) == 2)
				buttonArray[i].setForeground(Color.BLACK);
			else
				buttonArray[i].setForeground(Color.WHITE);
			add(buttonArray[i]);
			buttonArray[i].setVisible(false);
		}

		//creating first and second card for each player
		for(int i = 0; i < 9; i++)
		{
			playerFirstCards[i] = new JLabel(cardIcons[i+1][(int)(Math.abs((i/2.0)-.5))]);
			playerFirstCards[i].setBounds(playerLocations[i].getX()+FIRST_CARD_HORIZONTAL_OFFSET, 
					playerLocations[i].getY()+CARD_VERTICAL_OFFSET, CARD_WIDTH, CARD_HEIGHT);
			playerFirstCards[i].setVisible(false);

			playerSecondCards[i] = new JLabel(cardIcons[i+5][(int)(Math.abs((i/2.0)-.5))]);
			playerSecondCards[i].setBounds(playerLocations[i].getX()+SECOND_CARD_HORIZONTAL_OFFSET, 
					playerLocations[i].getY()+CARD_VERTICAL_OFFSET, CARD_WIDTH, CARD_HEIGHT);
			playerSecondCards[i].setVisible(false);
		}

		//font used for big and small blind buttons is Evo Pro Regular
		ImageIcon dealerButtonImage = new ImageIcon(getClass().getResource("table images/dealer button.png"));
		ImageIcon smallBlindButtonImage = new ImageIcon(getClass().getResource("table images/small blind button.png"));
		ImageIcon bigBlindButtonImage = new ImageIcon(getClass().getResource("table images/big blind button.png"));
		if(SCALE_FACTOR != 1) //if scale factor is not 1, scale background image to scaled size
		{
			Image newDealerButtonImage = dealerButtonImage.getImage();
			Image newSmallBlindButtonImage = smallBlindButtonImage.getImage();
			Image newBigBlindButtonImage = bigBlindButtonImage.getImage();

			Image thirdDealerButtonImage = newDealerButtonImage.getScaledInstance(BLIND_DEALER_BUTTON_WIDTH, 
					BLIND_DEALER_BUTTON_WIDTH, Image.SCALE_AREA_AVERAGING);
			Image thirdSmallBlindButtonImage = newSmallBlindButtonImage.getScaledInstance(BLIND_DEALER_BUTTON_WIDTH, 
					BLIND_DEALER_BUTTON_WIDTH, Image.SCALE_AREA_AVERAGING);
			Image thirdBigBlindButtonImage = newBigBlindButtonImage.getScaledInstance(BLIND_DEALER_BUTTON_WIDTH,
					BLIND_DEALER_BUTTON_WIDTH, Image.SCALE_AREA_AVERAGING);

			dealerButton = new JLabel(new ImageIcon(thirdDealerButtonImage));
			smallBlindButton = new JLabel(new ImageIcon(thirdSmallBlindButtonImage));
			bigBlindButton = new JLabel(new ImageIcon(thirdBigBlindButtonImage));
		}
		else
		{
			dealerButton = new JLabel(dealerButtonImage);
			smallBlindButton = new JLabel(smallBlindButtonImage);
			bigBlindButton = new JLabel(bigBlindButtonImage);
		}
		dealerButton.setBounds(playerLocations[0].getX()+DEALER_BUTTON_HORIZONTAL_OFFSET,
				playerLocations[0].getY()+DEALER_BUTTON_VERTICAL_OFFSET, BLIND_DEALER_BUTTON_WIDTH, BLIND_DEALER_BUTTON_WIDTH);
		dealerButton.setVisible(false);

		bigBlindButton.setBounds(playerLocations[0].getX()+BLIND_BUTTON_HORIZONTAL_OFFSET,
				playerLocations[0].getY()+BLIND_BUTTON_VERTICAL_OFFSET, BLIND_DEALER_BUTTON_WIDTH, BLIND_DEALER_BUTTON_WIDTH);
		bigBlindButton.setVisible(false);

		smallBlindButton.setBounds(playerLocations[8].getX()+BLIND_BUTTON_HORIZONTAL_OFFSET,
				playerLocations[8].getY()+BLIND_BUTTON_VERTICAL_OFFSET, BLIND_DEALER_BUTTON_WIDTH, BLIND_DEALER_BUTTON_WIDTH);
		smallBlindButton.setVisible(false);

		//create an image to cover up the slider gradient in the background when it's not the player's turn
		ImageIcon sliderCoverImage = new ImageIcon(getClass().getResource("table images/slider cover.png"));
		if(SCALE_FACTOR != 1) //if scale factor is not 1, scale slider cover to scaled size
		{
			Image newSliderCoverImage = sliderCoverImage.getImage();
			Image thirdSliderCoverImage = newSliderCoverImage.getScaledInstance((int)(55*SCALE_FACTOR), 
					(int)(194*SCALE_FACTOR), Image.SCALE_SMOOTH);
			sliderCover = new JLabel(new ImageIcon(thirdSliderCoverImage));
			sliderCover.setBounds(0,0,(int)(55*SCALE_FACTOR),(int)(194*SCALE_FACTOR));
		}
		else
		{
			sliderCover = new JLabel(sliderCoverImage);
			sliderCover.setBounds(0, 0, 55, 194);
		}
		sliderCover.setVisible(true);

		pokerChipArray = new JLabel[9];
		for(int i = 0; i < 9; i++)
		{
			if(SCALE_FACTOR != 1)
			{
				ImageIcon pokerChipImage = new ImageIcon(getClass().getResource("table images/poker chip"+i+".png"));
				Image newPokerChipImage = pokerChipImage.getImage();
				Image thirdPokerChipImage = newPokerChipImage.getScaledInstance(POKER_CHIP_WIDTH,
						POKER_CHIP_WIDTH, Image.SCALE_SMOOTH);
				pokerChipArray[i] = new JLabel(new ImageIcon(thirdPokerChipImage));
			}
			else
				pokerChipArray[i] = new JLabel(new ImageIcon(getClass().getResource("table images/poker chip"+i+".png")));
			pokerChipArray[i].setBounds(0,0,POKER_CHIP_WIDTH,POKER_CHIP_WIDTH);
			pokerChipArray[i].setVisible(false);
		}

		//create cards on the table
		for(int i = 0; i < tableCards.length; i++)
		{
			tableCards[i] = new JLabel(cardIcons[0][0]);
			tableCards[i].setBounds(((int)(1300.0*SCALE_FACTOR*0.226+(3.742*DISTANCE_BETWEEN_TABLE_CARDS)))
					+(i*(DISTANCE_BETWEEN_TABLE_CARDS+CARD_WIDTH)),
					(int)(SCALE_FACTOR*350), CARD_WIDTH, CARD_HEIGHT);
			tableCards[i].setVisible(false);
		}

		Color colorArray[] = {new Color(20,158,163), new Color(163,20,37), new Color(191,86,1),
				Color.white, new Color(40,163,20), Color.black, new Color(224,65,192),
				new Color(163,158,20), new Color(40,20,163)};

		//create RoundRectPanels for the players
		for(int i = 0; i < 9; i++)
		{
			playerPanels[i] = new RoundRectPanel(colorArray[i]);
			playerPanels[i].setBounds(playerLocations[i].getX(), playerLocations[i].getY(), RECT_WIDTH, RECT_HEIGHT);
			playerPanels[i].setVisible(false);
		}

		//create player titles and actions
		int playerLabelHeight = 40;
		if(SCALE_FACTOR > 1)
			playerLabelHeight = (int)(SCALE_FACTOR*playerLabelHeight);
		for(int i = 0; i < 9; i++)
		{
			playerNameAndStackSizeLabels[i] = new JLabel("", SwingConstants.CENTER);
			playerNameAndStackSizeLabels[i].setFont(new Font(Constants.fontName, Font.BOLD, (int)(SCALE_FACTOR*26)));
			playerNameAndStackSizeLabels[i].setBounds(playerLocations[i].getX(), 
					playerLocations[i].getY()+((int)(SCALE_FACTOR*NAME_VERTICAL_OFFSET)), RECT_WIDTH, playerLabelHeight);
			playerNameAndStackSizeLabels[i].setVisible(false);

			playerActions[i] = new JLabel("", SwingConstants.CENTER);
			playerActions[i].setFont(new Font(Constants.fontName, Font.BOLD, (int)(SCALE_FACTOR*18)));
			playerActions[i].setBounds(playerLocations[i].getX(), 
					playerLocations[i].getY()+ACTION_VERTICAL_OFFSET, RECT_WIDTH, (int)(SCALE_FACTOR*30));
			playerActions[i].setVisible(false);

			//if the background is black, pink, or blue, make the font white
			if(colorArray[i].equals(Color.black) || colorArray[i].equals(new Color(224,65,192)) ||
					colorArray[i].equals(new Color(40,20,163)))
			{
				playerNameAndStackSizeLabels[i].setForeground(Color.white);
				playerActions[i].setForeground(Color.white);
			}
			else
			{
				playerNameAndStackSizeLabels[i].setForeground(Color.black);
				playerActions[i].setForeground(Color.black);
			}
		}
		//add everything that was not already added
		for(int i = 0; i < 9; i++)
		{
			add(pokerChipArray[i]);
		}
		add(dealerButton);
		add(bigBlindButton);
		add(smallBlindButton);
		for(int i = 0; i < 9; i++)
		{
			add(playerFirstCards[i]);
			add(playerSecondCards[i]);
			add(playerActions[i]);
			add(playerNameAndStackSizeLabels[i]);
			add(playerPanels[i]);
		}
		add(potLabel);
		for(int i = 0; i < tableCards.length; i++)
		{
			add(tableCards[i]);
		}
		add(timerTitle);
		add(timerLabel);
		add(soundTextLabel);
		add(soundCheckBox);
		add(sliderCover);
		add(betAmountSlider);
		add(backgroundLabel);
	}
	private void setActions()
	{	//changes values of bet/raise as you slide the slider
		betAmountSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent ce)
			{
				int sliderValue = betAmountSlider.getValue();
				if(sliderValue == betAmountSlider.getMaximum())
				{
					betButton.setText("Bet All In");
					raiseButton.setText("Raise All In");
				}
				else
				{
					betButton.setText("Bet "+sliderValue);
					if(sliderValue > 999)
						raiseButton.setText("Raise "+sliderValue);
					else
						raiseButton.setText("Raise to "+sliderValue);
				}
			}
		});
		checkButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				//do something when you check
				client.sendMessage("game;"+uniqueID+";checks;gucci");
				setButtonsVisible(false, false);
			}
		});
		betButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				//do something when you bet
				client.sendMessage("game;"+uniqueID+";bets;"+betAmountSlider.getValue());
				setButtonsVisible(false, false);
			}
		});
		foldButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				//do something when you fold
				client.sendMessage("game;"+uniqueID+";folds;gucci");
				setButtonsVisible(false, false);
			}
		});
		callButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				//do something when you call
				client.sendMessage("game;"+uniqueID+";calls;"+callButton.getText().split(" ")[1]);
				setButtonsVisible(false, true);
			}
		});
		raiseButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				//do something when you raise
				if (Integer.parseInt(callButton.getText().split(" ")[1]) == betAmountSlider.getMaximum()) {
					client.sendMessage("game;"+uniqueID+";calls;"+callButton.getText().split(" ")[1]);
				} else {
					client.sendMessage("game;"+uniqueID+";raises;"+betAmountSlider.getValue());
				}
				setButtonsVisible(false, true);
			}
		});
		soundCheckBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae) 
			{
				soundOn = !soundOn;
				if(!soundOn)
					lobbyGUI.pauseMusic();
				else
					lobbyGUI.playMusic();
				client.setSoundCheckBoxesSelected(soundOn);
			}

		});
	}
	/**
	 * Initializes a newly created TableGUI object that will not be visible initially
	 * <pre><ul>{@code public TableGUI(Client client, String title, int uniqueID)}</ul></pre>
	 * @param client the {@code Client} that this {@code TableGUI} will communicate with
	 * @param title the title to be shown at the top of the window
	 * @param uniqueID an int representing the table number
	 */
	public TableGUI(Client client, LobbyGUI lobbyGUI, String title, int uniqueID, int blindLevel) //should also take in BB level
	{
		super();
		if(!debug)
			this.client = client;
		this.lobbyGUI = lobbyGUI;
		this.uniqueID = uniqueID;
		this.blindLevel = blindLevel;
		initializeVariables();
		setActions();
		createGUI();
	}
	public static void main(String[] args)
	{
		TableGUI table = new TableGUI(null, null, "MyTable", 1, 69);
		table.setVisible(true);
		table.setPotAmount(200);
		table.setDealerButtonPosition(0);
		table.setBigBlindButtonPosition(1);
		table.setSmallBlindButtonPosition(2);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		table.showFlopCards(((int)((13*Math.random())+1)), ((int)(Math.random()*4)), 
				((int)((13*Math.random())+1)), ((int)(Math.random()*4)),
				((int)((13*Math.random())+1)), ((int)(Math.random()*4)));

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		table.showTurnCard(((int)((13*Math.random())+1)), ((int)(Math.random()*4)));

		table.showRiverCard(((int)((13*Math.random())+1)), ((int)(Math.random()*4)));
		table.setButtonsVisible(true, false);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < 9; i++)
		{
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			table.addPlayer(i, "Bill Cosby", (int)(Math.random()*301));
			table.setPlayerCards(i, ((int)((13*Math.random())+1)), ((int)(Math.random()*4)),
					((int)((13*Math.random())+1)), ((int)(Math.random()*4)));
			table.setPlayerAction(i, "Folding");
		}
		for(int i = 0; i < 9; i++)
		{
			table.putInChips(i, 5);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		table.putInChips(5, 100);
		table.setPlayerCardsToBackSide(2);
		/*try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
		/*
		table.showTurnCard(Rank.valueOf((int)((13*Math.random())+1)), Suit.valueOf((int)(Math.random()*4)));
		table.showRiverCard(Rank.valueOf((int)((13*Math.random())+1)), Suit.valueOf((int)(Math.random()*4)));
		table.setPotAmount(105);
		String[] actions = {"Check", "Fold", "Bet", "Call", "Fold", "Bet", "Raise", "Check", "Call"};
		table.setDealerButtonPosition(0);
		for(int i = 0; i < 9; i++)
		{
			table.addPlayer(i, "Player "+(i+1), (int)(Math.random()*301));
			table.setPlayerAction(i, actions[i]);
		}
		for(int i = 0; i < 9; i++)
		{
			table.setPlayerCards(i, Rank.valueOf((int)((13*Math.random())+1)), Suit.valueOf((int)(Math.random()*4)),
					Rank.valueOf((int)((13*Math.random())+1)), Suit.valueOf((int)(Math.random()*4)));
		}
		table.addPlayer(0, "Bill Cosby", (int)(Math.random()*301));
		double dealerButtonPosition = 1;
		double smallBlindButtonPosition = 0;
		table.setButtonsVisible(true, true);
		boolean betIsOnTable = true;
		boolean visibleButtons = true;
		int putInChipsPosition = 0;
		table.timer.start();
		try
		{
			while(true)
			{
				betIsOnTable = !betIsOnTable;
				if(((int)dealerButtonPosition)%5 == 0 || ((int)dealerButtonPosition)%4 == 0)
				{
					table.setPlayerCardsToBackSide((int)(9*Math.random()));
					visibleButtons = false;
				}
				else
				{
					table.setPlayerCards((int)(9*Math.random()), Rank.valueOf((int)((13*Math.random())+1)), Suit.valueOf((int)(Math.random()*4)),
							Rank.valueOf((int)((13*Math.random())+1)), Suit.valueOf((int)(Math.random()*4)));
					visibleButtons = true;
				}
				table.setButtonsVisible(visibleButtons, betIsOnTable);
				if(dealerButtonPosition > 8.6)
					dealerButtonPosition = 0;
				if(smallBlindButtonPosition > 8.6)
					smallBlindButtonPosition = 0;

				//table.updateTimer();
				table.setMaximumBetAmount((int)(450*Math.random())+50);
				table.setSmallBlindButtonPosition((int)smallBlindButtonPosition);
				table.setDealerButtonPosition((int)dealerButtonPosition);
				table.setBigBlindButtonPosition((int)dealerButtonPosition);

				table.putInChips(putInChipsPosition, 5);
				putInChipsPosition++;
				if(putInChipsPosition > 8)
					putInChipsPosition = 0;
				dealerButtonPosition += 0.5;
				smallBlindButtonPosition += 0.5;
				Thread.sleep(2000);
			}
		}
		catch(InterruptedException ie)
		{
			System.out.println("InterruptedException: "+ie.getMessage());
		}*/
	}
}
