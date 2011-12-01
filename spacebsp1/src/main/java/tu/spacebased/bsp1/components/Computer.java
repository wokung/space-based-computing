package tu.spacebased.bsp1.components;

import java.io.Serializable;
import java.util.List;

/**
 * Jeder Computer besteht aus:
 * 1 x CPU
 * 1 x Mainboard
 * 1, 2 oder 4 RAM-Module
 * optional 1 x Grafikkarte
 * @author Kung
 */
public class Computer implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final int makerId;
	private final boolean defect;
	
	private final CPU cpu;
	private final Mainboard mainboard;
	private final List<Ram> ram;
	private final GPU gpu;
	
	public Computer(int makerID, Mainboard mainboard, CPU cpu, List<Ram> ram, GPU gpu) {
		// TODO: CHECKS for mandatory components
		
		this.makerId = makerID;
		this.mainboard = mainboard;
		this.ram = ram;
		this.cpu = cpu;
		this.gpu = gpu;
		
		// COMPUTE DEFECTS; check if components got a defect
		this.defect = false;
	}
	
	public int getMakerID() {
		return makerId;
	}
	
}
