package tu.spacebased.bsp1.workers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import org.mozartspaces.capi3.DuplicateKeyException;
import org.mozartspaces.capi3.KeyCoordinator;
import org.mozartspaces.capi3.LabelCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.MzsConstants.RequestTimeout;

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
 * Wegen m�glicherweise defekter Teile muss jeder Computer, bevor er ausgeliefert wird, ausf�hrlich getestet werden. Die Test-Mitarbeiter/-innen 
 * sind daf�r zust�ndig, fertige Computer auf ihre Vollst�ndigkeit und auf die Fehlerfreiheit ihrer Komponenten zu testen. Ein(e) 
 * Test-Mitarbeiter/-in ist immer nur f�r einen der beiden Tests zust�ndig, also entweder �berpr�ft er/sie, ob alle verpflichteten Teile vorhanden 
 * sind oder ob eines der vorhandenen Teile defekt ist. 
 * Speichern Sie bei jedem Computer, ob dieser defekt ist oder nicht.
 * @author Kung
 */
public class Tester extends Worker {
	protected Tester() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	private static Integer id;
	
	private static ArrayList<String> serverNames = new ArrayList<String>();
	private static Registry reg = null;
    private static String host = "localhost";
    private static int port = 11203;
	
    
	public static void main(String[] args)
	{
		serverNames.add("Assembler");
		serverNames.add("Tester");
		serverNames.add("Logistician");
		// CONNECT ASSEMLBER WITH REGISTRY
		
		try {
			Server ns= new Tester();
			System.out.println("versuche getRegistry: ");
			reg = LocateRegistry.getRegistry(host, port);
			System.out.println("done. versuche binding: ");
			//reg.rebind(bindingName, ns);
			reg.bind("Tester", ns);
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
		
		try {
			Tester tester = new Tester();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// do some command checking
		
		int firstArg = -1;
		int secondArg = -1;
		
		if (args.length == 2) {
		    try {
		        firstArg = Integer.parseInt(args[0]);
		        secondArg = Integer.parseInt(args[1]);
		    } catch (NumberFormatException e) {
		        System.err.println("Argument 1 must be an positive integer of WorkerID");
		        System.exit(1);
		    }
		} else {
			System.err.println("Usage: java Tester 'workerId' '0|1'");
			System.exit(1);
		}
		
		
		// check if arguments are correct
		// try to insert worker id into space, exit if not unique
		id = firstArg;
		
		ArrayList<Computer> computerList = null;
		
		//TODO: Maybe we should change this from command-line arguments to query for input? 
		if (secondArg == 0){
			for (;;) {
				
				boolean defect;
				
				if (!computerList.isEmpty()) {
					Computer computer = computerList.get(0);
					
					// keep track of Tester that processed it;
					computer.setTesterId(id);
					
					defect = false;
					
					// Checks for mandatory components or throw exceptions
					if ((computer.getMainboard() == null) || (computer.getCpu() == null) || (computer.getRam().isEmpty())) {
						defect = true;
					} else if ((computer.getRam().size() != 1) && (computer.getRam().size() != 2) && (computer.getRam().size() != 4)) {
						defect = true;
					}
						
					computer.setDefect(defect);
						
					Entry compEntry = new Entry(computer, LabelCoordinator.newCoordinationData("preTestedComputer"));
					
					// WRUITE
				} else {
					System.out.println("DEBUG: Computerlist is Empty, retrying ");
				}
			}	
			
		} else if (secondArg == 1) {
	
			for (;;) {
				
				boolean defect;
				
				//READ

				if (!computerList.isEmpty()) {
					Computer computer = computerList.get(0);
					
					// keep track of Tester that processed it;
					computer.setTesterId(id);
					
					defect = false;
					
					if(computer.getCpu().isDefect() || computer.getMainboard().isDefect()) {
						defect = true;
					}
					if(computer.getGpu() != null) {
						if (computer.getGpu().isDefect()) {
							defect = true;
						}
					}
					for (int i = 0; i < computer.getRam().size() && defect == false; i++) {
						if (computer.getRam().get(i).isDefect()) {
							defect = true;
						}
					} 
						
					computer.setDefect(defect);
						
					Entry compEntry = new Entry(computer, LabelCoordinator.newCoordinationData("testedComputer"));
					
					//WRITE
				} else {
					System.out.println("DEBUG: Computerlist is Empty, retrying ");
				}
			}
		} else {
			System.out.println("You must support either 1 or 0 as command line argument");
		}
	}
	
	// GETTER SETTER
	public Integer getId(){
		return id;
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
