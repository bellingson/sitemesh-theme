
<%@ include file="/components/taglibs.jsp" %>

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
