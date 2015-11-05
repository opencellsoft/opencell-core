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

    <xsl:template match="//org.meveo.model.payments.CustomerAccount/creditCategory | //org.meveo.model.payments.DunningPlan/creditCategory">
        <xsl:choose>
            <xsl:when test="not(@code)">
                <creditCategory code="{.}" provider="{//provider[@code]/@code}" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()" />
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="//org.meveo.model.catalog.CounterTemplate">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" />
            <xsl:if test="not(./counterType)">
                <counterType>USAGE</counterType>
            </xsl:if>
            <xsl:if test="not(./counterLevel)">
                <counterLevel>UA</counterLevel>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="//org.meveo.model.jobs.TimerEntity[not(code)]">
        <org.meveo.model.jobs.TimerEntity>
            <xsl:copy-of select="id" />
            <xsl:copy-of select="version" />
            <xsl:copy-of select="provider" />
            <auditable>
                <created class="sql-timestamp">2015-06-23 18:13:23.166</created>
            </auditable>
            <code>
                <xsl:text>code_</xsl:text>
                <xsl:value-of select="id"></xsl:value-of>
            </code>
            <xsl:copy-of select="year" />
            <xsl:copy-of select="month" />
            <xsl:copy-of select="dayOfMonth" />
            <xsl:copy-of select="dayOfWeek" />
            <xsl:copy-of select="hour" />
            <xsl:copy-of select="minute" />
            <xsl:copy-of select="second" />
            <jobInstances>
                <org.meveo.model.jobs.JobInstance>
                    <xsl:copy-of select="id" />
                    <xsl:copy-of select="version" />
                    <provider code="{//provider[@code]/@code}" />
                    <auditable>
                        <created class="sql-timestamp">2015-06-23 18:13:23.166</created>
                    </auditable>
                    <userId>1</userId>
                    <code>
                        <xsl:value-of select="name" />
                    </code>
                    <jobTemplate>
                        <xsl:value-of select="jobName" />
                    </jobTemplate>
                    <xsl:copy-of select="jobCategoryEnum" />
                    <timerEntity reference="../../.." />
                </org.meveo.model.jobs.JobInstance>
            </jobInstances>
        </org.meveo.model.jobs.TimerEntity>

    </xsl:template>

    <xsl:template match="//org.meveo.model.crm.CustomFieldInstance/timerEntity[@name]">
        <jobInstance id="{@id}" code="{@name}" provider="{@provider}" />
    </xsl:template>

    <xsl:template match="//org.meveo.model.crm.CustomFieldInstance/timerEntity[@reference]">
        <jobInstance reference="{substring-before(@reference,'/timerEntity')}/jobInstance" />
    </xsl:template>

</xsl:stylesheet>