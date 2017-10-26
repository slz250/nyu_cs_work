import java.util.*;
	
public class InstructionsTable {
	ArrayList<Instruction> ins_list;
	
	InstructionsTable(int ins_num) {
		this.ins_list = new ArrayList<Instruction>(ins_num);
	}
	
	void add_instruction(Instruction ins) {
		this.ins_list.add(ins);
	}
}
