package tu.spacebased.bsp1;

import java.io.Serializable;
import java.rmi.RemoteException;

import tu.spacebased.bsp1.exceptions.RegisterServerException;
import tu.spacebased.bsp1.exceptions.ServerNotFoundException;

public class RemoteImpl extends java.rmi.server.UnicastRemoteObject implements IRemote {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4954418765772722576L;
	private App server;
	
	   public RemoteImpl(App server) throws RemoteException {
	    	super(0);
	    	this.server = server;
	    }
	
}
