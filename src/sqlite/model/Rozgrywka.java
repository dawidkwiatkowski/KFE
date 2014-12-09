package sqlite.model;



public class Rozgrywka {

	int id;
	String data;

	// constructors
	public Rozgrywka() {
	}

	public Rozgrywka(String data) {
		this.data = data;
	}

	public Rozgrywka(int id, String data) {
		this.id = id;
		this.data=data;
	}

	// setters
	public void setId(int id) {
		this.id = id;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	

	// getters
	public long getId() {
		return this.id;
	}


	public String getData() {
		return this.data;
	}
}
