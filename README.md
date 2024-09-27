# WebSocket통신을 활용한 퀴즈 페이지

### 개요

- 명칭 : 도전 퀴즈쇼!
- 제작 인원 : 6인 ( 역할 - 백엔드, 프론트엔드 )
- 제작 기간 : 2024.07.01~2024.07.23
- 제작 동기 :
    
    퀴즈쇼 컨텐츠에서 사용하기 위한 프로그램이 필요했었습니다, VRChat이라는 게임에서 월드를 만들어서 진행하기엔 참여율이 우려되었고, 채팅으로 참여하게 하면 정답이 공개가 되어서 도전 골든벨 같은 컨텐츠로 진행은 어려웠습니다. 그래서 별게의 웹페이지를 제작하여서 시청자가 페이지에서 정답을 입력하고, 관리자의 진행에 따라 송출화면에서 송출이 되게끔하는 프로그램이 필요해서 제작하게 되었습니다.
    

### 사용도구 및 기술

- Spring
- WebSocket
- JSP

### 클래스 설계 및 다이어그램

### 제작 과정 및 핵심 코드

 회의를 통해 관리자 페이지의 필요기능과 송출페이지 표시방법, 시청자의 참여페이지에서 필요한 기능을 정리하였습니다.

- 관리자 페이지 기능 :
    - 방 생성
    - 방 참여 여부 설정
    - 문제 출제 모드 선택 ( 아이스브레이킹, 일반, 패자부활전, 골든벨)
    - 문제를 로컬의 txt파일을 업로드하여 생성
    - 문제 출제, 타이머 재생
    - 송출 페이지에서 사용자들이 입력한 답안 공개
    - 정답 공개시 송출페이지 정답/오답자 공개 및 참여자화면에서 정답/오답 표시
    - 참여자 관리
- 참여자 페이지 기능 :
    - 방 id, 패스워드 입력을 통한 방 입장
    - 문제 출제시 난이도 및 문제 표시
    - 입장 후 문제 출제 후부터 타이머가 끝날때까지 문제 출제하기
    - 출제시 제출 됨 표시, 정답 공개시 정답,오답 표시
- 송출 페이지 기능 :
    - 참여자 참여시 참여자 테이블 및 리스트에 추가
    - 문제 출제 시 문제 난이도 문제 표시
    - 답안 공개 시 참여자의 답안 공개
    - 정답 공개 시 참여자의 답안 정답 여부 표시

 기능 구현 이전 컨트롤러, 서비스, 리포지토리, 컨테이너, enum 그리고 모델로 패키지를 구분하여 클래스를 생성하였고, 최대한 SOLID  규칙에 맞춰 코드를 작성하였습니다.

- 관리자 페이지

 관리자 페이지는 관리자가 사용할 패스워드를 입력하고 생성하게 되면 해당 방의 고유 id를 부여하게 되고 이 id를 통해 관리자 페이지, 송출페이지, 사용자 페이지가 연결되게 작성하였습니다.

 관리자는 생성된 송출 페이지 url을 통해 송출페이지를 시청자들에게 보여주고, 참여자들에겐 생성된 방의 id와 패스워드를 공개하게 됩니다. 관리자 참여를 허가하게 되면 참여자들은 참여자 페이지를 통해 참여가 가능하게됩니다.

```java
// 방 생성 코드
@PostMapping("/createRoom")
	public String createRoom(Model model, @RequestParam("password") String password, HttpSession session) {
//		System.out.println(session.getAttribute("room"));
		if(session.getAttribute("room")!=null) {
			try {
				QuizRoom savedQR = (QuizRoom)session.getAttribute("room");
				QuizRoom qr = qrService.findQuizRoomByRoomNum(savedQR.getRoomNum());
				System.out.println(qr);
				if(qr == null) {throw new Exception("해당 방이 존재하지않습니다");}
				model.addAttribute("quizRoom",session.getAttribute("room"));
				return "admin";
			}catch (Exception e) {
				// TODO: handle exception
				session.removeAttribute("room");
			}
			
		}
		
		if(password == null || password.isEmpty()) {
			model.addAttribute("msg", "비밀번호를 입력해주세요");
			model.addAttribute("url", "/");
			return "alert";
		}
		
		QuizRoom quizRoom = qrService.createQuizRoom(password);
		
		model.addAttribute("quizRoom", quizRoom);
		session.setAttribute("room", quizRoom);
		
		return "admin";
	}
```

 관리자는 게임을 시작하기 전 참여를 막고, 사전에 준비된 문제 txt 파일을 업로드하여 서버에 문제와 답, 난이도를 등록하게 됩니다 ( 예 : 문제;정답;난이도 )

```java
//JavaScript
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

// 문제 업로드 및 등록 코드
@PostMapping("/upload")
	@ResponseBody
	public String uploadQuiz(MultipartFile[] uploadFile,String roomNum) {
		log.info("upload Quiz file");
		MultipartFile file = uploadFile[0];
		log.info("Upload FileName : " + file.getOriginalFilename());	
		try {
			qService.getQuizToFile(roomNum,file.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "complete upload";
	}
```

 문제출제 버튼을 통해 문제를 출제, 타이머 재생을 통해 타이머를 동작, 답안 공개를 통해 사용자의 답안 공개, 정답 공개를 통해 정답 유무를 판정 하게 되고, 다양한 문제모드 및 난이도를 선택함으로 퀴즈대회를 재미를 올릴 수 있게 하였습니다.

```java
// StompClient 문제 출제, 타이머 동작, 모드 변경
// 난이도 변경
@MessageMapping("/changeDifficulty")
	public void ChangeDifficulty(WebSocketDTO dto) {
		log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", changeDifficulty : "+dto.getMsg());
		QuizRoom qr = qrService.findQuizRoomByRoomNum(dto.getRoomNum());
		if(qr.getCurrMode() == QuizMode.DEFAULT) {
			qrService.changeTargetDifficulty(dto.getRoomNum(), dto.getMsg());
		}
	}
	
	// 문제 모드 변경
@MessageMapping("/changeMode")
public void ChangeMode(WebSocketDTO dto) {
	log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", changeMode : "+dto.getMsg());
	QuizMode currMode = null;
	String targetDifficulty = "";
	
	switch (dto.getMsg()) {
	case "DEFAULT":
		currMode = QuizMode.DEFAULT;
		targetDifficulty="하";
		break;
	case "ICEBREAKING":
		currMode = QuizMode.ICEBREAKING;
		targetDifficulty="아이스";
		break;
	case "GOLDEN_BELL":
		currMode = QuizMode.GOLDEN_BELL;
		int goldenBellParticipant = qrService.findGoldenBellParticipant(dto.getRoomNum());
		if(goldenBellParticipant == -1){
			template.convertAndSend("/quiz/cantgoldenbell","{\"msg\":\"can't goldenBell\"}");
		}else {
			template.convertAndSend("/quiz/goldenbell","{\"id\":\""+goldenBellParticipant+"\"}");
		}
		targetDifficulty="상";
		break;
	case "CONSOLATION_MATCH":
		currMode = QuizMode.CONSOLATION_MATCH;
		List<Integer> dropouttedList = participantService.findDropOutParticipant(dto.getRoomNum());
		for(int id : dropouttedList) {
			template.convertAndSend("/quiz/changePartState/"+id,"{\"state\":\"true\"}");
//				log.info("send state");
		}
		
		String listToJSON = "";
		try {
			listToJSON = mapper.writeValueAsString(dropouttedList);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 	  template.convertAndSend("/quiz/consolationmatch", "{\"msg\":\"consolation match\",\"list\":"+listToJSON+"}");
 	  targetDifficulty="하";
	  break;
	}
	
	qrService.changeQuizRoomMode(dto.getRoomNum(), currMode);
	qrService.changeTargetDifficulty(dto.getRoomNum(), targetDifficulty);
}
	
//문제 출제
@MessageMapping("/selectQuiz")
@SendTo("/quiz/selectedQuiz")
public String  SelectQuiz(WebSocketDTO dto) {
	log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", requestSelectQuiz : "+dto.getMsg());
	QuizRoom qr = qrService.findQuizRoomByRoomNum(dto.getRoomNum());
	qService.selectQuiz(qr);
	log.info("[select complete] Quiz : "+qr.getCurrQuiz().getQuestion()+", Answer : "+qr.getCurrQuiz().getAnswer()+", Difficulty : "+qr.getCurrQuiz().getDifficulty());
	String result ="{\"num\":\""+qr.getCurrQuizNum()+"\","
				+ "\"question\":\""+qr.getCurrQuiz().getQuestion()+"\","
				+ "\"answer\":\""+qr.getCurrQuiz().getAnswer()+"\","
				+"\"difficulty\":\""+qr.getCurrQuiz().getDifficulty()+"\"}";
	return result;
}

// 사용자 답안 공개	
@MessageMapping("/openAnswer")
@SendTo("/quiz/openAnswer")
public String OpenAnswer(WebSocketDTO dto) {
	log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", request Open Submitted Answer : "+dto.getMsg());
	return "{\"msg\":\"openAnswer\"}";
}

// 문제 정답 공개 및 정답 여부 공개
@MessageMapping("/openCorrect")
@SendTo("/quiz/openCorrect")
public String OpenCorrect(WebSocketDTO dto) {
	log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", request Open Correct Answer: "+dto.getMsg());
	QuizRoom qr = qrService.findQuizRoomByRoomNum(dto.getRoomNum());
	
List<Integer> dropOutParticipant = participantService.CompareAnswer(dto.getRoomNum());
	
	String listToJSON ="";
	try {
		listToJSON = mapper.writeValueAsString(dropOutParticipant);
	} catch (JsonProcessingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	if(qr.getCurrMode() == QuizMode.GOLDEN_BELL) {
		String state = "";
		if(dropOutParticipant.size() > 0) state = "fail";
		else state = "success";
		template.convertAndSend("/quiz/goldenBellResult","{\"msg\":\""+state+"\",\"list\":"+listToJSON+"}");
		return "";
	}
	
	for(int id : dropOutParticipant) {
		template.convertAndSend("/quiz/changePartState/"+id,"{\"state\":\"false\"}");
		log.info("send state");
	}
	
	return "{\"msg\":\"openCorrect\",\"list\":"+listToJSON+"}";
}
```

→ 관리자 페이지 이미지, 참여 허용중 및 참여자가 들어왔을때 이미지, 문제 출제 시 이미지

- 참여자 페이지

 참여자는 관리자가 공개한 방 id와 패스워드를 참여허가 중에 사용하여 접속이 가능하게하였습니다.

```java
@PostMapping("/partLogin")
	public String Login(@RequestParam String roomNum, @RequestParam String password, Participant participant, Model model) {
		QuizRoom qr = null;
		
		try {
			qr = qrService.findQuizRoomByRoomNum(roomNum);
		}catch (Exception e) {
			// TODO: handle exception
			model.addAttribute("msg", "입력하신 방이 없습니다. 방 번호를 확인해주세요");
			model.addAttribute("url", "backPage");
			return "alert";
		}
		if(qr == null){
			model.addAttribute("msg", "입력하신 방이 없습니다. 방 번호를 확인해주세요");
			model.addAttribute("url", "backPage");
			return "alert";
		}
		if(!qr.isAllowParticipant()) {
			model.addAttribute("msg", "해당 방이 참여를 허용하지 않고 있습니다. 다음에 다시 시도해주세요");
			model.addAttribute("url", "backPage");
			return "alert";
		}
		if(!qr.getPassword().equals(password)) {
			model.addAttribute("msg", "비밀번호가 일치하지 않습니다");
			model.addAttribute("url", "backPage");
			return "alert";
		}
		if(participant.getNickname().isEmpty() || participant.getNickname().equals("")) {
			model.addAttribute("msg", "닉네임을 입력해주세요");
			model.addAttribute("url", "backPage");
			return "alert";
		}
		
		
		qrService.partQuizRoom(qr, participant);
		model.addAttribute("roomNum", roomNum);
		
		
		return "participant/playPage";
	} 
```

→ 참여자 로그인 페이지

관리자가 문제 출제시 화면에 문제가 갱신되고, 타이머 시작시 타이머가 동작되게 하였습니다. 

```java
// 참여하였음을 서버에 전송 data = 참여한 방, 참여자 id, 
stompClient.send("/app/participation", {}, JSON.stringify(data));

//StompController 문제 출제, jsp 문제 입력 부분
// PlayerPage.jsp 중 입력한 답을 보내는 부분
function sendAnswer() {
					// console.log("call sendAnswer")/
					if (currState == "start") {
						let answer = $("#answer").val();
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
						$("#resultTxt").fadeOut(3000);
					}

}

// StompController 중 답안을 입력받는 함수
@MessageMapping("/submitAnswer")
@SendTo("/quiz/submittedAnswer")
public String submitAnswer(WebSocketDTO dto) {
	log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", answer : "+dto.getMsg());
	participantService.setAnswer(dto.getRoomNum(), dto.getPartId(), dto.getMsg());
	String submittedAnswer = "{\"partId\":"+dto.getPartId()+",\"answer\":\""+dto.getMsg()+"\"}";
	return submittedAnswer;
}

....
// /quiz/selectedQuiz로 데이터 전송시 데이터를 수신한다는 코드
stompClient.subscribe('/quiz/selectedQuiz');
...
	// 여러 url을 subscribe 중에 관련 단어가 포함된어 있을때 해당 함수를 호출
else if (value.includes("selectedQuiz")) {
		changeQuiz(message);
}
...

// 출제된 문제 JSON 데이터를 받아 화면에 표시
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

@MessageMapping("/selectQuiz")
@SendTo("/quiz/selectedQuiz")
public String  SelectQuiz(WebSocketDTO dto) {
	log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", requestSelectQuiz : "+dto.getMsg());
	QuizRoom qr = qrService.findQuizRoomByRoomNum(dto.getRoomNum());
	qService.selectQuiz(qr);
	log.info("[select complete] Quiz : "+qr.getCurrQuiz().getQuestion()+", Answer : "+qr.getCurrQuiz().getAnswer()+", Difficulty : "+qr.getCurrQuiz().getDifficulty());
	String result ="{\"num\":\""+qr.getCurrQuizNum()+"\","
				+ "\"question\":\""+qr.getCurrQuiz().getQuestion()+"\","
				+ "\"answer\":\""+qr.getCurrQuiz().getAnswer()+"\","
				+"\"difficulty\":\""+qr.getCurrQuiz().getDifficulty()+"\"}";
	return result;
}
```

→ 문제가 출제된 참여자 페이지 표시

- 송출페이지

 방이 생성되었을 때 생성 id를 통해서 해당 방의 참여자와 문제, 참여자의 답안 및 문제의 정답 공개가 가능해지며

 관리자의 요청에 따라 적절하게 대응을 해야하는 페이지로 기능을 구현하였습니다.

 참여자가 참여시 참여자의 닉네임을 화면에 표시하게끔 구현하였습니다

```java
...
stompClient.subscribe("/quiz/partParticipant");
...
if (value.includes("partParticipant")) {
       addParticipant(message);
}
...                        
// 참여시 참여자 항목 추가
function addParticipant(participant) {
                //console.log(typeof (participant));
                let participantJSON = JSON.parse(participant);
                let partId = participantJSON.partId;
                let nickname = participantJSON.nickname;

                appendElementTable(partId, nickname);
                appendElementList(partId, nickname);
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
```

→ 아무 참여자가 없는 페이지, 참여 후 페이지

 퀴즈쇼를 진행하면서 사용자가 정답을 입력 후 관리자가 답안 공개, 정답 공개시 사용자가 입력한 답안을 공개하고, 문제의 정답을 공개하면서 정답과 오답인지를 표시하였습니다

```java
//StompController 답안 공개, 정답공개
...
// 아래 url로 JSON 데이터를 수신하겠다는 의미
stompClient.subscribe("/quiz/submittedAnswer");
stompClient.subscribe("/quiz/openAnswer");
stompClient.subscribe("/quiz/openCorrect");
...
// 여러 url을 subscribe 중에 관련 단어가 포함된어 있을때 해당 함수를 호출
 else if (value.includes("submittedAnswer")) {
    updateParticipantAnswer(message);
} else if (value.includes("openAnswer")) {
    openParticipantAnswer();
} else if (value.includes("openCorrect")) {
    openQuestionAnswer(message);
	  updateList(message,false);
}
...

 function updateParticipantAnswer(answerjson) {
                // console.log(answerjson);
                let answerJSON = JSON.parse(answerjson)
                let partId = "#answer" + answerJSON.partId;
                let answer = answerJSON.answer;

                $(partId).text(answer);
            }

function openParticipantAnswer() {
                // console.log("openAnswer");
                $(".answer").css("visibility", "visible");

            }
 function openQuestionAnswer(message) {
                $("#Qanswer").css("visibility", "visible");
            }
// updateList 표시

```

→ 문제 출제 후 페이지, 정답 오답 표시 페이지

 모든 페이지가 서버에 접속하여 서로 연결되어 정보를 주고 받아야하기 때문에 기존의 웹은 Stateless이기 때문이기에 별도의 Stateful 방식의 통신이 필요한데 Polling은 클라이언트의 요청이 있어야만하기 때문에 송출 페이지와 사용자 페이지에서는 서버에서 문제를 전송하고나 답안을 공개해야할 때 적합하지 않습니다. 양방향 통신이 필요한 경우이기 때문에 SSE보다는 WebSocket 방식이 적합하다고 판단하였고 WebSocket과 STOMP(Simple TextOriented Messaging Protocol)을 활용하였습니다.

### 제작하면서 어려웠던 점 및 아쉬웠던 점

- 처음으로 WebSocket을 사용하다 보니, Spring에서 웹소켓을 사용하는 방법이 dependency 충돌과 같은 이슈를 해결하기 위해서 SpringBoot에서 WebSocket dependency를 사용했던점이 어려웠고, 아쉬웠습니다. 이 부분은 스프링에서 단순히 WebSocket을 사용하는 점에 대해서 공부해야겠다고 생각하였습니다.
- 방에 접속한유저를 기억하고, 재접속에 관련된 기능을 구현하지 않아 새로 고침시에 동일한 유저가 추가되는 문제 점이 있다는 점 당장에 유저의 정보를 어떻게 저장할지를 조금 더 고민해 봐야해서 아쉬운 점입니다.
- 서비스 진행시에 가끔 답을 입력이 누락되는 경우가 있어서, 이 점에대한 원인을 파악하지 못한 점이 아쉬운 점입니다. STOMP를 사용하였을 때 서버의 동작이 모든 클라이언트와의통신이 비동기적으로 이루어지는지 알아봐야 할 것 같습니다.

### 서비스 진행 후 느낀 점

- 제작한 시스템을 활용하여 서비스를 제공할 때 항상 느끼는 점 중 하나인 사용자는 생각하지 못한 사용 방법으로 오류를 찾아낸다라는 점입니다.
- 서비스 개시 후에는 웹 페이지로 사용자들이 접근을 하다보니 ‘robot.txt’와 ‘favicon’ 등 기존에 시스템에서 사용하기 위해 사용하는 url이 아닌 다른 url로 접근을 시도하려는 것을 발견하였고, 로그를 조금 더 활용하여서 기록들을 남겨야 겠다고 생각하였습니다.
- 또한 서비스를 해봐야 찾을 수 있는 문제점들이 있다는 점을 배웠고, 이 문제를 어떻게 피드백을 받고, 사용자가 하는 피드백을 받을 때의 개발자로서 어떤 태도로 받아들여야할까, 그리고 해결을 어떻게 해야할까를 생각해 볼 수 있었습니다.
    
     분명 서비스를 하기 전에 같이 서비스를 담당하던 분들과 테스트할 때는 이상이 없을 것 같아지만 생각하지 못했던 부분에서 문제가 생기는 것을 찾을 수 있었습니다. 그리고 사용자가 사용하면서 불편한 부분들도 생각하지 못했던 부분에서 나왔습니다. 이러한 피드백을 받을 때 시스템을 만든 사람으로서의 입장에서는 불편하게 다가올 수 있지만, 개발자로서는 이 서비스가 사용자에게 조금 더 개선되어 이 서비스를 더 찾게 만들어야한다면 피드백을 불편하게만 받아들이면 안되겠다고 생각하였습니다.
    
    - 오답자에게 정답공개시 정답표시 버그 제거 <br/>
    -> 송출화면에서 정답 공개 시 탈락자를 제외하고 O 표시를 하게끔 최초 작성<br/>
    -> 송출화면에서 이미 x표시가 되어있다면 O 표시가 되지 않게끔 수정<br/>
    - 강퇴시 참여자 아이디 중복 버그 제거<br/>
    -> 참여자 ID를 기존에는 참여할때의 인원수에 맞춰 부여 하였는데 참여 허용 중 사용자를 강퇴 시켰을 때 참여인원이 줄어들었기에 이후에 추가된 인원은 중복된 ID를 가지게 되었음<br/>
    -> 전체 참여인원수로 사용하던 변수로 ID를 부여하여서 문제가 발생 하였습니다. 진행시 전체 참여자에 대한 정보가 불필요하게 되어서 강퇴시 줄어들게하는 과정을 제거하였고 이 문제를 해결할 수 있었습니다.<br/>  
    - 일반 모드로 변경시 난이도도 함께 전송하게끔 수정<br/>
    -> 난이도 변경이 마지막 테스트에 추가 되다보니 모드 변경시에 일반 모드에서 난이도를 함께 전송하는 부분이 누락되어 모드 변경 후 난이도를 재설정해야하는 문제 발생<br/>
    -> 일반 모드 선택시 난이도 선택 라디오 버튼의 값도 함께 전송하여 난이도를 다시 설정하진 않게 수정 완료<br/>
    - 앞뒤띄워쓰기 생략 기능<br/>
    -> 서비스 규칙에서 띄워쓰기 없이 답을 입력하게 규칙을 정하겠다고 하였는데 앞 뒤의 뛰어쓰기로 틀리게 되는 문제가 발생하였습니다<br/>
    -> 참여자의 답변에서 trim()을 수행하여 앞뒤의 공백을 제거<br/>
    - 문제가 길면 드래그 해야한다<br/>
    -> 페이지가 늘어나지 않게 하기 위해서 문제 출력 범위를 고정시켰으나 사용자들이 문제가 길어졌을 때 드래그를 해야하는 점이 불편하다고 하였습니다.<br/>
    -> 문제 출력범위 능동적으로 변경하기엔 문제가 있기 때문에 회의를 통해 수정 방안을 결정해야할 거 같다고 판단하였습니다.<br/>

지속적으로 서비스를 운영하면서 사용자의 니즈를 충족하는 시스템으로 개편시킬것입니다.

### 실제 사용 모습

https://www.youtube.com/watch?v=xK8TMh5x4vI&t=7943s
