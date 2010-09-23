<%@page contentType="text/html;"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<body>
    <h1>New Customer</h1>
    <form:form>
        <form:label path="name">Name</form:label>
        <form:input path="name"/>
        <input type="submit" label="Create Customer"/>       
    </form:form>
</body>
</html>