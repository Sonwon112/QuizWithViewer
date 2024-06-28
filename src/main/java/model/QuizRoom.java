package model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import customEnum.QuizMode;

public class QuizRoom {
	
	private static int leftLimit = 48;
	private static int rightLimit = 122;
	private static int roomNumLength = 10;
	private Random rand = new Random();
	
	private String roomNum;
	private String password;
	private Map<Integer,Participant> participantMap = new HashMap<>();
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

	public Map<Integer,Participant> getParticipantList() {
		return participantMap;
	}
	
	public void addParticipantToList(Participant participant) {
		participantMap.put(++currParticipantNum, participant);
	}
	
	public void removeParticipantToList(int id) {
		participantMap.remove(id);
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
	
	
}
