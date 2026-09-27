<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head><title>Error</title></head>
<body>
<%@ include file="/WEB-INF/header.jspf" %>
<main>
    <div class="card">
        <h2>Something went wrong</h2>
        <p class="subtle">The page you requested doesn't exist, or an error occurred. Please go back and try again.</p>
        <a class="btn" href="${pageContext.request.contextPath}/index.jsp">Back to Home</a>
    </div>
</main>
</body>
</html>
