package tu.spacebased.bsp1.workers;

import java.util.ArrayList;

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
    
	public static void main(String[] args)
	{
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
		
		//TODO: get unique id
		
		// check if arguments are correct
		// try to insert worker id into space, exit if not unique
		id = firstArg;
		
		ArrayList<Computer> computerList = null;
		
		//TODO: Maybe we should change this from command-line arguments to query for input? 
		if (secondArg == 0){
			for (;;) {
				
				//TODO: get computer
				
				boolean defect;
				
				if (!computerList.isEmpty()) {
					Computer computer = computerList.get(0);
					
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
						
					//TODO: put computer as pretested
					
				} else {
					System.out.println("DEBUG: Computerlist is Empty, retrying ");
				}
			}	
			
		} else if (secondArg == 1) {
	
			for (;;) {
				
				boolean defect;
				
				//TODO: get pretested Computer

				if (!computerList.isEmpty()) {
					Computer computer = computerList.get(0);
					
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
						
					//TODO put tested computer
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
