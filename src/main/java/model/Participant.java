package model;

import org.springframework.web.socket.WebSocketSession;

public class Participant {
	
	private int partId;
	private WebSocketSession session;
	private String nickname;
	private boolean isPart = true;
	private String answer;
	
	public Participant(WebSocketSession session,String nickname) {
		this.session = session;
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
	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public WebSocketSession getSession() {
		return session;
	}


	public void setSession(WebSocketSession session) {
		this.session = session;
	}
	
	
}
