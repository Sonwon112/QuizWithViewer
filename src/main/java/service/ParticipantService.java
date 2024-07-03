package service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	
	public List<Integer> CompareAnswer(String roomNum) {
		QuizRoom qr = qrService.findQuizRoomByRoomNum(roomNum);
		return qr.CompareAnswer();
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
