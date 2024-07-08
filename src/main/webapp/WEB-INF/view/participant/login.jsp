<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<!DOCTYPE html>
	<html>

	<head>
		<meta charset="UTF-8">
		<link rel="icon" href="/static/img/favicon.png" />
		<link rel="apple-touch-icon" href="/static/img/favicon.png" />
		<title>도전! 니야 골든벨</title>
		<link href="/static/css/player.css" rel="stylesheet" />
	</head>

	<body>
		<div>
			<div id="background">
				<img id="logo" src="/static/img/logo.png">
				<form id="inputBox" action="/partLogin" method="post">
					<label>
						방 번호 :
						<input class="input-text" type="text" name="roomNum" placeholder="방 번호를 입력해주세요" />
					</label>
					<br>
					<label>
						비밀번호 :
						<input class="input-text" type="password" name="password" placeholder="비밀번호를 입력해주세요" />
					</label>
					<br>
					<label>
						닉네임 :
						<input class="input-text" type="text" name="nickname" placeholder="채팅창 닉네임과 맞춰주세요" />
					</label>
					<br>
					<input id="submitBtn" type="submit">
				</form>
			</div>

		</div>
	</body>

	</html>