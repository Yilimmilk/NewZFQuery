<%--
  Created by IntelliJ IDEA.
  User: yili
  Date: 2021/1/10
  Time: 7:54 下午
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>这是一个API</title>
</head>
<style>
    .card {
        box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2);
        transition: 0.3s;
        width: 25%;
        border-radius: 5px;
    }

    .card:hover {
        box-shadow: 0 8px 16px 0 rgba(0,0,0,0.2);
    }

    img {
        border-radius: 5px 5px 0 0;
    }

    .container {
        padding: 2px 16px;
    }
</style>
<body>
<div>
    <div class="card">
        <img src="https://cdn.treeoncloud.com/source/biaoqingbao/%E6%86%A8%E6%86%A8%E9%9C%87%E6%83%8A%E8%A1%A8%E6%83%85%E5%8C%85.jpg" alt="Avatar" style="width:100%">
        <div class="container">
            <p>嘛，既然你看到这个页面了,</p>
            <p>说明你应该对我的App抓了包或者反编译了吧～</p>
            <p>或者，爬虫爬到了这个页面？</p>
            <p>既然来都来了，就去看看孩子的主页或者博客叭～</p>
            <p>那咱们下次有缘再见咯～<br/></p>

            <p>俺的主页-><a href="https://mapotofu.cn" target="_blank">麻婆豆腐～</a></p>
            <p>俺的博客--><a href="https://blog.treeoncloud.com" target="_blank">云树森林</a></p>
            <h3><b>By Yili</b></h3>
        </div>
    </div>
</div>
</body>
</html>
