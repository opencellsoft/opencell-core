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


    <!-- Add rounding, roundingMode, invoiceRounding and invoiceRoundingMode to Provider -->
    <xsl:template match="//org.meveo.model.crm.Provider">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" />
            <xsl:if test="not(rounding)">
                <rounding>6</rounding>
            </xsl:if>
            <xsl:if test="not(roundingMode)">
                <roundingMode>NEAREST</roundingMode>
            </xsl:if>
            <xsl:if test="not(invoiceRounding)">
                <invoiceRounding>2</invoiceRounding>
            </xsl:if>
            <xsl:if test="not(invoiceRoundingMode)">
                <invoiceRoundingMode>NEAREST</invoiceRoundingMode>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
