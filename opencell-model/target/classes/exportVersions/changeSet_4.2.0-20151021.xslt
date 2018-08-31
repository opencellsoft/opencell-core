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

    <xsl:template match="//org.meveo.model.crm.CustomFieldInstance[not(valuePeriods/org.meveo.model.crm.CustomFieldPeriod)]">
        <xsl:copy>
            <xsl:apply-templates select="@*|id|version|disabled|auditable|code|cfValue" />
            <xsl:if test="../../../provider[@xsId]">
                <provider reference="{../../../provider/@xsId}" />
            </xsl:if>
            <xsl:if test="../../../provider[@reference]">
                <provider reference="{../../../provider/@reference}" />
            </xsl:if>
            <appliesToEntity>
                <xsl:value-of select="concat(name(../../..),'_')" />
                <xsl:value-of select="../../../id" />
            </appliesToEntity>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="//org.meveo.model.crm.CustomFieldPeriod">
        <org.meveo.model.crm.CustomFieldInstance xsId="{@xsId}">
            <xsl:apply-templates select="id|version|periodStartDate|periodEndDate|priority|../../disabled|../../auditable|../../code|cfValue" />
            <xsl:if test="../../../../../provider[@xsId]">
                <provider reference="{../../../../../provider/@xsId}" />
            </xsl:if>
            <xsl:if test="../../../../../provider[@reference]">
                <provider reference="{../../../../../provider/@reference}" />
            </xsl:if>
            <appliesToEntity>
                <xsl:value-of select="concat(name(../../../../..),'_')" />
                <xsl:value-of select="../../../../../id" />
            </appliesToEntity>
        </org.meveo.model.crm.CustomFieldInstance>
    </xsl:template>

    <xsl:template
        match="//org.meveo.model.catalog.OfferTemplate|//org.meveo.model.mediation.Access|//org.meveo.model.billing.BillingAccount|//org.meveo.model.crm.Customer|//org.meveo.model.payments.CustomerAccount|//org.meveo.model.billing.UserAccount|//org.meveo.model.catalog.OneShotChargeTemplate|//org.meveo.model.catalog.RecurringChargeTemplate|//org.meveo.model.catalog.UsageChargeTemplate|//org.meveo.model.customEntities.CustomEntityInstance|//org.meveo.model.jobs.JobInstance|//org.meveo.model.admin.Seller|//org.meveo.model.catalog.ServiceTemplate|//org.meveo.model.billing.Subscription|//org.meveo.model.crm.Provider">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()[not(self::customFields)]" />
            <xsl:if test="not(uuid)">
                <uuid>
                    <xsl:value-of select="concat(name(.),'_')" />
                    <xsl:value-of select="id" />
                </uuid>
            </xsl:if>
        </xsl:copy>
        <xsl:apply-templates select=".//org.meveo.model.crm.CustomFieldPeriod|.//org.meveo.model.crm.CustomFieldInstance[not(valuePeriods/org.meveo.model.crm.CustomFieldPeriod)]" />
    </xsl:template>

    <xsl:template match="//classesToExportAsFull">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()[not(self::java-class='org.meveo.model.crm.CustomFieldPeriod')]" />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="//org.meveo.model.crm.CustomFieldTemplate[accountLevel]">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()[not(self::code)][not(self::accountLevel)]" />
            <allowEdit>true</allowEdit>
            <xsl:if test="accountLevel[. = 'TIMER']">
                <appliesTo>
                    <xsl:value-of select="concat('JOB_',substring-before(code,'_'))" />
                </appliesTo>
                <xsl:choose>
                    <xsl:when test="contains(code, '_nbRuns')">
                        <code>nbRuns</code>
                    </xsl:when>
                    <xsl:when test="contains(code,'_waitingMillis')">
                        <code>waitingMillis</code>
                    </xsl:when>
                    <xsl:otherwise>
                        <code>
                            <xsl:value-of select="code" />
                        </code>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="accountLevel[not(.= 'TIMER')]">
                <appliesTo>
                    <xsl:value-of select="accountLevel" />
                </appliesTo>
                <code>
                    <xsl:value-of select="code" />
                </code>
            </xsl:if>
            <xsl:if test="not(storageType)">
                <storageType>SINGLE</storageType>
            </xsl:if>
            <xsl:if test="storageType = 'MAP'">
                <mapKeyType>STRING</mapKeyType>
            </xsl:if>            
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
