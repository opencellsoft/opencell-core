<%@ page import="java.io.PrintWriter" %>
<%@ page import="org.meveo.commons.utils.ExceptionUtils" %>
<html>
<head>
    <meta HTTP-EQUIV="Content-Type" CONTENT="text/html;charset=UTF-8"/>
    <title>Application Error</title>
</head>
<body>
<style>
    body {
        font-family: arial, verdana, Geneva, Arial, Helvetica, sans-serif;
        font-size: 1.1em;
    }

    .errorHeader {
        font-size: 1.6em;
        background-color: #6392C6;
        color: white;
        font-weight: bold;
        padding: 3px;
        margin-bottom: 10px;
    }

    .errorFooter {
        font-size: 0.8em;
        background-color: #6392C6;
        color: white;
        font-style: italic;
        padding: 3px;
        margin-top: 5px;
    }

    .errorMessage {
        color: red;
        font-weight: bold;
    }

    .errorExceptionCause {
        padding: 3px;
        border-style: solid;
        border-width: 1px;
        border-color: #9F9F9F;
        background-color: #E0E0E0;
        font-size: 80%;
    }

</style>
<div class="errorHeader">Your request was not successful, server-side error encountered:</div>
<%
    String customMessage = "Please contact your system administrator. Phone: ?, Email: ?";
    Boolean showstack = true;

    if (customMessage != null) {
%><span class="errorMessage"><%=customMessage%></span><%
    }

    Throwable exception = (Throwable) request.getAttribute("javax.servlet.error.exception");
    if ((showstack == null || showstack) && exception != null) {
        Throwable rootCause = ExceptionUtils.getRootCause(exception);
        String exceptionMessage = rootCause.getMessage();

%><hr/>Root cause exception message: <span class="errorMessage"><%=exceptionMessage%></span><%

%><br/><%
%><span id="errorDetails" class="errorExceptions"><%
%><pre class="errorExceptionCause"><%
    PrintWriter pw = new PrintWriter(out);
    rootCause.printStackTrace(pw);
%></pre><%
%></span><%
    }
%>
<div class="errorFooter"><%= new java.util.Date() %></div>
</body>
</html>
