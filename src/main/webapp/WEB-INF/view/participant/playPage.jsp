<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<!DOCTYPE html>
		<html>

		<head>
			<meta charset="UTF-8">
			<link rel="icon" href="/static/img/favicon.png"/>
       		<link rel="apple-touch-icon" href="/static/img/favicon.png"/>
			<title>도전! 니야 골든벨</title>
			<link href="/static/css/admin.css" rel="stylesheet" />
			<link href="/static/css/player.css" rel="stylesheet" />
		</head>

		<body>
			<div id="background">
				<div style="display:none;" id="imgO"></div>
				<div class="dropout" id="imgX"></div>
				<div id="info">${participant.partId}번 ${participant.nickname}</div>
				<div id="questionBox">
					<div id="question">문제</div>
				</div>
				<div id="inputBox" class="flex">
					<div id="difficulty">난</div>
					<input class="input-text" id="answer" type="text" name="answer" onkeyup="enterKey()"/>
					<button id="sendAnswerBtn" onclick="sendAnswer()"> 제출</button>
				</div>
				<div style="visibility: hidden" id="resultTxt">제출됨!</div>
				<div id="timer">--</div>
				<!--<div class="scroll" id="test">테스트</div>-->
			</div>
			<div class="dropout" id="dropout"></div>

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
				stompClient.debug = null;

				document.addEventListener("DOMContentLoaded", function () {
					socket.onopen = () => {
						stompClient.subscribe('/quiz/selectedQuiz');
						stompClient.subscribe('/quiz/changePartState/' + partId);
						stompClient.subscribe("/quiz/startTimer");
						stompClient.subscribe("/quiz/deleteRoom");
						stompClient.subscribe("/quiz/openCorrect");
						stompClient.subscribe("/quiz/out/" + partId);

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
						// console.log(event);
						if (startPoint != -1) {
							let message = value.substring(startPoint, value.length - 1);
							if (value.includes("selectedQuiz")) {
								changeQuiz(message);
							} else if (value.includes("changePartState")) {
								changePartState(message);
							} else if (value.includes("startTimer")) {
								startTimer(message);
							} else if (value.includes("deleteRoom")) {
								out();
							} else if (value.includes("out")) {
								out();
							} else if (value.includes("openCorrect")){
								showCorrect(message);
							}
						}
					}

					socket.onerror = (event) => {

					}
					socket.onclose = () => {

					}

					// setScrollMap();
				});

				function enterKey(){
					if(window.event.keyCode == 13){
						sendAnswer();
					}
				}

				function sendAnswer() {
					// console.log("call sendAnswer")/
					if (currState == "start") {
						let answer = $("#answer").val();
						answer = answer.trim();
						// console.log("send "+answer);

						let data = {
							"roomNum": roomNum,
							"partId": partId,
							"type": "answer",
							"msg": answer
						}
						stompClient.send("/app/submitAnswer", {}, JSON.stringify(data));
						$("#resultTxt").css("visibility", "visible");
						$("#resultTxt").css("display", "");
						$("#resultTxt").fadeOut(5000);
					}

				}

				

				function changeQuiz(quiz) {
					currState = "start";
					let quizJSON = JSON.parse(quiz)
					let difficulty = quizJSON.difficulty === "아이스" ? "빙" : quizJSON.difficulty;
					switch (difficulty) {
						case "상":
							difficulty = "上";
							break;
						case "중":
							difficulty = "中";
							break;
						case "하":
							difficulty = "下";
							break;

					}
					let question = quizJSON.num + ". " + quizJSON.question

					let second = difficulty === "上" ? 15 : 10;
					$("#answer").val('');
					if (quizJSON.question != "더 이상 문제가 존재하지 않습니다") {
						$("#difficulty").text(difficulty);
						$("#question").html(question);
						setTime(second);
					}
				}



				function changePartState(state) {

					let stateJSON = JSON.parse(state);
					let isPart = stateJSON.state;

					if (isPart === 'true') {
						$(".dropout").css("visibility", "hidden");
					} else {
						$(".dropout").css("visibility", "visible");
					}
				}

				function out() {
					history.back();
				}

				function showCorrect(message){
					let dropoutJSON =JSON.parse(message);
					let dropoutList = dropoutJSON.list;
					// console.log(typeof dropoutList[0]);
					// console.log(typeof partId);
					// console.log(dropoutList.indexOf(Number(partId)))

					if(!(dropoutList.includes(Number(partId)))){
						$("#imgO").css("display","");
						$("#imgO").fadeOut(2000);	
					}
				}

			</script>

		</body>

		</html>