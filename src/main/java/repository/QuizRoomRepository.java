package repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import jdk.internal.org.jline.utils.Log;
import model.QuizRoom;

@Repository
public class QuizRoomRepository {
	
	private List<QuizRoom> quizRoomList = new ArrayList<>();
	
	public QuizRoom createQuizRoom(String password) {
		QuizRoom quizRoom = new QuizRoom(password);
		quizRoomList.add(quizRoom);
//		System.out.println("ListSize : " + quizRoomList.size());
		return quizRoom;
	}
	
	public void removeQuizRoom(String roomNum) {
		QuizRoom quizRoom = findQuizRoomByRoomNum(roomNum);
		quizRoomList.remove(quizRoom);
	}
	
	public QuizRoom findQuizRoomByRoomNum(String roomNum) {
		QuizRoom result = null;
//		System.out.println("ListSize : " + quizRoomList.size());
		List<QuizRoom> tmp = quizRoomList.stream().filter(v->v.getRoomNum().equals(roomNum)).toList();
		if(!tmp.isEmpty() || tmp != null) {
			result = tmp.get(0);
		}
		
		return result;
	}
	
	public boolean getPartState(String roomNum) {
		QuizRoom tmp = findQuizRoomByRoomNum(roomNum);
		return tmp.isAllowParticipant();
	}
	
}
