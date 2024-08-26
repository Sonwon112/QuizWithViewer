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
	
	@MessageMapping("/changeDifficulty")
	public void ChangeDifficulty(WebSocketDTO dto) {
		log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", changeDifficulty : "+dto.getMsg());
		QuizRoom qr = qrService.findQuizRoomByRoomNum(dto.getRoomNum());
		if(qr.getCurrMode() == QuizMode.DEFAULT) {
			qrService.changeTargetDifficulty(dto.getRoomNum(), dto.getMsg());
		}
	}
	
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
	
	@MessageMapping("/changeParticipantState")
	public void ChangeParticipantState(WebSocketDTO dto) {
		log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", changeMode : "+dto.getMsg());
		qrService.changeParticipantState(dto.getRoomNum(), Boolean.parseBoolean(dto.getMsg()));
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
	
	@MessageMapping("/openAnswer")
	@SendTo("/quiz/openAnswer")
	public String OpenAnswer(WebSocketDTO dto) {
		log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", request Open Submitted Answer : "+dto.getMsg());
		return "{\"msg\":\"openAnswer\"}";
	}
	
	@MessageMapping("/startTimer")
	@SendTo("/quiz/startTimer")
	public String StartTimer(WebSocketDTO dto) {
		log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", timer Start");
		QuizRoom qr = qrService.findQuizRoomByRoomNum(dto.getRoomNum());
		String difficulty = qr.getCurrQuiz().getDifficulty();
		return "{\"difficulty\":\""+difficulty+"\"}";
	}
	
	
	@MessageMapping("/submitAnswer")
	@SendTo("/quiz/submittedAnswer")
	public String submitAnswer(WebSocketDTO dto) {
		log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", answer : "+dto.getMsg());
		participantService.setAnswer(dto.getRoomNum(), dto.getPartId(), dto.getMsg());
		String submittedAnswer = "{\"partId\":"+dto.getPartId()+",\"answer\":\""+dto.getMsg()+"\"}";
		return submittedAnswer;
	}
	
	@MessageMapping("/deleteRoom")
	@SendTo("/quiz/deleteRoom")
	public String deleteRoom(WebSocketDTO dto) {
		qrService.removeQuizRoom(dto.getRoomNum());
		return "{\"msg\":\"delete\"}";
	}
	
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
