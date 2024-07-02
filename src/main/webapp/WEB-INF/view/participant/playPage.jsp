<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<!DOCTYPE html>
		<html>

		<head>
			<meta charset="UTF-8">
			<title>도전! 니야 골든벨</title>
			<link href="/static/css/admin.css" rel="stylesheet" />
			<link href="/static/css/player.css" rel="stylesheet" />
		</head>

		<body>
			<div id="background">
				<div id="info">${participant.partId}번 ${participant.nickname}</div>
			<div id="questionBox">
				<div id="difficulty">난이도</div>
				<div id="question">문제</div>
				<br>
			</div>
			<div id="inputBox">
				<input id="answer" type="text" name="answer" />
				<button id="sendAnswerBtn" onclick="sendAnswer()"> ↲</button>

			</div>
			<div style="visibility: hidden" id="resultTxt">제출됨!</div>
			<div id="timer">--</div>

			<!--<div class="scroll" id="test">테스트</div>-->

			</div>
			
			<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
			<script type="text/javascript"
				src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
			<script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
			<script src="/static/js/timer.js"></script>
			<script src="/static/js/autoScroll.js"></script>
			<script type="text/javascript">
				let roomNum = "<c:out value='${roomNum}'/>";
				let partId = "<c:out value='${participant.partId}'/>";
				const socket = new SockJS("/ws/init");
				let stompClient = Stomp.over(socket);
				

				document.addEventListener("DOMContentLoaded", function () {
					socket.onopen = () => {
						stompClient.subscribe('/quiz/selectedQuiz');
						stompClient.subscribe('/quiz/changePartState/' + partId);

						let data = {
							"roomNum": roomNum,
							"partId": partId,
							"type": "part",
							"msg": "request Participation"
						};
						stompClient.send("/app/participation", {}, JSON.stringify(data));
					}

					socket.onmessage = (event) => {
						let value = event.data;
						let startPoint = value.indexOf('{');

						if (startPoint != -1) {
							let message = value.substring(startPoint, value.length - 1);
							if (value.includes("selectedQuiz")) {
								changeQuiz(message);
							} else if (value.includes("changePartState")) {

							}
						}
					}

					socket.onerror = (event) => {

					}
					socket.onclose = () => {

					}
					
					// setScrollMap();
				});

				function sendAnswer() {
						// console.log("call sendAnswer")/
						if (currSec>0) {
							let answer = $("#answer").val();
							// console.log("send "+answer);

							let data = {
								"roomNum": roomNum,
								"partId": partId,
								"type": "answer",
								"msg": answer
							}
							stompClient.send("/app/submitAnswer", {}, JSON.stringify(data));
							$("#resultTxt").css("visibility","visible");
							$("#resultTxt").fadeOut('slow');
						}

					}

					function changeQuiz(quiz) {
						let quizJSON = JSON.parse(quiz)
						let difficulty = quizJSON.difficulty === "아이스" ? "하" : quizJSON.difficulty;
						let question = quizJSON.num + ". " + quizJSON.question

						let second = difficulty === "상" ? 15 : 10;

						if (quizJSON.question != "더 이상 문제가 존재하지 않습니다") {
							setTime(second);
							start();
							$("#difficulty").text(difficulty);
							$("#question").text(question);
						}
					}



					function changePartState(state) {

					}

			</script>

		</body>

		</html>