<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<html>
<head>
    <meta charset="UTF-8">
</head>
<body>
<h2>Hello World!</h2>

springMVC 上传文件
<form name="form1" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="springmvc上传文件"/>
</form>


富文本 上传文件
<form name="form1" action="/manage/product/rich_text_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="富文本上传文件"/>
</form>

</body>
</html>
