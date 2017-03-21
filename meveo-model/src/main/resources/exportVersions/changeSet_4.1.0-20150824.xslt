<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" omit-xml-declaration="yes" indent="yes" />

    <xsl:param name="version" />

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="meveoExport">
        <meveoExport version="{$version}">
            <xsl:apply-templates />
        </meveoExport>
    </xsl:template>

    <xsl:template match="//org.meveo.model.crm.CustomFieldTemplate[not(storageType)]">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" />
            <xsl:if test="not(storageType)">
                <storageType>SINGLE</storageType>
            </xsl:if>
        </xsl:copy>
    </xsl:template>


    <xsl:template match="//org.meveo.model.crm.CustomFieldInstance[not(cfValue)]">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()[not(self::stringValue)][not(self::longValue)][not(self::dateValue)][not(self::doubleValue)]" />
            <xsl:if test="stringValue"><cfValue>
					<stringValue><xsl:value-of select="stringValue" /></stringValue>
				</cfValue>
            </xsl:if>
            <xsl:if test="longValue">
                <cfValue>
					<longValue><xsl:value-of select="longValue" /></longValue>
				</cfValue>
            </xsl:if>
            <xsl:if test="dateValue">
                <cfValue>
					<dateValue><xsl:value-of select="dateValue" /></dateValue>
				</cfValue>
            </xsl:if>
            <xsl:if test="doubleValue">
                <cfValue>
					<doubleValue><xsl:value-of select="doubleValue" /></doubleValue>
				</cfValue>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>