package tu.spacebased.bsp1.workers;

public abstract class Worker {	
	private static Integer id;
	
	public Worker() {
		;
	}
	
	public static void main(String[] args){
	}
	// simulate a working period between 1-3 seconds
	public void doWork() {
	}
	
	// GETTER SETTER
	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
		this.id = id;
	}
}
