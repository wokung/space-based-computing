package tu.spacebased.bsp1.workers;
/**
 * Produzenten/-innen sind Akkordarbeiter/-innen. Das heißt, sie produzieren die ihnen vorgegebene Anzahl an Teilen 
 * und haben dann ihre Arbeit erledigt. Sollen noch mehr Teile produziert werden, muss man eine(n) neue(n) Arbeiter/-in 
 * damit beauftragen. Die Produktion jedes Teiles soll eine gewisse Zeit dauern (ein Zufallswert von 1-3 Sekunden reicht).
 * Einzelteile haben eine eindeutige ID und man soll zu jeder Zeit überprüfen können, welche(r) Arbeiter/-in welches Teil erzeugt hat.
 * Zu Arbeitsbeginn wird jedem/r Produzenten/-in die Menge mitgeteilt.
 * 
 * Da bei der Produktion ab und zu Fehler passieren, gibt es fehlerhafte Teile. Beim Starten eines/r Produzenten/-in wird neben 
 * der Anzahl der Teile eine Fehlerrate angegeben, mit der defekte Teile produziert werden. Defekte Teile werden 
 * einfach als defekt markiert.
 * 
 * @author Kung
 */
// Implement as Thread
public class Producer implements Runnable {

	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	// simulate a working period between 1-3 seconds
	public void doWork() {
	}
	
}
