package model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.web.socket.WebSocketSession;

import customEnum.QuizMode;

public class QuizRoom {
	
	private static int leftLimit = 48;
	private static int rightLimit = 122;
	private static int roomNumLength = 10;
	private Random rand = new Random();
	
	private String roomNum;
	private String password;
	private Map<Integer,Participant> participantMap = new HashMap<>();
	private int participantLastNum = 0;
	private int currParticipantNum = 0;
	private QuizMode currMode = QuizMode.DEFAULT;
	private String currQuestion;
	private String currAnswer;
	
	public QuizRoom(String password) {
		roomNum = rand.ints(leftLimit, rightLimit+1)
				.filter(i->(i <= 57 || i >= 65) && (i <= 90 || i >= 97))
				.limit(roomNumLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getRoomNum() {
		return roomNum;
	}

	public void setRoomNum(String roomNum) {
		this.roomNum = roomNum;
	}

	public Map<Integer,Participant> getParticipantMap() {
		return participantMap;
	}
	
	public void addParticipantToMap(Participant participant) {
		currParticipantNum+=1;
		participantLastNum = currParticipantNum;
		participant.setPartId(participantLastNum);
		participantMap.put(participantLastNum, participant);
	}
	
	public void removeParticipantToMap(int id) {
		participantMap.remove(id);
		currParticipantNum-=1;
	}

	public QuizMode getCurrMode() {
		return currMode;
	}

	public void setCurrMode(QuizMode currMode) {
		this.currMode = currMode;
	}

	public String getCurrQuestion() {
		return currQuestion;
	}

	public void setCurrQuestion(String currQuestion) {
		this.currQuestion = currQuestion;
	}

	public String getCurrAnswer() {
		return currAnswer;
	}

	public void setCurrAnswer(String currAnswer) {
		this.currAnswer = currAnswer;
	}
	
	public void CompareAnswer() {
		participantMap.forEach((i,p)->{
			if(!p.getAnswer().equals(currAnswer)) {
				p.setPart(false);
			}
		});
	}
	
}
