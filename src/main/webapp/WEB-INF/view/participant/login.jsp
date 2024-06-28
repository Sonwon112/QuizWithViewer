<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>도전! 니야 골든벨</title>
</head>
<body>
<div>
	<h2>도전! 니야 골든벨</h2><!--추후 이미지로 변경 예정-->
	<form action="/partLogin" method="post">
		<label>
		방 번호 :
		<input type="text" name="roomNum" placeholder="방 번호를 입력해주세요"/>
		</label>
		<label>
		비밀번호 :
		<input type="password" name="password" placeholder="방 번호를 입력해주세요"/>
		</label>
		<label>
		닉네임 :
		<input type="text" name="nickname" placeholder="채팅창 닉네임과 맞춰주세요"/>
		</label>
		<input type="submit">
	</form>
</div>
</body>
</html>