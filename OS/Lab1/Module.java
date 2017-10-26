import java.util.*;

public class Module {
	int start_Address;
	int num;
	ArrayList<Symbol> syms_used = new ArrayList<Symbol>();
	InstructionsTable ins_tab;
	int insNum;
	
	Module(int start_add, InstructionsTable ins_tab) {
		this.start_Address = start_add;
		this.ins_tab = ins_tab;
	}
	
	Module() {
	}
	
	void print_syms_used_info() {
		for (int i = 0; i < syms_used.size(); i++) {
			System.out.println(syms_used.get(i).getSymbol() + syms_used.get(i).lines.toString());
		}
	}
	
	int getStart_Address() {
		return start_Address;
	}

	void setStart_Address(int start_Address) {
		this.start_Address = start_Address;
	}

	int getNum() {
		return num;
	}

	void setNum(int num) {
		this.num = num;
	}
}
