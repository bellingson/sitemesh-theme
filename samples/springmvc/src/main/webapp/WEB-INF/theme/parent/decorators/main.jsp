
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="page" uri="http://www.opensymphony.com/sitemesh/page" %>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>



<!doctype html>
<html>
<head>
   <title><decorator:title/> - ${appTheme.name}</title>

    <!-- // main.jsp -->

    <link href="/style/common.css"  rel="stylesheet" type="text/css"/>
    <link href="/style/theme.css"  rel="stylesheet" type="text/css"/>

    <decorator:head/>

</head>
<body>
<div id="container">

    <head>

        Decorator for: ${appTheme.name}

    </head>

    <div class="body">

        <decorator:body/>

        <br class="clear"/>
    </div>



</div><!--/ container -->
</body>
</html>
