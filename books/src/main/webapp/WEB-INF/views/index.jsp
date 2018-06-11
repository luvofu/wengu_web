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

        /*background: #45A6AD;*/
    }

    .wenya {
        width: 960px;
        /*margin-top: 100px;*/
    }

    .wenya h3 {
        font-size: 30px;
        font-weight: 400;
    }

    .wenya h6 {
        font-size: 18px;
        font-weight: 300;
    }

    .html-bottom{
        margin-top: 40px;
    }

    .center-vertical {
        position:relative;
        top:50%;
        transform:translateY(-50%);
    }
</style>
<body>
<%--<div class="main">--%>
<div class="">
    <h3 style="padding-left: 20px">文芽</h3>
    <hr>
</div>
<div class="container-fluid wenya">
    <div class="row">

        <div class="col-md-12">
            <div class="col-md-6">
                <div class="col-md-4">
                    <img class="col-md-12" src="/images/index/logo.png" alt="文芽"/>
                </div>
                <div class="col-md-8">
                    <h3>拥有自己的书房</h3>
                    <h6>让阅读更高效</h6>
                </div>
                <div class="clearfix"></div>
                <div style="margin-top: 30px">
                    <div class="col-md-6">
                        <p><a href="https://itunes.apple.com/cn/app/id1170983858" class="">
                            <img class="col-md-12" src="/images/index/iOS_down.png" alt="App Store下载" title="App Store下载">
                        </a></p>
                        <div class="clearfix"></div>
                        <p style="margin-top: 20px">
                            <a href="/down/apk/wenya.apk">
                                <img class="col-md-12" src="/images/index/Android_down.png" alt="Android 下载" title="Android 下载">
                            </a>
                        </p>
                    </div>
                    <div class="col-md-3 text-center" style="border: 1px solid #e6e6e6; padding: 5px">
                        <img class="col-md-12" src="/img/qr/qrcode.png" alt="文芽"/>
                        <span  class="col-md-12">扫码下载</span >
                    </div>
                </div>
            </div>
            <div class="col-md-6 ">
                <div class="text-center">
                    <img src="/images/index/wenya8.png" class="col-md-12">
                </div>
            </div>
        </div>
        <div class="col-md-12">
            <div class="col-md-6">
                <img src="/images/index/manage.png" class="col-xs-12">
            </div>
            <div class="col-md-6 text-left">
                <h3>图书管理</h3>
                <p></p>
                <h6>快速录入图书，制作书单，记录笔记，标记阅读进度，
                    让阅读更高效</h6>
            </div>
            <div class="col-md-12" style="margin-top: 50px;margin-bottom: 50px ;">
                <hr>
            </div>
        </div>
        <div class="col-md-12">

            <div class="col-md-6 text-left">
                <h3>图书共享</h3>
                <h6>与附近书友相互借阅、交换图书，
                    让阅读更实惠</h6>
            </div>
            <div class="col-md-6">
                <img src="/images/index/share.jpg" class="col-xs-12">
            </div>
            <div class="col-md-12" style="margin-top: 50px;margin-bottom: 50px ;">
                <hr>
            </div>
        </div>
        <div class="col-md-12 text-center">
            <a href="https://itunes.apple.com/cn/app/id1170983858" class="col-md-3 col-md-offset-2">
                <img class="col-md-10" style="margin-top: 25px" src="/images/index/iOS_down.png" alt="App Store下载" title="App Store下载">
            </a>

                <a href="/down/apk/wenya.apk" class="col-md-3">
                    <img class="col-md-10" style="margin-top: 25px" src="/images/index/Android_down.png" alt="Android 下载" title="Android 下载">
                </a>

           <p class="col-md-3"><img style="    width: 80px;" src="/img/qr/qrcode.png" alt="文芽"/></p>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12 html-bottom">
            <p class="text-center"><a href="http://www.miibeian.gov.cn/" target="_blank"><label class=" ">${msg}</label></a>
            </p>
        </div>
    </div>
</div>
<%--</div>--%>
</body>
</html>
