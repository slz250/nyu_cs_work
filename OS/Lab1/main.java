import java.util.*;
import java.io.*;

public class main {
	private static void first_pass(File input_file) {
		try {
			Scanner input = new Scanner(input_file);
			
			int num_modules = input.nextInt();
			int num_symbols;
			int num_uses;
			SymbolTable symbol_table = new SymbolTable();
			ArrayList<Symbol> sym_tab_list = symbol_table.list;
			int add_in_linker = 0;
			
			
			Linker linker = new Linker(symbol_table);
			
			for (int modNum = 0; modNum < num_modules; modNum++) { 
				Module m = new Module(add_in_linker, null);
				m.num = modNum + 1;
				num_symbols = input.nextInt();
				Symbol tempSym;
				ArrayList<Symbol> symsDefinedInMod = new ArrayList<Symbol> ();
				for (int j = 0; j < num_symbols; j++) {
					if (num_symbols == 0) {
						break;
					}
					String sym = input.next();
					Integer add = input.nextInt();
					int defInMod = add;
					add += add_in_linker;
					tempSym = new Symbol(sym, add);
					tempSym.definitionInMod = defInMod; 
					tempSym.setModNumDefined(modNum);
					Symbol symInTable;
					if (symbol_table.contains(tempSym)) {
						 symInTable = symbol_table.get_symbol(sym);
						 symInTable.errorMsgs.add(
								 "Error: This variable is multiply defined; first value used.");
					}
					else {
						symbol_table.add_symbol(tempSym);
						symsDefinedInMod.add(tempSym);
					}
					
				}
				
				ArrayList<Symbol> syms_used = m.syms_used;

				int termination = 0;
				num_uses = input.nextInt();
				Integer line_used;
				for (int k = 0; k < num_uses; k++) {
					if (num_uses == 0) {
						break;
					}
					String sym_name = input.next();
					Symbol temp_symbol = new Symbol(sym_name);
					if (!symbol_table.contains(temp_symbol)) {
						syms_used.add(temp_symbol);
						line_used = input.nextInt();
						while (line_used != -1) {
							temp_symbol.lines.add(line_used);
							line_used = input.nextInt();
						}
					} else {
						Symbol temp_symbol_2 = new Symbol(sym_name);
						syms_used.add(temp_symbol_2);
						line_used = input.nextInt();
						while (line_used != -1) {
							temp_symbol_2.lines.add(line_used);
							line_used = input.nextInt();
						}
					}
				}
				
				int ins_num = input.nextInt();
				m.insNum = ins_num;
				//check this
				for (Symbol sym: symsDefinedInMod) {
					//0 > 0
					if (sym.definitionInMod >= ins_num) {
						sym.address = add_in_linker;
						sym.errorMsgs.add("Error: Definition exceeds module size; first word in module used.");
					}
				}
				
				//System.out.println("num ins: " + ins_num);
				add_in_linker += ins_num;
				InstructionsTable ins_tab = new InstructionsTable(ins_num);
				ArrayList<Instruction> ins_list = ins_tab.ins_list;
				for (int l = 0; l < ins_num; l++) {
					Instruction temp_ins = new Instruction();
					temp_ins.type = input.next();
					temp_ins.word = input.nextInt();
					ins_list.add(temp_ins);
				}

				m.ins_tab = ins_tab;
				linker.addModule(m);
			}
			
			//linker.print_mem_map();
			//linker.printInstructionLists();
			//symbol_table.print_symbol_table();
			//linker.printModStartAdd();
			linker.checkSomeErrors();
			//linker.printSymbolsUsed();
			linker.resolveEs();
			linker.resolveRs();
			linker.resolveAs();
			symbol_table.print_symbol_table();
			
			linker.print_mem_map();

			input.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		if (args.length < 1) {
			System.out.println("Missing file arguments.");
		}
	
		File input_file = new File(args[0]);
		first_pass(input_file);	
	}
}
