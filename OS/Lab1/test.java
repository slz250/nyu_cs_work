import java.io.File;
import java.util.*;

public class test {
	public static void main (String args[]) {
		ArrayList<Integer> lines = new ArrayList<Integer>();
		lines.add(1);
		lines.add(2);
		
		Iterator<Integer> it = lines.iterator();
		while(  it.hasNext()) {
			
			int test = it.next();
			if( test == 1){
				lines.remove(it);
			}

		}
	
		for (Integer line: lines) {
			System.out.println(line);
		}
	}
}
