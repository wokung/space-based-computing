package tu.spacebased.bsp1.workers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
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
public class Assembler {	
	private static int id;
	
	// For Container in space
	private static Capi capi;
	private static ContainerReference cRef = null;
    private static String containerName = "store";
	
	public static void main(String [] args)
	{
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
					Arrays.asList(new FifoCoordinator(), new KeyCoordinator()),
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
				mainboardList = capi.take(cRef, LabelCoordinator.newSelector(Components.MAINBOARD.toString(),1), RequestTimeout.INFINITE, null);
			} catch (MzsCoreException e) {
				 System.out.println("this should never happen :S");
			}
			Mainboard mainboard = mainboardList.get(0);
			
			try {
				cpuList = capi.take(cRef, LabelCoordinator.newSelector(Components.CPU.toString(),1), RequestTimeout.INFINITE, null);
			} catch (MzsCoreException e) {
				 System.out.println("this should never happen :S");
			}
			CPU cpu = cpuList.get(0);
			
			try {
				ramList = capi.take(cRef, LabelCoordinator.newSelector(Components.RAM.toString(),4), RequestTimeout.ZERO, null);
				ramList = capi.take(cRef, LabelCoordinator.newSelector(Components.RAM.toString(),2), RequestTimeout.ZERO, null);
				ramList = capi.take(cRef, LabelCoordinator.newSelector(Components.RAM.toString(),1), RequestTimeout.ZERO, null);
			} catch (MzsCoreException e) {
				 ;
			}
			
			try {
				gpuList = capi.take(cRef, LabelCoordinator.newSelector(Components.GPU.toString(),1), RequestTimeout.ZERO, null);
			} catch (MzsCoreException e) {
				 ;
			}
			GPU gpu = gpuList.get(0);
			
			try {
				computer = new Computer(id, mainboard, cpu, ramList, gpu);
			} catch (BuildComputerException e1) {
				e1.printStackTrace();
			}
			
			Entry compEntry = new Entry(computer, LabelCoordinator.newCoordinationData("computer"));
			
			try {
				capi.write(compEntry, cRef, RequestTimeout.INFINITE, null);
			} catch (MzsCoreException e) {
				 System.out.println("this should never happen :S");
			}
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
	}
	// GETTER SETTER
	public int getId(){
		return id;
	}
}
