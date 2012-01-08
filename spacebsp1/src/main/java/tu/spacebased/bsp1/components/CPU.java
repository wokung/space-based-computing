package tu.spacebased.bsp1.components;

public class CPU implements Component {
	/**
	 * 
	 */
	private static final long serialVersionUID = -443861561580335004L;
		
	private final String id;
	private final String makerId;
	private final boolean defect;
	
	/**
	 * 
	 * @param ID
	 * @param makerID
	 * @param defect
	 */
	
	public CPU(String ID, String makerID, boolean defect) {
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
