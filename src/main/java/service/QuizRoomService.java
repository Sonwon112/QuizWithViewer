package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import repository.QuizRoomRepository;

@Service
public class QuizRoomService {
	
	@Autowired
	private QuizRoomRepository repo;
	
	public String createQuizRoom(String password) {
		return repo.createQuizRoom(password);
	}
}
