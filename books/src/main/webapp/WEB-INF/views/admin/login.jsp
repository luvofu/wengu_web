<%--
  Created by IntelliJ IDEA.
  User: wzhonggo
  Date: 16-11-6
  Time: 上午10:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-type" content="text/html;charset=UTF-8">
    <title>管理人员登录页面</title>
    <script type="text/javascript" src="/js/jquery-3.1.1.min.js"></script>
    <script type="text/javascript" src="/js/jquery-ui-1.12.1/jquery-ui.min.js"></script>
    <link rel="stylesheet" type="text/css" href="/js/jquery-ui-1.12.1/jquery-ui.min.css"/>
    <link rel="stylesheet" type="text/css" href="/css/main.css">
    <style type="text/css">
         #adminLogin {
             width: 300px;
             height: 100px;
         }

    </style>

    <script type="text/javascript">
        $(document).ready(function(){
            $( "#adminLogin" ).position({
                of: $( "body" ),
                my: "center",
                at:"center"
            });

            $( "#adminLogin" ).on( "click", "#adminSubmit", function() {
                var param={};
                param["username"] = $("#username").val();
                param["password"] = $("#password").val();
                $.post("/api/admin/login", param).done(function (data) {
                    if(data.code==200){
                        window.location =" /api/admin/home"   ;
                    }  else{
                        $("#errorMsg").text(data.msg);
                    }
                })

            });
        })
    </script>
</head>
<body>
<div id="adminLogin">
        <table>
            <tr>
                <td colspan="2"> <span id="errorMsg" class="error"/></td>
            </tr>
            <tr>
                <td>用户名:</td>
                <td><input id="username"></td>
            </tr>
            <tr>
                <td>密码:</td>
                <td> <input id="password" type="password"></td>
            </tr>
            <tr>
                <td colspan="2"> <input id="adminSubmit" type="submit"></td>
            </tr>
        </table>






</div>
<jsp:include page="../common/footer.jsp"/>
</body>
</html>