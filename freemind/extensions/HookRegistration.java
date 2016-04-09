/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 *
 * Created on 12.08.2004
 */
/*$Id: HookRegistration.java,v 1.1.4.1 2004/10/17 23:00:07 dpolivaev Exp $*/

package freemind.extensions;

import freemind.modes.MindMap;
import freemind.modes.mindmapmode.MindMapController;

/**
 * A plugin (or a set of plugins) may have a plugin base class. 
 * 
 * It is created at the start of each mindmap and registered.
 * 
 * It is deregistered at mindmap shutdown (eg. the user closes the mindmap or the application).
 * 
 * The use case for these registration classes to provide some static data, caches.
 * Some plugins use it to register additional preference pages. Others register a mouse wheel listener.
 *   
 * @author foltin
 * 
 */
public interface HookRegistration {

	/**
	 * Is called at mindmap startup. The constructor passes the {@link MindMapController} 
	 * and the {@link MindMap}.
	 */
	void register();

	/**
	 * Is called at mindmap shutdown.
	 */
	void deRegister();

}
