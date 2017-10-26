import java.util.*;
import java.io.*;

public class DemandPager {
	int fid, machineSize, pageSize, processSize, jobMix, numReferences, numFrames, quantum = 3,
			numProcs, time, debugLevel;
	String replacementAlgo;
	Scanner input;
	LinkedList<Frame> frameLL = new LinkedList<Frame>();
	LinkedList<Process> processLL = new LinkedList<Process>();
	
	
	private DemandPager(String machineSize, String pageSize, String processSize, String jobMix, String numReferences,
			String replacementAlgo, String debugLevel, Scanner input) {
		super();
		this.machineSize = Integer.parseInt(machineSize);
		this.pageSize = Integer.parseInt(pageSize);
		this.processSize = Integer.parseInt(processSize);
		this.jobMix = Integer.parseInt(jobMix);
		this.numReferences = Integer.parseInt(numReferences);
		this.replacementAlgo = replacementAlgo;
		this.debugLevel = Integer.parseInt(debugLevel);
		this.input = input;
		this.fid = this.numFrames = this.machineSize / this.pageSize;
		
	}
	
	class frameIDComparator implements Comparator<Frame> {
		@Override
		public int compare(Frame f1, Frame f2) {
			return ((f1.fid < f2.fid) ? -1 : 
											((f1.fid == f2.fid) ? 0 : 1));
		}
	}
	
	void init() {
		Process process;
		switch (jobMix) {
			case 1:
				process = new Process(1, this.processSize, this.numReferences, (111 + this.processSize) % this.processSize, this.numReferences,
						new JobMixProbability(1, 0, 0, 0));
				processLL.add(process);
				break;
			case 2:
				for (int i = 1; i <= 4; i++) {
					process = new Process(i, this.processSize, this.numReferences, (111 * i + this.processSize) % this.processSize,this.numReferences,
							new JobMixProbability(1, 0, 0, 0));
					processLL.add(process);
				}
				break;
			case 3:
				for (int i = 1; i <= 4; i++) {
					process = new Process(i, this.processSize, this.numReferences, (111 * i + this.processSize) % this.processSize, this.numReferences,
							new JobMixProbability(0, 0, 0, 1));
					processLL.add(process);
				}
				break;
			case 4:
				process = new Process(1, this.processSize, this.numReferences, (111 * 1 + this.processSize) % this.processSize,
						this.numReferences, new JobMixProbability(.75, .25, 0, 0));
				processLL.add(process);
				process = new Process(2, this.processSize, this.numReferences, (111 * 2 + this.processSize) % this.processSize,
						this.numReferences, new JobMixProbability(.75, 0, .25, 0));
				processLL.add(process);
				process = new Process(3, this.processSize, this.numReferences, (111 * 3 + this.processSize) % this.processSize,
						this.numReferences, new JobMixProbability(.75, .125, .125, 0));
				processLL.add(process);
				process = new Process(4, this.processSize, this.numReferences, (111 * 4 + this.processSize) % this.processSize,
						this.numReferences, new JobMixProbability(.5, .125, .125, .25));
				processLL.add(process);
				break;
		}
	}
	
	boolean isTerminated() {
		for (int i = 0; i < processLL.size(); i++) {
			if (!processLL.get(i).terminated) {
				return false;
			}
		}
		return true;
	}
	
	void updateEviction(Frame frame, Process p) {
		System.out.printf("Fault, evicting page %d of %d from frame %d.\n", 
				(frame.lowerBound / frame.frameSize), frame.pid, frame.fid);
		frame.timeEvicted = this.time;
		Process pToUpdate = null;
		for (Process pTemp: this.processLL) {
			if (pTemp.pid == frame.pid) pToUpdate = pTemp;
		}
		pToUpdate.avgResTime += frame.timeEvicted - frame.timeLoaded;
//		 System.out.printf("time evicted: %d time loaded: %d so time in: %f\n", 
//				frame.timeEvicted, frame.timeLoaded, pToUpdate.avgResTime);
		p.pageFaults++;
		pToUpdate.numEvictions++;
//		System.out.printf("p%d evics: %d\n", pToUpdate.pid, pToUpdate.numEvictions);
		
		frame.timeLoaded = this.time;
		frame.pid = p.pid;
		frame.setBounds(p.reference);
	}
	
	void checkFrames(Process p) {
		System.out.printf("%d references word %d (page %d) at time %d: ", 
				p.pid, p.reference, (p.reference / this.pageSize), this.time); 
		
		for (Frame f: frameLL) {
			if ((f.pid == p.pid) && (f.lowerBound <= p.reference && p.reference <= f.upperBound)) {
				System.out.printf("Hit in frame %d\n", f.fid);
				if (!this.replacementAlgo.equals("random")) {
					this.frameLL.remove(f);
					this.frameLL.add(f);
				}
				return;
			}
		}
		
		if (this.frameLL.size() != numFrames) {
			p.pageFaults++;
			Frame frame = new Frame(--this.fid, p.pid, this.pageSize, this.time);
			frame.setBounds(p.reference);
			this.frameLL.add(frame);
			System.out.printf("Fault, using free frame %d.\n", frame.fid);
			if (this.replacementAlgo.equals("random")) this.frameLL.sort(new frameIDComparator());
			return;
		}
		
		
		
		if (this.replacementAlgo.equals("lru")) {
			Frame frame = this.frameLL.poll();
			this.updateEviction(frame, p);
			this.frameLL.add(frame);
		} else if (this.replacementAlgo.equals("random")) {
			int randNum = this.input.nextInt();
			System.out.println();
			System.out.println(this.frameLL.size());
			for (Frame f: this.frameLL) {
				System.out.println(f.fid);
			}
			System.out.printf("%d uses random number: %d\n", p.pid, randNum);
			System.out.printf("(%d + %d) mod %d\n", randNum, this.numFrames, this.numFrames);
			int randIndex =  (randNum + this.numFrames) % this.numFrames;
			System.out.println(randIndex);
			Frame frame = this.frameLL.get(randIndex);
			this.updateEviction(frame, p);
		} else if (this.replacementAlgo.equals("fifo")) {
			Frame frame = this.frameLL.peek();
			for (Frame f: this.frameLL) {
				if (f.timeLoaded < frame.timeLoaded) {
					frame = f;
				}
			}
			this.updateEviction(frame, p);
		}
	}
	
	void printOutput() {
		System.out.printf("The machine size is %d.\n", this.machineSize);
		System.out.printf("The page size is %d.\n", this.pageSize);
		System.out.printf("The process size is %d.\n", this.processSize);
		System.out.printf("The job mix number is %d.\n", this.jobMix);
		System.out.printf("The number of references per process is %d.\n", this.numReferences);
		System.out.printf("The replacement algoritm is %s.\n", this.replacementAlgo);
		System.out.printf("The level of debugging output is %d.\n", this.debugLevel);
		
		int totalFaults = 0, totalEvictions = 0, avgResProcCount = 0;
		double totalAvgResTime = 0;
		
		for (Process p: this.processLL) {
			//System.out.println(p.avgResTime);
			//System.out.println(p.numEvictions);
			if (p.numEvictions != 0) {
				totalAvgResTime += p.avgResTime;
				p.avgResTime = p.avgResTime / p.numEvictions;
				totalEvictions += p.numEvictions;
				avgResProcCount++;
			}
			totalFaults += p.pageFaults;
			
			if (p.numEvictions == 0) {
				System.out.printf("Process %d had %d faults.\n", p.pid, p.pageFaults);
				System.out.println("With no evictions, the average residence is undefined.");
				continue;
			}
			System.out.printf("Process %d had %d faults and %f average residency.\n", p.pid,
					p.pageFaults, p.avgResTime);
		}
		
		totalAvgResTime = totalAvgResTime / totalEvictions;
		System.out.printf("The total number of faults is %d.", totalFaults);
		if (totalEvictions == 0) {
			System.out.println("\nWith no evictions, the overall average residence is undefined.");
		}
		else {
			System.out.printf("The overall average residency is %f.\n", totalAvgResTime);
		}
	}
	
	Process getNextRunning() {
		for (int i = 0; i < this.processLL.size(); i++) {
			Process tempP = this.processLL.peek();
			if (!tempP.terminated) {
				return tempP;
			}
			this.processLL.poll();
			this.processLL.add(tempP);
		}
		return null;
		
		
		/*for (int i = 0; i < this.processLL.size(); i++) {
			if (!this.processLL.get(i).terminated) {
				return this.processLL.get(i);
			}	
		}
		return null;*/
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		File randFile = new File("random-numbers.txt");
		Scanner input = new Scanner(randFile);
		
		DemandPager DPager = new DemandPager(args[0], args[1], args[2], args[3], args[4],
				args[5], args[6], input);
		DPager.init();
		final double RAND_MAX = Integer.MAX_VALUE + 1d;
		double y;
		int randNum, temp, time;
		Process running = null; 
		//running = DPager.processLL.peek();
		
		//System.out.printf("%f, %f, %f, %f\n", running.jobMixProb.A, running.jobMixProb.B, running.jobMixProb.C,
				//running.jobMixProb.remaining);
		while(!DPager.isTerminated()) {		
		//while (DPager.time != 39) {
			running = DPager.getNextRunning();
			for (int i = 0; i < DPager.quantum; i++) {
				DPager.time++;
				DPager.checkFrames(running);
				running.refsRemaining--;
//				System.out.printf("p%d -- refs remaining: %d\n", running.pid, running.refsRemaining);
				randNum = input.nextInt();
				System.out.printf("%d uses random number: %d\n", running.pid, randNum);
				y = randNum / RAND_MAX;
				//System.out.println("y: " + y);
				temp = running.reference;
				if (y < running.jobMixProb.A) {
					//System.out.println("in 1.");
					running.reference = (temp + 1 + DPager.processSize) % DPager.processSize;
				}
				else if (y < (running.jobMixProb.A + running.jobMixProb.B)) {
					//System.out.println("in 2.");
					running.reference = (temp - 5 + DPager.processSize) % DPager.processSize;
				}
				else if (y < (running.jobMixProb.A + running.jobMixProb.B + running.jobMixProb.C)) {
					//System.out.println("in 3.");
					running.reference = (temp + 4 + DPager.processSize) % DPager.processSize;
				}
				else if (y >= (running.jobMixProb.A + running.jobMixProb.B + running.jobMixProb.C)) {
					//System.out.println("in 4.");
					int tempRand = input.nextInt();
					running.reference = (tempRand + DPager.processSize) % DPager.processSize;
				}
				if (running.refsRemaining == 0) {
					running.terminated = true;
					break;
				}
			}
		
			DPager.processLL.remove(running);
			DPager.processLL.add(running);
		}
		DPager.printOutput();
	}
}
