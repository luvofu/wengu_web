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
    <title>书籍社区合并页面</title>
    <script type="text/javascript" src="/js/jquery-3.1.1.min.js"></script>
    <script type="text/javascript" src="/js/jquery-ui-1.12.1/jquery-ui.min.js"></script>
    <link rel="stylesheet" type="text/css" href="/js/jquery-ui-1.12.1/jquery-ui.min.css"/>
    <link rel="stylesheet" type="text/css" href="/js/jquery-ui-1.12.1/jquery-ui.theme.min.css">
    <link rel="stylesheet" type="text/css" href="/css/main.css">
    <style type="text/css">
        .bookDiv {
            display: inline;
            width: 50%;
            float: left;
        }

        #query {
            margin-bottom: 20px;
        }

        .bookSelected {
            background-color: red;
        }

        .bookCommunitySelected {
            background-color: red;
        }

        #msgDiv {
            text-align: center;
            font-size: 24px;
        }
    </style>
    <script type="text/javascript">
        $(document).ready(function () {
            function query() {
                var param = {};
                var page = parseInt($("#currentPage").text() - 1);
                if (page < 0) {
                    page = 0;
                }
                param["page"] = page;
                $.post("/api/admin/book/notBookCommunity", param).done(function (data) {
                    if (data.code == 200) {
                        $.each(data.data.bookList, function (index, sub) {
                            var content = "<tr class='oldContent book'>";
                            content = content + "<td class='bookId'>" + sub.bookId + "</td>";
                            content = content + "<td class='bookTitle'>" + sub.title + "</td>";
                            content = content + "<td class='bookAuthor'>" + sub.author + "</td>";
                            content = content + "<td>" + sub.isbn13 + "</td>";
                            content = content + "<td>" + sub.publisher + "</td>";
                            content = content + "<td>" + sub.pubDate + "</td>";
                            content = content + "<td>" + sub.communityId + "</td>";
                            content = content + "</tr>";
                            $("#bookResult").append(content);
                        })
                    } else {
                        $("#errorMsg").text(data.msg);
                    }
                })
            }


            $("#main").on("click", "#prevPage", function () {
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


            $("#main").on("click", "#nextPage", function () {
                $(".oldContent").remove();
                var currentPage = 0;
                if ($("#currentPage").text() != "") {
                    currentPage = parseInt($("#currentPage").text())
                }
                currentPage = currentPage + 1;
//                        if(currentPage<0){
//                            currentPage=1;
//                        }
                $("#currentPage").text(currentPage);
                query();
            });

            $("#bookResult").on("click", ".book", function () {
                //clear msg
                $("#msg").html("");
                var title = $(this).find(".bookTitle").text();
                if (title == "") {
                    alert("book title is empty");
                } else {
                    $(".book").removeClass("bookSelected");
                    $(this).addClass("bookSelected");
                    $(".oldBookCommunityContent").remove();
                    var param = {};
                    param["title"] = title;
                    $.post("/api/admin/book/bookCommunity", param).done(function (data) {
                        if (data.code == 200) {
                            $.each(data.data.bookCommunityList, function (index, sub) {
                                var content = "<tr class='oldBookCommunityContent bookCommunity'>";
                                content = content + "<td class='bookCommunityId'>" + sub.communityId + "</td>";
                                content = content + "<td ><input class='bookCommunityTitle'  value='" + sub.title + "'></td>";
                                content = content + "<td ><input class='bookCommunityAuthor' value='" + sub.author + "'></td>";
                                content = content + "<td><input class='saveBookCommunity'  type='button' value='保存'></td>";
                                content = content + "</tr>";
                                $("#bookCommunityResult").append(content);
                            })
                        } else {
                            $("#errorMsg").text(data.msg);
                        }
                    })
                }
            });


            $("#bookCommunityResult").on("click", ".bookCommunity", function () {
                //clear msg
                $("#msg").html("");

                $(".bookCommunity").removeClass("bookCommunitySelected");
                $(this).addClass("bookCommunitySelected");
            });

            $("#main").on("click", "#mergeBookCommunity", function () {
                if ($(".bookCommunitySelected").length == 0 || $(".bookSelected").length == 0) {
                    alert("请选择一个BookCommunity和一个Book");
                } else {
                    var param = {};
                    param["bookId"] = parseInt($(".bookSelected").find(".bookId").text());
                    param["communityId"] = parseInt($(".bookCommunitySelected").find(".bookCommunityId").text());
                    $.post("/api/admin/redirectBookCommunity", param).done(function (data) {
                        if (data.code == 200) {
                            $("#msg").html("归入成功");
                        } else {
                            $("#msg").text(data.msg);
                        }
                    });
                }
            });

            $("#main").on("click", "#newBookCommunity", function () {
                if ($(".bookSelected").length == 0) {
                    alert("请选择一个Book");
                } else {
                    var param = {};
                    param["bookId"] = parseInt($(".bookSelected").find(".bookId").text());
                    $.post("/api/admin/redirectBookCommunity", param).done(function (data) {
                        if (data.code == 200) {
                            $("#msg").html("新建社区成功");
                        } else {
                            $("#msg").text(data.msg);
                        }
                    });

                }
            });


            $("#main").on("click", ".saveBookCommunity", function () {
                if ($(".bookCommunitySelected").length == 0) {
                    alert("请选择一个Book");
                } else {
                    var param = {};
                    param["bookCommunityId"] = parseInt($(".bookCommunitySelected").find(".bookCommunityId").text());
                    param["title"] = $(".bookCommunitySelected").find(".bookCommunityTitle").val();
                    param["author"] = $(".bookCommunitySelected").find(".bookCommunityAuthor").val();
                    $.post("/api/admin/bookCommunity/update", param).done(function (data) {
                        if (data.code == 200) {
                            $("#msg").html("更新社区成功");
                        } else {
                            $("#msg").text(data.msg);
                        }
                    });
                }
            });
            function init() {
                $("#currentPage").text(1);
                query();
            }

            init();
        })
    </script>
</head>
<body>
<jsp:include page="../common/header.jsp"/>
<div id="main">
    <div id="query">
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
    <div class="bookDiv">
        <table id="bookResult" border="1px">
            <tr>
                <td colspan="4">书籍（无社区）</td>
            </tr>
            <tr>
                <td>书籍ID</td>
                <td>书名</td>
                <td>作者</td>
                <td>ISBN13</td>
                <td>出版社</td>
                <td>出版日期</td>
                <td>社区ID</td>
            </tr>
        </table>
        <div>
            <input type="button" value="新建社区" id="newBookCommunity">
        </div>
    </div>
    <div class="bookDiv">
        <table id="bookCommunityResult" border="1px">
            <tr>
                <td colspan="3">书籍备选社区</td>
            </tr>
            <tr>
                <td>社区ID</td>
                <td>书名</td>
                <td>作者</td>
            </tr>
        </table>
        <div>
            <input type="button" value="归入社区" id="mergeBookCommunity">
        </div>
    </div>

    <div id="msgDiv" class="clear">
        <span id="msg"></span>
    </div>
</div>
<jsp:include page="../common/footer.jsp"/>
</body>
</html>