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
	
	public void removeQuizRoom(String roomNum) {
		QuizRoom quizRoom = findQuizRoomByRoomNum(roomNum);
		quizRoomList.remove(quizRoom);
	}
	
	public QuizRoom findQuizRoomByRoomNum(String roomNum) {
		QuizRoom result = null;
		List<QuizRoom> tmp = quizRoomList.stream().filter(v->v.getRoomNum().equals(roomNum)).toList();
		if(!tmp.isEmpty() || tmp != null) {
			result = tmp.get(0);
		}
		
		return result;
	}
	
}
