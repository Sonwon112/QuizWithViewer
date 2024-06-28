package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import model.Participant;
import model.ParticipantDTO;
import service.ParticipantService;
import service.QuizRoomService;

@Slf4j
@Controller
public class StompController {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ParticipantService participantService;
	@Autowired
	private QuizRoomService qrService;
	@Autowired
	private ObjectMapper mapper;

	
	@MessageMapping("/participation")
	@SendTo("/quiz/partParticipant")
	public String example(ParticipantDTO dto) {
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
	public void LostConnection(ParticipantDTO dto) {
		log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+" "+dto.getMsg());
		if(dto.getPartId() == -1) {
			qrService.removeQuizRoom(dto.getRoomNum());
			return;
		}
		participantService.removePartipant(dto.getRoomNum(), dto.getPartId());
	}
	
	
}
