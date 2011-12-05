package tu.spacebased.bsp1.workers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import org.mozartspaces.capi3.CountNotMetException;
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
import org.mozartspaces.core.MzsCoreRuntimeException;

import tu.spacebased.bsp1.components.CPU;
import tu.spacebased.bsp1.components.Computer;
import tu.spacebased.bsp1.components.GPU;
import tu.spacebased.bsp1.components.Mainboard;
import tu.spacebased.bsp1.components.Ram;
import tu.spacebased.bsp1.exceptions.BuildComputerException;
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
	private static Integer id;
	
	// For Container in space
	private static Capi capi;
	private static ContainerReference cRef = null;
    private static String containerName = "store";

	public static void main(String [] args)
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
		
		Assembler assembler = new Assembler();
		
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
		
		Entry entry = new Entry(assembler.getClass(), KeyCoordinator.newCoordinationData(id.toString()));
        
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
		
		for (;;) {
			
			Computer computer = null;
			
			ArrayList<Mainboard>mainboardList = null;
			ArrayList<CPU>cpuList = null;
			ArrayList<Ram>ramList = null;
			ArrayList<GPU>gpuList = null;
			
			//TODO: Fix so only oldest mainboard is selected
			//This one is trickier, we actually need FIFO and LabelSelector, this is not possible... query could work, but how?
			// this should be possible by adding 2 coordinators: List<Coord> xx = ... xx.add(fifo), xx.add(keycord)
			try {
				System.out.println("TRYING TO GET MAINBOARDLIST");
				mainboardList = capi.take(cRef, LabelCoordinator.newSelector(Components.MAINBOARD.toString(),1), RequestTimeout.INFINITE, null);
			} catch (MzsCoreException e) {
				 System.out.println("this should never happen :S");
			}
			Mainboard mainboard = null;
			//these checks should never trigger...
			if (!mainboardList.isEmpty()) {
				mainboard = mainboardList.get(0);
			} else {
				System.out.println("EMPTY MAINBOARDLIST :(");
			}
			
			try {
				System.out.println("TRYING TO GET CPULIST");
				cpuList = capi.take(cRef, LabelCoordinator.newSelector(Components.CPU.toString(),1), RequestTimeout.INFINITE, null);
			} catch (MzsCoreException e) {
				 System.out.println("this should never happen :S");
			}
			CPU cpu = null;
			if (!cpuList.isEmpty()) {
				cpu = cpuList.get(0);
			}  else {
				System.out.println("EMPTY cpuList :(");
			}
			
			
			//TODO: i don't like the look of this, but i don't care right now, time is ticking
			try {
				ramList = capi.take(cRef, LabelCoordinator.newSelector(Components.RAM.toString(),4), RequestTimeout.TRY_ONCE, null);
			} catch (CountNotMetException c) {
				try {
					ramList = capi.take(cRef, LabelCoordinator.newSelector(Components.RAM.toString(),2), RequestTimeout.TRY_ONCE, null);
				} catch (CountNotMetException cp) {
					try {
						ramList = capi.take(cRef, LabelCoordinator.newSelector(Components.RAM.toString(),1), RequestTimeout.INFINITE, null);
					} catch (MzsCoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (MzsCoreException e) {
					e.printStackTrace();
				}
			} catch (MzsCoreException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			System.out.println("-----RAMLIST has SIZE: " + ramList.size());
			
			boolean noGpu = true;
			
			try {
				gpuList = capi.take(cRef, LabelCoordinator.newSelector(Components.GPU.toString(),1), RequestTimeout.TRY_ONCE, null);
				noGpu = false;
			} catch (MzsCoreException e) {
				 ;
			}
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
			
			Entry compEntry = new Entry(computer, LabelCoordinator.newCoordinationData("computer"));
			
			try {
				capi.write(compEntry, cRef, RequestTimeout.INFINITE, null);
			} catch (NullPointerException n) {
				n.printStackTrace();
			} catch (MzsCoreException e) {
				 System.out.println("this should never happen :S");
			}
		}
	}
	
	// GETTER SETTER
	public Integer getId(){
		return id;
	}
}
