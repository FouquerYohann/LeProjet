package server;

import enums.PartieState;

public class GameThread extends Thread {
	private Chrono		chronoTour;
	private Server		server;
	private PartieState	partieState	= PartieState.debut;

	public GameThread(Server server) {
		this.server = server;
		chronoTour = server.getChronoTour();
	}

	public PartieState getPartieState() {
		return partieState;
	}

	public void setPartieState(PartieState partieState) {
		this.partieState = partieState;
	}

}
