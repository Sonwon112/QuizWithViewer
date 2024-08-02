<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<!DOCTYPE html>
		<html>

		<head>
			<meta charset="UTF-8">
			<link rel="icon" href="/static/img/favicon.png"/>
        	<link rel="apple-touch-icon" href="/static/img/favicon.png"/>
			<title>관리자 페이지</title>
			<link href="/static/css/admin.css" rel="stylesheet" />

		</head>

		<body>
			<h1>관리자 페이지</h1>
			<button onclick="deleteRoom()">나가기</button>
			<hr>
			<p>방 번호 : ${quizRoom.roomNum}</p>
			<p>비밀번호 : ${quizRoom.password}</p>
			<p>오버레이 url : 146.56.102.79:8080/overlay?room=${quizRoom.roomNum}</p>
			<p>참여 페이지 url : https://bit.ly/3zCLngK</p>
			<div class="outline">
				<div class="box inner">
					<div class="subtitle">문제</div>
					<hr>
					<!--문제 모드 설정-->
					<div class="box quiz">
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
						<br>
						난이도:
						<input type="radio" name="targetDifficulty" value="하" onclick="sendDifficulty()" checked="checked"/> 하
						<input type="radio" name="targetDifficulty" value="중" onclick="sendDifficulty()"/> 중
						<input type="radio" name="targetDifficulty" value="상" onclick="sendDifficulty()"/> 상
					</div>
					<!--문제 출제/답안 공개/정답 공개-->
					<div class="box quiz">
						<div class="inline-block">
							<button class="left controlBtn" onclick="sendAction('selectQuiz')">문제 출제</button>
							<button class="left controlBtn" onclick="sendAction('startTimer')">타이머 시작</button>
							<button class="left controlBtn" onclick="sendAction('openAnswer')">답안 공개</button>
							<button class="left controlBtn" onclick="sendAction('openCorrect')">정답 공개</button>
							
							<span class="left" id="timer"></span>
						</div>

						<div id="questionBox">${quizRoom.currQuiz.question}</div>
						<div id="answerBox">정답 : ${quizRoom.currQuiz.answer}</div>

					</div>
				</div>

				<!--참여자 목록-->

				<div class="box inner" id="participantList">
					<div class="subtitle" id="listTitle">참여자 목록(00/00)</div>
					<div class="inline-block">
						<div class="left">
							<input type="checkbox" id="toggle" hidden onclick="changeParticipantState()">
							<label for="toggle" class="toggleSwitch">
								<span class="toggleButton"></span>
							</label>
						</div>
						<div class="left" id="participantState">비활성화</div>
					</div>
					<hr style="width: 100%;">
					<div class="scroll" id="listElementBox"></div>
				</div>
			</div>






			<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
			<script type="text/javascript"
				src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
			<script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
			<script src="/static/js/timer.js"></script>
			<script src="/static/js/counting.js"></script>
			<script type="text/javascript">
				let participantList;
				let roomNum = '${quizRoom.roomNum}';
				const socket = new SockJS("/ws/init");
				let stompClient = Stomp.over(socket);
				stompClient.debug = null;

				
				document.addEventListener("DOMContentLoaded", function () {

					participantList = document.getElementById("listElementBox");

					socket.onopen = () => {
						let data = {
							"roomNum": roomNum,
							"partId": "-1",
							"type": "part",
							"msg": "admin Participation"
						};
						stompClient.subscribe("/quiz/partParticipant");
						stompClient.subscribe("/quiz/selectedQuiz");
						stompClient.subscribe("/quiz/startTimer");
						stompClient.subscribe("/quiz/openCorrect");
						stompClient.subscribe("/quiz/consolationmatch");
						stompClient.subscribe("/quiz/cantgoldenbell");
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
							} else if (value.includes("startTimer")){
								startTimer(message);
							} else if(value.includes("openCorrect")){
								updateList(message);
							} else if(value.includes("consolationmatch")){
								consolationMatchList(message);
							} else if(value.includes("cantgoldenbell")){
								cantGoldenBell(message);
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


				function addParticipant(participant) {
					//console.log(typeof (participant));
					let participantJSON = JSON.parse(participant);
					let partId = participantJSON.partId;
					let nickname = participantJSON.nickname;

					let participantElement = document.createElement("div");
					participantElement.id = "participantListElement"+partId;
					let participantText = partId + "." + nickname + "<button id='btnOut"+partId+"' onclick='sendOut("+partId+")'>-</button>"
					participantElement.innerHTML = participantText;
					participantList.appendChild(participantElement);
					addCount();
				}

				function changeQuiz(quiz) {
					let quizJSON = JSON.parse(quiz)
					let difficulty = quizJSON.difficulty === "아이스" ? "빙" : quizJSON.difficulty;

					let answer = "정답 : " + quizJSON.answer;
					let question = "난이도 : " + difficulty + "<br>" + quizJSON.num + ". " + quizJSON.question

					
					if (quizJSON.question != "더 이상 문제가 존재하지 않습니다") {
						
					}

					$("#questionBox").html(question);
					$("#answerBox").text(answer);
				}

				function changeParticipantState() {
					let isChecked = $("#toggle").is(":checked");
					switch (isChecked) {
						case true:
							$("#participantState").text("활성화 중...")
							break;
						case false:
							$("#participantState").text("비활성화")
							break;
					}

					let data = {
						"roomNum": roomNum,
						"partId": "-1",
						"type": "changeState",
						"msg": isChecked
					};
					stompClient.send("/app/changeParticipantState", {}, JSON.stringify(data));
				}

				function sendOut(partId) {
					let data = {
						"roomNum": roomNum,
						"partId": "-1",
						"type": "out",
						"msg": partId
					};
					stompClient.send("/app/out", {}, JSON.stringify(data));

					let id = "#participantListElement"+partId;
					$(id).remove();

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

				function sendDifficulty() {
					let value = $("input:radio[name='targetDifficulty']:checked")[0].value;
					let data = {
						"roomNum": roomNum,
						"partId": "-1",
						"type": "changeDifficulty",
						"msg": value
					};
					stompClient.send("/app/changeDifficulty", {}, JSON.stringify(data))
				}

				function sendAction(value) {
					if (currSec <= 0) {
						let data = {
							"roomNum": roomNum,
							"partId": "-1",
							"type": value,
							"msg": value
						};
						stompClient.send("/app/" + value, {}, JSON.stringify(data));
					}
				}

				function updateList(message){
					let updateListJSON = JSON.parse(message);
					let list = updateListJSON.list;
					// console.log(list);
					for(var i = 0; i < list.length;i++){
						let id = "#participantListElement"+list[i];
						$(id).css("text-decoration","line-through");
					}
					subtractCount(list.length);
				}

				function consolationMatchList(message){
					let updateListJSON = JSON.parse(message);
					let list = updateListJSON.list;
					console.log(list);
					for(var i = 0; i < list.length;i++){
						let listId = "#participantListElement"+list[i];
               	    	$(listId).css("text-decoration","none");
					}
					instractCount(list.length);
				}

				function cantGoldenBell(){
					$("#uploadResultBox").text("골든벨 모드를 할수 없습니다! 일반모드로 바꿔주세요")
				}

				function deleteRoom(){
					let data = {
							"roomNum": roomNum,
							"partId": "-1",
							"type": "delete",
							"msg": "delete"
						};
					stompClient.send("/app/deleteRoom", {}, JSON.stringify(data))
					sessionStorage.clear();
					history.back();
				}
			</script>
		</body>

		</html>