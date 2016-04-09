<?xml version="1.0" encoding="iso-8859-1"?>
<!--
    adapted from mm2oowriter.xsl by Ondrej Popp  
/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2008  Christian Foltin and others.
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
 */
-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:manifest="urn:oasis:names:tc:opendocument:xmlns:manifest:1.0">

	<xsl:output method="text" version="1.0" indent="no" encoding="UTF-8" omit-xml-declaration="no"/>
	<xsl:strip-space elements="*"/>
	
	<xsl:template match="map"><xsl:text>application/vnd.oasis.opendocument.text</xsl:text>
	</xsl:template>
	
</xsl:stylesheet>
