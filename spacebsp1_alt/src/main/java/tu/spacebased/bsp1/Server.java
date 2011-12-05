package tu.spacebased.bsp1;

import java.rmi.RemoteException;

import tu.spacebased.bsp1.exceptions.RegisterServerException;
import tu.spacebased.bsp1.exceptions.ServerNotFoundException;

public interface Server extends java.rmi.Remote 
{
	String getUrl() throws java.rmi.RemoteException; 
	void printMsg(String msg) throws java.rmi.RemoteException;  
	void unregisterServer(String url, String tempURL) throws java.rmi.RemoteException, ServerNotFoundException;
	IRemote getRemote() throws RemoteException;
	
}
