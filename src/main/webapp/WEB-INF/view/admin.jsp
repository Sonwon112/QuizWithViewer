<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<!DOCTYPE html>
		<html>

		<head>
			<meta charset="UTF-8">
			<title>관리자 페이지</title>
			<link href="/static/css/admin.css" rel="stylesheet" />

		</head>

		<body>
			<h1>관리자 페이지</h1>
			<hr>
			<p>방 번호 : ${quizRoom.roomNum}</p>
			<p>비밀번호 : ${quizRoom.password}</p>
			<p>오버레이 url : 146.56.102.79:8080/overlay?room=${quizRoom.roomNum}</p>
			<div class="outline">
				<div class="box inner">
					<div class="subtitle">문제</div>
					<hr>
					<!--문제 모드 설정-->
					<div class="box quiz" v>
						<div>
							문제파일 : <input type="file" name="file" />
							<button id="uploadBtn">업로드</button>
							<div id="uploadResultBox"></div>
						</div>
						<br>
						<input type="radio" name="mode" value="DEFAULT" onclick="sendMode()" checked="checked" />일반
						<input type="radio" name="mode" value="ICEBREAKING" onclick="sendMode()" />아이스브레이킹
						<input type="radio" name="mode" value="GOLDEN_BELL" onclick="sendMode()" />골든벨
						<input type="radio" name="mode" value="CONSOLATION_MATCH" onclick="sendMode()" />패자부활전
					</div>
					<!--문제 출제/답안 공개/정답 공개-->
					<div class="box quiz">
						<div class="inline-block">
							<button class="left controlBtn" onclick="sendAction('selectQuiz')">문제 출제</button>
							<button class="left controlBtn" onclick="sendAction('openCorrect')">정답 공개</button>
							<button class="left controlBtn" onclick="sendAction('openAnswer')">답안 공개</button>
						</div>

						<div id="questionBox">${quizRoom.currQuiz.question}</div>
						<div id="answerBox">정답 : ${quizRoom.currQuiz.answer}</div>

					</div>
				</div>

				<!--참여자 목록-->

				<div class="box inner" id="participantList">
					<div class="subtitle">참여자 목록</div>
					<hr>
				</div>
			</div>






			<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
			<script type="text/javascript"
				src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
			<script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
			<script type="text/javascript">
				let participantList;
				let roomNum = '${quizRoom.roomNum}';
				const socket = new SockJS("/ws/init");
				let stompClient = Stomp.over(socket);
				socket.debug = null;
				document.addEventListener("DOMContentLoaded", function () {

					participantList = document.getElementById("participantList");

					socket.onopen = () => {
						let data = {
							"roomNum": roomNum,
							"partId": "-1",
							"type": "part",
							"msg": "admin Participation"
						};
						stompClient.subscribe("/quiz/partParticipant");
						stompClient.subscribe("/quiz/selectedQuiz");
						stompClient.send("/app/participation", {}, JSON.stringify(data));
					}

					socket.onmessage = (event) => {
						let value = event.data;
						let startPoint = value.indexOf('{');

						if (startPoint != -1) {
							let message = value.substring(startPoint, value.length - 1);
							if (value.includes("partParticipant")) {
								addParticipant(message);
							} else if (value.includes("selectedQuiz")) {
								changeQuiz(message);
							}
						}




					}

					socket.onerror = (event) => {

					}
					socket.onclose = () => {

					}

					/**const sendAnswer = ()=>{
						const answer = document.getElementById("answer").value;
						
						let data={
								"roomNum" : roomNum,
								"partId" : partId,
								"type" : "answer",
								"msg" : answer
						}
						stompClient.send("/app/submitAnswer",{},JSON.stringify(data));
					}**/

					$("#uploadBtn").on("click", function (e) {
						let formData = new FormData();
						let inputFile = $("input[name='file']");
						let files = inputFile[0].files;

						for (var i = 0; i < files.length; i++) {
							formData.append("uploadFile", files[i]);
						}
						formData.append("roomNum", roomNum)

						$.ajax({
							url: "/upload",
							processData: false,
							contentType: false,
							data: formData,
							type: 'POST',
							success: function (result) {
								console.log("Uploaded");
								$("#uploadResultBox").text("업로드 완료!");
							}
						});
					});
				});

				window.onbeforeunload = function () {
					// console.log("새로고침");/
					// let data = {
					// 	"roomNum": roomNum,
					// 	"partId": "-1",
					// 	"type": "lost",
					// 	"msg": "lost Participation"
					// };
					// stompClient.send("/app/lostConnection", {}, JSON.stringify(data))
					// stompClient.close();
				}

				function addParticipant(participant) {
					//console.log(typeof (participant));
					let participantJSON = JSON.parse(participant);
					let partId = participantJSON.partId;
					let nickname = participantJSON.nickname;

					let participantElement = document.createElement("div");
					participantElement.id = "participantListElement"
					let participantText = partId + "." + nickname + "<button id='btnOut' onclick='sendOut()'>-</button>"
					participantElement.innerHTML = participantText;
					participantList.appendChild(participantElement);
				}

				function changeQuiz(quiz) {
					let quizJSON = JSON.parse(quiz)
					let question = "난이도 : "+quizJSON.difficulty+"<br>"+quizJSON.question
					let answer = "정답 : "+quizJSON.answer;

					$("#questionBox").html(question);
					$("#answerBox").text(answer);
				}

				function sendOut() {

				}

				function sendMode() {
					let value = $("input:radio[name='mode']:checked")[0].value;
					let data = {
						"roomNum": roomNum,
						"partId": "-1",
						"type": "changeMode",
						"msg": value
					};
					stompClient.send("/app/changeMode", {}, JSON.stringify(data))
				}

				function sendAction(value) {
					let data = {
						"roomNum": roomNum,
						"partId": "-1",
						"type": value,
						"msg": value
					};
					stompClient.send("/app/" + value, {}, JSON.stringify(data));
				}
			</script>
		</body>

		</html>