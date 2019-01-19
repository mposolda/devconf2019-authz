<#import "/spring.ftl" as spring />
<#assign xhtmlCompliant = true in spring>
<!DOCTYPE html>
<html>
    <head>
        <title>Access Token Details</title>
        <link rel="stylesheet" type="text/css" href="/styles.css"/>
    </head>
    <body>
        <div class="wrapper" id="profile">
            <div class="menu">
                <button name="backBtn" onclick="location.href = '/app'">Back</button>
            </div>

            <div class="content">
                <div id="token-content" class="message">${tokenString}</div>
            </div>

        </div>

    </body>
</html>