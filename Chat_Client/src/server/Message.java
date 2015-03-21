package server;

public class Message {

	private int type;
	private String message;

	public Message() {
	}

	public boolean decodeMessage(String string) {
		if (string == null)
			return false;
		if (string.length() == 0)
			return false;
		if (string.indexOf('[') == -1)
			return false;
		if (string.indexOf(']') == -1)
			return false;
		if (string.indexOf('[') != 0)
			return false;
		if (string.indexOf('[') > string.indexOf(']'))
			return false;
		String typeStr = string.substring(string.indexOf("[") + 1, string.indexOf("]"));
		try {
			this.type = Integer.parseInt(typeStr);
		} catch (NumberFormatException e) {
			System.out.println("Packet is damaged");
			return false;
		}
		this.message = string.substring(string.indexOf(']') + 1);
		return true;
	}

	public void setMessage(int type, String message) {
		this.type = type;
		this.message = message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public String toString() {
		return "[" + type + "]" + message;
	}

}
