package tu.spacebased.bsp1.components;

public class GPU implements Component {
	
	private static final long serialVersionUID = -1567193835116521032L;
	/**
	 * 
	 */
	private final int id;
	private final String makerId;
	private final boolean defect;
	
	// Constructor
	public GPU(int ID, String makerID, boolean defect) {
		this.id = ID;
		this.makerId = makerID;
		this.defect = defect;
	}
	
	public int getID() {
		return id;
	}

	public String getMakerID() {
		return makerId;
	}

	public boolean isDefect() {
		return defect;
	}
	
}
