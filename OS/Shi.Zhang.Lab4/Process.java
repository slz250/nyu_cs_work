
public class Process {
	int pid, processSize, refsRemaining, pageFaults, resTime, reference, numEvictions; 
	double avgResTime = 0.0;
	boolean terminated = false;
	JobMixProbability jobMixProb;
	
	Process(int pid, int processSize, int refsRemaining, int reference, int numReferences, JobMixProbability jobMixProb) {
		super();
		this.pid = pid;
		this.processSize = processSize;
		this.refsRemaining = refsRemaining;
		this.reference = reference;
		this.refsRemaining = numReferences;
		this.jobMixProb = jobMixProb;
	}
}
