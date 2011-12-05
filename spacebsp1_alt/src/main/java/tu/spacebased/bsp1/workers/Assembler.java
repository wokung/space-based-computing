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

import tu.spacebased.bsp1.IRemote;
import tu.spacebased.bsp1.Server;

import tu.spacebased.bsp1.App;
import tu.spacebased.bsp1.components.CPU;
import tu.spacebased.bsp1.components.Computer;
import tu.spacebased.bsp1.components.GPU;
import tu.spacebased.bsp1.components.Mainboard;
import tu.spacebased.bsp1.components.Ram;
import tu.spacebased.bsp1.exceptions.BuildComputerException;
import tu.spacebased.bsp1.exceptions.ServerNotFoundException;
import tu.spacebased.bsp1.workers.Producer.Components;


/**
 * Es soll in jeder Abteilung (Produktion, Montage, Test, Logistik) festgehalten werden, welche(r) Mitarbeiter/-in die Arbeiten ausgef�hrt hat.
 * Jede(r) Mitarbeiter/-in wird dabei durch eine eigene ID identifiziert. Alle Beteiligten k�nnen dynamisch hinzugef�gt und weggenommen werden. 
 * Jede(r) Mitarbeiter/-in stellt f�r sich ein kleines unabh�ngiges Programm dar (mit eigener main-Methode) mit der Ausnahme von Produzenten/-innen, 
 * die von anderen Programmen erzeugt und verwendet werden k�nnen.
 * 
 * Um die Computer zusammenzusetzen, werden die Montage-Mitarbeiter/-innen ben�tigt. Sie nehmen 1 CPU, 1 Mainboard, 1, 2 oder 4 St�ck RAM (je nach Verf�gbarkeit)
 * und eine Grafikkarte (falls vorhanden) und fertigen daraus einen Computer. Mainboards sollen dabei in FIFO-Ordnung verarbeitet werden (�ltere zuerst!),
 * damit sie nicht zu lange im Lager verstauben.
 * @author Kung
 */
public class Assembler extends Worker {	
	protected Assembler() throws RemoteException {
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
			Server ns= new Assembler();
			System.out.println("versuche getRegistry: ");
			reg = LocateRegistry.getRegistry(host, port);
			System.out.println("done. versuche binding: ");
			//reg.rebind(bindingName, ns);
			reg.bind("Assembler", ns);
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
			Assembler assembler = new Assembler();
		} catch (RemoteException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		// do some command checking
		System.out.println("Assembler started");
		
		int firstArg = -1;
		
		if (args.length == 1) {
		    try {
		        firstArg = Integer.parseInt(args[0]);
		    } catch (NumberFormatException e) {
		        System.err.println("Argument 1 must be an positive integer of WorkerID");
		        System.exit(1);
		    }
		} else {
			System.err.println("Usage: java Assembler 'workerId'");
			System.exit(1);
		}
		
		//TODO: check if worker unique
		
		// check if arguments are correct
		// try to insert worker id into space, exit if not unique
		id = firstArg;
		
		for (;;) {
			
			Computer computer = null;
			
			ArrayList<Mainboard>mainboardList = null;
			ArrayList<CPU>cpuList = null;
			ArrayList<Ram>ramList = null;
			ArrayList<GPU>gpuList = null;
			
			//TODO: Fix so only oldest mainboard is selected
			//This one is trickier, we actually need FIFO and LabelSelector, this is not possible... query could work, but how?
			// this should be possible by adding 2 coordinators: List<Coord> xx = ... xx.add(fifo), xx.add(keycord)
			Mainboard mainboard = null;
			//these checks should never trigger...
			if (!mainboardList.isEmpty()) {
				mainboard = mainboardList.get(0);
			} else {
				System.out.println("EMPTY MAINBOARDLIST :(");
			}
			
			CPU cpu = null;
			if (!cpuList.isEmpty()) {
				cpu = cpuList.get(0);
			}  else {
				System.out.println("EMPTY cpuList :(");
			}
			
			
			System.out.println("-----RAMLIST has SIZE: " + ramList.size());
			
			boolean noGpu = true;
			
			GPU gpu = null;
			if (!noGpu) {
				gpu = gpuList.get(0);
				
			}
			try {
				if (gpu != null) {
					System.out.println("-----BUILDING COMPUTER WITH: " + id + " " + mainboard.toString() + " " + cpu.toString() + " " + ramList.toString() + " " + gpu);
				} else {
					System.out.println("-----BUILDING COMPUTER WITH: " + id + " " + mainboard.toString() + " " + cpu.toString() + " " + ramList.toString() + " and NO GPU");
				}
				computer = new Computer(id, mainboard, cpu, ramList, gpu);
			} catch (NullPointerException n) {
				n.printStackTrace();
			} catch (BuildComputerException e1) {
				e1.printStackTrace();
			}
			
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
