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

    <xsl:template match="//classesToExportAsFull">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" />
            <xsl:if test="not(java-class[.= 'org.meveo.model.crm.CustomFieldFields'])">
                <java-class>org.meveo.model.crm.CustomFieldFields</java-class>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="//customFields[not(parent::cfFields)]">
        <cfFields xsId="{@xsId}00001">
            <xsl:if test="../provider[@xsId]">
                <provider reference="{../provider/@xsId}" />
            </xsl:if>
            <xsl:if test="../provider[@reference]">
                <provider reference="{../provider/@reference}" />
            </xsl:if>            
            <uuid>
                <xsl:value-of select="name(..)" />_<xsl:value-of select="../id" />
            </uuid>
            <xsl:copy>
                <xsl:apply-templates select="@*|node()" />
            </xsl:copy>
        </cfFields>
    </xsl:template>

    <xsl:template match="//org.meveo.model.crm.CustomFieldInstance[not(cfFields)]">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" />
            <cfFields reference="{../../@xsId}00001" />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="//org.meveo.model.crm.CustomFieldTemplate[accountLevel]">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()[not(self::code)][not(self::accountLevel)]" />
            <xsl:if test="accountLevel[. = 'TIMER']">
                    <appliesTo>JOB_<xsl:value-of select="substring-before(code,'_')" /></appliesTo>
                <xsl:choose>
                    <xsl:when test="contains(code, '_nbRuns')">
                        <code>nbRuns</code>
                    </xsl:when>
                    <xsl:when test="contains(code,'_waitingMillis')">
                        <code>waitingMillis</code>
                    </xsl:when>
                    <xsl:otherwise>
                        <code><xsl:value-of select="code" /></code>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="accountLevel[not(.= 'TIMER')]">
                <appliesTo><xsl:value-of select="accountLevel" /></appliesTo>
                <code><xsl:value-of select="code" /></code>
            </xsl:if>
            <xsl:if test="not(storageType)">
                <storageType>SINGLE</storageType>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
