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

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.OsmMercator;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;

import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.modes.common.plugins.MapNodePositionHolderBase;
import freemind.modes.mindmapmode.MindMapController;
import freemind.view.mindmapview.NodeView;

/**
 * @author foltin
 * @date 27.10.2011
 */
public class MapNodePositionHolder extends MapNodePositionHolderBase {
	/**
	 * 
	 */
	private static final String MAP_LOCATION = "map_location";
	private Coordinate mPosition = new Coordinate(0, 0);
	private Coordinate mMapCenter = new Coordinate(0, 0);
	private String mTileSource = null;
	private int mZoom = 1;
	private static String sMapLocationGif;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.extensions.NodeHookAdapter#invoke(freemind.modes.MindMapNode)
	 */
	public void invoke(MindMapNode pNode) {
		super.invoke(pNode);
		getRegistration().registerMapNode(this);
	}

	protected Registration getRegistration() {
		return (Registration) getPluginBaseClass();
	}

	public void showTooltip() {
		if (isTooltipDesired()) {
			setTooltip();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.PermanentNodeHookAdapter#shutdownMapHook()
	 */
	public void shutdownMapHook() {
		getRegistration().deregisterMapNode(this);
		super.shutdownMapHook();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.extensions.PermanentNodeHookAdapter#save(freemind.main.XMLElement
	 * )
	 */
	public void save(XMLElement xml) {
		super.save(xml);
		HashMap values = new HashMap();
		values.put(XML_STORAGE_POS_LON, toString(mPosition.getLon()));
		values.put(XML_STORAGE_POS_LAT, toString(mPosition.getLat()));
		values.put(XML_STORAGE_MAP_LON, toString(mMapCenter.getLon()));
		values.put(XML_STORAGE_MAP_LAT, toString(mMapCenter.getLat()));
		values.put(XML_STORAGE_ZOOM, toString(mZoom));
		if (mTileSource != null) {
			values.put(XML_STORAGE_TILE_SOURCE, mTileSource);
		}
		if (mTooltipLocation != null) {
			values.put(XML_STORAGE_MAP_TOOLTIP_LOCATION, mTooltipLocation);
		}
		saveNameValuePairs(values, xml);
	}

	/**
	 * @param pDouble
	 * @return
	 */
	private String toString(double pDouble) {
		return "" + pDouble;
	}

	/**
	 * @param pInt
	 * @return
	 */
	private String toString(int pInt) {
		return "" + pInt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.PermanentNodeHookAdapter#loadFrom(freemind.main.
	 * XMLElement)
	 */
	public void loadFrom(XMLElement pChild) {
		super.loadFrom(pChild);
		HashMap values = loadNameValuePairs(pChild);
		mPosition.setLat(fromString(values.get(XML_STORAGE_POS_LAT)));
		mPosition.setLon(fromString(values.get(XML_STORAGE_POS_LON)));
		mMapCenter.setLat(fromString(values.get(XML_STORAGE_MAP_LAT)));
		mMapCenter.setLon(fromString(values.get(XML_STORAGE_MAP_LON)));
		mZoom = intFromString(values.get(XML_STORAGE_ZOOM));
		// is done in super implementation
		// // if no value stored, the get method returns null.
		// mTooltipLocation = (String)
		// values.get(XML_STORAGE_MAP_TOOLTIP_LOCATION);
		mTileSource = (String) values.get(XML_STORAGE_TILE_SOURCE);
	}

	/**
	 * @param pObject
	 * @return
	 */
	private double fromString(Object pObject) {
		if (pObject == null) {
			return 0.0;
		}
		try {
			return Double.parseDouble((String) pObject);
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			return 0.0;
		}
	}

	/**
	 * @param pObject
	 * @return
	 */
	private int intFromString(Object pObject) {
		if (pObject == null) {
			return 1;
		}
		try {
			return Integer.parseInt((String) pObject);
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			return 1;
		}
	}

	/**
	 * Set map position. Is undoable.
	 * 
	 * @param pTileSource
	 * 
	 */
	public void changePosition(Coordinate pPosition, Coordinate pMapCenter,
			int pZoom, String pTileSource) {
		getRegistration().changePosition(this, pPosition, pMapCenter, pZoom,
				pTileSource);
	}

	public static interface MapNodePositionListener {
		void registerMapNode(MapNodePositionHolder pMapNodePositionHolder);

		void deregisterMapNode(MapNodePositionHolder pMapNodePositionHolder);
	}

	public Coordinate getPosition() {
		return mPosition;
	}

	public static MapNodePositionHolder getHook(MindMapNode node) {
		return (MapNodePositionHolder) getBaseHook(node);
	}

	/**
	 * @param pTileSource
	 */
	public void setTileSource(String pTileSource) {
		mTileSource = pTileSource;
	}

	/**
	 * @return
	 */
	public String getTileSource() {
		return mTileSource;
	}

	public void setPosition(Coordinate pPosition) {
		mPosition = pPosition;
	}

	public Coordinate getMapCenter() {
		return mMapCenter;
	}

	public void setMapCenter(Coordinate pMapCenter) {
		mMapCenter = pMapCenter;
	}

	public int getZoom() {
		return mZoom;
	}

	public void setZoom(int pZoom) {
		mZoom = pZoom;
	}

	public MindMapNode getNode() {
		return super.getNode();
	}

	public String getNodeId() {
		return getMindMapController().getNodeID(getNode());
	}

	/**
	 * @return
	 */
	private MindMapController getMindMapController() {
		return (MindMapController) getController();
	}

	/**
	 * @return This method returns true, when a parent of the corresponding node
	 *         is folded.
	 */
	public boolean hasFoldedParents() {
		return getNode().hasFoldedParents();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.extensions.PermanentNodeHookAdapter#onViewCreatedHook(freemind
	 * .view.mindmapview.NodeView)
	 */
	public void onViewCreatedHook(NodeView pNodeView) {
		super.onViewCreatedHook(pNodeView);
		logger.fine("View created for " + this);
		getRegistration().fireNodeVisibilityChanged(true, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.extensions.PermanentNodeHookAdapter#onViewRemovedHook(freemind
	 * .view.mindmapview.NodeView)
	 */
	public void onViewRemovedHook(NodeView pNodeView) {
		super.onViewRemovedHook(pNodeView);
		logger.fine("Removed view for " + this);
		getRegistration().fireNodeVisibilityChanged(false, this);
	}

	public String toString() {
		return "MapNodePositionHolder [mPosition=" + mPosition
				+ ", mMapCenter=" + mMapCenter + ", mTileSource=" + mTileSource
				+ ", mZoom=" + mZoom + ", getNode()=" + getNode() + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.extensions.PermanentNodeHookAdapter#saveHtml(java.io.Writer)
	 */
	public void saveHtml(Writer pFileout) throws IOException {
		super.saveHtml(pFileout);
		if (sMapLocationGif == null) {
			sMapLocationGif = getController().getFrame().getProperty(
					MAP_LOCATION);
		}
		String link = FreeMindMapController.getLink(mTileSource, mPosition,
				mMapCenter, mZoom);
		// to embed the original thumb nail: getImageTag() +
		pFileout.append("<a href=\"" + link + "\">" + "<img src=\""
				+ sMapLocationGif + "\"/></a>");
	}

	/**
	 * @return
	 */
	public String getImageHtml() {
		if(mTileSource == null) {
			return super.getImageHtml();
		}
		TileSource tileSource = FreeMindMapController.getTileSourceByName(mTileSource).mTileSource;
		int tileSize = tileSource.getTileSize();
		int exactx = (int) OsmMercator.LonToX(mPosition.getLon(), mZoom);
		int exacty = (int) OsmMercator.LatToY(mPosition.getLat(), mZoom);
		int x = exactx / tileSize;
		int y = exacty / tileSize;
		// determine other surrounding tiles that are close to the exact
		// point.
		int dx = exactx % tileSize;
		int dy = exacty % tileSize;
		// determine quadrant of cursor in tile:
		int posx = 0;
		int posy = 0;
		if (dx < tileSize / 2) {
			x -= 1;
			posx++;
			dx += tileSize;
		}
		if (dy < tileSize / 2) {
			y -= 1;
			posy++;
			dy += tileSize;
		}
		String imageHtml = "<html><body><table cellspacing='0' cellpadding='0'>";
		for (int j = 0; j < 2; ++j) {
			imageHtml+="<tr>";
			for (int i = 0; i < 2; ++i) {
				try {
					// getUrl:
					imageHtml += "<td valign='top' align='top' width='" + tileSize + "' height='"
							+ tileSize + "' BACKGROUND=\""
							+ tileSource.getTileUrl(mZoom, x + i, y + j)
							+ "\" style='background-repeat:no-repeat;'/>";
					if(i==posx && j==posy) {
						imageHtml += "<p style='margin-left:" + (dx % tileSize)
								+ "pt; margin-top:" + (dy % tileSize)
								+ "pt; color:red'><strong>X</strong></p>";
					}
					imageHtml += "</td>";
				} catch (IOException e) {
					freemind.main.Resources.getInstance().logException(e);
				}
			}
			imageHtml+="</tr>";
		}
		imageHtml+="</table></body></html>";
//		logger.fine("Tooltipp HTML: " + imageHtml);
		return imageHtml;
	}


	
}
