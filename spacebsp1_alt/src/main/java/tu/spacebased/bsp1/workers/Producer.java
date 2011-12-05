package tu.spacebased.bsp1.workers;

import java.util.Random;
import java.util.UUID;

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

	public enum Components {
	    CPU, GPU, MAINBOARD, RAM
	}
	
	private int quantity;
	private double errorRate;	
	private String makerID;
	private Components component;
	private Thread thread;
	private Random random = null;
	
	/**
	 * Constructor of Producer
	 * Producer needs the number of pieces to produce. Its creator must also be saved.
	 * An errorrate is also necessary, as well as which component has to be produced.
	 * @param quantity
	 * @param makerID
	 * @param errorRate
	 * @param component
	 */
	public Producer (final int quantity, final String makerID, final double errorRate, final Components component) {
		
		this.quantity  = quantity;
		this.makerID   = makerID;
		this.errorRate = errorRate;
		this.component = component;
		
		// Let the producer start a new thread when instantiated
		synchronized (this) {
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public void run() {
		
		// create new uuid for this worker
		UUID uid= UUID.randomUUID();
		this.setId(uid.toString());
		
		Component newComponent = null;
		
		random = new Random();
		// create components until reached quantity
		for (int i = 0; i<quantity; i++) {
			// simulate work
			simulateWork();
						
			// create new objects
			switch (component) {
				case CPU:
					newComponent = new CPU(uniqueID(), makerID, getFailure());
					break;
						
				case GPU:
					newComponent = new GPU(uniqueID(), makerID, getFailure());
					break;
						     
				case MAINBOARD:
					newComponent = new Mainboard(uniqueID(), makerID, getFailure());
					break;
								
				case RAM:
					newComponent = new Ram(uniqueID(), makerID, getFailure());
					break;
						     
				default:
					thread.interrupt();
					break;
			}

			System.out.println("i inserted a"+component.toString());
			// WRITE TO REGISTRY
			
		}
		return;
	}
	
	// TODO get last id from space and increment
	private int uniqueID() {
		return (0);
	}
	
	/**
	 * Simulates the defect 
	 * @return
	 */
	private boolean getFailure() {
		// 100%
		int decision = random.nextInt(100);
		
		if(decision < (int) (errorRate*100)) {
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
	// GETTER SETTER
	public String getMakerId(){
		return makerID;
	}
	public void setId(String id){
		this.makerID = id;
	}
}
