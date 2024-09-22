package controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import customEnum.QuizMode;
import lombok.extern.slf4j.Slf4j;
import model.Participant;
import model.QuizRoom;
import model.WebSocketDTO;
import service.ParticipantService;
import service.QuizRoomService;
import service.QuizService;

@Slf4j
@Controller
public class StompController {
		
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ParticipantService participantService;
	@Autowired
	private QuizRoomService qrService;
	@Autowired
	private QuizService qService;
	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private SimpMessagingTemplate template;
	
	/**
	 * socket 접속 (admin : -1, overlay : -2, 양수는 시청자 참여자)
	 * @param dto 
	 * @return
	 */
	@MessageMapping("/participation")
	@SendTo("/quiz/partParticipant")
	public String example(WebSocketDTO dto) {
		log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+" "+dto.getMsg());
		String result = "";
		switch(dto.getPartId()) {
		case -1: // admin
			result = "admin Participant";
			return result;
		case -2: // overlay
			result = "overlay Participant";
			return result;
		}
		Participant participant = participantService.findParticipant(dto.getRoomNum(), dto.getPartId());
		
		try {
			result = mapper.writeValueAsString(participant);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		log.info(result);
		return result;
	}
	
	/**
	 * 연결이 끊긴 경우 관리자의 경우 방을 제거 참여자인 경우 참여자 리스트에서 제거
	 * @param dto
	 * @param session
	 */
	@MessageMapping("/lostConnection")
	public void LostConnection(WebSocketDTO dto,HttpSession session) {
		log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+" "+dto.getMsg());
		if(dto.getPartId() == -1) {
			qrService.removeQuizRoom(dto.getRoomNum());
			return;
		}
		session.invalidate();
		participantService.removePartipant(dto.getRoomNum(), dto.getPartId());
	}
	
	/**
	 * 난이도 변경시 호출되는 Controller 메서드
	 * @param dto
	 */
	@MessageMapping("/changeDifficulty")
	public void ChangeDifficulty(WebSocketDTO dto) {
		log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", changeDifficulty : "+dto.getMsg());
		QuizRoom qr = qrService.findQuizRoomByRoomNum(dto.getRoomNum());
		if(qr.getCurrMode() == QuizMode.DEFAULT) {
			qrService.changeTargetDifficulty(dto.getRoomNum(), dto.getMsg());
		}
	}
	
	/**
	 * 문제 출제 모드 변경시 호출되는 Controller 메서드
	 * @param dto
	 */
	@MessageMapping("/changeMode")
	public void ChangeMode(WebSocketDTO dto) {
		log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", changeMode : "+dto.getMsg());
		QuizMode currMode = null;
		String targetDifficulty = "";
		String msg[] = dto.getMsg().split(";");
		
		switch (msg[0]) {
		case "DEFAULT":
			currMode = QuizMode.DEFAULT;
			targetDifficulty=msg[1];
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
	
	/**
	 * 참여 허용 여부 변경시 호출되는 Controller 메서드
	 * @param dto
	 */
	@MessageMapping("/changeParticipantState")
	public void ChangeParticipantState(WebSocketDTO dto) {
		log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", changeMode : "+dto.getMsg());
		qrService.changeParticipantState(dto.getRoomNum(), Boolean.parseBoolean(dto.getMsg()));
	}
	
	/**
	 * 문제 출제시 호출되는 Controller 메서드
	 * @param dto
	 * @return
	 */
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
	
	/**
	 * 문제의 정답을 공개할 때 호출되는 Controller 메서드
	 * @param dto
	 * @return
	 */
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
	
	/**
	 * 참여자의 정답을 공개할 때 호출되는 Controller 메서드
	 * @param dto
	 * @return
	 */
	@MessageMapping("/openAnswer")
	@SendTo("/quiz/openAnswer")
	public String OpenAnswer(WebSocketDTO dto) {
		log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", request Open Submitted Answer : "+dto.getMsg());
		return "{\"msg\":\"openAnswer\"}";
	}
	
	/**
	 * 타이머 시작 버튼 클릭시 호출되는 Controller 메서드
	 * @param dto
	 * @return
	 */
	@MessageMapping("/startTimer")
	@SendTo("/quiz/startTimer")
	public String StartTimer(WebSocketDTO dto) {
		log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", timer Start");
		QuizRoom qr = qrService.findQuizRoomByRoomNum(dto.getRoomNum());
		String difficulty = qr.getCurrQuiz().getDifficulty();
		return "{\"difficulty\":\""+difficulty+"\"}";
	}
	
	/**
	 * 시청자가 답변을 제출할 때 호출되는 Controller 메서드
	 * @param dto
	 * @return
	 */
	@MessageMapping("/submitAnswer")
	@SendTo("/quiz/submittedAnswer")
	public String submitAnswer(WebSocketDTO dto) {
		log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", answer : "+dto.getMsg());
		participantService.setAnswer(dto.getRoomNum(), dto.getPartId(), dto.getMsg());
		String submittedAnswer = "{\"partId\":"+dto.getPartId()+",\"answer\":\""+dto.getMsg()+"\"}";
		return submittedAnswer;
	}
	
	/**
	 * 관리자가 방을 떠날 때 호출되는 Controller 메서드
	 * @param dto
	 * @return
	 */
	@MessageMapping("/deleteRoom")
	@SendTo("/quiz/deleteRoom")
	public String deleteRoom(WebSocketDTO dto) {
		qrService.removeQuizRoom(dto.getRoomNum());
		return "{\"msg\":\"delete\"}";
	}
	
	/**
	 * 참여자를 내보낼 때 호출되는 Controller 메서드
	 * @param dto
	 * @return
	 */
	@MessageMapping("/out")
	@SendTo("/quiz/outPlayer")
	public String outParticipant(WebSocketDTO dto) {
		if(!qrService.getPartState(dto.getRoomNum())) {
			participantService.removePartipant(dto.getRoomNum(), Integer.parseInt(dto.getMsg()));
			template.convertAndSend("/quiz/out/"+dto.getMsg(),"{\"msg\":\"out\"}");
			return "{\"msg\":\"outPlayer\",\"list\":["+dto.getMsg()+"]}";
		}
		return "";
	}
}
