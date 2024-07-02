<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html>

    <head>
        <meta charset="UTF-8">
        <title>송출용 오버레이</title>
        <link href="/static/css/overlay.css" rel="stylesheet" />
    </head>

    <body>
        <div id="participantBox">
            <div class="box scroll" id="participantTable">
                <label></label>
            </div>
            <div class="box" id="participantList">
                <div style="font-size:large;font-weight:bold;padding:5px">참여자</div>
                <hr style="width: 100%;">
                <div class="scroll" id="listElementBox"></div>
            </div>
        </div>
        <div class="box" id="QnABox">
            <div id="questionBox">
                <div id="difficulty">난</div>
                <div class="innerBox" id="question">현재 출제된 문제가 없습니다.</div>
            </div>
            <div class="innerBox" style="visibility: hidden;" id="Qanswer">정답</div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
        <script type="text/javascript"
            src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
        <script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
        <script src="/static/js/timer.js"></script>
        <script type="text/javascript">
            let roomNum = '${quizRoom.roomNum}';
            const socket = new SockJS("/ws/init");
            let stompClient = Stomp.over(socket);            

            document.addEventListener("DOMContentLoaded", function () {
                socket.onopen = () => {
						let data = {
							"roomNum": roomNum,
							"partId": "-2",
							"type": "part",
							"msg": "overlay Participation"
						};
						stompClient.subscribe("/quiz/partParticipant");
                        stompClient.subscribe("/quiz/selectedQuiz");
                        stompClient.subscribe("/quiz/submittedAnswer");
                        stompClient.subscribe("/quiz/openAnswer");
                        stompClient.subscribe("/quiz/openCorrect");
						stompClient.send("/app/participation", {}, JSON.stringify(data));
					}

                    socket.onmessage = (event) => {
						let value = event.data;
						let startPoint = value.indexOf('{');

						if (startPoint != -1) {
							let message = value.substring(startPoint, value.length - 1);
							if (value.includes("partParticipant")) {
								addParticipant(message);
							}else if(value.includes("selectedQuiz")){
                                changeQuiz(message);
                            }else if(value.includes("submittedAnswer")){
                                updateParticipantAnswer(message);
                            }else if(value.includes("openAnswer")){
                                openParticipantAnswer();
                            }else if(value.includes("openCorrect")){
                                openQuestionAnswer();
                            }
						}
                    }
            });

            function addParticipant(participant) {
				//console.log(typeof (participant));
				let participantJSON = JSON.parse(participant);
				let partId = participantJSON.partId;
				let nickname = participantJSON.nickname;

                appendElementTable(partId, nickname);
                appendElementList(partId,nickname);
            }

            function changeQuiz(quiz){
                let quizJSON = JSON.parse(quiz)
				let difficulty = quizJSON.difficulty === "아이스" ? "하" : quizJSON.difficulty;
                let question = quizJSON.question;
                let answer = quizJSON.answer;
                $("#difficulty").text(difficulty);
                $("#question").text(question);

                $("#Qanswer").css("visibility","hidden")
                $("#Qanswer").text(answer);
                closeParticipantAnswer();
            }

            function appendElementTable(partId,nickname){
                let elementId = "ptTableElement"+partId;
                let answerId = "answer"+partId;
                $("#participantTable").append(
                    $('<div>').prop({
                        id:elementId,
                        className:'box table-element',
                        innerHTML:'<div class="text-center" id="tableTitle">'
                                   +partId+'. '+nickname
                                   +'</div><hr>'+
                                  '<div class="" id="answerBox">'+
                                      '<div class="text-center answer" id='+answerId+' style="visibility:hidden">'+"정답"
                                      +'</div>'
                                 +'</div>'
                    })
                );
            }

            function appendElementList(partId,nickname){
                let elementId = 'ptListElement'+partId;
                $("#listElementBox").append(
                    $('<div style="text-aligh:center;">').prop({
                        id:elementId,
                        innerHTML:partId+'. '+nickname
                    })
                );
            }

            function updateParticipantAnswer(answerjson){
                console.log(answerjson);
                let answerJSON = JSON.parse(answerjson)
                let partId = "#answer"+answerJSON.partId;
                let answer = answerJSON.answer;
                console.log(partId+"가 제출한 답은 "+answer);

                $(partId).text(answer);
            }

            function openParticipantAnswer(){
                // console.log("openAnswer");
                $(".answer").css("visibility","visible");

            }
            function closeParticipantAnswer(){
                $(".answer").css("visibility","hidden");
            }

            function openQuestionAnswer(){
                $("#Qanswer").css("visibility","visible");
            }

        </script>
    </body>

    </html>