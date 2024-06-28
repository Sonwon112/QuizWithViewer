package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import model.Participant;
import model.QuizRoom;

@Service
public class ParticipantService {
	
	@Autowired
	private QuizRoomService qrService;
	
	public void removePartipant(String roomNum, int partId) {
		QuizRoom qr = qrService.findQuizRoomByRoomNum(roomNum);
		
		qr.removeParticipantToMap(partId);
	}
	
	public void setAnswer(String roomNum, int partId, String answer) {
		Participant currParticipant = findParticipant(roomNum, partId);
		currParticipant.setAnswer(answer);
		
	}
	
	public Participant findParticipant(String roomNum, int partId) {
		QuizRoom qr = qrService.findQuizRoomByRoomNum(roomNum);
		if(qr == null) {
			//exception
		}
		Participant currParticipant = qr.getParticipantMap().get(partId);
		if(currParticipant == null) {
			//exception
		}
		return currParticipant;
	}
}
