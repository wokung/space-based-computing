package tu.spacebased.bsp1.workers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.mozartspaces.capi3.DuplicateKeyException;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.KeyCoordinator;
import org.mozartspaces.capi3.LabelCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.CapiUtil;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants;
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
	private static Integer id;
	// For Container in space
	private static Capi capi;
	private static ContainerReference cRef = null;
    private static String containerName = "store";
    
	public static void main(String[] args)
	{
		Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
            	try {
        			capi.take(cRef, KeyCoordinator.newSelector(id.toString()), RequestTimeout.INFINITE, null);
        		} catch (MzsCoreException e) {
        			 System.out.println("this should never happen :S");
        		}
            }
        });
		
		Tester tester = new Tester();
		
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
		
		// get the last Tester from space and check if the id is already initialized
		
		MzsCore core = DefaultMzsCore.newInstance();
	    capi = new Capi(core);
	    
	    URI uri = null;
		try {
			uri = new URI("xvsm://localhost:9877");
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			cRef = capi.lookupContainer(
					containerName,
					uri,
					MzsConstants.RequestTimeout.INFINITE,
					null);
		} catch (MzsCoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// check if arguments are correct
		// try to insert worker id into space, exit if not unique
		id = firstArg;
		
		Entry entry = new Entry(tester.getClass(), KeyCoordinator.newCoordinationData(id.toString()));
        
    	try {
			capi.write(cRef, MzsConstants.RequestTimeout.TRY_ONCE, null, entry);
    	} catch (DuplicateKeyException dup) {
    		System.out.println("ERROR: A Worker with this key already exists, take another one!");
    		//TODO: cleanup!
    		return;
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<Computer> computerList = null;
		
		//TODO: Maybe we should change this from command-line arguments to query for input? 
		if (secondArg == 0){
			for (;;) {
				
				boolean defect;
				
				try {
					computerList = capi.take(cRef, LabelCoordinator.newSelector("computer",1), RequestTimeout.INFINITE, null);
				} catch (MzsCoreException e) {
					 System.out.println("this should never happen :S");
				}
				if (!computerList.isEmpty()) {
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
				} else {
					System.out.println("DEBUG: Computerlist is Empty, retrying ");
				}
			}	
			
		} else if (secondArg == 1) {
	
			for (;;) {
				
				boolean defect;
				
				try {
					computerList = capi.take(cRef, LabelCoordinator.newSelector("preTestedComputer",1), RequestTimeout.INFINITE, null);
				} catch (MzsCoreException e) {
					 System.out.println("this should never happen :S");
				}

				if (!computerList.isEmpty()) {
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
}
