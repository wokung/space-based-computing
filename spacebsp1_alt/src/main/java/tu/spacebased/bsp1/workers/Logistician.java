package tu.spacebased.bsp1.workers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import tu.spacebased.bsp1.App;
import tu.spacebased.bsp1.IRemote;
import tu.spacebased.bsp1.Server;
import tu.spacebased.bsp1.components.Computer;
import tu.spacebased.bsp1.exceptions.ServerNotFoundException;

/**
 * Es soll in jeder Abteilung (Produktion, Montage, Test, Logistik) festgehalten werden, welche(r) Mitarbeiter/-in die Arbeiten ausgef�hrt hat.
 * Jede(r) Mitarbeiter/-in wird dabei durch eine eigene ID identifiziert. Alle Beteiligten k�nnen dynamisch hinzugef�gt und weggenommen werden. 
 * Jede(r) Mitarbeiter/-in stellt f�r sich ein kleines unabh�ngiges Programm dar (mit eigener main-Methode) mit der Ausnahme von Produzenten/-innen, 
 * die von anderen Programmen erzeugt und verwendet werden k�nnen.
 * 
 * In der Logistik werden die vollst�ndig getesteten Computer von Logistik-Mitarbeitern/-innen ausgeliefert (= als fertig markiert). 
 * Es werden nur funktionst�chtige Computer ausgeliefert. Defekte Computer werden in einem eigenen Lager gelagert.
 * @author Kung
 */
public class Logistician extends Worker {
	protected Logistician() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
	private static Integer id;
	private static ArrayList<String> serverNames = new ArrayList<String>();
	private static Registry reg = null;
    private static String host = "localhost";
    private static int port = 11203;
    
	public static void main(String [] args)
	{
	
		serverNames.add("Assembler");
		serverNames.add("Tester");
		serverNames.add("Logistician");
		// CONNECT ASSEMLBER WITH REGISTRY
		
		try {
			Server ns= new Logistician();
			System.out.println("versuche getRegistry: ");
			reg = LocateRegistry.getRegistry(host, port);
			System.out.println("done. versuche binding: ");
			//reg.rebind(bindingName, ns);
			reg.bind("Logistician", ns);
			System.out.println("done.");
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			
		    System.out.println("Server up. Hit enter to exit.");
			try {
				stdIn.readLine();	//warten auf enter
			} catch (IOException e) {
				System.out.println("Es ist ein Fehler aufgetreten. Die Anwendung wurde beendet.");
				UnicastRemoteObject.unexportObject(ns,true);
				return;
			}			
			UnicastRemoteObject.unexportObject(ns,true);
		} catch (RemoteException e) {
			System.out.println("Es ist ein Verbindungsproblem aufgetreten. Die Anwendung wurde beendet");
			return;
		} catch (AlreadyBoundException e) {
			System.out.println("Server wurde bereits gebunden.");
			return;
		}
//		Logistician logistician = new Logistician();
		// do some command checking
		
		int firstArg = -1;
		
		if (args.length == 1) {
		    try {
		        firstArg = Integer.parseInt(args[0]);
		    } catch (NumberFormatException e) {
		        System.err.println("Argument 1 must be an positive integer of WorkerID");
		        System.exit(1);
		    }
		} else {
			System.err.println("Usage: java Logistician 'workerId'");
			System.exit(1);
		}
		
		//TODO: get uniqueID
	
		ArrayList<Computer> computerList = null;
		
		for (;;) {
			
			//TODO: get computer
			
			if (!computerList.isEmpty()) {
				Computer computer = computerList.get(0);

				System.out.println("DEBUG: GOT COMPUTERLIST WITH COMPUTER CREATOR: " + computer.getMakerID() + " and Tester " + computer.getTesterID() + " AND ISDEFECT:" + computer.isDefect());

				// keep track of Logistician that processed it;
				computer.setLogisticianID(id);
				
				if (computer.isDefect()) {
					//TODO insert shitty
				} else {
					//TODO insert sell
				}
			} else {
				System.out.println("DEBUG: Computerlist is Empty, retrying ");

			}
		}
	}
	
	// GETTER SETTER
	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
		this.id = id;
	}

	@Override
	public String getUrl() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void printMsg(String msg) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterServer(String url, String tempURL)
			throws RemoteException, ServerNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IRemote getRemote() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
}
