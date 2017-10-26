import java.util.*;
import java.io.*;
import java.lang.*;

public class Scheduler {
	int numOfProcesses;
	int cycleNum;
	int numTerminated;
	ArrayList<Process> pList = new ArrayList<Process>();
	ArrayList<Process> tempPList = new ArrayList<Process>();

	public Scheduler(int numOfProcesses) {
		super();
		this.numOfProcesses = numOfProcesses;
	}

	static Scanner input1;
	static int randNumFromTxt;
	static File file1 = new File("/Users/shizhang/Documents/workspace/OSLab2/src/RandomNumbers.txt");
	static Scanner input;
	static Process running = null;
	static ArrayList<Process> blocked = new ArrayList<Process>();
	static ArrayList<Process> unstarted = new ArrayList<Process>();
	static ReadyProcesses ready = new ReadyProcesses();
	static ArrayList<Process> terminated = new ArrayList<Process>();
	static int blockedCycles = 0;
	static int quantum = 2;

	static void printProcesses(ArrayList<Process> pList) {
		int count = 0;
		for (Process process : pList) {
			System.out.println("process num " + count + " arr time: " + process.arrivalTime + " b: " + process.b
					+ " cpu time needed: " + process.CPUTotalTime + " d: " + process.d);
		}
		count++;
	}

	static void printProcesses(PriorityQueue<Process> pQueue) {
		int count = 0;
		for (Process process : pQueue) {
			System.out.println("process num " + count + " arr time: " + process.arrivalTime + " b: " + process.b
					+ " cpu time needed: " + process.CPUTotalTime + " d: " + process.d);
		}
		count++;
	}

	static int randomOS(int CPUBurstTime) {
		randNumFromTxt = input1.nextInt();
		return (1 + (randNumFromTxt % CPUBurstTime));
	}

	// POSSIBLE BUG
	Process startScheduler(Process running, ReadyProcesses ready) {
		if (ready.pList.size() > 0) {
			Process p = ready.pList.get(ready.pList.size() - 1);
			p.stateCounter = p.CPUBurstTime = randomOS(p.b);
			System.out.println("Find burst when choosing " + p.state + " process to run " + randNumFromTxt);
			p.state = "running";
			running = p;
			running.currCPUTime++;
			ready.pList.remove(p);
			p = null;
		}
		return running;
	}

	void checkUnStartedLCFS(ArrayList<Process> unstarted, ReadyProcesses ready) {
		ArrayList<Process> elementsToRemove = new ArrayList<Process>();
		if (!unstarted.isEmpty()) {
			for (Process p : unstarted) {
				if (p.arrivalTime == this.cycleNum) {
					p.state = "ready";
					ready.stackLCFS.add(p);
					elementsToRemove.add(p);
				}
			}
		}

		unstarted.removeAll(elementsToRemove);
		elementsToRemove.clear();
	}
	
	void checkUnStarted(ArrayList<Process> unstarted, ReadyProcesses ready) {
		ArrayList<Process> elementsToRemove = new ArrayList<Process>();
		if (!unstarted.isEmpty()) {
			for (Process p : unstarted) {
				if (p.arrivalTime == this.cycleNum) {
					p.state = "ready";
					ready.pList.add(p);
					elementsToRemove.add(p);
				}
			}
		}

		unstarted.removeAll(elementsToRemove);
		elementsToRemove.clear();
	}

	void checkTerminated(ArrayList<Process> terminated) {
		for (Process p : this.pList) {
			if ((p.currCPUTime == p.CPUTotalTime) && (!p.terminated)) {
				p.finishingTime = this.cycleNum;
				p.turnaroundTime = this.cycleNum - p.arrivalTime;
				p.terminated = true;
				terminated.add(p);
			}
		}
	}

	void checkingRunning(boolean verboseFlag) {
		/*if (running.stateCounter > 1) {
			if (running.currCPUTime != running.CPUTotalTime) {
				running.stateCounter--;
				running.currCPUTime++;
			} else if (running.currCPUTime == running.CPUTotalTime) {
				running.state = "terminated";
				running.stateCounter = 0;
				running = null;
			}
		} else if (running.stateCounter == 1) {
			if (running.currCPUTime == running.CPUTotalTime) {
				running.state = "terminated";
				running.stateCounter = 0;
				running = null;
			}*/
		if (running.state == "running") {
			if (running.currCPUTime == running.CPUTotalTime) {
				running.state = "terminated";
				running.stateCounter = 0;
				running = null;
			} else {
				if (running.stateCounter > 1) {
					running.stateCounter--;
					running.CPUBurstTime--;
					running.currCPUTime++;
				} else if (running.stateCounter == 1) {
			Process blockedProcess = running;
			blockedProcess.stateCounter = randomOS(blockedProcess.d);
			if (verboseFlag) {
			System.out.println("Find I/O burst when blocking a process " + randNumFromTxt);
			}
			blockedProcess.state = "blocked";
			//blockedProcess.stateCounter++;
			blocked.add(blockedProcess);
			running = null;
		}
			}
			}
	}

	void printDetails(Scheduler scheduler) {
		scheduler.cycleNum++;
		System.out.format("Before cycle%5d:", scheduler.cycleNum);
		for (int k = 0; k < scheduler.pList.size(); k++) {
			Process p = scheduler.pList.get(k);
			if (k == 0) {
				if (p.terminated == true) {
					System.out.format("%12s%3d", "teriminated", 0);
				} else
					System.out.format("%12s%3d", p.state, p.stateCounter);
			} else {
				if (p.terminated == true) {
					System.out.format("%11s%3d", "teriminated", 0);
				} else
					System.out.format("%11s%3d", p.state, p.stateCounter);
			}
		}
		System.out.format(".");
		System.out.println("");
	}

	/*void printSummary(Scheduler scheduler) {

		System.out.println("The scheduling algorithm used was First Come First Served");

		int l = 0;
		for (Process p : scheduler.pList) {
			System.out.format("Process: %d\n", l);
			System.out.format("		(A,B,C,IO) = (%d,%d,%d,%d)\n", p.arrivalTime, p.b, p.CPUTotalTime, p.d);
			System.out.format("		Finishing time: %d \n", p.finishingTime);
			System.out.format("		Turnaround time: %d\n", p.turnaroundTime);
			System.out.format("		I/O time: %d\n", p.TotalIOTime);
			System.out.format("		Waiting Time: %d\n", p.waitingTime);
			l++;
		}

		System.out.println("Summary Data:");
		System.out.println("Finishing Time: " + (scheduler.cycleNum - 1));
		int totalCPUTime = 0;
		for (Process p : scheduler.pList) {
			totalCPUTime += p.CPUTotalTime;
		}

		int totalIOTime;
		for (Process p : scheduler.pList) {
			totalIOTime = p.TotalIOTime;
		}

		System.out.println();
		System.out.println("CPU Utilization: " + (double) totalCPUTime / (double) (scheduler.cycleNum - 1));
		System.out.println("I/O Utilization: " + (double) blockedCycles);
		// (double) (scheduler.cycleNum - 1));
		System.out.println("Throughput: ");
	}*/

	/*
	 * System.out.format("The original input was: %d  ", this.numOfProcesses);
		for (Process p : this.tempPList) {
			System.out.format("%d %d %d %d  ", p.arrivalTime, p.b, p.CPUTotalTime, p.d);
		}

		System.out.format("\nThe sorted input is: %d  ", this.numOfProcesses);
		for (Process p : this.pList) {
			System.out.format("%d %d %d %d  ", p.arrivalTime, p.b, p.CPUTotalTime, p.d);
		}

		System.out.println("\n\nThis detailed printout gives the state and remaining burst for each process");
	 */
	static void initialize(Scheduler scheduler) {
		int inputTime = 1;
		for (int i = 0; i < scheduler.numOfProcesses; i++) {
			Process process = new Process(input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt());
			process.inputTime = inputTime;
			scheduler.tempPList.add(process);
			inputTime++;
		}

		System.out.format("The original input was: %d  ", scheduler.numOfProcesses);
		for (Process p : scheduler.tempPList) {
			System.out.format("%d %d %d %d  ", p.arrivalTime, p.b, p.CPUTotalTime, p.d);
		}
		
		while (!scheduler.tempPList.isEmpty()) {
			Process min = scheduler.tempPList.get(0);
			for (int i = 1; i < scheduler.tempPList.size(); i++) {
				if (scheduler.tempPList.get(i).arrivalTime < min.arrivalTime) {
					min = scheduler.tempPList.get(i);
				}
			}
			scheduler.tempPList.remove(min);
			scheduler.pList.add(min);
		}

		System.out.format("\nThe sorted input is: %d  ", scheduler.numOfProcesses);
		for (Process p : scheduler.pList) {
			System.out.format("%d %d %d %d  ", p.arrivalTime, p.b, p.CPUTotalTime, p.d);
		}

		System.out.println("\n\nThis detailed printout gives the state and remaining burst for each process.\n");
		
		for (Process p : scheduler.pList) {
			p.state = "unstarted";
			p.stateCounter = 0;
			unstarted.add(p);
		}
	}

	static File file;
	static void FCFS(String fileName, boolean verboseFlag) throws FileNotFoundException {
		//NEED TO CHANGE THIS WHEN SUBMITTING ASSIGNMENT!!!
		file = new File(fileName);
		input = new Scanner(file);
		input1 = new Scanner(file1);
		Scheduler scheduler = new Scheduler(input.nextInt());
		initialize(scheduler);
		//scheduler.printBeginning();
		ready.verboseFlag = verboseFlag; 

		scheduler.cycleNum = 0;
		int orgSize = scheduler.pList.size();

		while (terminated.size() != orgSize) {
			//System.out.println("blockedCycles: " + blockedCycles);
			//System.out.println("blocked: " + blocked.toString());
			scheduler.printCycleInfo();
			scheduler.checkTerminated(terminated);
			scheduler.checkUnStarted(unstarted, ready);

			scheduler.checkBlocked();
			
			if ((scheduler.cycleNum == 0) && (running == null)) {
				running = ready.findNextReady();
			} else if (running != null) {
				scheduler.checkingRunning(verboseFlag);
			}

			//scheduler.checkBlocked();

			if (running == null) {
				running = ready.findNextReady();
			}

			for (Process p : ready.pList) {
				p.waitingTime++;
				p.currWaitingTime++;
			}

			scheduler.cycleNum++;
		}

		scheduler.printSummary("The scheduling algorithm used was First Come First Served.");
	}

	static void LCFS(String fileName, boolean verboseFlag) throws FileNotFoundException {
		//NEED TO CHANGE THIS WHEN SUBMITTING ASSIGNMENT!!!
		file = new File(fileName);
		input = new Scanner(file);
		input1 = new Scanner(file1);
		Scheduler scheduler = new Scheduler(input.nextInt());
		initialize(scheduler); //check
		//scheduler.printBeginning(); 

		scheduler.cycleNum = 0;
		int orgSize = scheduler.pList.size(), blockedCycles = 0;
		ready.verboseFlag = verboseFlag;
		//System.out.println(scheduler.pList.toString());
	while (terminated.size() != orgSize) {
			//while (scheduler.cycleNum != 24) {
				//System.out.println(ready.stackLCFS.toString());
		
			scheduler.printCycleInfo();
			scheduler.checkTerminated(terminated);
			scheduler.checkUnStartedLCFS(unstarted, ready);

			scheduler.checkBlockedLCFS();
			if ((scheduler.cycleNum == 0) && (running == null)) {
				running = ready.findNextReadyLCFS();
			} else if (running != null) {
				scheduler.checkingRunning(verboseFlag);
			}

			//System.out.println("blocked: " + blocked.toString());

			if (running == null) {
				running = ready.findNextReadyLCFS();
			}

			for (Process p : ready.stackLCFS) {
				p.waitingTime++;
				p.currWaitingTime++;
			}

			scheduler.cycleNum++;
		}

		scheduler.printSummary("The scheduling algorithm used was LCFS.");
	}

	Process creatingRunning() {
		Process running = ready.pList.get(0);
		running.state = "running";
		running.CPUBurstTime = randomOS(running.b);

		// System.out.println(ready.pList.toString());
		// System.out.println(p.CPUBurstTime);

		if (running.CPUBurstTime > quantum) {
			running.stateCounter = quantum;
		} else {
			running.stateCounter = running.CPUBurstTime;
		}
		System.out.println("Find burst when choosing " + running.state + " process to run " + randNumFromTxt);
		return running;
	}

	void printCycleInfo() {
		System.out.format("Before cycle%5d:", this.cycleNum);
		for (int k = 0; k < this.pList.size(); k++) {
			System.out.format("%12s%3d", this.pList.get(k).state, this.pList.get(k).stateCounter);
		}
		System.out.print(".");
		System.out.println();

		/*
		 * System.out.format("Before cycle%5d:", this.cycleNum); for (int k = 0;
		 * k < this.pList.size(); k++) { Process p = this.pList.get(k); if (k ==
		 * 0) { if (p.terminated == true) { System.out.format("%12s%3d",
		 * "teriminated", 0); } else System.out.format("%12s%3d", p.state,
		 * p.stateCounter); } else { if (p.terminated == true) {
		 * System.out.format("%11s%3d", "teriminated", 0); } else
		 * System.out.format("%11s%3d", p.state, p.stateCounter); } }
		 * System.out.format("."); System.out.println("");
		 */
	}

	void printBeginning() {
		System.out.format("The original input was: %d  ", this.numOfProcesses);
		for (Process p : this.tempPList) {
			System.out.format("%d %d %d %d  ", p.arrivalTime, p.b, p.CPUTotalTime, p.d);
		}

		System.out.format("\nThe sorted input is: %d  ", this.numOfProcesses);
		for (Process p : this.pList) {
			System.out.format("%d %d %d %d  ", p.arrivalTime, p.b, p.CPUTotalTime, p.d);
		}

		System.out.println("\n\nThis detailed printout gives the state and remaining burst for each process");
	}

	void checkRunningRR(boolean verboseFlag) {
		if (running.currCPUTime == running.CPUTotalTime) {
			running.state = "terminated";
			running.stateCounter = 0;
			running = null;
		} else {
		if (running.stateCounter > 1) {
				running.stateCounter--;
				running.currCPUTime++;
				running.CPUBurstTime--;
		} else if (running.stateCounter == 1) {
			if (running.CPUBurstTime > 0) {
				running.quantumNotYetFinished = true;
				running.state = "ready";
				running.stateCounter = 0;
				ready.pList.add(running);
				running = null;
			}
			else if (running.CPUBurstTime == 0) {
				running.quantumNotYetFinished = false;
				Process blockedProcess = running;
				blockedProcess.stateCounter = randomOS(blockedProcess.d);
				if (verboseFlag) {
				System.out.println("Find I/O burst when blocking a process " + randNumFromTxt);
				}
				blockedProcess.state = "blocked";
				blocked.add(blockedProcess);
				running = null;
			}
		}
		}
			/*if (running.stateCounter > 1) {
				if (running.currCPUTime != running.CPUTotalTime) {
					running.stateCounter--;
					running.currCPUTime++;
					running.CPUBurstTime--;
				} else if (running.currCPUTime == running.CPUTotalTime) {
					running.state = "terminated";
					running.stateCounter = 0;
					running = null;
				}
			} else if (running.stateCounter == 1) {
				if (running.currCPUTime == running.CPUTotalTime) {
					running.state = "terminated";
					running.stateCounter = 0;
					running = null;
				} else if (running.CPUBurstTime > 0) {
					running.quantumNotYetFinished = true;
					running.state = "ready";
					running.stateCounter = 0;
					ready.pList.add(running);
					running = null;
				}

				// extra decrement done
				else if (running.CPUBurstTime == 0) {
					running.quantumNotYetFinished = false;
					Process blockedProcess = running;
					blockedProcess.stateCounter = randomOS(blockedProcess.d);
					if (verboseFlag) {
					System.out.println("Find I/O burst when blocking a process " + randNumFromTxt);
					}
					blockedProcess.state = "blocked";
					blockedProcess.stateCounter++;
					blocked.add(blockedProcess);

					running = null;
				}
			}*/
	}
	
	static void RR(String fileName, boolean verboseFlag) throws FileNotFoundException {
		//NEED TO CHANGE THIS WHEN SUBMITTING ASSIGNMENT!!!
		file = new File(fileName); 
		input = new Scanner(file);
		input1 = new Scanner(file1);
		Scheduler scheduler = new Scheduler(input.nextInt());
		initialize(scheduler);

		scheduler.cycleNum = 0;
		int orgSize = scheduler.pList.size();
		ready.verboseFlag = verboseFlag;
		while (terminated.size() != orgSize) {
			scheduler.printCycleInfo();
			scheduler.checkTerminated(terminated);
			scheduler.checkUnStarted(unstarted, ready);
			scheduler.checkBlocked();
			// maybe change this
			if ((scheduler.cycleNum == 0) && (running == null)) {
				running = ready.findNextReadyRR(blocked);
				/*if (ready.pList.size() > 0) {
					running = scheduler.creatingRunning();
					running.currCPUTime++;
					running.CPUBurstTime--;
					ready.pList.remove(running);
				}*/
			}
			else if (running != null) {
				scheduler.checkRunningRR(verboseFlag);
			}
			

			if (running == null) {
				running = ready.findNextReadyRR(blocked);
			}

			for (Process p : ready.pList) {
				p.waitingTime++;
				p.currWaitingTime++;
			}

			scheduler.cycleNum++;
		}

		scheduler.printSummary("The scheduling algorithm used was RR.");
	}

	void checkBlocked() {
		ArrayList<Process> elementsToRemove = new ArrayList<Process>();
		// System.out.println(blocked.toString());
		// System.out.println("blocked cycles " + blockedCycles);
		if (!blocked.isEmpty()) {
			blockedCycles++;
			for (Process p : blocked) {
				if (p.stateCounter > 1) {
					p.stateCounter--;
					p.TotalIOTime++;
				} else if (p.stateCounter == 1) {
					p.TotalIOTime++;
					Process readyProcess = p;
					readyProcess.state = "ready";
					readyProcess.stateCounter = 0;
					readyProcess.currWaitingTime = 0;
					ready.pList.add(readyProcess);
					elementsToRemove.add(p);
				}
			}
			blocked.removeAll(elementsToRemove);
		}
	}
	
	void checkBlockedLCFS() {
		ArrayList<Process> elementsToRemove = new ArrayList<Process>();
		// System.out.println(blocked.toString());
		// System.out.println("blocked cycles " + blockedCycles);
		if (!blocked.isEmpty()) {
			blockedCycles++;
			for (Process p : blocked) {
				if (p.stateCounter > 1) {
					p.stateCounter--;
					p.TotalIOTime++;
				} else if (p.stateCounter == 1) {
					p.TotalIOTime++;
					Process readyProcess = p;
					readyProcess.state = "ready";
					readyProcess.stateCounter = 0;
					readyProcess.currWaitingTime = 0;
					ready.stackLCFS.add(readyProcess);
					elementsToRemove.add(p);
				}
			}
			blocked.removeAll(elementsToRemove);
		}
	}

	void printSummary(String algoStr) {
		System.out.println(algoStr);

		int l = 0;
		for (Process p : this.pList) {
			System.out.format("Process: %d\n", l);
			System.out.format("		(A,B,C,IO) = (%d,%d,%d,%d)\n", p.arrivalTime, p.b, p.CPUTotalTime, p.d);
			System.out.format("		Finishing time: %d \n", p.finishingTime);
			System.out.format("		Turnaround time: %d\n", p.turnaroundTime);
			System.out.format("		I/O time: %d\n", p.TotalIOTime);
			System.out.format("		Waiting Time: %d\n", p.waitingTime);
			l++;
		}

		System.out.println("Summary Data:");
		System.out.println("Finishing Time: " + (this.cycleNum - 1));
		int totalCPUTime = 0, totalIOTimeScheduler = 0;
				double avgTurnaroundTime = 0, avgWaitingTime = 0;
		for (Process p : this.pList) {
			totalIOTimeScheduler += p.TotalIOTime;
			totalCPUTime += p.CPUTotalTime;
			avgTurnaroundTime += p.turnaroundTime;
			avgWaitingTime += p.waitingTime;
		}

		avgTurnaroundTime = (double) (avgTurnaroundTime / numOfProcesses);
		avgWaitingTime = (double) (avgWaitingTime / numOfProcesses);
		
		System.out.println();
		System.out.println("CPU Utilization: " + (double) totalCPUTime / (double) (this.cycleNum - 1));
		System.out.println("I/O Utilization: " + (double) blockedCycles / (double) (this.cycleNum - 1));
		System.out.println("Throughput: " + (double) (numOfProcesses * 100) / (double) (this.cycleNum - 1));
		System.out.println("Average turnaround time: " + avgTurnaroundTime);
		System.out.println("Average waiting time: "+ avgWaitingTime);
		
	}

	
	Process findNextSJF(Process shortestJob) {

		ArrayList<Process> tempSJList = new ArrayList<Process>();
		for (Process p : this.pList) {
			if (p.terminated == false) {
				tempSJList.add(p);
			}
		}

		if (tempSJList.isEmpty() == false) {
			tempSJList.remove(shortestJob);
			Process nextShortestJob;

			if (tempSJList.isEmpty()) {
				nextShortestJob = shortestJob;
			} else {
				nextShortestJob = tempSJList.get(0);
				for (Process p : tempSJList) {
					if (p.remainingTime < nextShortestJob.remainingTime) {
						nextShortestJob = p;
					}
				}
			}

			return nextShortestJob;
		} else {
			return null;
		}
	}

	Process findSJF() {
		ArrayList<Process> tempSJList = new ArrayList<Process>();
		for (Process p : this.pList) {
			if (!p.terminated) {
				tempSJList.add(p);
			}
		}

		if (!tempSJList.isEmpty()) {
			Process shortestJob = tempSJList.get(0);
			for (Process p : tempSJList) {
				if (p.remainingTime < shortestJob.remainingTime) {
					shortestJob = p;
				}
			}
			return shortestJob;
		} else {
			return null;
		}
	}

	void setRemainingTime() {
		for (Process p : this.pList) {
			p.remainingTime = p.CPUTotalTime - p.currCPUTime;
		}

		for (Process p : this.tempPList) {
			p.remainingTime = p.CPUTotalTime - p.currCPUTime;
		}
	}

	
	void checkingRunningPSJF(boolean verboseFlag) {
		if (running.state == "running") {
			if (running.currCPUTime == running.CPUTotalTime) {
				running.state = "terminated";
				running.stateCounter = 0;
				running = null;
			} else {
				if (running.stateCounter > 1) {
					running.stateCounter--;
					running.CPUBurstTime--;
					running.currCPUTime++;
				} else if (running.stateCounter == 1) {
					//running.CPUBurstTime--;
					//running.currCPUTime++;
					//not changing CPU counters b/c stateCounters is always one value above the actual time cycle
					Process blockedProcess = running;
					blockedProcess.stateCounter = randomOS(blockedProcess.d);
					if (verboseFlag) {
					System.out.println("Find I/O burst when blocking a process " + randNumFromTxt);
					}
					blockedProcess.state = "blocked";
					blocked.add(blockedProcess);
					running = null;
				}
			}
		}
	}
	

	static Process shortestJob;

	static void PSJF(String fileName, boolean verboseFlag) throws FileNotFoundException {
		//NEED TO CHANGE THIS WHEN SUBMITTING ASSIGNMENT!!!
		file = new File(fileName);
		input = new Scanner(file);
		input1 = new Scanner(file1);
		Scheduler scheduler = new Scheduler(input.nextInt());
		initialize(scheduler);
		// scheduler.printBeginning();

		ready.verboseFlag = verboseFlag;
		scheduler.cycleNum = 0;
		int orgSize = scheduler.pList.size();
		//System.out.println(scheduler.pList.toString());

		while (terminated.size() != orgSize) {
			// while (scheduler.cycleNum != 9) {
			//System.out.println("p1-- curr CPU " + scheduler.pList.get(0).currCPUTime + 
					//"stateCounter " + scheduler.pList.get(0).stateCounter);
			scheduler.printCycleInfo();
			//System.out.println(scheduler.pList.get(0).currCPUTime);
			scheduler.setRemainingTime();
			scheduler.checkTerminated(terminated);
			scheduler.checkUnStarted(unstarted, ready);
			scheduler.checkBlocked();
			shortestJob = scheduler.findSJF();

			/*if (scheduler.cycleNum == 8) {
				System.out.println(shortestJob + " " + shortestJob.remainingTime);
				System.out.println("p1: " + scheduler.pList.get(0).terminated);
			}*/
			
			if (running == null) {
				running = ready.findNextReadyPSJF();
			} else if (running != null) {
				scheduler.checkingRunningPSJF(verboseFlag);
			}

			//if (scheduler.cycleNum == 107) System.out.println(running.currCPUTime);

			shortestJob = scheduler.findSJF();
			//if (scheduler.cycleNum == 8) System.out.println(shortestJob + " " + shortestJob.remainingTime);
			
			if (shortestJob != null) {
				if (shortestJob.state == "ready") {
				if (running != null) {
					//running.currCPUTime++;
					//running.CPUBurstTime--;
					Process readyProc = running;
					readyProc.state = "ready";
					ready.pList.add(readyProc);
				}
				shortestJob.stateCounter = shortestJob.CPUBurstTime = randomOS(shortestJob.b);
				if (verboseFlag) {
				System.out.println("Find burst when choosing " + shortestJob.state + " process to run " + randNumFromTxt);
				}
				shortestJob.state = "running";
				running = shortestJob;
				running.currCPUTime++;
				running.CPUBurstTime--;
				ready.pList.remove(shortestJob);
			} else if (running == null) {
				running = ready.findNextReadyPSJF();
			}
			}
			for (Process p : ready.pList) {
				p.waitingTime++;
				p.currWaitingTime++;
			}

			// System.out.println("CPU BURST TIME" +
			// scheduler.pList.get(1).CPUBurstTime);
			scheduler.cycleNum++;
		}

		scheduler.printSummary("The scheduling algorithm used was PSJF.");
	}

	
	public static void main(String[] args) throws FileNotFoundException {
		boolean verboseFlag =  false;
		
		String algoToUse = null, fileName = null, verbose = "";
		if (args.length == 2){
			algoToUse = args[0];
			fileName = args[1];
		} else if (args.length == 3) {
			algoToUse = args[0];
			verbose = args[1];
			fileName = args[2];
		} else {
			System.out.println("Incorrect number of arguments.");
			return;
		}
		
		if (verbose.equals("--verbose")) {
			verboseFlag = true;
		}

		
		if (algoToUse.equals("FCFS")) {
			FCFS(fileName, verboseFlag);
		}
		else if (algoToUse.equals("LCFS")) {
			LCFS(fileName, verboseFlag);
		}
		else if (algoToUse.equals("RR")) {
			RR(fileName, verboseFlag);
		}
		else if (algoToUse.equals("PSJF")) {
			PSJF(fileName, verboseFlag);
		}
		else {
			System.out.println("Incorrect arguments.");
		}
		/*
		 * input = new Scanner(file); input1 = new Scanner(file1); Scheduler
		 * scheduler = new Scheduler(input.nextInt()); initialize(scheduler);
		 * //scheduler.printBeginning();
		 * 
		 * scheduler.cycleNum = 0; int orgSize = scheduler.pList.size();
		 * scheduler.setRemainingTime(); Process shortestJob;
		 * System.out.println(scheduler.pList.toString()); shortestJob =
		 * scheduler.findSJF(); Process nextShortestJob =
		 * scheduler.findNextSJF(shortestJob);
		 * System.out.println(shortestJob.toString() + " " +
		 * nextShortestJob.toString()); System.out.format("%d %d\n",
		 * shortestJob.remainingTime, scheduler.pList.get(1).remainingTime);
		 * //if (scheduler.tempPList.isEmpty()) System.out.println("empty");
		 */
	}
}
