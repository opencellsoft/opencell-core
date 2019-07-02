<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!--author Abdellatif BARI-->

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

    <!-- Update entity aliases for CustomFieldTemplates to match class names -->
    <xsl:template match="//org.meveo.model.crm.CustomFieldTemplate/appliesTo">
        <xsl:choose>
            <xsl:when test=".='PROVIDER'">
                <appliesTo>Provider</appliesTo>
            </xsl:when>
            <xsl:when test=".='PRODUCT'">
                <appliesTo>ProductTemplate</appliesTo>
            </xsl:when>
            <xsl:when test=".='PRODUCT_INSTANCE'">
                <appliesTo>ProductInstance</appliesTo>
            </xsl:when>
            <xsl:when test=".='OFFER'">
                <appliesTo>OfferTemplate</appliesTo>
            </xsl:when>
            <xsl:when test=".='SELLER'">
                <appliesTo>Seller</appliesTo>
            </xsl:when>
            <xsl:when test=".='CUST'">
                <appliesTo>Customer</appliesTo>
            </xsl:when>
            <xsl:when test=".='CA'">
                <appliesTo>CustomerAccount</appliesTo>
            </xsl:when>
            <xsl:when test=".='BA'">
                <appliesTo>BillingAccount</appliesTo>
            </xsl:when>
            <xsl:when test=".='UA'">
                <appliesTo>UserAccount</appliesTo>
            </xsl:when>
            <xsl:when test=".='SERVICE'">
                <appliesTo>ServiceTemplate</appliesTo>
            </xsl:when>
            <xsl:when test=".='SERVICE_INSTANCE'">
                <appliesTo>ServiceInstance</appliesTo>
            </xsl:when>
            <xsl:when test=".='SUB'">
                <appliesTo>Subscription</appliesTo>
            </xsl:when>
            <xsl:when test=".='ACC'">
                <appliesTo>Access</appliesTo>
            </xsl:when>
            <xsl:when test=".='CHARGE'">
                <appliesTo>ChargeTemplate</appliesTo>
            </xsl:when>
            <xsl:when test=".='PRICEPLAN'">
                <appliesTo>PricePlanMatrix</appliesTo>
            </xsl:when>


            <xsl:when test=".='BILLING_CYCLE'">
                <appliesTo>BillingCycle</appliesTo>
            </xsl:when>
            <xsl:when test=".='TAX'">
                <appliesTo>Tax</appliesTo>
            </xsl:when>
            <xsl:when test=".='INV_CAT'">
                <appliesTo>InvoiceCategory</appliesTo>
            </xsl:when>
            <xsl:when test=".='INVOICE'">
                <appliesTo>Invoice</appliesTo>
            </xsl:when>
            <xsl:when test=".='ACCT_CODE'">
                <appliesTo>AccountingCode</appliesTo>
            </xsl:when>
            <xsl:when test=".='FILTER'">
                <appliesTo>Filter</appliesTo>
            </xsl:when>
            <xsl:when test=".='QUOTE'">
                <appliesTo>Quote</appliesTo>
            </xsl:when>
            <xsl:when test=".='ORDER'">
                <appliesTo>Order</appliesTo>
            </xsl:when>
            <xsl:when test=".='USER'">
                <appliesTo>User</appliesTo>
            </xsl:when>
            <xsl:when test=".='JOB'">
                <appliesTo>JobInstance</appliesTo>
            </xsl:when>
            <xsl:when test=".='DISCOUNT_PLAN_INSTANCE'">
                <appliesTo>DiscountPlanInstance</appliesTo>
            </xsl:when>
            <xsl:when test=".='DISCOUNT_PLAN'">
                <appliesTo>DiscountPlan</appliesTo>
            </xsl:when>
            <xsl:when test=".='OFFER_CATEGORY'">
                <appliesTo>OfferTemplateCategory</appliesTo>
            </xsl:when>
            <xsl:when test=".='INV_SUB_CAT'">
                <appliesTo>InvoiceSubCategory</appliesTo>
            </xsl:when>
            <xsl:when test=".='ACC_OP'">
                <appliesTo>AccountOperation</appliesTo>
            </xsl:when>


            <xsl:when test=".='BILLING_RUN'">
                <appliesTo>BillingRun</appliesTo>
            </xsl:when>
            <xsl:when test=".='INVOICE_TYPE'">
                <appliesTo>InvoiceType</appliesTo>
            </xsl:when>
            <xsl:when test=".='DISCOUNT_PLAN_ITEM'">
                <appliesTo>DiscountPlanItem</appliesTo>
            </xsl:when>
            <xsl:when test=".='OTH_TR'">
                <appliesTo>OtherTransaction</appliesTo>
            </xsl:when>
            <xsl:when test=".='REPORT'">
                <appliesTo>ReportExtract</appliesTo>
            </xsl:when>
            <xsl:when test=".='BUNDLE'">
                <appliesTo>BundleTemplate</appliesTo>
            </xsl:when>
            <xsl:when test=".='PAYMENT_SCH_INSTANCE'">
                <appliesTo>PaymentScheduleInstance</appliesTo>
            </xsl:when>
            <xsl:when test=".='DDREQ_BUILDER'">
                <appliesTo>DDRequestBuilder</appliesTo>
            </xsl:when>
            <xsl:when test=".='PAYMENT_SCH'">
                <appliesTo>PaymentScheduleTemplate</appliesTo>
            </xsl:when>

            <xsl:when test="starts-with(.,'JOB_')">
                <appliesTo>
                    <xsl:value-of select="concat('JobInstance_',substring-after(., 'JOB_'))" />
                </appliesTo>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()" />
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!--author Abdellatif BARI-->
</xsl:stylesheet>
