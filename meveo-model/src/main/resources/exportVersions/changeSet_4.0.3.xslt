<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="xml" omit-xml-declaration="yes" indent="yes" />

	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="//org.meveo.model.billing.WalletOperation/unityDescription">
		<ratingUnitDescription>
			<xsl:value-of select="." />
		</ratingUnitDescription>
	</xsl:template>

	<xsl:template match="//org.meveo.model.catalog.UsageChargeTemplate/unityDescription">
		<ratingUnitDescription>
			<xsl:value-of select="." />
		</ratingUnitDescription>
	</xsl:template>

	<xsl:template match="//org.meveo.model.catalog.UsageChargeTemplate/unityMultiplicator">
		<unitMultiplicator>
			<xsl:value-of select="." />
		</unitMultiplicator>
	</xsl:template>

	<xsl:template match="//org.meveo.model.catalog.UsageChargeTemplate/unityNbDecimal">
		<unitNbDecimal>
			<xsl:value-of select="." />
		</unitNbDecimal>
	</xsl:template>

	<xsl:template match='//*[@class="org.meveo.model.billing.UsageChargeInstance"]/unityDescription'>
		<ratingUnitDescription>
			<xsl:value-of select="." />
		</ratingUnitDescription>
	</xsl:template>


</xsl:stylesheet>