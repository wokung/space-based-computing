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
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.TransactionReference;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
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
 * In der Logistik werden die vollst�ndig getesteten Computer von Logistik-Mitarbeitern/-innen ausgeliefert (= als fertig markiert). 
 * Es werden nur funktionst�chtige Computer ausgeliefert. Defekte Computer werden in einem eigenen Lager gelagert.
 * @author Kung
 */
public class Logistician extends Worker implements NotificationListener {
	private static Integer id;
	// For Container in space
	private static Capi capi;
	private static ContainerReference cRef = null;
	private static ContainerReference shittyRef = null;
	private static ContainerReference sellRef = null;
    private static String containerName = "store";

    
    private static NotificationManager notification = null;
    private static ContainerReference crefPc;
	private static ContainerReference crefPcDefect;
	private static ContainerReference crefStorage;
	
    private static String shittyContainerName = "shitty";
    private static String sellContainerName = "sell";
    private static TransactionReference transaction = null;
    private static URI uri = null;
//    private static String shittyContainerName = "shitty";
//    private static String sellContainerName = "sell";
	
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
	    

		try {
			uri = new URI("xvsm://localhost:9877");
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			crefPc = App.getPcContainer(uri, capi);
			crefPcDefect = App.getPcDefectContainer(uri, capi);
			crefStorage = App.getStorageContainer(uri, capi);
	
			//create Notifications
			notification.createNotification(crefPc, logistician, Operation.WRITE);
		} catch (MzsCoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
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
//		try {
//			shittyRef = capi.lookupContainer(
//					shittyContainerName,
//					uri,
//					MzsConstants.RequestTimeout.INFINITE,
//					null);
//		} catch (MzsCoreException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		try {
//			sellRef = capi.lookupContainer(
//					sellContainerName,
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
		id = firstArg;
//		
//		Entry entry = new Entry(logistician.getClass(), KeyCoordinator.newCoordinationData(id.toString()));
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
	
		ArrayList<Computer> computerList = null;
		ArrayList<Computer> readComputerList = null;
		
    	try {
    		readComputerList = capi.read(crefPc, Arrays.asList(FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_MAX)), MzsConstants.RequestTimeout.INFINITE , null);
		} catch (MzsCoreException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
    	
		for (int i=0;i<=readComputerList.size();i++) {
			try {
				computerList = capi.take(crefPc, LabelCoordinator.newSelector("testedComputer",1), RequestTimeout.INFINITE, null);
			} catch (MzsCoreException e) {
				 System.out.println("this should never happen :S");
			}
			
			if (!computerList.isEmpty()) {
				Computer computer = computerList.get(0);

				System.out.println("DEBUG: GOT COMPUTERLIST WITH COMPUTER CREATOR: " + computer.getMakerID() + " and Tester " + computer.getTesterID() + " AND ISDEFECT:" + computer.isDefect());

				// keep track of Logistician that processed it;
				computer.setLogisticianID(id);
				
				if (computer.isDefect()) {
					TransactionReference tx = null;
					try {
						tx = capi.createTransaction(5000, uri);
					} catch (MzsCoreException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
/**
					Entry compEntry = new Entry(computer);
					
					try {
						transaction = capi.createTransaction(MzsConstants.RequestTimeout.INFINITE, uri);
						capi.write(compEntry, shittyRef, RequestTimeout.INFINITE, transaction);
						capi.commitTransaction(transaction);
*/
					Entry compEntry = new Entry(computer, LabelCoordinator.newCoordinationData("shitty"));
					
					try {
						capi.write(compEntry, crefPcDefect, RequestTimeout.INFINITE, null);

					} catch (MzsCoreException e) {
						 System.out.println("this should never happen :S");
					}
					try {
						capi.commitTransaction(tx);
					} catch (MzsCoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("DEBUG: WROTE DEFECT TRUE TO COMPUTER NR: " + computer.getMakerID());
				} else {
					TransactionReference tx = null;
					try {
						tx = capi.createTransaction(5000, uri);
					} catch (MzsCoreException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
/**	
					Entry compEntry = new Entry(computer);
					
					try {
						transaction = capi.createTransaction(MzsConstants.RequestTimeout.INFINITE, uri);
						capi.write(compEntry, sellRef, RequestTimeout.INFINITE, transaction);
						capi.commitTransaction(transaction);
*/
					Entry compEntry = new Entry(computer, LabelCoordinator.newCoordinationData("sell"));
					
					try {
						capi.write(compEntry, crefStorage, RequestTimeout.INFINITE, null);

					} catch (MzsCoreException e) {
						 System.out.println("this should never happen :S");
					}
					try {
						capi.commitTransaction(tx);
					} catch (MzsCoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("DEBUG: WROTE DEFECT FALSE TO COMPUTER NR: " + computer.getMakerID());
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
// FACTORIZE
	@Override
	public void entryOperationFinished(Notification arg0, Operation arg1,
			List<? extends Serializable> arg2) {
		try {
			ArrayList<Serializable> pc = capi.take(crefPc, Arrays.asList(LabelCoordinator.newSelector("testedComputer", 1)), MzsConstants.RequestTimeout.ZERO, null);
			Computer computer = (Computer) pc.get(0);
			// keep track of Logistician that processed it;
			computer.setLogisticianID(id);
			
			if (computer.isDefect()) {
				TransactionReference tx = null;
				try {
					tx = capi.createTransaction(5000, uri);
				} catch (MzsCoreException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
/**
				Entry compEntry = new Entry(computer);
				
				try {
					transaction = capi.createTransaction(MzsConstants.RequestTimeout.INFINITE, uri);
					capi.write(compEntry, shittyRef, RequestTimeout.INFINITE, transaction);
					capi.commitTransaction(transaction);
*/
				Entry compEntry = new Entry(computer, LabelCoordinator.newCoordinationData("shitty"));
				
				try {
					capi.write(compEntry, crefPcDefect, RequestTimeout.INFINITE, null);

				} catch (MzsCoreException e) {
					 System.out.println("this should never happen :S");
				}
				try {
					capi.commitTransaction(tx);
				} catch (MzsCoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("DEBUG: WROTE DEFECT TRUE TO COMPUTER NR: " + computer.getMakerID());
			} else {
				TransactionReference tx = null;
				try {
					tx = capi.createTransaction(5000, uri);
				} catch (MzsCoreException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
/**	
				Entry compEntry = new Entry(computer);
				
				try {
					transaction = capi.createTransaction(MzsConstants.RequestTimeout.INFINITE, uri);
					capi.write(compEntry, sellRef, RequestTimeout.INFINITE, transaction);
					capi.commitTransaction(transaction);
*/
				Entry compEntry = new Entry(computer, LabelCoordinator.newCoordinationData("sell"));
				
				try {
					capi.write(compEntry, crefStorage, RequestTimeout.INFINITE, null);

				} catch (MzsCoreException e) {
					 System.out.println("this should never happen :S");
				}
				try {
					capi.commitTransaction(tx);
				} catch (MzsCoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("DEBUG: WROTE DEFECT FALSE TO COMPUTER NR: " + computer.getMakerID());
			}
		} catch (MzsCoreException e) {
			//do nothing
		}
	}
}
