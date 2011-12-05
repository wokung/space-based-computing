package tu.spacebased.bsp1.workers;

import java.io.Serializable;
import java.rmi.RemoteException;

import tu.spacebased.bsp1.Server;

public abstract class Worker extends java.rmi.server.UnicastRemoteObject implements Server, Serializable {	
	protected Worker() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
	private static Integer id;
	
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
