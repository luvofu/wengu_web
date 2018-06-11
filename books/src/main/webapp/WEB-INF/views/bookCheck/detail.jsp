<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: wzhonggo
  Date: 16-11-6
  Time: 下午2:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>书籍录入审核</title>
    <script type="text/javascript" src="/js/jquery-3.1.1.min.js"></script>
    <script type="text/javascript" src="/js/jquery-ui-1.12.1/jquery-ui.min.js"></script>
    <link rel="stylesheet" type="text/css" href="/js/jquery-ui-1.12.1/jquery-ui.min.css"/>
    <link rel="stylesheet" type="text/css" href="/js/jquery-ui-1.12.1/jquery-ui.theme.min.css">
    <link rel="stylesheet" type="text/css" href="/css/main.css">
    <script type="text/javascript">
        $(document).ready(function () {
            $("#detail").on("click", "#submit", function () {
                var param = {};
                param["bookCheckId"] = parseInt($("#bookCheckId").text());
                param["checkStatus"] = parseInt($("#checkStatus").val());
                param["checkInfo"] = $("#checkInfo").val();
                $.post("/api/admin/bookCheck/update", param).done(function (data) {
                    if (data.code == 200) {
                        $("#errorMsg").text("更新成功");
                    } else {
                        $("#errorMsg").text(data.msg);
                    }
                });
            });
        })
    </script>
</head>
<body>
<jsp:include page="../common/header.jsp"/>
<div id="main">
    <div id="detail">
        <table id="result" border="1px">
            <tr>
                <td>nickname</td>
                <td> ${bookCheckQueryResponse.nickname}</td>
            </tr>
            <tr>
                <td>author</td>
                <td>${bookCheckQueryResponse.author}</td>
            </tr>
            <tr>
                <td>bookCheckId</td>
                <td id="bookCheckId">${bookCheckQueryResponse.bookCheckId}</td>
            </tr>
            <tr>
                <td>title</td>
                <td>${bookCheckQueryResponse.title}</td>
            </tr>
            <tr>
                <td>originTitle</td>
                <td> ${bookCheckQueryResponse.originTitle}</td>
            </tr>
            <tr>
                <td>subTitle</td>
                <td> ${bookCheckQueryResponse.subTitle}</td>
            </tr>
            <tr>
                <td>summary</td>
                <td>${bookCheckQueryResponse.summary}</td>
            </tr>
            <tr>
                <td>price</td>
                <td>${bookCheckQueryResponse.price}</td>
            </tr>
            <tr>
                <td>cover</td>
                <td><img src="${bookCheckQueryResponse.cover}"></td>
            </tr>
            <tr>
                <td>pages</td>
                <td>${bookCheckQueryResponse.pages}</td>
            </tr>
            <tr>
                <td>authorInfo</td>
                <td>${bookCheckQueryResponse.authorInfo}</td>
            </tr>
            <tr>
                <td>translator</td>
                <td> ${bookCheckQueryResponse.translator}</td>
            </tr>
            <tr>
                <td>publisher</td>
                <td> ${bookCheckQueryResponse.publisher}</td>
            </tr>
            <tr>
                <td>pubDate</td>
                <td>${bookCheckQueryResponse.pubDate}</td>
            </tr>
            <tr>
                <td>isbn13</td>
                <td>${bookCheckQueryResponse.isbn13}</td>
            </tr>
            <tr>
                <td>binding</td>
                <td> ${bookCheckQueryResponse.binding}</td>
            </tr>
            <tr>
                <td>createdTime</td>
                <td> ${bookCheckQueryResponse.createdTime}</td>
            </tr>
            <tr>
                <td>updatedTime</td>
                <td> ${bookCheckQueryResponse.updatedTime}</td>
            </tr>
            <tr>
                <td>extendInfo</td>
                <td> ${bookCheckQueryResponse.extendInfo}</td>
            </tr>
            <tr>
                <td>old checkStatus</td>
                <td> ${bookCheckQueryResponse.checkStatusMsg}</td>
            </tr>
            <tr>
                <td>checkStatus</td>
                <td>
                    <select id="checkStatus">
                        <option value="0">审核中</option>
                        <option value="1">审核未通过</option>
                        <option value="2">审核通过</option>
                    </select></td>
            </tr>
            <tr>
                <td>checkInfo</td>
                <td><textarea id="checkInfo">${bookCheckQueryResponse.checkInfo}</textarea></td>
            </tr>
            <tr>
                <c:if test="${bookCheckQueryResponse.checkStatus!=2}">
                    <td colspan="2"><input type="button" id="submit" value="更新"></td>
                </c:if>
                <c:if test="${bookCheckQueryResponse.checkStatus==2}">
                    <td colspan="2"><input disabled="disabled" type="button" value="Locked"></td>
                </c:if>
            </tr>
            <tr>
                <td colspan="2"><span id="errorMsg" class="error"/></td>
            </tr>
        </table>
    </div>
</div>
<jsp:include page="../common/footer.jsp"/>
</body>
</html>