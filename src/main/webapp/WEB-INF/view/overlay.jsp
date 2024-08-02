<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html>

    <head>
        <meta charset="UTF-8">
        <link rel="icon" href="/static/img/favicon.png" />
        <link rel="apple-touch-icon" href="/static/img/favicon.png" />
        <title>도전! 니야 골든벨</title>
        <link href="/static/css/overlay.css" rel="stylesheet" />
    </head>

    <body>
        <div id="participantBox">
            <div class="box scroll" id="participantTable">
                <label></label>
            </div>
            <div class="list-block">
                <img id="imgLogo" src="/static/img/logo.png">
                <div class="box" id="participantList">
                    <div style="font-size:1.8em;font-weight:bold;padding:5px" id="listTitle">참여자</div>
                    <hr style="width: 100%;">
                    <div class="scroll" id="listElementBox"></div>
                </div>
            </div>

        </div>
        <div class="box" id="QnABox">
            <div id="difficulty">난</div>
            <div id="questionBox">
                <div class="innerBox scroll" id="question">현재 출제된 문제가 없습니다.</div>
                <div class="innerBox" style="visibility: hidden;margin-top:1%;" id="Qanswer">정답</div>
            </div>
        </div>
        <div id="resultImgBox" style="display: none;">
            <img src="/static/img/goldenbell.png" id="successGoldenBell" style="display: none;">
            <button id="openResult"></button>
        </div>
        <audio id="successSrc" src="/static/audio/success.mp3"></audio>
        <audio id="failSrc" src="/static/audio/fail.mp3"></audio>

        <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
        <script type="text/javascript"
            src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
        <script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
        <script src="/static/js/timer.js"></script>
        <script src="/static/js/autoScroll.js"></script>
        <script src="/static/js/howler.min.js"></script>
        <script type="text/javascript">
            let roomNum = '${quizRoom.roomNum}';
            const socket = new SockJS("/ws/init");
            let stompClient = Stomp.over(socket);
            stompClient.debug = null;
            let sound;
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
                    stompClient.subscribe("/quiz/consolationmatch");
                    stompClient.subscribe("/quiz/goldenbell");
                    stompClient.subscribe("/quiz/outPlayer");
                    stompClient.subscribe("/quiz/goldenBellResult");
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
                        } else if (value.includes("submittedAnswer")) {
                            updateParticipantAnswer(message);
                        } else if (value.includes("openAnswer")) {
                            openParticipantAnswer();
                        } else if (value.includes("openCorrect")) {
                            openQuestionAnswer(message);
                            updateList(message,false);
                        } else if (value.includes("consolationmatch")) {
                            consolationMatchList(message);
                        } else if (value.includes("goldenbell")) {
                            changeGoldenBellMode(message);
                        } else if (value.includes("outPlayer")) {
                            updateList(message,true);
                        } else if (value.includes("goldenBellResult")) {
                            showGoldenBellResult(message);
                        }
                    }
                }
                setScrollMap();
                $("#openResult").click(()=>{
                    const context = new AudioContext();
                    context.resume().then(()=>{
                        sound.play();
                    });
                    $("#openResult").css("display","none");
                    switch(goldenbellResult){
                        case "success":
                            $("#successGoldenBell").css("display","");
                            $("#Qanswer").css("visibility", "visible");
                            break;
                        case "fail":
                            let listId = "#ptListElement" + goldenBellParticipantId;
                            let imgX = "#imgX" + goldenBellParticipantId;
                            let dropout = "#dropout" + goldenBellParticipantId;
                            //console.log(listId+', '+imgX+', '+dropout);
                            $(listId).css("text-decoration", "line-through");
                            $(imgX).css("visibility", "visible");
                            $(dropout).css("visibility", "visible");
                            break;    
                    }
                });
            });

            function addParticipant(participant) {
                //console.log(typeof (participant));
                let participantJSON = JSON.parse(participant);
                let partId = participantJSON.partId;
                let nickname = participantJSON.nickname;

                appendElementTable(partId, nickname);
                appendElementList(partId, nickname);
            }

            function changeQuiz(quiz) {
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
                let question = quizJSON.question;
                let answer = quizJSON.answer;
                $("#difficulty").text(difficulty);
                $("#question").html(question);

                $(".imgO").css("visibility","hidden");
                $("#Qanswer").css("visibility", "hidden");
                $("#Qanswer").text("정답 : " + answer);
                closeParticipantAnswer();
            }

            function appendElementTable(partId, nickname) {
                let elementId = "ptTableElement" + partId;
                let answerId = "answer" + partId;
                let imgX = "imgX" + partId;
                let imgO = "imgO" + partId;
                let dropout = "dropout" + partId;
                $("#participantTable").append(
                    $('<div>').prop({
                        id: elementId,
                        className: 'box table-element',
                        innerHTML: '<div class="imgX" id=' + imgX + '></div>'
                            +'<div class="imgO" id=' + imgO + '></div>'
                            + '<div class="text-center" id="tableTitle">'
                            + partId + '. ' + nickname
                            + '</div><hr>' +
                            '<div id="answerBox">' +
                            '<div class="text-center answer" id=' + answerId + ' style="visibility:hidden">' + ""
                            + '</div>'
                            + '</div>'
                            + '<span class="dropout" id=' + dropout + '></span>'
                    })
                );
            }

            function appendElementList(partId, nickname) {
                let elementId = 'ptListElement' + partId;
                $("#listElementBox").append(
                    $('<div class="listElement">').prop({
                        id: elementId,
                        innerHTML: partId + '. ' + nickname
                    })
                );
            }

            function updateParticipantAnswer(answerjson) {
                // console.log(answerjson);
                let answerJSON = JSON.parse(answerjson)
                let partId = "#answer" + answerJSON.partId;
                let answer = answerJSON.answer;


                $(partId).text(answer);
            }

            function updateList(message, isOut) {
                let updateListJSON = JSON.parse(message);
                let list = updateListJSON.list;
                
                if(!isOut){
                    $('.imgO').css("visibility","visible");    
                }
                
                // console.log(list);
                for (var i = 0; i < list.length; i++) {
                	let listId = "#ptListElement" + list[i];
                    let imgX = "#imgX" + list[i];
                    let imgO = "#imgO" + list[i];
                    let dropout = "#dropout" + list[i]
                    //console.log(listId+', '+imgX+', '+dropout);
                    $(listId).css("text-decoration", "line-through");
                    $(imgX).css("visibility", "visible");
                    $(imgO).css("visibility", "hidden");
                    $(dropout).css("visibility", "visible");
                }
            }

            function openParticipantAnswer() {
                // console.log("openAnswer");
                $(".answer").css("visibility", "visible");

            }
            function closeParticipantAnswer() {
                $(".answer").css("visibility", "hidden");
            }

            function openQuestionAnswer(message) {
                $("#Qanswer").css("visibility", "visible");
            }

            function consolationMatchList(message) {
                let updateListJSON = JSON.parse(message);
                let list = updateListJSON.list;
                // console.log(list);
                for (var i = 0; i < list.length; i++) {
                    let listId = "#ptListElement" + list[i];
                    let imgX = "#imgX" + list[i]
                    let dropout = "#dropout" + list[i]
                    //console.log(listId+', '+imgX+', '+dropout);
                    $(listId).css("text-decoration", "none");
                    $(imgX).css("visibility", "hidden");
                    $(dropout).css("visibility", "hidden");
                }
            }
            
            let goldenBellParticipantId;

            function changeGoldenBellMode(message) {
                let lastParticipantJSON = JSON.parse(message);
                let lastParticipantId = lastParticipantJSON.id;
                goldenBellParticipantId = lastParticipantId;
                let elementId = "#ptTableElement" + lastParticipantId;
                $(".table-element").css("display", "none");
                $(elementId).css("display", "");
                $(elementId).css("width", "50%");
            }


            let goldenbellResult = "";
            function showGoldenBellResult(message) {
                let resultJSON = JSON.parse(message);
                goldenbellResult = resultJSON.msg;


                switch(goldenbellResult){
                    case "success":
                        sound = new Howl({
                            src:['/static/audio/success.mp3'],
                            volume:0.2,
                            onend:()=>{
                                // cosole.log('Finished!');
                            }
                        });
                        break;
                    case "fail":
                    sound = new Howl({
                            src:['/static/audio/fail.mp3'],
                            volume:0.2,
                            onend:()=>{
                                // cosole.log('Finished!');
                            }
                        });
                        break;
                } 

                $("#resultImgBox").css("display", "");
            }



            
               
            
        </script>
    </body>

    </html>