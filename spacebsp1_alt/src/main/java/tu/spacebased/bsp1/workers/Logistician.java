package tu.spacebased.bsp1.workers;

import java.util.ArrayList;

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
	
	public static void main(String [] args)
	{
//		Logistician logistician = new Logistician();
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
		
		//TODO: get uniqueID
	
		ArrayList<Computer> computerList = null;
		
		for (;;) {
			
			//TODO: get computer
			
			if (!computerList.isEmpty()) {
				Computer computer = computerList.get(0);

				System.out.println("DEBUG: GOT COMPUTERLIST WITH COMPUTER CREATOR: " + computer.getMakerID() + " and Tester " + computer.getTesterID() + " AND ISDEFECT:" + computer.isDefect());

				// keep track of Logistician that processed it;
				computer.setLogisticianID(id);
				
				if (computer.isDefect()) {
					//TODO insert shitty
				} else {
					//TODO insert sell
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
	public void setId(Integer id){
		this.id = id;
	}
}
