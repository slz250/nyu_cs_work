import java.util.*;

public class ReadyProcesses {
	static int quantum = 2;
	//doesn't need to be static?
	boolean verboseFlag;
	
	Stack<Process> stackLCFS = new Stack<Process>();
	
	ArrayList<Process> pList = new ArrayList<Process>();
	
	ArrayList<Process> lowestCurrWaitingTime() {
		Process lowestWTime = pList.get(0);
		for (int i = 1; i < pList.size(); i++) {
			if (pList.get(i).currWaitingTime > lowestWTime.currWaitingTime) {
				lowestWTime = pList.get(i);
			}
		}
		
		ArrayList<Process> lowestList = new ArrayList<Process>();
		for (Process p: pList) {
			if (p.currWaitingTime == lowestWTime.currWaitingTime) {
				lowestList.add(p);
			}
		}
		return lowestList;
	}
	
	ArrayList<Process> lowestCurrWaitingTimeLCFS() {
		Process lowestWTime = stackLCFS.get(0);
		for (int i = 1; i < stackLCFS.size(); i++) {
			if (stackLCFS.get(i).currWaitingTime < lowestWTime.currWaitingTime) {
				lowestWTime = stackLCFS.get(i);
			}
		}
		
		ArrayList<Process> lowestList = new ArrayList<Process>();
		for (Process p: stackLCFS) {
			if (p.currWaitingTime == lowestWTime.currWaitingTime) {
				lowestList.add(p);
			}
		}
		return lowestList;
	}
	
	ArrayList<Process> smallestArrTime(ArrayList<Process> arrList) {
		Process smallest = arrList.get(0);
		for (int i = 1; i < arrList.size(); i++) {
			if (arrList.get(i).arrivalTime < smallest.arrivalTime) {
				smallest = arrList.get(i);
			}
		}
		
		ArrayList<Process> smallestList = new ArrayList<Process>();
		for (Process p: arrList) {
			if (p.arrivalTime == smallest.arrivalTime) {
				smallestList.add(p);
			}
		}
		return smallestList;
	}
	
	Process smallestInputTime(ArrayList<Process> pList) {
		Process smallest = pList.get(0);
		for (int i = 1; i < pList.size(); i++) {
			if (pList.get(i).inputTime < smallest.inputTime) {
				smallest = pList.get(i);
			}
		}
		return smallest;
	}

	Process smallestInputTimeLCFS(Stack<Process> readyStack) {
		Process smallest = readyStack.get(0);
		for (int i = 1; i < readyStack.size(); i++) {
			if (readyStack.get(i).inputTime < smallest.inputTime) {
				smallest = readyStack.get(i);
			}
		}
		return smallest;
	}
	
	Process findNextReady() {
		Process running = null;
		if (this.pList.size() > 0) {
			if (this.pList.size() > 1) {
				ArrayList<Process> lowestCurrWaitingTimeList = this.lowestCurrWaitingTime();
				if (lowestCurrWaitingTimeList.size() > 1) {
					ArrayList<Process> smallestArrTimeList = this.smallestArrTime(lowestCurrWaitingTimeList);
					if (smallestArrTimeList.size() > 1) {
						running = this.smallestInputTime(smallestArrTimeList);
						running = this.modifyRunning(running, verboseFlag);
					}
					else {
						running = smallestArrTimeList.get(0);
						running = this.modifyRunning(running, verboseFlag);
					}
				}
				else {
					running = lowestCurrWaitingTimeList.get(0);
					running = this.modifyRunning(running, verboseFlag);
				}
			}
			else {
				running = this.pList.get(0);
				running = this.modifyRunning(running, verboseFlag);
			}
		}
		return running;
	}

	Process modifyRunning(Process running, boolean verboseFlag) {
		running.stateCounter = running.CPUBurstTime = Scheduler.randomOS(running.b);
		if (verboseFlag) {
		System.out.println("Find burst when choosing this process to run " +  Scheduler.randNumFromTxt);
		}
		running.state = "running";
		running.currCPUTime++;
		running.CPUBurstTime--;
		this.pList.remove(running);
		return running;
	}
	
	Process modifyRunningLCFS(Process running, boolean verboseFlag) {
		running.stateCounter = running.CPUBurstTime = Scheduler.randomOS(running.b);
		if (verboseFlag) {
		System.out.println("Find burst when choosing this process to run " +  Scheduler.randNumFromTxt);
		}
		running.state = "running";
		running.currCPUTime++;
		running.CPUBurstTime--;
		this.stackLCFS.remove(running);
		return running;
	}
	
	Process modifyRunningRR(Process running, boolean verboseFlag) {
		if (running.quantumNotYetFinished) {
			if (running.CPUBurstTime >= 2) {
				running.stateCounter = quantum;
			}
			else {
				running.stateCounter = running.CPUBurstTime;
			}
		}
		else if (!running.quantumNotYetFinished){
			running.CPUBurstTime = Scheduler.randomOS(running.b);
			if (running.CPUBurstTime > quantum) {
				running.stateCounter = quantum;
			}
			else {
				running.stateCounter = running.CPUBurstTime;
			}
			if (verboseFlag) {
			System.out.println("Find burst when choosing this process to run " +  Scheduler.randNumFromTxt);
			}
		}
		
		running.currWaitingTime = 0;
		running.state = "running";
		running.currCPUTime++;
		running.CPUBurstTime--;
		
		this.pList.remove(running);
		
		return running;
	}

	Process modifyRunningPSJF(Process running, boolean verboseFlag) {
			//System.out.println(running);
				if (running.CPUBurstTime >= 1) {
					running.stateCounter = running.CPUBurstTime;
				}
				else {
					running.stateCounter = running.CPUBurstTime = Scheduler.randomOS(running.b);
					if (verboseFlag) {
					System.out.println("Find burst when choosing this process to run " +  Scheduler.randNumFromTxt);
					}
				}
			
			running.currCPUTime++;
			running.CPUBurstTime--;
			
			running.currWaitingTime = 0;
			running.state = "running";
			this.pList.remove(running);
			return running;
	}
	/*
	 * Process findNextReadyRR(Process running) {
	 * //Process use;
		if (this.pList.size() > 0) {
			if (this.pList.size() > 1) {
				ArrayList<Process> lowestCurrWaitingTimeList = this.lowestCurrWaitingTime();
				//System.out.println("TEST" + TEMP.toString());
				if (lowestCurrWaitingTimeList.size() > 1) {
					ArrayList<Process> smallestArrTimeList = this.smallestArrTime(lowestCurrWaitingTimeList);
					//System.out.println("TEST1" + temp.toString());
					if (smallestArrTimeList.size() > 1) {
						running = this.smallestInputTime(smallestArrTimeList);
						running = modifyRunning(running);
					}
					else {
						running = smallestArrTimeList.get(0);
						System.out.println("running2");
						if (running.quantumNotYetFinished) {
							if (running.CPUBurstTime >= 2) {
								running.stateCounter = quantum;
								running.currCPUTime++;
								running.CPUBurstTime--;
							}
						}
						else if (!running.quantumNotYetFinished){
							running.CPUBurstTime = Scheduler.randomOS(running.b);
							if (running.CPUBurstTime > quantum) {
								running.stateCounter = quantum;
							}
							else {
								running.stateCounter = running.CPUBurstTime;
							}
							running.currCPUTime++;
							running.CPUBurstTime--;
							System.out.println("Find burst when choosing this process to run " +  Scheduler.randNumFromTxt);
						}
						running.state = "running";
						running = running;
						running.currCPUTime++;
						this.pList.remove(running);
						running = null;
					}
				}
				else {
					running = lowestCurrWaitingTimeList.get(0);
					System.out.println("running3");
					if (running.quantumNotYetFinished) {
						if (running.CPUBurstTime >= 2) {
							running.stateCounter = quantum;
							running.currCPUTime++;
							running.CPUBurstTime--;
						}
					}
					else if (!running.quantumNotYetFinished){
						running.CPUBurstTime = Scheduler.randomOS(running.b);
						if (running.CPUBurstTime > quantum) {
							running.stateCounter = quantum;
						}
						else {
							running.stateCounter = running.CPUBurstTime;
						}
						running.currCPUTime++;
						running.CPUBurstTime--;
						System.out.println("Find burst when choosing this process to run " +  Scheduler.randNumFromTxt);
					}
					running.state = "running";
					running = running;
					this.pList.remove(running);
					running = null;
				}
			}
			else {
				running = this.pList.get(0);
				System.out.println("running4");
				System.out.println(running.CPUBurstTime);
				if (running.quantumNotYetFinished) {
					if (running.CPUBurstTime >= 2) {
						running.stateCounter = quantum;
						running.currCPUTime++;
						running.CPUBurstTime--;
					}
				}
				else if (!running.quantumNotYetFinished){
					running.CPUBurstTime = Scheduler.randomOS(running.b);
					if (running.CPUBurstTime > quantum) {
						running.stateCounter = quantum;
					}
					else {
						running.stateCounter = running.CPUBurstTime;
					}
					running.currCPUTime++;
					running.CPUBurstTime--;
					System.out.println("Find burst when choosing this process to run " +  Scheduler.randNumFromTxt);
					running.state = "running";
				}
				running = running;
				this.pList.remove(running);
				running = null;
			}
		}
		//System.out.println(running.quantumNotYetFinished);
		return running;
		}	
	 */
	
	Process findNextReadyRR(ArrayList<Process> blocked) {	
		Process running = null;
		if (pList.size() > 0) {
			if (pList.size() > 1) {
				ArrayList<Process>  lowestCurrWaitingTimeList = this.lowestCurrWaitingTime();
				if (lowestCurrWaitingTimeList.size() > 1) {
					ArrayList<Process> smallestArrTimeList = this.smallestArrTime(lowestCurrWaitingTimeList);
					if (smallestArrTimeList.size() > 1) {
						running = this.smallestInputTime(smallestArrTimeList);
						running = modifyRunningRR(running, verboseFlag);
					}
					else {
						running = smallestArrTimeList.get(0);
						running = modifyRunningRR(running, verboseFlag);
					}
				}
				else {
					running = lowestCurrWaitingTimeList.get(0);
					running = modifyRunningRR(running, verboseFlag);
				}
			}
			else if (pList.size() == 1){
				running = pList.get(0);
				running = modifyRunningRR(running, verboseFlag);
			}
			else if (pList.size() == 0) {
				running = blocked.get(0);
			}
		}
		
		return running;
	}
	
	Process findSJFInReady() {
		Process shortestJob = this.pList.get(0);
		for (Process p : this.pList) {
			if (p.remainingTime < shortestJob.remainingTime) {
				shortestJob = p;
			}
		}
		return shortestJob;
	}
	
	ArrayList<Process> findSJFList() {
		ArrayList<Process> shortestJobList = new ArrayList<Process>();
		Process shortestJob = this.findSJFInReady();
		for (Process p : this.pList) {
			if (p.remainingTime == shortestJob.remainingTime) {
				shortestJobList.add(p);
			}
		}
		return shortestJobList;
	}
	
	Process findNextReadyPSJF() {
		Process running = null;
		if (this.pList.size() > 0) {
			if (this.pList.size() > 1) {
				ArrayList <Process> shortestJobsList = this.findSJFList();
				if (shortestJobsList.size() > 1) {
					ArrayList<Process> lowestCurrWaitingTimeList = this.lowestCurrWaitingTime();
					if (lowestCurrWaitingTimeList.size() > 1) {
						ArrayList<Process> smallestArrTimeList = this.smallestArrTime(lowestCurrWaitingTimeList);
						if (smallestArrTimeList.size() > 1) {
							running = this.smallestInputTime(smallestArrTimeList);
							running = this.modifyRunningPSJF(running, verboseFlag);
						}
						else {
							running = smallestArrTimeList.get(0);
							running = this.modifyRunningPSJF(running, verboseFlag);
						}
					}
					else {
						running = lowestCurrWaitingTimeList.get(0);
						running = this.modifyRunningPSJF(running, verboseFlag);
					}
				}
				else {
					running = shortestJobsList.get(0);
					running = this.modifyRunningPSJF(running, verboseFlag);
				}
			}
			else {
				running = this.pList.get(0);
				running = this.modifyRunningPSJF(running, verboseFlag);
			}
		}
		return running;
	}
	
	Process findNextReadyLCFS() {
		Process running = null;
		if (this.stackLCFS.size() > 0) {
			if (this.stackLCFS.size() > 1) {
				ArrayList<Process> lowestCurrWaitingTimeList = this.lowestCurrWaitingTimeLCFS();
				//System.out.println("lowestCurrWaitingTimeList: " + lowestCurrWaitingTimeList.toString());
				if (lowestCurrWaitingTimeList.size() > 1) {
					running = this.smallestInputTime(lowestCurrWaitingTimeList);
					running = this.modifyRunningLCFS(running, verboseFlag);
					/*ArrayList<Process> smallestArrTimeList = this.smallestArrTime(lowestCurrWaitingTimeList);
					if (smallestArrTimeList.size() > 1) {
						running = this.smallestInputTime(smallestArrTimeList);
						running = this.modifyRunning(running);
					}
					else {
						running = smallestArrTimeList.get(0);
						running = this.modifyRunning(running);
					}*/
				}
				else {
					running = lowestCurrWaitingTimeList.get(0);
					running = this.modifyRunningLCFS(running, verboseFlag);
				}
			}
			else {
				running = this.stackLCFS.get(0);
				running = this.modifyRunningLCFS(running, verboseFlag);
			}
		}
		return running;
	}
}
