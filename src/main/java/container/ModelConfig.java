package container;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import repository.QuizRepository;
import repository.QuizRoomRepository;
import service.ParticipantService;
import service.QuizRoomService;
import service.QuizService;

@Configuration
public class ModelConfig {
	
	// --------------------- QuizRoom의 전반적인 관리를 위한 Bean 객체
	@Bean
	public QuizRoomService quizRoomServie() {
		return new QuizRoomService();
	}
	
	@Bean
	public QuizRoomRepository quizRoomRepository() {
		return new QuizRoomRepository();
	}
	
	// ---------------------- 참여자 관리를 위한 Bean 객체
	@Bean
	public ParticipantService submitAnswerService() {
		return new ParticipantService();
	}
	
	// --------------------- JSON 작업을 위한 Bean 객체
	@Bean
	public ObjectMapper mapper() {
		return new ObjectMapper();
	}
	
	// --------------------- 문제를 관리하기 위한 Bean 객체
	@Bean
	public QuizService quizService() {
		return new QuizService();
	}
	
	@Bean
	public QuizRepository quizRepository() {
		return new QuizRepository();
	}
	
}
