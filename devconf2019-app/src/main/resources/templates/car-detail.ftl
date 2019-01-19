<#import "/spring.ftl" as spring />
<#assign xhtmlCompliant = true in spring>
<!DOCTYPE html>
<html>
    <head>
        <title>Car Detail Page</title>
    </head>
    <body>

        <header>
            <a href="/app" id="back-to-list">Back to list</a>
        </header>

        <h1>Car Detail</h1>
        Car name: ${car.name} <br />
        Car owner: ${car.owner.username} <br />
        <img width="50%" src="/app/img/${car.id}" />
    </body>
</html>