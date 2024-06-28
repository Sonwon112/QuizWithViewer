package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;
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
	
	@MessageMapping("/participation")
	public void example(ParticipantDTO dto) {
		log.info("Room : "+dto.getRoomNum()+", member : "+dto.getPartId()+" "+dto.getMsg());
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
