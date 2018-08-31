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

    <!-- Split org.meveo.model.scripts.EntityActionScript to org.meveo.model.crm.custom.EntityCustomAction and org.meveo.model.scripts.ScriptInstance -->

    <xsl:template match="//exportTemplate[@name='EntityActionScript']">
        <exportTemplate xsId="{@xsId}" name="EntityCustomAction" entityToExport="org.meveo.model.crm.custom.EntityCustomAction" canDeleteAfterExport="false">
            <exportAllClassesAsFull>false</exportAllClassesAsFull>
            <classesToExportAsFull xsId="{classesToExportAsFull/@xsId}">
                <java-class>org.meveo.model.scripts.ScriptInstance</java-class>
            </classesToExportAsFull>
        </exportTemplate>
    </xsl:template>

    <xsl:template match="//org.meveo.model.scripts.EntityActionScript">
        <org.meveo.model.crm.custom.EntityCustomAction xsId="{@xsId}">
            <xsl:apply-templates select="id|version|provider|disabled|auditable|code|description|appliesTo|applicableOnEl|label" />
            <script xsId="2000{@xsId}">
                <xsl:if test="provider[@xsId]">
                    <provider reference="{provider/@xsId}" />
                </xsl:if>
                <xsl:if test="provider[@reference]">
                    <provider reference="{provider/@reference}" />
                </xsl:if>
                <xsl:apply-templates select="disabled|auditable|code|description|script|sourceTypeEnum|error" />
            </script>
        </org.meveo.model.crm.custom.EntityCustomAction>
    </xsl:template>

    <xsl:template match="//classesToExportAsFull">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()[not(self::java-class='org.meveo.model.scripts.EntityActionScript')]" />
        </xsl:copy>
    </xsl:template>



</xsl:stylesheet>