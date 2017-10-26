import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.*;

public class Process {
	int arrivalTime;
	int b;
	int d;
	int CPUBurstTime;
	int CPUTotalTime;
	int IOBurstTime;
	int inputTime;
	
	/*int remainingRunningTime;
	int remainingBlockedTime;*/
	
	int currCPUTime;
	int finishingTime;
	int turnaroundTime;
	int TotalIOTime;
	int waitingTime;
	int currWaitingTime;
	int remainingTime;
	
	String state;
	int stateCounter;
	
	boolean terminated = false;
	boolean quantumNotYetFinished;
	
	public Process() {
		
	}
	
	public Process(int arrivalTime, int b, int CPUTotalTime, int d) {
		super();
		this.arrivalTime = arrivalTime;
		this.b = b;
		this.CPUTotalTime = CPUTotalTime;
		this.d = d;
	}	
}
