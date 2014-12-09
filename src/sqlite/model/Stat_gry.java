package sqlite.model;



public class Stat_gry {

	int id;
	int punkty;

	// constructors
	public Stat_gry() {
	}

	public Stat_gry(int punkty) {
		this.punkty = punkty;
	}

	public Stat_gry(int id, int punkty) {
		this.id = id;
		this.punkty=punkty;
	}

	// setters
	public void setId(int id) {
		this.id = id;
	}

	public void setPunkty(int punkty) {
		this.punkty = punkty;
	}
	
	

	// getters
	public long getId() {
		return this.id;
	}


	public int getPunkty() {
		return this.punkty;
	}
}
