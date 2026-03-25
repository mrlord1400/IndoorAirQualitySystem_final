
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login Page</title>
        <style>
            
            h1, form{
                text-align: center;
            }
            input{
                font-size: 18px;
                margin-top: 10px;
                margin-left: 10px;
                padding: 5px 20px;
            }
            .button{
                font-size: 20px;
                background-color: #00BFFF;
                margin-top: 10px;
                padding: 8px 30px;
                border: 1px solid black;
                border-radius: 5px;
                cursor: pointer;
                transition: all 0.5s;
            }
            .button:hover{
                background-color: #483D8B; 
                color: white;
            }
        </style>
    </head>
    <body>
        <h1>Login Page</h1>
        <form action="MainController" method="POST">
            User ID <input type="text" name="username"/></br>
            Password<input type="password" name="password"/></br>
            <input class="button" type="submit" name="action" value="Login"/>
        </form>
        <% 
            String error= (String)request.getAttribute("ERROR");
            if(error== null) error= "";
        %>
         <h1><%= error %></h1>
    </body>
</html>
