/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2011 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
 *
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package plugins.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.Timer;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.TileController;
import org.openstreetmap.gui.jmapviewer.interfaces.TileCache;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;

import freemind.main.FreeMind;
import freemind.main.Resources;
import freemind.modes.mindmapmode.MindMapController;

/**
 * @author foltin
 * @date 24.10.2011
 */
final class JCursorMapViewer extends JMapViewer {

	private static final class ScalableTileController extends TileController {
		private ScalableTileController(TileSource pSource,
				TileCache pTileCache, TileLoaderListener pListener) {
			super(pSource, pTileCache, pListener);
		}

		@Override
		public Tile getTile(int tilex, int tiley, int zoom) {
		       int max = (1 << zoom);
		        if (tilex < 0 || tilex >= max || tiley < 0 || tiley >= max)
		            return null;
		        Tile tile = tileCache.getTile(tileSource, tilex, tiley, zoom);
		        if (tile == null) {
		        	int scale = Resources.getInstance().getIntProperty(FreeMind.SCALING_FACTOR_PROPERTY, 100);
		        	if(scale >= 200){
		        		tile = new ScaledTile(this, tileSource, tilex, tiley, zoom);
		        	} else {
		        		tile = new Tile(tileSource, tilex, tiley, zoom);
		        	}
		            tileCache.addTile(tile);
		            tile.loadPlaceholderFromCache(tileCache);
		        }
		        return super.getTile(tilex, tiley, zoom);
		}
	}

	private static final class ScaledTile extends Tile {
		private BufferedImage scaledImage = null;
		private TileController tileController;

		private ScaledTile(TileController pTileController, TileSource pSource, int pXtile, int pYtile, int pZoom) {
			super(pSource, pXtile, pYtile, pZoom);
			tileController = pTileController;
		}

		@Override
		public void paint(Graphics pG, int pX, int pY) {
			if(scaledImage == null){
				int xtile_low = xtile >> 1;
				int ytile_low = ytile >> 1;
				Tile tile = tileController.getTile(xtile_low, ytile_low, zoom-1);
				if (tile != null && tile.isLoaded()) {
					int tileSize = source.getTileSize();
					int translate_x = (xtile % 2) * tileSize/2;
					int translate_y = (ytile % 2) * tileSize/2;
		    		BufferedImage image2 = tile.getImage();
		    		scaledImage = new BufferedImage(tileSize, tileSize, image2.getType());
		    	    Graphics2D g = scaledImage.createGraphics();
		    	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		    	    g.drawImage(image2, 0, 0, scaledImage.getWidth(), scaledImage.getHeight(), translate_x, translate_y, translate_x+tileSize/2, translate_y+tileSize/2, null);
		    	    g.dispose();
		        }
			}
			if(scaledImage != null){
				pG.drawImage(scaledImage, pX, pY, null);
			} else {
				pG.drawImage(getImage(), pX, pY, null);
			}
		}
	}

	boolean mShowCursor;
	boolean mUseCursor;
	Coordinate mCursorPosition;
	Stroke mStroke;
	Stroke mRectangularStroke = new BasicStroke(1, BasicStroke.CAP_SQUARE,
			BasicStroke.JOIN_MITER, 10.0f, new float[] { 10.0f, 10.0f }, 0.0f);
	private FreeMindMapController mFreeMindMapController;
	private final MapDialog mMapHook;
	private boolean mHideFoldedNodes = true;
	private Coordinate mRectangularStart;
	private Coordinate mRectangularEnd;
	private boolean mDrawRectangular = false;
	private int mCursorLength;

	/**
	 * @param pMindMapController
	 * @param pMapDialog
	 * @param pMapHook 
	 * 
	 */
	public JCursorMapViewer(MindMapController pMindMapController,
			JDialog pMapDialog, TileCache pTileCache, MapDialog pMapHook) {
		super(pTileCache, 8);
		tileController = new ScalableTileController(tileSource, pTileCache, this);
		int scaleProperty = Resources.getInstance().getIntProperty(FreeMind.SCALING_FACTOR_PROPERTY, 100);
		mCursorLength = 15 * scaleProperty/100;
		mStroke = new BasicStroke(2 * scaleProperty / 100);
		mMapHook = pMapHook;
		mFreeMindMapController = new FreeMindMapController(this,
				pMindMapController, pMapDialog, pMapHook);
		Action updateCursorAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				mShowCursor = !mShowCursor;
				repaint();
			}
		};
		new Timer(1000, updateCursorAction).start();
		setFocusable(false);
	}

	public FreeMindMapController getFreeMindMapController() {
		return mFreeMindMapController;
	}

	public boolean isUseCursor() {
		return mUseCursor;
	}

	public void setUseCursor(boolean pUseCursor) {
		mUseCursor = pUseCursor;
		repaint();

	}

	public Coordinate getCursorPosition() {
		return mCursorPosition;
	}

	public void setCursorPosition(Coordinate pCursorPosition) {
		mCursorPosition = pCursorPosition;
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openstreetmap.gui.jmapviewer.JMapViewer#paintComponent(java.awt.Graphics
	 * )
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (g instanceof Graphics2D) {
			Graphics2D g2d = (Graphics2D) g;
			Stroke oldStroke = g2d.getStroke();
			Color oldColor = g2d.getColor();
			// do cursor
			if (mUseCursor && mShowCursor) {
				Point position = getMapPosition(mCursorPosition);
				if (position != null) {
					int size_h = mCursorLength;
					g2d.setStroke(mStroke);
					g2d.setColor(Color.RED);
					g2d.drawLine(position.x - size_h, position.y, position.x
							+ size_h, position.y);
					g2d.drawLine(position.x, position.y - size_h, position.x,
							position.y + size_h);
				}
			}
			if (mDrawRectangular) {
				g2d.setColor(Color.BLACK);
				g2d.setStroke(mRectangularStroke);
				Rectangle r = getRectangle(mRectangularStart, mRectangularEnd);
				if (r != null) {
					g2d.drawRect(r.x, r.y, r.width, r.height);
				}
			}
			g2d.setColor(oldColor);
			g2d.setStroke(oldStroke);

		}
	}

	public Rectangle getRectangle(Coordinate rectangularStart,
			Coordinate rectangularEnd) {
		Point positionStart = getMapPosition(rectangularStart);
		Point positionEnd = getMapPosition(rectangularEnd);
		Rectangle r = null;
		if (positionStart != null && positionEnd != null) {
			int x = Math.min(positionStart.x, positionEnd.x);
			int y = Math.min(positionStart.y, positionEnd.y);
			int width = Math.abs(positionStart.x - positionEnd.x);
			int height = Math.abs(positionStart.y - positionEnd.y);
			r = new Rectangle(x, y, width, height);
		}
		return r;
	}
	
	public TileController getTileController() {
		return tileController;
	}

	/* (non-Javadoc)
	 * @see org.openstreetmap.gui.jmapviewer.JMapViewer#initializeZoomSlider()
	 */
	protected void initializeZoomSlider() {
		super.initializeZoomSlider();
		//focus
		zoomSlider.setFocusable(false);
		zoomInButton.setFocusable(false);
		zoomOutButton.setFocusable(false);
	}

	/**
	 * @param pHideFoldedNodes
	 */
	public void setHideFoldedNodes(boolean pHideFoldedNodes) {
		mHideFoldedNodes = pHideFoldedNodes;
		repaint();
	}

	public boolean isHideFoldedNodes() {
		return mHideFoldedNodes;
	}

	public void setRectangular(Coordinate pRectangularStart, Coordinate pRectangularEnd) {
		mRectangularStart = pRectangularStart;
		mRectangularEnd = pRectangularEnd;
	}


	public boolean isDrawRectangular() {
		return mDrawRectangular;
	}

	public void setDrawRectangular(boolean pDrawRectangular) {
		mDrawRectangular = pDrawRectangular;
	}

	/**
	 * @param pMapCenterLatitude
	 * @param pMapCenterLongitude
	 * @param pZoom
	 */
	public void setDisplayPositionByLatLon(double pMapCenterLatitude,
			double pMapCenterLongitude, int pZoom) {
		super.setDisplayPosition(new Coordinate(pMapCenterLatitude, pMapCenterLongitude), pZoom);
	}
	
	
}