/***************************************************************************
 *
 *   FreeMindSplash, taken from GanttSplash.java.
 *
 *   Copyright (C) 2002 by Thomas Alexandre (alexthomas(at)ganttproject.org)
 *   Copyright (C) 2005-2008 by Christian Foltin and Daniel Polansky
 *    
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package freemind.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.text.MessageFormat;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import freemind.view.ImageFactory;

/**
 * Class to put a splash during launching the application.
 */

public class FreeMindSplashModern extends JFrame implements IFreeMindSplash {

	private static final String FREEMIND_SPLASH = "images/Freemind_Splash_Derek.png";
	
	private static final int SPLASH_HEIGHT = 200;
	private static final int SPLASH_WIDTH = 300;
	private static final int SPLASH_FONT_SIZE = 16;

	private class FeedBackImpl implements FeedBack {

		private int mActualValue;
		private long mActualTimeStamp = System.currentTimeMillis();
		private long mTotalTime = 0;
		private String lastTaskId = null;
		private JLabel mImageJLabel = null;

		public void progress(final int act, String messageId, Object[] pMessageParameters) {
			MessageFormat formatter = new MessageFormat(
					frame.getResourceString(messageId));
			final String progressString = formatter.format(pMessageParameters);
			logger.fine(progressString);
			this.mActualValue = act;
			long timeDifference = System.currentTimeMillis() - mActualTimeStamp;
			mActualTimeStamp = System.currentTimeMillis();
			mTotalTime += timeDifference;
			logger.fine("Task: " + lastTaskId + " (" + act + ") last "
					+ (timeDifference) / 1000.0 + " seconds.\nTotal: "
					+ mTotalTime / 1000.0 + "\n");
			SwingUtilities.invokeLater(() -> {
                mProgressBar.setValue(act);
                double percent = act * 1.0 / mProgressBar.getMaximum();
                mProgressBar.setString(progressString);
                if (mImageJLabel != null) {
                    mImageJLabel.putClientProperty("progressString",
                            progressString);
                    mImageJLabel.putClientProperty("progressPercent",
							percent);
                    mImageJLabel.repaint();
                }
            });
			logger.fine("Beginning task:" + messageId);
			lastTaskId = messageId;

			FreeMindSplashModern.this.toFront();
		}

		public int getActualValue() {
			return mActualValue;
		}

		public void setMaximumValue(int max) {
			mProgressBar.setMaximum(max);
			mProgressBar.setIndeterminate(false);
		}

		public void increase(String messageId, Object[] pMessageParameters) {
			progress(getActualValue() + 1, messageId, pMessageParameters);
		}

		public void setImageJLabel(JLabel imageJLabel) {
			mImageJLabel = imageJLabel;
		}
	}

	private final FreeMindMain frame;
	private final FeedBackImpl feedBack; // !
	private JProgressBar mProgressBar;
	private static Logger logger;
	private ImageIcon mIcon;

	public FeedBack getFeedBack() {
		return feedBack;
	}

	public FreeMindSplashModern(final FreeMindMain frame) {
		super("FreeMind");
		this.frame = frame;
		if (logger == null) {
			logger = frame.getLogger(this.getClass().getName());
		}

		this.feedBack = new FeedBackImpl();

		ImageFactory imageFactory = ImageFactory.getInstance();
		mIcon = imageFactory.createIcon(frame.getResource("images/76812-freemind_v0.4.png"));
		setIconImage(mIcon.getImage()); // Set the icon
		setDefaultLookAndFeelDecorated(false);
		setUndecorated(true);
		getRootPane().setWindowDecorationStyle(JRootPane.NONE);

		final ImageIcon splashImage = imageFactory.createIcon(frame.getResource(FREEMIND_SPLASH));
		JLabel splashImageLabel = new JLabel(splashImage) {
			private Integer mWidth = null;
			private final Font progressFont = new Font("Helvetica", Font.BOLD, 12);
			private Font versionTextFont = null;
			{
				Set availableFontFamilyNames = Tools.getAvailableFontFamilyNames();
				versionTextFont = availableFontFamilyNames.contains("Century Gothic") ? new Font("Century Gothic", Font.BOLD, 14) : new Font("Arial", Font.BOLD, 12);
			}

			private int calcYRelative(int y){
				return (int) (((float)y)/SPLASH_HEIGHT*splashImage.getIconHeight());
			}
			
			private int calcXRelative(int x){
				return (int) (((float)x)/SPLASH_WIDTH*splashImage.getIconWidth());
			}
			
			public void paint(Graphics g) {
				super.paint(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2.setFont(versionTextFont);
				// Determine width of string to center it
				String freemindVersion = "v" + frame.getFreemindVersion().toString();
				if (mWidth == null) {
					mWidth = g2.getFontMetrics().stringWidth(freemindVersion);
				}
				double splashHeight = getSize().getHeight();
				double splashWidth = getSize().getWidth();
                int yCoordinate = (int) (splashHeight - (splashHeight / 6));
				int xCoordinate = (int) (splashWidth - (splashWidth / 30) - mWidth);
				g2.setColor(new Color(218, 252, 255));
				g2.drawString(freemindVersion, xCoordinate, yCoordinate);

				// Draw progress bar
				String progressString = (String) getClientProperty("progressString");
				if (progressString != null) {
					Double percent = (Double) getClientProperty("progressPercent");
					int xBase = calcXRelative(7);
					int yBase = calcYRelative(185);
					int width = calcXRelative(281);
					g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
					g2.setFont(progressFont);
					g2.setColor(new Color(0xff, 0xff, 0xff));
					g2.drawString(progressString, xBase + calcXRelative(1), yBase - calcYRelative(4));
					g2.setColor(new Color(0xff, 0xff, 0xff));
					g2.fill(new Rectangle(xBase + calcXRelative(1), yBase + calcYRelative(1), (int) (width * percent), calcYRelative(5)));
				}
			}
		};

		feedBack.setImageJLabel(splashImageLabel);

		getContentPane().add(splashImageLabel, BorderLayout.CENTER);

		mProgressBar = new JProgressBar();
		mProgressBar.setIndeterminate(true);
		mProgressBar.setStringPainted(true);

		pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = splashImageLabel.getPreferredSize();

		// Put image at the middle of the screen
		setLocation(screenSize.width / 2 - (labelSize.width / 2), screenSize.height / 2 - (labelSize.height / 2));

	}

    public void drawProgressBar() {

    }

	public void close() {
		setVisible(false);
		dispose();
	}

	public ImageIcon getWindowIcon() {
		return mIcon;
	}

}