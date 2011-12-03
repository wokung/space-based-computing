package tu.spacebased.bsp1.workers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
import tu.spacebased.bsp1.components.Component;
import tu.spacebased.bsp1.components.GPU;
import tu.spacebased.bsp1.components.Mainboard;
import tu.spacebased.bsp1.components.Ram;

/**
 * Produzenten/-innen sind Akkordarbeiter/-innen. Das hei�t, sie produzieren die ihnen vorgegebene Anzahl an Teilen 
 * und haben dann ihre Arbeit erledigt. Sollen noch mehr Teile produziert werden, muss man eine(n) neue(n) Arbeiter/-in 
 * damit beauftragen. Die Produktion jedes Teiles soll eine gewisse Zeit dauern (ein Zufallswert von 1-3 Sekunden reicht).
 * Einzelteile haben eine eindeutige ID und man soll zu jeder Zeit �berpr�fen k�nnen, welche(r) Arbeiter/-in welches Teil erzeugt hat.
 * Zu Arbeitsbeginn wird jedem/r Produzenten/-in die Menge mitgeteilt.
 * 
 * Da bei der Produktion ab und zu Fehler passieren, gibt es fehlerhafte Teile. Beim Starten eines/r Produzenten/-in wird neben 
 * der Anzahl der Teile eine Fehlerrate angegeben, mit der defekte Teile produziert werden. Defekte Teile werden 
 * einfach als defekt markiert.
 * 
 * Jedes Einzelteil und jeder Computer soll durch (mindestens) ein eigenes Objekt im Space repr�sentiert sein (und nicht etwa in 
 * einer Liste, die als Ganzes in den Space geschrieben wird). Jede(r) Arbeiter/-in wird dabei als eigener Prozess gestartet. 
 * Diese Prozesse k�nnen als einfache Konsolenapplikationen implementiert werden, bei denen die ID als Argument �bergeben wird. 
 * Die Kommunikation zwischen den Mitarbeitern/-innen darf nur �ber den gemeinsamen Space erfolgen, der vor dem Starten der 
 * Mitarbeiter/-innen gestartet und evtl. initialisiert werden muss. Die Anzeigetafel aus Task 1.1 erh�lt alle Informationen nur �ber
 * den Space. Prozesse f�r Mitarbeiter/-innen sollen jederzeit dynamisch gestartet oder geschlossen werden k�nnen, ohne dass die Funktionalit�t 
 * beeintr�chtigt wird.
 * @author Kung
 */
// Implement as Thread
public class Producer implements Runnable {
	
	private enum Components {
	    CPU, GPU, MAINBOARD, RAM
	}
	
	private int quantity, makerID, errorRate;	
	private Components component;
	private Thread thread;
	private Random random = null;
	
	// For Container in space
	private Capi capi;
	private ContainerReference cRef = null;
    private String containerName = "store";
	
	/**
	 * Constructor of Producer
	 * Producer needs the number of pieces to produce. Its creator must also be saved.
	 * An errorrate is also necessary, as well as which component has to be produced.
	 * @param quantity
	 * @param makerID
	 * @param errorRate
	 * @param component
	 */
	public Producer (final int quantity, final int makerID, final int errorRate, final Components component) {
		
		this.quantity  = quantity;
		this.makerID   = makerID;
		this.errorRate = errorRate;
		this.component = component;
		
		// Let the producer start a new thread when instantiated
		synchronized (this) {
			thread = new Thread(this);
			thread.start();
		}
		
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
	}
	
	public void run() {
		
		Component newComponent = null;
		
		random = new Random();
		// create components until reached quantity
		for (int i = 0; i<quantity; i++) {
			// simulate work
			simulateWork();
			
			// create new objects
			switch (component) {
				case CPU:       newComponent = new CPU(uniqueID(), makerID, getFailure());
						        break;
						
				case GPU:       newComponent = new GPU(uniqueID(), makerID, getFailure());
						        break;
						     
				case MAINBOARD: newComponent = new Mainboard(uniqueID(), makerID, getFailure());
								break;
								
				case RAM:       newComponent = new Ram(uniqueID(), makerID, getFailure());
						        break;
						     
				default:	    thread.interrupt();
						        break;
			}
			
			// TODO Die in Task 1.1 durch die Produzenten/-innen erstellten Teile sollen in den Space geschrieben werden. 
			// Anschlie�end soll die L�sung die Einzelteile zu Computern zusammensetzen, sie testen und schlie�lich die 
			// fertigen Computer im Space speichern.
			// TODO: PUBLISH TO SPACE
			
			Entry id = new Entry(newComponent, LabelCoordinator.newCoordinationData(component.toString()));
			
			try {
				capi.write(cRef, RequestTimeout.INFINITE, null, id);
			} catch (MzsCoreException e) {
				 System.out.println("this should never happen :S");
			}
		}
	}
	
	// TODO get last id from space and increment
	private int uniqueID() {
		
		ArrayList<Integer>readEntries = null;
		
		try {
			readEntries = capi.take(cRef, KeyCoordinator.newSelector("uniqueId"), RequestTimeout.INFINITE, null);
		} catch (MzsCoreException e) {
			 System.out.println("this should never happen :S");
		}
		
		Entry id = new Entry(readEntries.get(0)+1, KeyCoordinator.newCoordinationData("uniqueId"));
		
		try {
			capi.write(cRef, RequestTimeout.INFINITE, null, id);
		} catch (MzsCoreException e) {
			 System.out.println("this should never happen :S");
		}
		
		return (readEntries.get(0));
	}
	
	/**
	 * Simulates the defect 
	 * @return
	 */
	private boolean getFailure() {
		// 100%
		int decision = random.nextInt(100);
		if(decision < errorRate*100) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Die Produktion jedes Teiles soll eine gewisse Zeit dauern (ein Zufallswert von 1-3 Sekunden reicht).
	 */
	private void simulateWork() {
		int time = (int) (Math.random()*3+1)*1000;
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			thread.interrupt();
		   // TODO: some excecption handling
			System.out.println(e.getMessage());
		}
	}
	
}
