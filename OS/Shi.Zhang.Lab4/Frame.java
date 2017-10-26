
public class Frame {
	int fid, pid, frameSize, upperBound, lowerBound, timeLoaded, timeEvicted;

	Frame(int fid, int pid, int frameSize, int timeLoaded) {
		super();
		this.fid = fid;
		this.pid = pid;
		this.frameSize = frameSize;
		this.timeLoaded = timeLoaded;
	}
	
	void setBounds(int num) {
		this.lowerBound = (num / frameSize) * frameSize;
		this.upperBound = lowerBound + frameSize - 1;
	}
}
