import java.util.*;
public class Linker {
	ArrayList<Module> mods = new ArrayList<Module>();
	SymbolTable sym_tab;
	
	Linker(SymbolTable sym_tab) {
		this.sym_tab = sym_tab;
	}
	
	void addModule(Module m) {
		this.mods.add(m);
	}
	
	void print_mem_map() {
		System.out.println("Memory Map");
		Module temp_mod;
		ArrayList<Instruction> ins_list;
		int num_ins;
		int line_num = 0;
		for (int i = 0; i < mods.size(); i++) {
			temp_mod = mods.get(i);
			ins_list = temp_mod.ins_tab.ins_list;
			num_ins = ins_list.size();
			Instruction temp;
			for (int j = 0; j < num_ins; j++) {
				temp = ins_list.get(j);
				System.out.print(line_num + ": " + temp.word);
				if (!temp.errorMsgs.isEmpty()) {
					for (String errorMsg: temp.errorMsgs) {
						System.out.print(errorMsg);
					}
				}
				line_num++;
				System.out.println();
			}
		}
		for (Symbol sym: this.sym_tab.list) {
			if (!sym.used) {
				System.out.println("Warning: " + sym.getSymbol() + " was defined in module " + sym.modNumDefined + " but never used.");
			}
		}
	}
	
	void printSymbolsUsed() {
		System.out.println("Instructions List: ");
		int modNum = 1;
		for (Module mod: mods) {
			System.out.println("Mod num: " + modNum);
			for (Symbol sym: mod.syms_used) {
					System.out.println("Symbol: " + sym.getSymbol() + " Lines Used: " + sym.lines.toString());
			}
			modNum++;
		}
	}
	
	void printModStartAdd() {
		System.out.println("Mods Start Adds: ");
		int modNum = 1;
		for (Module mod: mods) {
			System.out.println("Mod num: " + modNum);
			System.out.println("Add: " + mod.getStart_Address());
			modNum++;
		}
	}
	
	void resolveEs() {
		int modNum = 1;
		ArrayList<Instruction> insList;
		int word;
		int replace;
		SymbolTable symTable = this.sym_tab;
		Instruction ins;
		Symbol symInTable;
		for (Module mod: mods) {
			insList= mod.ins_tab.ins_list;
			System.out.println("Mod num: " + modNum);
			for (Symbol sym: mod.syms_used) {
				//System.out.println(sym.getSymbol());
				//symName = symTable.get_symbol(sym.getSymbol()).getSymbol();
				//symAdd = symTable.get_symbol(sym.getSymbol()).address;
				//System.out.println(symName + " " + symAdd);
				
				//used but not found in table
				if (symTable.get_symbol(sym.getSymbol()) == null) {
					for (int line: sym.lines) {
						ins = insList.get(line);
						ins.errorMsgs.add(" Error: " + sym.getSymbol() + " is not defined, zero used.");
						word = insList.get(line).word;
						word = ((word / 1000) * 1000);
						insList.get(line).word = word;
					}
				}
				
				//symbol is used and defined
				else {
					replace = symTable.get_symbol(sym.getSymbol()).address;
					for (int line: sym.lines) {
						ins = insList.get(line);
						//double check this!
						if (ins.changed) {
							ins.errorMsgs.add(" Error: Multiple variables used in instruction; all but first ignored.");
							break;
						}
						word = insList.get(line).word;
						word = ((word / 1000) * 1000) + replace;
						insList.get(line).word = word;
						symInTable = symTable.get_symbol(sym.getSymbol());
						symInTable.used = true;
						ins.changed = true;
					}
				}
				//System.out.println(insList.get(line).word);
			}
			modNum++;
		}
		
	}

	void resolveRs() {
		//System.out.println("Resolving Es: ");
		int modNum = 1;
		ArrayList<Instruction> insList;
		String word;
		String replace;
		String finalWord;
		int length = 0;
		SymbolTable symTable = this.sym_tab;
		for (Module mod: mods) {
			insList= mod.ins_tab.ins_list;
			//System.out.println("Mod num: " + modNum);
			for (Instruction ins: insList) {
				if (ins.type.equals("R")) {
					if (ins.word % 1000 > insList.size()) {
						ins.errorMsgs.add(" Error: Relative address exceeds module size; zero used.");
						ins.word = (ins.word / 1000) * 1000;
					}
					else {
						ins.word += mod.getStart_Address();
					}
				}
			}
			modNum++;
		}
	}
	
	void resolveAs() {
		int modNum = 1;
		ArrayList<Instruction> insList;
		String word;
		String replace;
		String finalWord;
		int length = 0;
		SymbolTable symTable = this.sym_tab;
		for (Module mod: mods) {
			insList= mod.ins_tab.ins_list;
			//System.out.println("Mod num: " + modNum);
			for (Instruction ins: insList) {
				if (ins.type.equals("A")) {
					if ((ins.word % 1000) >= 200) {
						ins.errorMsgs.add(" Error: Absolute address exceeds machine size; zero used.");
						ins.word = (ins.word / 1000) * 1000;
					}
				}
			}
			modNum++;
		}
	}
	
	void checkSomeErrors() {
		Symbol temp;
		for (Module mod: mods) {
			for (Symbol sym: mod.syms_used) {
				for (int i = 0; i < sym.lines.size(); i++) {
					if (sym.lines.get(i) >= mod.insNum) {
						temp = sym_tab.get_symbol(sym.getSymbol());
						//System.out.println("line: " + sym.lines.get(i) + "symbol: " + temp.getSymbol() + "mod num: " + mod.num);
						temp.errorMsgs.add("Error: Use of " + temp.getSymbol() + " in module " + mod.num + " exceeds module size; use ignored.");
						sym.lines.remove(i);
					}
				}
			}
		}
	}
}
