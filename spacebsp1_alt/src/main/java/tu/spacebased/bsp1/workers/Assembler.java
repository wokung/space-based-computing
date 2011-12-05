package tu.spacebased.bsp1.workers;

import java.util.ArrayList;

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

	public static void main(String [] args)
	{
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
		
		//TODO: check if worker unique
		
		// check if arguments are correct
		// try to insert worker id into space, exit if not unique
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
			Mainboard mainboard = null;
			//these checks should never trigger...
			if (!mainboardList.isEmpty()) {
				mainboard = mainboardList.get(0);
			} else {
				System.out.println("EMPTY MAINBOARDLIST :(");
			}
			
			CPU cpu = null;
			if (!cpuList.isEmpty()) {
				cpu = cpuList.get(0);
			}  else {
				System.out.println("EMPTY cpuList :(");
			}
			
			
			System.out.println("-----RAMLIST has SIZE: " + ramList.size());
			
			boolean noGpu = true;
			
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
			
		}
	}
	
	// GETTER SETTER
	public Integer getId(){
		return id;
	}
}
