import java.util.*;
public class Instruction {
	String type;
	int word;
	ArrayList<String> errorMsgs = new ArrayList<String>();
	boolean changed = false;
	
	Instruction(String type, int word) {
		super();
		this.type = type;
		this.word = word;
	}

	Instruction () {
		
	}

	String getType() {
		return type;
	}

	void setType(String type) {
		this.type = type;
	}

	int getWord() {
		return word;
	}

	void setWord(int word) {
		this.word = word;
	}
}
