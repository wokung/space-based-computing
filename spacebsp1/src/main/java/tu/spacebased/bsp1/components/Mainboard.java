package tu.spacebased.bsp1.components;

public class Mainboard implements Component {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final int id;
	private final int makerId;
	private final boolean defect;
	
	// Constructor
	public Mainboard(int ID, int makerID, boolean defect) {
		this.id = ID;
		this.makerId = makerID;
		this.defect = defect;
	}
	
	public int getID() {
		return id;
	}

	public int getMakerID() {
		return makerId;
	}

	public boolean isDefect() {
		return defect;
	}
}
