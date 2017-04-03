package enums;

public enum PowerUp {
	Lx2(2), Lx3(3), Mx2(1), Mx3(1), Normal(1);

	public int mult;

	PowerUp(int m) {
		mult = m;
	}

	public int getMult() {
		return mult;
	}

	public int multWorld() {
		if (this == Mx2)
		    return 2;
		if (this == Mx3)
		    return 3;
		return 1;
	}

}
