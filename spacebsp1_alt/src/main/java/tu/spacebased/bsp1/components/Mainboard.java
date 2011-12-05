package tu.spacebased.bsp1.components;

public class Mainboard implements Component {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4939899355425205767L;
	
	private final int id;
	private final String makerId;
	private final boolean defect;
	
	// Constructor
	public Mainboard(int ID, String makerID, boolean defect) {
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
