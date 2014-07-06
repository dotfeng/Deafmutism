package net.fengg.app.deafmutism.model;

public class ChatMessage {
	public static final int MESSAGE_RIGHT = 0;
	public static final int MESSAGE_LEFT = 1;
	private int direction;
	private String content;

	public ChatMessage(int direction, String content) {
		super();
		this.direction = direction;
		this.content = content;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}	
	
}
