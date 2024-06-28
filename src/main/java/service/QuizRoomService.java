package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import model.Participant;
import model.QuizRoom;
import repository.QuizRoomRepository;

@Service
public class QuizRoomService {
	
	@Autowired
	private QuizRoomRepository repo;
	
	public String createQuizRoom(String password) {
		return repo.createQuizRoom(password);
	}
	
	public void removeQuizRoom(String roomNum) {
		repo.removeQuizRoom(roomNum);
	}
	
	public QuizRoom findQuizRoomByRoomNum(String roomNum) {
		return repo.findQuizRoomByRoomNum(roomNum);
	}
	
	public void partQuizRoom(QuizRoom qr, Participant participant) {
		qr.addParticipantToMap(participant);
	}
	

	
}
