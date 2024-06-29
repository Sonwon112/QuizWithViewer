package service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import model.QuizRoom;
import repository.QuizRepository;

@Service
public class QuizService {

	@Autowired
	private QuizRepository repo;
	
	public void getQuizToFile(String roomNum, InputStream in) throws IOException {
		repo.getQuizToFile(roomNum, in);
	}

	public void selectQuiz(QuizRoom qr) {
		repo.selectQuiz(qr);
	}
}
