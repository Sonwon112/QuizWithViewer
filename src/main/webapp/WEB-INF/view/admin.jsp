<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>관리자 페이지</title>
<link href="/static/css/admin.css" rel="stylesheet"/>

</head>
<body>
<h1>관리자 페이지</h1>
<hr>
<p>방 번호 : ${roomNum}</p>
<p>비밀번호 : ${password}</p>
<p>오버레이 url : 146.56.102.79:8080/overlay?room=${roomNum}</p>
<div>
<!--문제 모드 설정-->
<div>
</div>
<!--참여자 목록-->
<div id="participantList">

</div>
</div>
<!--문제 출제/답안 공개/정답 공개-->
<div>
</div>





<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
<script type="text/javascript">
	let participantList;
	const socket = new SockJS("/ws/init");
	let stompClient = Stomp.over(socket);
	
	document.addEventListener("DOMContentLoaded",function(){
			let roomNum = '${roomNum}';	
			participantList  = document.getElementById("participantList");
			
			socket.onopen = ()=>{				
				let data = {
						"roomNum": roomNum,
						"partId" : "-1",
						"type" : "part",
						"msg" : "admin Participation"
					};
				stompClient.subscribe("/quiz/partParticipant");
				stompClient.send("/app/participation",{},JSON.stringify(data));
			}
			
			socket.onmessage=(event)=>{
				let value = event.data
				let startPoint = value.indexOf('{');
				if(startPoint != -1){
					let message = value.substring(startPoint,value.length-1);
					addParticipant(message);
				}
			}
			
			socket.onerror=(event)=>{
				
			}
			socket.onclose=()=>{
				
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
	});
	
	window.onbeforeunload = function(){
		console.log("새로고침");
		let data = {
				"roomNum": roomNum,
				"partId" : "-1",
				"type" : "lost",
				"msg" : "lost Participation"
			};
		stompClient.send("/app/lostConnection",{},JSON.stringify(data))
		stompClient.close();
		history.back();
	}
	
	function addParticipant(participant){
		console.log(typeof(participant));
		let participantJSON = JSON.parse(participant);
		let partId = participantJSON.partId;
		let nickname = participantJSON.nickname;

		let participantElement = document.createElement("div");
		participantElement.id = "participantListElement"
		let participantText = partId+"."+nickname+"<button id='btnOut' onclick='sendOut()'>-</button>"
		participantElement.innerHTML = participantText;
		participantList.appendChild(participantElement);
	}
	
	function sendOut(){
		
	}
</script>
</body>
</html>