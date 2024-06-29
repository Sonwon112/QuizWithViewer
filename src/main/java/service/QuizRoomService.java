package service;

import java.lang.module.FindException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import customEnum.QuizMode;
import model.Participant;
import model.QuizRoom;
import repository.QuizRoomRepository;

@Service
public class QuizRoomService {
	
	@Autowired
	private QuizRoomRepository repo;
	
	/**
	 * 관리자가 방생성 시 호출되는 방 생성함수
	 * @param password
	 * @return
	 */
	public QuizRoom createQuizRoom(String password) {
		return repo.createQuizRoom(password);
	}
	
	/**
	 * 관리자가 생성한 방에서 빠져나가면 호출되는 방 삭제함수
	 * @param roomNum
	 */
	public void removeQuizRoom(String roomNum) {
		repo.removeQuizRoom(roomNum);
	}
	
	/**
	 * 방번호를 통해 QuizroomRepository에 저장된 Quizroom을 찾는 함수
	 * @param roomNum
	 * @return
	 */
	public QuizRoom findQuizRoomByRoomNum(String roomNum) {
		return repo.findQuizRoomByRoomNum(roomNum);
	}
	
	/**
	 * 참여자가 방번호와 비밀번호, 닉네임을 입력하여 참여시 QuizRoom의 ParticipantMap에 참여자를 추가하기 위한 참여함수
	 * @param qr
	 * @param participant
	 */
	public void partQuizRoom(QuizRoom qr, Participant participant) {
		qr.addParticipantToMap(participant);
	}
	
	/**
	 * 방의 문제 출제 모드를 변경하는 함수
	 * @param roomNum
	 * @param mode
	 */
	public void changeQuizRoomMode(String roomNum, QuizMode mode) {
		QuizRoom qr = findQuizRoomByRoomNum(roomNum);
		qr.setCurrMode(mode);
	}
	
	public void changeParticipantState(String roomNum, boolean state) {
		QuizRoom qr = findQuizRoomByRoomNum(roomNum);
		qr.setAllowParticipant(state);
	}
	
	
}
