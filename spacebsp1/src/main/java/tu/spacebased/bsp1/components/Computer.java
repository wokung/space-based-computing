package tu.spacebased.bsp1.components;

import java.io.Serializable;
import java.util.List;

import tu.spacebased.bsp1.exceptions.BuildComputerException;

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
	
	private final CPU cpu;
	private final Mainboard mainboard;
	private final List<Ram> ram;
	private final GPU gpu;
	
	// will be assigned by Tester ??
	private boolean defect;
	
	public Computer(int makerID, Mainboard mainboard, CPU cpu, List<Ram> ram, GPU gpu) throws BuildComputerException {
		
		// Checks for mandatory components or throw exceptions
		if ((mainboard == null) || (cpu == null) || (ram.isEmpty())) {
			throw new BuildComputerException("Mandatory components missing");
		}
		
		if ((ram.size() != 1) || (ram.size() != 2) || (ram.size() != 4)) {
			throw new BuildComputerException("Ram size not possible");
		}
		
		this.makerId = makerID;
		this.mainboard = mainboard;
		this.ram = ram;
		this.cpu = cpu;
		this.gpu = gpu;
		
		// COMPUTE DEFECTS; check if components got a defect (or let the tester do it :D)
		
	}
	
	public int getMakerID() {
		return makerId;
	}
	
	public boolean isDefect() {
		return defect;
	}
	
	public void setDefect(boolean defect) {
		this.defect = defect;
	}
}
