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

            <#if app_error??>
                <div class="content">
                    <span class="error">${app_error}</span>
                </div>
            </#if>

            <div class="menu">
                <#if create_car_allowed == true>
                    <button name="carBtn" onclick="location.href = '/app/create-car'">Buy New Car</button>
                </#if>
                <button name="tokenBtn" onclick="location.href = '/app/show-token'">Token</button>
                <button name="rptBtn" onclick="location.href = '/app/show-rpt'">RPT</button>
                <button name="accountBtn" onclick="location.href = '${accountUri}'" type="button">Account</button>
                <button name="logoutBtn" onclick="location.href = '/logout'" type="button">Logout</button>
            </div>

            <div class="content">
                <div id="profile-header" class="header">
                    User profile
                </div>

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

            <#list cars?keys as username>
                <div class="users-divider">
                </div>

                <div class="content">
                    <div class="header">
                        ${username}'s cars
                    </div>

                    <div class="message">
                        <table cellpadding="0" cellspacing="0">
                            <#list cars[username] as car>
                                <#if car?is_even_item>
                                <tr class="even">
                                <#else>
                                <tr>
                                </#if>

                                    <td class="label">${car.name}</td>
                                    <#if car.hasViewDetailsScope>
                                        <td class="button"><button onclick="location.href = '/app/details/${car.id}'">Details</button></td>
                                    <#else>
                                        <td class="button"><button onclick="location.href = '/app/details/${car.id}'">Ask for details</button></td>
                                    </#if>
                                    <#if car.hasDeleteScope>
                                        <td class="button"><button onclick="location.href = '/app/delete/${car.id}'">Delete</button></td>
                                    <#else>
                                        <td class="button"><button onclick="location.href = '/app/delete/${car.id}'">Ask for delete</button></td>
                                    </#if>
                                    <td></td>
                                </tr>
                            </#list>
                        </table>
                    </div>
                </div>
            </#list>
        </div>

    </body>
</html>