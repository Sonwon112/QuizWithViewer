package model;

public class Participant {
	
	private int partId; // -1 : admin, -2 : overlay
	private String nickname;
	private boolean isPart = true;
	private String answer="";
	
	public Participant(String nickname) {
		this.nickname = nickname;
	}
	
	
	public int getPartId() {
		return partId;
	}
	public void setPartId(int partId) {
		this.partId = partId;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public boolean isPart() {
		return isPart;
	}
	public void setPart(boolean isPart) {
		this.isPart = isPart;
	}
	public String getAnswer() {
		return answer;
	}
	public boolean compareAnswer(String currAnswer) {
		String[] answers = currAnswer.split(",");
		for(String tmp : answers) {
			tmp = tmp.trim();
			if(tmp.equals(answer)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
}
