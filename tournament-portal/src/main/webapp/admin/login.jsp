<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head><title>Admin Login</title></head>
<body>
<%@ include file="/WEB-INF/header.jspf" %>
<main>
    <h2>Admin Login</h2>
    <c:if test="${not empty error}"><div class="error-box">${error}</div></c:if>
    <div class="card" style="max-width:420px;">
        <form method="post" action="${pageContext.request.contextPath}/admin/login">
            <label>Username</label>
            <input type="text" name="username" required>
            <label>Password</label>
            <input type="password" name="password" required>
            <button type="submit">Log In</button>
        </form>
    </div>
</main>
</body>
</html>
