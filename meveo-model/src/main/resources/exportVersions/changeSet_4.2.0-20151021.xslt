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


    <!-- <xsl:template match="//org.meveo.model.jobs.TimerEntity[not(code)]"> -->
    <!-- <org.meveo.model.jobs.TimerEntity> -->
    <!-- <xsl:copy-of select="id" /> -->
    <!-- <xsl:copy-of select="version" /> -->
    <!-- <xsl:copy-of select="provider" /> -->
    <!-- <auditable> -->
    <!-- <created class="sql-timestamp">2015-06-23 18:13:23.166</created> -->
    <!-- </auditable> -->
    <!-- <code> -->
    <!-- <xsl:text>code_</xsl:text> -->
    <!-- <xsl:value-of select="id"></xsl:value-of> -->
    <!-- </code> -->
    <!-- <xsl:copy-of select="year" /> -->
    <!-- <xsl:copy-of select="month" /> -->
    <!-- <xsl:copy-of select="dayOfMonth" /> -->
    <!-- <xsl:copy-of select="dayOfWeek" /> -->
    <!-- <xsl:copy-of select="hour" /> -->
    <!-- <xsl:copy-of select="minute" /> -->
    <!-- <xsl:copy-of select="second" /> -->
    <!-- <jobInstances> -->
    <!-- <org.meveo.model.jobs.JobInstance> -->
    <!-- <xsl:copy-of select="id" /> -->
    <!-- <xsl:copy-of select="version" /> -->
    <!-- <provider code="{//provider[@code]/@code}" /> -->
    <!-- <auditable> -->
    <!-- <created class="sql-timestamp">2015-06-23 18:13:23.166</created> -->
    <!-- </auditable> -->
    <!-- <userId>1</userId> -->
    <!-- <code> -->
    <!-- <xsl:value-of select="name" /> -->
    <!-- </code> -->
    <!-- <jobTemplate> -->
    <!-- <xsl:value-of select="jobName" /> -->
    <!-- </jobTemplate> -->
    <!-- <xsl:copy-of select="jobCategoryEnum" /> -->
    <!-- <timerEntity reference="../../.." /> -->
    <!-- </org.meveo.model.jobs.JobInstance> -->
    <!-- </jobInstances> -->
    <!-- </org.meveo.model.jobs.TimerEntity> -->

    <!-- </xsl:template> -->

    <!-- <xsl:template match="//org.meveo.model.crm.CustomFieldInstance/timerEntity[@name]"> -->
    <!-- <jobInstance id="{@id}" code="{@name}" provider="{@provider}" /> -->
    <!-- </xsl:template> -->

    <!-- <xsl:template match="//org.meveo.model.crm.CustomFieldInstance/timerEntity[@reference]"> -->
    <!-- <jobInstance reference="{substring-before(@reference,'/timerEntity')}/jobInstance" /> -->
    <!-- </xsl:template> -->

</xsl:stylesheet>
