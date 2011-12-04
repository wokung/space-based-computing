package tu.spacebased.bsp1.workers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.KeyCoordinator;
import org.mozartspaces.capi3.LabelCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.CapiUtil;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.MzsConstants.RequestTimeout;

import tu.spacebased.bsp1.components.Computer;

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
public class Tester {
	private static int id;
	// For Container in space
	private static Capi capi;
	private static ContainerReference cRef = null;
    private static String containerName = "store";
	
	public static void main(String[] args)
	{
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
			System.err.println("Usage: java Tester 'workerId' '0|1'");
			System.exit(1);
		}
		
		// get the last Tester from space and check if the id is already initialized
		
		MzsCore core = DefaultMzsCore.newInstance();
	    Capi capi = new Capi(core);
	    
	    URI uri = null;
		try {
			uri = new URI("xvsm://localhost:9877");
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			cRef = CapiUtil.lookupOrCreateContainer(
					containerName,
					uri,
					Arrays.asList(new FifoCoordinator()),
					null, capi);
		} catch (MzsCoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// check if arguments are correct
		try {
			int luwid;
			if (firstArg <= (luwid = getLastUniqueWorkerID())) {
				System.err.println("Please specify a WorkerId, that is not already initialized, the hightest workerId is " + luwid);
			}
		} catch (Exception e) {
			System.err.println("Couldn't resolve lastuniqueworkerId from space");
			e.printStackTrace();
			System.exit(1);
		}
		
		// arguments correct, proceeding
		id = firstArg;
		
		ArrayList<Computer> computerList = null;
		
		//TODO: Maybe we should change this from command-line arguments to query for input? 
		if (args[1].equals("0")){
			for (;;) {
				
				boolean defect;
				
				try {
					computerList = capi.take(cRef, LabelCoordinator.newSelector("computer",1), RequestTimeout.INFINITE, null);
				} catch (MzsCoreException e) {
					 System.out.println("this should never happen :S");
				}
				Computer computer = computerList.get(0);
				
				// keep track of Tester that processed it;
				computer.setTesterId(id);
				
				defect = false;
				
				// Checks for mandatory components or throw exceptions
				if ((computer.getMainboard() == null) || (computer.getCpu() == null) || (computer.getRam().isEmpty())) {
					defect = true;
				} else if ((computer.getRam().size() != 1) || (computer.getRam().size() != 2) || (computer.getRam().size() != 4)) {
					defect = true;
				}
					
				computer.setDefect(defect);
					
				Entry compEntry = new Entry(computer, LabelCoordinator.newCoordinationData("preTestedComputer"));
				
				try {
					capi.write(compEntry, cRef, RequestTimeout.INFINITE, null);
				} catch (MzsCoreException e) {
					 System.out.println("this should never happen :S");
	
				}
			}	
			
		} else if (args[1].equals("1")) {
	
			for (;;) {
				
				boolean defect;
				
				try {
					computerList = capi.take(cRef, LabelCoordinator.newSelector("preTestedComputer",1), RequestTimeout.INFINITE, null);
				} catch (MzsCoreException e) {
					 System.out.println("this should never happen :S");
				}
				Computer computer = computerList.get(0);
				
				// keep track of Tester that processed it;
				computer.setTesterId(id);
				
				defect = false;
				
				if(computer.getCpu().isDefect() || computer.getMainboard().isDefect()) {
					defect = true;
				}else if(computer.getGpu() != null) {
					if (computer.getGpu().isDefect()) {
						defect = true;
					}
				} else {
					for (int i = 0; i < computer.getRam().size() || defect == true; i++) {
						if (computer.getRam().get(i).isDefect()) {
							defect = true;
						}
					}
				}
					
				computer.setDefect(defect);
					
				Entry compEntry = new Entry(computer, LabelCoordinator.newCoordinationData("testedComputer"));
				
				try {
					capi.write(compEntry, cRef, RequestTimeout.INFINITE, null);
				} catch (MzsCoreException e) {
					 System.out.println("this should never happen :S");
	
				}
			}
		} else {
			System.out.println("You must support either 1 or 0 as command line argument");
		}
	}
	
	private static int getLastUniqueWorkerID() {
		
		ArrayList<Integer>readEntries = null;
		
		try {
			readEntries = capi.take(cRef, KeyCoordinator.newSelector("uniqueWorkerId"), RequestTimeout.INFINITE, null);
		} catch (MzsCoreException e) {
			 System.out.println("this should never happen :S");
		}
		
		return (readEntries.get(0));
		/**
		Entry id = new Entry(readEntries.get(0)+1, KeyCoordinator.newCoordinationData("uniqueWorkerId"));
		
		try {
			capi.write(cRef, RequestTimeout.INFINITE, null, id);
		} catch (MzsCoreException e) {
			 System.out.println("this should never happen :S");
		}
		
		return (readEntries.get(0));
		**/
	}
	
	// GETTER SETTER
	public int getId(){
		return id;
	}
}
