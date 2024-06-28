package container;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import repository.QuizRoomRepository;
import service.ParticipantService;
import service.QuizRoomService;

@Configuration
public class ModelConfig {
	
	@Bean
	public QuizRoomService quizRoomServie() {
		return new QuizRoomService();
	}
	
	@Bean
	public QuizRoomRepository quizRoomRepository() {
		return new QuizRoomRepository();
	}
	
	@Bean
	public ParticipantService submitAnswerService() {
		return new ParticipantService();
	}
	
}
