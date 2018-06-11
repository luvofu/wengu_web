<%--
  Created by IntelliJ IDEA.
  User: wzhonggo@gmail.com
  Date: 8/1/2016
  Time: 2:52 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>文芽</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script type="text/javascript" src="/js/jquery-3.1.1.min.js"></script>
    <script type="text/javascript" src="/js/bootstrap/js/bootstrap.min.js"></script>
    <link rel="stylesheet" type="text/css" href="/js/bootstrap/css/bootstrap-theme.min.css">
    <link rel="stylesheet" type="text/css" href="/js/bootstrap/css/bootstrap.min.css">

</head>
<script type="text/javascript">
</script>
<style>

    html, body {
        font-family: "Microsoft YaHe", STXihei;
        margin: 0 auto;
        /*width: 800px;*/
        /*background: #45A6AD;*/

        /*height: 600px;*/
    }

    /*.code {*/
        /*width: 150px;*/
        /*height: 150px;*/
    /*}*/

    /*.book {*/
        /*width: 100%;*/
        /*!*width: 200px;*!*/
        /*!*height: 300px;*!*/
    /*}*/

    /*.book-info{*/
        /*line-height: 180%;*/
    /*}*/

    /*.content-text {*/
        /*color: #FFFFFF;*/
    /*}*/

    /*.app-info{*/
        /*margin-top: 20px;*/
        /*margin-bottom: 20px;*/
        /*background-color: #e3e3e3;*/
        /*text-align: center;*/
        /*width: 150px;*/
    /*}*/

    /*@media (min-width: 768px) {*/
        /*.content{*/
            /*margin-top:50px;*/
        /*}*/
        /*.html-bottom{*/
            /*margin-top: 50px;*/
        /*}*/
    /*}*/


    /*@media (min-width: 992px) {*/
        /*.content{*/
            /*margin-top:100px;*/
        /*}*/
        /*.html-bottom{*/
            /*margin-top: 100px;*/
        /*}*/
    /*}*/

    /*.title {*/
        /*font-size: 22px;*/
        /*font-weight: 400;*/
    /*}*/

    /*.detail{*/
        /*font-size: 15px;*/
        /*font-weight: 300;*/
    /*}*/

    .wenya h3{
        font-size: 22px;
        font-weight: 400;
    }

    .wenya h6{
        font-size: 15px;
        font-weight: 300;
    }
</style>
<body>
<%--<div class="main">--%>
<div class="container-fluid wenya">
    <div class="row">
        <div class="col-xs-12">
            <div class="" style="margin-top: 50px">
                <div class="text-center">
                    <p class="col-xs-12"><img class="col-xs-4 col-xs-offset-4" src="/images/index/logo.png" alt="文芽"/></p>
                    <div>
                       <h3>拥有自己的书房</h3>
                        <h6>让阅读更高效</h6>
                    </div>

                </div>
            </div>
            <div style="margin-top: 30px;margin-bottom: 30px">
                <a href="https://itunes.apple.com/cn/app/id1170983858" class="">
                    <img class="col-xs-5 col-xs-offset-1" src="/images/index/iOS_down.png" alt="App Store下载" title="App Store下载">
                </a>
                <a href="/down/apk/wenya.apk" >
                    <img class="col-xs-5" src="/images/index/Android_down.png" alt="Android 下载" title="Android 下载">
                </a>

            </div>
            <div class="text-center">
                <img src="/images/index/wenya8.png" class="col-xs-10 col-xs-offset-1">
            </div>
            <div class="text-center col-xs-12" style="margin-top: 50px">
                <img src="/images/index/manage.png" class="col-xs-10 col-xs-offset-1">
            </div>
            <div class="text-center col-xs-12">
                <p></p>
               <h3>图书管理</h3>
               <h6>快速录入图书，制作书单，记录笔记，标记阅读进度，
                让阅读更高效</h6>
                <div style="margin-top: 50px;margin-bottom: 50px ;">
                    <hr>
                </div>
            </div>

            <div class="text-center ">
                <p><img src="/images/index/share.jpg" class="col-xs-10 col-xs-offset-1"></p>
                <div class="col-xs-12">
                    <p></p>
                    <h3>图书共享</h3>
                    <h6>与附近书友相互借阅、交换图书，
                        让阅读更实惠</h6>
                </div>

            </div>
            <div style="margin-top: 30px;margin-bottom: 30px" class="col-xs-12">
                <a href="https://itunes.apple.com/cn/app/id1170983858" class="">
                    <img class="col-xs-5 col-xs-offset-1" src="/images/index/iOS_down.png" alt="App Store下载" title="App Store下载">
                </a>
                <a href="/down/apk/wenya.apk" >
                    <img class="col-xs-5" src="/images/index/Android_down.png" alt="Android 下载" title="Android 下载">
                </a>

            </div>

        </div>
    </div>


    <div class="row">
        <div class="col-xs-12"  style="margin-top: 50px">
            <p class="text-center"><a href="http://www.miibeian.gov.cn/" target="_blank"><label class=" ">${msg}</label></a>
            </p>
        </div>
    </div>
</div>
<%--</div>--%>
</body>
</html>
