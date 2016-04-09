<?xml version="1.0" encoding="iso-8859-1"?>
<!--
    (c) by Stephen Fitch, 2005, adapted to redmine wiki by Chris Foltin
    This file is licensed under the GPL.
-->

<xsl:stylesheet version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xlink="http://www.w3.org/1999/xlink">
    <xsl:output method="text" indent="no"/>

        <xsl:strip-space elements="*"/>
        
	<xsl:template match="map">            
            	<xsl:apply-templates select="node"/>		
	</xsl:template>
        
        <!-- match "node" -->
	<xsl:template match="node">
		<xsl:variable name="depth">
			<xsl:apply-templates select=".." mode="depthMesurement"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$depth &lt; 3">
		<xsl:text>
</xsl:text>
	                	<xsl:text>h</xsl:text><xsl:value-of select="$depth + 1"/><xsl:text>. </xsl:text>
				<xsl:apply-templates select="." mode="nodeoutput"/>
		<xsl:text>
</xsl:text>
			</xsl:when>
                        <xsl:otherwise>
	                        <xsl:call-template name="ul">
	                            <xsl:with-param name="count" 
	                            select="$depth - 2"/>
	                        </xsl:call-template><xsl:text> </xsl:text>
				<xsl:apply-templates select="." mode="nodeoutput"/>
                        </xsl:otherwise>
		</xsl:choose>
		<xsl:text>
</xsl:text>
		<xsl:apply-templates select="node"/>
	</xsl:template>
        
        <xsl:template name="ul">
            <xsl:param name="count" select="1"/>
            <xsl:if test="$count > 0">
                <xsl:text>*</xsl:text>
                <xsl:call-template name="ul">
                    <xsl:with-param name="count" select="$count - 1"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:template>

	<!-- Node - Output -->
	<xsl:template match="node" mode="nodeoutput">
		<xsl:if test="@LINK">
			<xsl:text>"</xsl:text>
		</xsl:if>
		<xsl:if test="font/@BOLD='true'"><xsl:text>*</xsl:text></xsl:if>
		<xsl:choose>
			<xsl:when test="./richcontent[@TYPE='NODE']">
				<xsl:value-of select="./richcontent/html/body" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="@TEXT" />
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="font/@BOLD='true'"><xsl:text>*</xsl:text></xsl:if>
		<xsl:if test="@LINK">
			<xsl:text>":</xsl:text><xsl:value-of select="@LINK" />
		</xsl:if>
	</xsl:template>
        
        <!-- Node Depth Mesurement -->
        <xsl:template match="node" mode="depthMesurement">
            <xsl:param name="depth" select=" '0' "/>
                <xsl:apply-templates select=".." mode="depthMesurement">
                    <xsl:with-param name="depth" select="$depth + 1"/>
                </xsl:apply-templates>
	</xsl:template>
        
        <!-- Map Depth Mesurement -->
        <xsl:template match="map" mode="depthMesurement">
            <xsl:param name="depth" select=" '0' "/>
            <xsl:value-of select="$depth"/>
	</xsl:template>
		
</xsl:stylesheet>
