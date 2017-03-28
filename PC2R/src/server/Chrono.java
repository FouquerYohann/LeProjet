package server;

public class Chrono {
	private double time;
	
	
	public void start(){
		time=System.currentTimeMillis();
	}
	
	public int getTemps() {
		return (int)((System.currentTimeMillis()-time)/1000);
	}

}
