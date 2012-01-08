package tu.spacebased.bsp1.components;

public class Ram implements Component {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6964427006031915666L;
	private final String id;
	private final String makerId;
	private final boolean defect;
	
	// Constructor
	public Ram(String ID, String makerID, boolean defect) {
		this.id = ID;
		this.makerId = makerID;
		this.defect = defect;
	}
	
	public String getID() {
		return id;
	}

	public String getMakerID() {
		return makerId;
	}

	public boolean isDefect() {
		return defect;
	}
}
