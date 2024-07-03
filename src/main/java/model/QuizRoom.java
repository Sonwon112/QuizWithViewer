package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	private boolean isAllowParticipant = false;
	
	private Map<Integer,Participant> participantMap = new HashMap<>();
	private int participantLastNum = 0;
	private int currParticipantNum = 0;
	
	private QuizMode currMode = QuizMode.DEFAULT;
	private Quiz currQuiz;
	private int currQuizNum;
	
	public QuizRoom(String password) {
		roomNum = rand.ints(leftLimit, rightLimit+1)
				.filter(i->(i <= 57 || i >= 65) && (i <= 90 || i >= 97))
				.limit(roomNumLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		this.password = password;
		currQuiz = new Quiz("문제가 출제되지 않았습니다","","");
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
	
	public Quiz getCurrQuiz() {
		return currQuiz;
	}

	public void setCurrQuiz(Quiz currQuiz) {
		this.currQuiz = currQuiz;
	}

	public List<Integer> CompareAnswer() {
		List<Integer> dropOutList = new ArrayList<>();
		switch (currMode.name()) {
		case "CONSOLATION_MATCH":
			participantMap.forEach((i,p)->{
				if(!p.isPart()) {
					p.setPart(true);
					if(!p.getAnswer().equals(currQuiz.getAnswer())) {
						p.setPart(false);
						dropOutList.add(p.getPartId());
					}
				}
			});
			break;
		default:
			participantMap.forEach((i,p)->{
				if(p.isPart() && !p.getAnswer().equals(currQuiz.getAnswer())) {
					p.setPart(false);
					dropOutList.add(p.getPartId());
				}
			});
			break;
		}
		
		return dropOutList;
	}

	public int getCurrQuizNum() {
		return currQuizNum;
	}

	public void updateCurrQuizNum() {
		currQuizNum+=1;
	}

	public boolean isAllowParticipant() {
		return isAllowParticipant;
	}

	public void setAllowParticipant(boolean isAllowParticipant) {
		this.isAllowParticipant = isAllowParticipant;
	}
	
	public List<Integer> findDropOutParticipant() {
		List<Integer> dropOuttedList = new ArrayList<>();
		participantMap.forEach((i,p)->{
			if(!p.isPart()) {
//				p.setPart(true);
				dropOuttedList.add(p.getPartId());
			}
		});
		return dropOuttedList;
	}
}
