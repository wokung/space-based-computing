package tu.spacebased.bsp1.workers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.mozartspaces.capi3.AnyCoordinator;
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

import ch.qos.logback.core.db.dialect.MsSQLDialect;

import tu.spacebased.bsp1.components.Computer;

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
	private static Integer id;
	// For Container in space
	private static Capi capi;
	private static ContainerReference cRef = null;
	private static ContainerReference shittyRef = null;
	private static ContainerReference sellRef = null;
    private static String containerName = "store";
    private static String shittyContainerName = "shitty";
    private static String sellContainerName = "sell";
	
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
		
		Logistician logistician = new Logistician();
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
		
		try {
			shittyRef = capi.lookupContainer(
					shittyContainerName,
					uri,
					MzsConstants.RequestTimeout.INFINITE,
					null);
		} catch (MzsCoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			sellRef = capi.lookupContainer(
					sellContainerName,
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
		
		Entry entry = new Entry(logistician.getClass(), KeyCoordinator.newCoordinationData(id.toString()));
        
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
	
		for (;;) {
			try {
				computerList = capi.take(cRef, LabelCoordinator.newSelector("testedComputer",1), RequestTimeout.INFINITE, null);
			} catch (MzsCoreException e) {
				 System.out.println("this should never happen :S");
			}
			Computer computer = computerList.get(0);
			
			// keep track of Logistician that processed it;
			computer.setLogisticianID(id);
			
			if (computer.isDefect()) {
				Entry compEntry = new Entry(computer);
				
				try {
					capi.write(compEntry, shittyRef, RequestTimeout.INFINITE, null);
				} catch (MzsCoreException e) {
					 System.out.println("this should never happen :S");
				}
			} else {
				Entry compEntry = new Entry(computer);
				
				try {
					capi.write(compEntry, sellRef, RequestTimeout.INFINITE, null);
				} catch (MzsCoreException e) {
					 System.out.println("this should never happen :S");
				}
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
}
