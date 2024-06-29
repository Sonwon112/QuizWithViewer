package controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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

	
	@MessageMapping("/participation")
	@SendTo("/quiz/partParticipant")
	public String example(WebSocketDTO dto) {
		log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+" "+dto.getMsg());
		
		Participant participant = participantService.findParticipant(dto.getRoomNum(), dto.getPartId());
		String result = "";
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
	
	
	@MessageMapping("/changeMode")
	public void ChangeMode(WebSocketDTO dto) {
		log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", changeMode : "+dto.getMsg());
		QuizMode currMode = null;
		
		switch (dto.getMsg()) {
		case "DEFAULT":
			currMode = QuizMode.DEFAULT;
			break;
		case "ICEBREAKING":
			currMode = QuizMode.ICEBREAKING;
			break;
		case "GOLDEN_BELL":
			currMode = QuizMode.GOLDEN_BELL;
			break;
		case "CONSOLATION_MATCH":
			currMode = QuizMode.CONSOLATION_MATCH;
			break;
		}
		
		qrService.changeQuizRoomMode(dto.getRoomNum(), currMode);
	}
	
	@MessageMapping("/selectQuiz")
	@SendTo("/quiz/selectedQuiz")
	public String  SelectQuiz(WebSocketDTO dto) {
		log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", changeMode : "+dto.getMsg());
		QuizRoom qr = qrService.findQuizRoomByRoomNum(dto.getRoomNum());
		qService.selectQuiz(qr);
		log.info("[select complete] Quiz : "+qr.getCurrQuiz().getQuestion()+", Answer : "+qr.getCurrQuiz().getAnswer()+", Difficulty : "+qr.getCurrQuiz().getDifficulty());
		String result ="{\"question\":\""+qr.getCurrQuiz().getQuestion()+"\","
					+ "\"answer\":\""+qr.getCurrQuiz().getAnswer()+"\","
					+"\"difficulty\":\""+qr.getCurrQuiz().getDifficulty()+"\"}";
		return result;
	}
	
	@MessageMapping("/openCorrect")
	public void OpenCorrect(WebSocketDTO dto) {
		log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", changeMode : "+dto.getMsg());
	}
	
	@MessageMapping("/openAnswer")
	public void OpenAnswer(WebSocketDTO dto) {
		log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+", changeMode : "+dto.getMsg());
	}
	
	
}
