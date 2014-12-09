package sqlite.model;





public class Haslo {

	int id;
	String haslo;
	
	public Haslo() {

	}
	public Haslo(int id, String haslo) {
		this.id = id;
		this.haslo = haslo;
	}
	public Haslo( String haslo) {
		this.haslo = haslo;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getHaslo() {
		return haslo;
	}
	public void setHaslo(String haslo) {
		this.haslo = haslo;
	}

	// constructors
	
}
