package model;

public class Quiz {
	
	private String question;
	private String answer;
	private String difficulty;
	private boolean isSubmitted = false;
	
	public Quiz(String question, String answer, String difficulty) {
		// TODO Auto-generated constructor stub
		this.question = question;
		this.answer = answer;
		this.difficulty = difficulty;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String questioin) {
		this.question = questioin;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}

	public boolean isSubmitted() {
		return isSubmitted;
	}

	public void setSubmitted(boolean isSubmitted) {
		this.isSubmitted = isSubmitted;
	}
	
	
	
}
