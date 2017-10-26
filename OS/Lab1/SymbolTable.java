import java.util.*;
import java.io.*;

public class SymbolTable {
	ArrayList<Symbol> list = new ArrayList<Symbol>();
	
	public SymbolTable() {
		
	}
	
	void add_symbol(Symbol symbol) {
		this.list.add(symbol);
	}
	
	public Symbol get_symbol(String s) {
		Symbol temp;
		for (int i = 0; i < this.list.size(); i++) {
			temp = list.get(i);
			if (temp.getSymbol().equals(s)) {
				return temp; 
			}
		}
		return null;
	}
	
	boolean contains(Symbol sym) {
		int len = this.list.size();
		for (int i = 0; i < len; i++) {
			Symbol temp = this.list.get(i);
			if (temp.getSymbol().equals(sym.getSymbol())) {
				return true;
			}
		}
		return false;
	}
	public void print_symbol_table() {
		System.out.println("Symbol Table");
		for (int l = 0; l < this.list.size(); l++) {
			Symbol temp = this.list.get(l);
			System.out.print(temp.getSymbol() + "=" + temp.address + " ");
			if (!temp.errorMsgs.isEmpty()) {
				for (String err: temp.errorMsgs) {
					System.out.println(err);
				}
			} 
			System.out.println(temp.used);
		}
	}
} 
