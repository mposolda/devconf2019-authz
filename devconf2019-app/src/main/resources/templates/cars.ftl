<#import "/spring.ftl" as spring />
<#assign xhtmlCompliant = true in spring>
<!DOCTYPE html>
<html>
<head>
    <title>Cars Page</title>
</head>
<body>

<header>
     <a href="/logout" id="logout">Logout</a>
</header>

<h1>Cars Page</h1>
<p>User ${principal.name} made this request.</p>


    <#list cars?keys as username>
        <h2>${username}'s cars</h2>
        <ul>
            <#list cars[username] as car>
                <li>${car.carDescription}</li>
            </#list>
        </ul>
    </#list>

</body>
</html>