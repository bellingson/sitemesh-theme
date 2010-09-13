
<%@ taglib prefix="page" uri="http://www.opensymphony.com/sitemesh/page" %>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>

<!doctype html>
<html>
<head>
   <title><decorator:title/> - ${app.name}</title>

   <link href="/styles/master.css" rel="stylesheet" type="text/css"/>
   <link href="/styles/theme.css" rel="stylesheet" type="text/css"/>

   <decorator:head/>



</head>
<body>

    <img src="/img/header.jpg" />

    <decorator:body/>

</body>
</html>