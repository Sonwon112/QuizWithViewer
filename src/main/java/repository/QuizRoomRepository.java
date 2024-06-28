package repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import model.QuizRoom;

@Repository
public class QuizRoomRepository {
	
	private List<QuizRoom> quizRoomList = new ArrayList<>();
	
	public String createQuizRoom(String password) {
		QuizRoom quizRoom = new QuizRoom(password);
		quizRoomList.add(quizRoom);
		
		return quizRoom.getRoomNum();
	}
	
}
