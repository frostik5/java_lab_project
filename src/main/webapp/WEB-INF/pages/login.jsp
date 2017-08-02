<%@ page import="java.net.URL" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet"
          type="text/css"
          href="<c:url value='resources/style.css'/>">
</head>

<body>
<div class="body">
    <div class="formLogReg">
        <div class="flagesLogReg">
            <%String path = new URL(request.getRequestURL().toString()).getPath();%>
            <a href="language?locale=${"enLocale"}&backPage=<%=path%>"><img src="resources/images/flag_en.png" alt="en"></a>
            <a href="language?locale=${"ruLocale"}&backPage=<%=path%>"><img src="resources/images/flag_ru.png" alt="ru"></a>
        </div>
        <p class="error">${nonexistentLogin}</p>
        <p class="error">${regSuccess}</p>
        <p class="error">${loginFailed}</p>
        <p class="error">${fieldEmpty}</p>
        <form action="doLogin" method="post">
            <p>Логин/Login (email): <input class="fieldLogReg" type="email" name="email" value="${requestScope.email}"></p>
            <p>Пароль/Password: <input class="fieldLogReg" type="password" name="password"></p>
            <input class="buttonLogReg" type="submit" value="Login/Войти">
            <p><a href="regPage">Регистрация\Registration</a></p>
        </form>
    </div>
</div>
</body>
</html>
