import java.util.*;
public class Symbol {
	String symbol;
	Integer address;
	Integer module_num;
	boolean used = false;
	ArrayList<Integer> lines = new ArrayList<Integer>();
	int modNumDefined;
	ArrayList<String> errorMsgs = new ArrayList<String>();
	Integer definitionInMod;

	int getModNumDefined() {
		return modNumDefined;
	}

	void setModNumDefined(int modNumDefined) {
		this.modNumDefined = modNumDefined;
	}

	public Symbol(String symbol, Integer address) {
		this.symbol = symbol;
		this.address = address;
		this.used = false;
	}
	
	public Symbol(String symbol) {
		this(symbol, null);
	}
	
	public Integer getAddress() {
		return address;
	}

	public void setAddress(Integer address) {
		this.address = address;
	}

	public Integer getModule_num() {
		return module_num;
	}

	public void setModule_num(Integer module_num) {
		this.module_num = module_num;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public ArrayList<Integer> getLines() {
		return lines;
	}

	public void setLines(ArrayList<Integer> lines) {
		this.lines = lines;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	void set_address(Integer address){
		this.address = address;
	}
	
	@Override
	public String toString() {
		return ("Symbol: " + this.getSymbol() + "Address: " + this.address);
	}

	public String getSymbol() {
		return symbol;
	}
	
	public boolean check_null() {
		if (this.symbol == null) {
			return true;
		}
		return false;
	}
}
