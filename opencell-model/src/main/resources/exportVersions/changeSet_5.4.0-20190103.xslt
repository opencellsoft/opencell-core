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


    <!-- Add uuid to CustomerCategory -->
    <xsl:template match="//org.meveo.model.crm.CustomerCategory">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" />
            <xsl:if test="not(uuid)">
                <uuid>
                    <xsl:text>ccat_</xsl:text>
                    <xsl:value-of select="id" />
                </uuid>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
