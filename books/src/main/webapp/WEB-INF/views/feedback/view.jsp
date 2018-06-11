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
    <title>用户反馈</title>
    <script type="text/javascript" src="/js/jquery-3.1.1.min.js"></script>
    <script type="text/javascript" src="/js/jquery-ui-1.12.1/jquery-ui.min.js"></script>
    <link rel="stylesheet" type="text/css" href="/js/jquery-ui-1.12.1/jquery-ui.min.css"/>
    <link rel="stylesheet" type="text/css" href="/js/jquery-ui-1.12.1/jquery-ui.theme.min.css">
    <link rel="stylesheet" type="text/css" href="/css/main.css">
    <script type="text/javascript">
        $(document).ready(function () {
            function query() {
                var param = {};
                var page = parseInt($("#currentPage").text() - 1);
                if (page < 0) {
                    page = 0;
                }
                param["page"] = page;
                $.post("/api/admin/feedback/query", param).done(function (data) {
                    if (data.code == 200) {
                        $.each(data.data.feedbackQueryResponseList, function (index, sub) {
                            var content = "<tr class='oldContent'>";
                            content = content + "<td>" + sub.createdTime + "</td>";
                            content = content + "<td>" + sub.username + "</td>";
                            content = content + "<td >" + sub.content + "</td>";
                            content = content + "<td>" + sub.connection + "</td>";
                            content = content + "<td>" + sub.feedbackId + "</td>";
                            content = content + "</tr>";
                            $("#result").append(content);
                        })
                    } else {
                        $("#errorMsg").text(data.msg);
                    }
                })
            }

            $("#startTime").datepicker();
            $("#endTime").datepicker();
            $("#param").on("click", "#query", function () {
                $(".oldContent").remove();
                $("#currentPage").text(1);
                query();
            });
            $("#param").on("click", "#prevPage", function () {
                $(".oldContent").remove();
                var currentPage = 0;
                if ($("#currentPage").text() != "") {
                    currentPage = parseInt($("#currentPage").text())
                }
                currentPage = currentPage - 1;
                if (currentPage < 0) {
                    currentPage = 1;
                }
                $("#currentPage").text(currentPage);
                query();
            });
            $("#param").on("click", "#nextPage", function () {
                $(".oldContent").remove();
                var currentPage = 0;
                if ($("#currentPage").text() != "") {
                    currentPage = parseInt($("#currentPage").text())
                }
                currentPage = currentPage + 1;
                $("#currentPage").text(currentPage);
                query();
            });
        })
    </script>
</head>
<body>
<jsp:include page="../common/header.jsp"/>
<div id="main">
    <div id="param">
        <table>
            <tr>
                <td colspan="2"><span id="errorMsg" class="error"/></td>
            </tr>
            <tr>
                <td colspan="2"><input type="button" id="query" value="查询"></td>
            </tr>
        </table>
        <table>
            <tr>
                <td>
                    <input type="button" id="prevPage" value="上一页">
                    <span id="currentPage"></span>
                    <input type="button" id="nextPage" value="下一页">
                </td>
            </tr>
        </table>
    </div>
    <div>
        <table id="result" border="1px">
            <tr>
                <td>时间</td>
                <td>昵称</td>
                <td>内容</td>
                <td>联系</td>
                <td>反馈ID</td>
            </tr>
        </table>
    </div>
</div>
<jsp:include page="../common/footer.jsp"/>
</body>
</html>