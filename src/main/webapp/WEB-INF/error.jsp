
<%@ taglib prefix="c" uri="https://jakarta.ee/xml/ns/jakartaee/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Errore Registrazione</title>
    <style>
        .error-box {
            padding: 20px;
            background-color: #ffe0e0;
            border: 1px solid red;
            border-radius: 10px;
            width: 400px;
            margin: 50px auto;
            text-align: center;
            font-family: Arial, sans-serif;
        }
    </style>
</head>
<body>
<div class="error-box">
    <h2>Errore durante la registrazione</h2>
    <c:choose>
        <c:when test="${signupError == 'emptyFields'}">
            <p>Tutti i campi sono obbligatori.</p>
        </c:when>
        <c:when test="${signupError == 'invalidFormat'}">
            <p>Uno o più campi non rispettano il formato richiesto.</p>
        </c:when>
        <c:when test="${signupError == 'invalidNumber'}">
            <p>Numero civico non valido.</p>
        </c:when>
        <c:when test="${signupError == 'usernameTaken'}">
            <p>Username già in uso. Scegline un altro.</p>
        </c:when>
        <c:when test="${signupError == 'dbCheckFailed'}">
            <p>Errore durante la verifica dell'username. Riprova.</p>
        </c:when>
        <c:when test="${signupError == 'dbInsertFailed'}">
            <p>Errore nel salvataggio dei dati. Riprova più tardi.</p>
        </c:when>
        <c:otherwise>
            <p>Errore sconosciuto.</p>
        </c:otherwise>
    </c:choose>
    <p><a href="signup.jsp">Torna alla registrazione</a></p>
</div>
</body>
</html>
