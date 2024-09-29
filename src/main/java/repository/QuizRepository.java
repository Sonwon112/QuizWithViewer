package repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Repository;

import customEnum.QuizMode;
import model.Quiz;
import model.QuizRoom;

@Repository
public class QuizRepository {
	
	private static final String[] difficultyArr = {"하", "중", "상"};
	private Random random = new Random();

	private Map<String, List<Quiz>> quizMap = new HashMap<>();

	public void getQuizToFile(String roomNum, InputStream in) throws IOException {
		InputStreamReader reader = new InputStreamReader(in);
		BufferedReader bf = new BufferedReader(reader);
		List<Quiz> quizList = new ArrayList<Quiz>();

		String line = "";
		while ((line = bf.readLine()) != null) {
			String[] tmp = line.split(";");
			Quiz quiz = new Quiz(tmp[0], tmp[1], tmp[2]);
			quizList.add(quiz);
		}

		quizMap.put(roomNum, quizList);
//		System.out.println(quizMap.size());
//		System.out.println(quizMap.get(roomNum).size());
	}

	public void selectQuiz(QuizRoom qr) throws IndexOutOfBoundsException {
		String roomNum = qr.getRoomNum();
		List<Quiz> qList = quizMap.get(roomNum);
		Quiz q = new Quiz("더 이상 문제가 존재하지 않습니다", "", "");

		ArrayList<Quiz> QList = new ArrayList<Quiz>();
		
		
		try {
			QList.addAll(
					qList.stream().filter(v -> !v.isSubmitted()).filter(v ->{ 
						String tmp = qr.getTargetDifficulty();
						if(tmp.equals("랜")) {
							int index = random.nextInt(3);
							tmp = difficultyArr[index];
						}
						return v.getDifficulty().equals(tmp);}).toList());
			Collections.shuffle(QList);
			q = QList.get(0);
			q.setSubmitted(true);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			qr.setCurrQuiz(q);
			qr.updateCurrQuizNum();
		}
	}
}
