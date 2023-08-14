<#function getLangCode locale>
    <#return locale?lower_case?substring(0, 2)>
</#function>
<#macro registrationLayout bodyClass="" displayWide="false" displayInfo=false displayMessage=true>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="${getLangCode(locale.current)}" xml:lang="${getLangCode(locale.current)}" xmlns="http://www.w3.org/1999/xhtml" class="${properties.kcHtmlClass!}">

<head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="robots" content="noindex, nofollow">

    <#if properties.meta?has_content>
        <#list properties.meta?split(' ') as meta>
            <meta name="${meta?split('==')[0]}" content="${meta?split('==')[1]}"/>
        </#list>
    </#if>
    <title><#nested "title"></title>
    <link rel="icon" href="${url.resourcesPath}/img/favicon.ico" />
    <#if properties.styles?has_content>
        <#list properties.styles?split(' ') as style>
            <link href="${url.resourcesPath}/${style}" rel="stylesheet" />
        </#list>
    </#if>
    <#if properties.scripts?has_content>
        <#list properties.scripts?split(' ') as script>
            <script src="${url.resourcesPath}/${script}" type="text/javascript"></script>
        </#list>
    </#if>
    <#if scripts??>
        <#list scripts as script>
            <script src="${script}" type="text/javascript"></script>
        </#list>
    </#if>
</head>

<body class="${properties.kcBodyClass!}">
   
    <div id="kc-container" class="${properties.kcContainerClass!}">
        <#if realm.internationalizationEnabled>
            <div id="kc-locale" class="${properties.kcLocaleClass!}">
                <div id="kc-locale-wrapper" class="${properties.kcLocaleWrapperClass!}">
                    <div class="kc-dropdown" id="kc-locale-dropdown">
                        <a href="#" id="kc-current-locale-link">${locale.current}</a>
                        <ul>
                            <#list locale.supported as l>
                                <li class="kc-dropdown-item"><a href="${l.url}" lang="${getLangCode(l.label)}" aria-label="${l.label}">${l.label}</a></li>
                            </#list>
                        </ul>
                    </div>
                </div>
            </div>
        </#if>

        <div id="kc-content" class="${properties.kcContentClass!}">
            <div id="logo-wrapper">
                <img src="${url.resourcesPath}/img/opencell.svg" aria-label="Opencell Logo" >
            </div>

            <div id="kc-content-wrapper" class="${properties.kcContentWrapperClass!}">
                <div id="kc-form-main">
                    <div id="kc-form-header">
                        <div id="kc-lock-wrapper">
                            <img id="kc-lock-icon" src="${url.resourcesPath}/img/lock.svg" aria-label="Secure Login" >
                        </div>
                    </div>
                    
                    <#if displayMessage && message?has_content>
                        <div class="${properties.kcFeedbackAreaClass!}">
                            <div class="alert alert-${message.type}">
                                <#if message.type = 'success'><span class="${properties.kcFeedbackSuccessIcon!}"></span></#if>
                                <#if message.type = 'warning'><span class="${properties.kcFeedbackWarningIcon!}"></span></#if>
                                <#if message.type = 'error'><span class="${properties.kcFeedbackErrorIcon!}"></span></#if>
                                <#if message.type = 'info'><span class="${properties.kcFeedbackInfoIcon!}"></span></#if>
                                <span class="kc-feedback-text">${message.summary}</span>
                            </div>
                        </div>
                    </#if>

                    <#nested "form">

                    <#if displayInfo>
                        <div id="kc-info" class="${properties.kcInfoAreaClass!}">
                            <div id="kc-info-wrapper" class="${properties.kcInfoAreaWrapperClass!}">
                                <#nested "info">
                            </div>
                        </div>
                    </#if>
                </div>
            </div>
        </div>            
    </div>   
</body>
</html>
</#macro>
