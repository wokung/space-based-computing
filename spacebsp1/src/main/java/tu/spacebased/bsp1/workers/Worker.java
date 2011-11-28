package tu.spacebased.bsp1.workers;

public abstract class Worker {	
	private String id;

	public static void main(String[] args){
	}
	// simulate a working period between 1-3 seconds
	public void doWork() {
	}
	
	// GETTER SETTER
	public String getId(){
		return id;
	}
	public void setId(String id){
		this.id = id;
	}
}
