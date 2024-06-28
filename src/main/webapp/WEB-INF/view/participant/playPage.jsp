<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>도전! 니야 골든벨</title>
</head>
<body>
플레이 화면
<label>
	<p>${participant.partId}</p>
	<p>${participant.nickname}</p>
</label>
<div id="answerBox">
	
</div>
<div id="inputBox">
	<input id="answer" type="text" name="answer"/>
	<button id="sendAnswer" onclick="sendAnswer()">전송</button>
	
</div>

<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
<script type="text/javascript">
	let roomNum = "<c:out value='${roomNum}'/>";
	let partId = "<c:out value='${participant.partId}'/>";
	const socket = new SockJS("/ws/init");
	let stompClient = Stomp.over(socket);
	
	document.addEventListener("DOMContentLoaded",function(){
			socket.onopen = ()=>{
				stompClient.subscribe('/quiz/Queistion',function(question){
					// 문제 출제시 얻어오는 부분
				});
				stompClient.subscribe('/quiz/out/'+partId,function(leaveOut){
					// 탈락시 이벤트 처리
				});
				
				let data = {
						"roomNum": roomNum,
						"partId" : partId,
						"type" : "part",
						"msg" : "request Participation"
					};
				stompClient.send("/app/participation",{},JSON.stringify(data));
			}
			
			socket.onmessage=(event)=>{
				
			}
			
			socket.onerror=(event)=>{
				
			}
			socket.onclose=()=>{
				
			}
			
			const sendAnswer = ()=>{
				const answer = document.getElementById("answer").value;
				
				let data={
						"roomNum" : roomNum,
						"partId" : partId,
						"type" : "answer",
						"msg" : answer
				}
				stompClient.send("/app/submitAnswer",{},JSON.stringify(data));
			}
	});
	
	window.onbeforeunload = function(){
		console.log("새로고침");
		let data = {
				"roomNum": roomNum,
				"partId" : partId,
				"type" : "lost",
				"msg" : "lost Participation"
			};
		stompClient.send("/app/lostConnection",{},JSON.stringify(data))
		stompClient.close();
		history.back();
	}
</script>

</body>
</html>