<#import "/spring.ftl" as spring />
<#assign xhtmlCompliant = true in spring>
<!DOCTYPE html>
<html>
    <head>
        <title>Cars Page</title>
        <link rel="stylesheet" type="text/css" href="/styles.css"/>
    </head>
    <body>
        <div class="wrapper" id="profile">

            <div class="menu">
                <button name="carBtn" onclick="location.href = '/app/create-car'">Buy New Car</button>
                <button name="tokenBtn" onclick="location.href = '/app/show-token'">Token</button>
                <button name="accountBtn" onclick="location.href = '${accountUri}'" type="button">Account</button>
                <button name="logoutBtn" onclick="location.href = '/logout'" type="button">Logout</button>
            </div>

        <div class="content">
            <div id="profile-content" class="message">
                <table cellpadding="0" cellspacing="0">
                    <tr>
                        <td class="label">Username</td>
                        <td><span id="username">${token.preferredUsername}</span></td>
                    </tr>
                    <tr class="even">
                        <td class="label">Email</td>
                        <td><span id="email">${token.email}</span></td>
                    </tr>
                    <tr>
                        <td class="label">Name</td>
                        <td><span id="firstName">${token.givenName} ${token.familyName}</span></td>
                    </tr>
                </table>
            </div>
        </div>

        </div>

        <#if app_error??>
           <font color="red">${app_error}</font>
        </#if>


        <h1>Cars Page</h1>
        <p>User ${principal.name} made this request.</p>


        <#list cars?keys as username>
            <h2>${username}'s cars</h2>
            <ul>
                <#list cars[username] as car>
                    <li>${car.name} <a href="/app/details/${car.id}">Show Details</a> <a href="/app/delete/${car.id}">Delete</a></li>
                </#list>
            </ul>
        </#list>

    </body>
</html>