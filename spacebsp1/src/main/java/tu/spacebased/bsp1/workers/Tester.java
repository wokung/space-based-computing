package tu.spacebased.bsp1.workers;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mozartspaces.capi3.DuplicateKeyException;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.KeyCoordinator;
import org.mozartspaces.capi3.LabelCoordinator;
import org.mozartspaces.capi3.LabelCoordinator.LabelData;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.mozartspaces.core.TransactionReference;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import tu.spacebased.bsp1.App;
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
public class Tester implements NotificationListener {
	private static Integer id;
	// For Container in space
	private static Capi capi;
	private static ContainerReference cRef = null;
    private static String containerName = "store";
    private static NotificationManager notification = null;
    private static ContainerReference crefPc;
    private static ContainerReference crefPcDefect;
    private static URI uri = null;
    
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
	    
	    
		try {
			uri = new URI("xvsm://localhost:9877");
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			crefPc = App.getPcContainer(uri, capi);
			crefPcDefect = App.getPcDefectContainer(uri, capi);
	
			//create Notifications
			notification.createNotification(crefPc, tester, Operation.WRITE);
		} catch (MzsCoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		
//		try {
//			cRef = capi.lookupContainer(
//					containerName,
//					uri,
//					MzsConstants.RequestTimeout.INFINITE,
//					null);
//		} catch (MzsCoreException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		// check if arguments are correct
//		// try to insert worker id into space, exit if not unique
//		id = firstArg;
//		
//		Entry entry = new Entry(tester.getClass(), KeyCoordinator.newCoordinationData(id.toString()));
//        
//    	try {
//			capi.write(cRef, MzsConstants.RequestTimeout.TRY_ONCE, null, entry);
//    	} catch (DuplicateKeyException dup) {
//    		System.out.println("ERROR: A Worker with this key already exists, take another one!");
//    		//TODO: cleanup!
//    		return;
//		} catch (MzsCoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		ArrayList<Computer> readComputerList = null;
		
    	try {
    		readComputerList = capi.read(crefPc, Arrays.asList(FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_MAX)), MzsConstants.RequestTimeout.INFINITE , null);
		} catch (MzsCoreException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
    	
		//TODO: Maybe we should change this from command-line arguments to query for input? 
		if (secondArg == 0){
			
			ArrayList<Computer> computerList = null;
			
			for (int i=0;i<=readComputerList.size();i++) {
				try {
					computerList = capi.take(crefPc, LabelCoordinator.newSelector("untested",1), RequestTimeout.INFINITE, null);
				} catch (MzsCoreException e) {
					 System.out.println("this should never happen :S");
				}
				if (!computerList.isEmpty()) {
					Computer computer = computerList.get(0);
					testComputer1(computer);
				} else {
					System.out.println("DEBUG: Computerlist is Empty, retrying ");
				}
			}	
		} else if (secondArg == 1) {
			ArrayList<Computer> computerList = null;
			
			for (int z=0;z<=readComputerList.size();z++) {				
				try {
					computerList = capi.take(crefPc, LabelCoordinator.newSelector("preTestedComputer",1), RequestTimeout.INFINITE, null);
				} catch (MzsCoreException e) {
					 System.out.println("this should never happen :S");
				}

				if (!computerList.isEmpty()) {
					Computer computer = computerList.get(0);
					testComputer2(computer);
				} else {
					System.out.println("DEBUG: Computerlist is Empty, retrying ");
				}
			}
		} else {
			System.out.println("You must support either 1 or 0 as command line argument");
		}
	}
	
	private static void testComputer1(final Computer computer) {
		boolean defect;
		TransactionReference tx = null;
		try {
			tx = capi.createTransaction(5000, uri);
		} catch (MzsCoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
		
		try {
			capi.write(compEntry, crefPc, RequestTimeout.INFINITE, null);
		} catch (MzsCoreException e) {
			 System.out.println("this should never happen :S");

		}
		try {
			capi.commitTransaction(tx);
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void testComputer2(final Computer computer) {
		boolean defect;
		TransactionReference tx = null;
		try {
			tx = capi.createTransaction(5000, uri);
		} catch (MzsCoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
		
		try {
			capi.write(compEntry, crefPc, RequestTimeout.INFINITE, null);
		} catch (MzsCoreException e) {
			 System.out.println("this should never happen :S");
		}
		try {
			capi.commitTransaction(tx);
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void entryOperationFinished(Notification arg0, Operation arg1, List<? extends Serializable> entries) {		
		Entry entry = (Entry) entries.get(0);
		if(((LabelData) entry.getCoordinationData().get(0)).getLabel().equals("untested")){
			try {
				ArrayList<Serializable> pcs = capi.take(crefPc, Arrays.asList(LabelCoordinator.newSelector("untested", 1)), MzsConstants.RequestTimeout.ZERO, null);
				testComputer1((Computer) pcs.get(0));
				
			} catch (MzsCoreException e) {
				//do nothing
			}
		} else if (((LabelData) entry.getCoordinationData().get(0)).getLabel().equals("preTestedComputer")){
			try {
				ArrayList<Serializable> pcs = capi.take(crefPc, Arrays.asList(LabelCoordinator.newSelector("preTestedComputer", 1)), MzsConstants.RequestTimeout.ZERO, null);
				testComputer2((Computer) pcs.get(0));
				
			} catch (MzsCoreException e) {
				//do nothing
			}
		}
	}
	// GETTER SETTER
	public Integer getId(){
		return id;
	}
}
