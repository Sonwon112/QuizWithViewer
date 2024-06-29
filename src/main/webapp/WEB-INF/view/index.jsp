<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>냐페이지</title>
</head>
<body>
<h1>냐페이지</h1>
<hr>
<div>
	<h3>도전 골든벨</h3>
	<div>
	<b>방 생성하기</b><br>
	<form action="/createRoom" method="post">
		<label>
		비밀번호 :
		<input type="text" name="password"/>
		<input type="submit" value="생성하기"/>
		</label>
	</form>
	</div>
</div>
</body>
</html>