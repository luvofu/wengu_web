<%--
  Created by IntelliJ IDEA.
  User: wzhonggo
  Date: 16-11-6
  Time: 上午10:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" pageEncoding="UTF-8"%>
<style type="text/css">
    ul,li,ol{
        margin: 0;
        padding:0;
        list-style: none outside none;
    }
    .active{background:#eee;}
    .hidden{display: none;}
    .menu{width:100%; background:#ECECEC; position:fixed; _position:absolute; top:0; left:0; z-index:3;}
    .menu ul{width:600px; margin:0 auto;}
    .menu ul li{float:left; margin-right:10px; padding:2px 6px;}
    .menu ul li.active{background: #fff;}
    .menu ul li a{line-height:32px; padding:0 4px;}
</style>

<div class="menu">
    <ul class="fix">
        <li>
            <div><a href="/api/admin/add">添加管理员</a></div>
        </li>
        <li>
            <div><a href="/api/admin/bookCheck">书籍审核</a></div>
        </li>
        <li>
            <div><a href="/api/admin/bookCommunity/merge">书籍社区管理</a></div>
        </li>
        <li>
            <div><a href="/api/admin/feedback/query">用户反馈</a></div>
        </li>
    </ul>
</div>
<div class="clear">

</div>
<%--<div>--%>
    <%--<table>--%>
        <%--<tr>--%>
            <%--<td><a href="/api/admin/add">添加管理员</a></td>--%>
            <%--<td><a href="/api/admin/bookCheck">书籍录入审核</a></td>--%>
            <%--<td><a href="/api/admin/bookCommunity/merge">书籍社区管理</a></td>--%>
        <%--</tr>--%>
    <%--</table>--%>
<%--</div>--%>
