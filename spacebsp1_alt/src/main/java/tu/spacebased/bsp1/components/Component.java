package tu.spacebased.bsp1.components;
import java.io.Serializable;
/**
 * Einzelteile haben eine eindeutige ID und man soll zu jeder Zeit ueberpruefen koennen, 
 * welche(r) Arbeiter/-in welches Teil erzeugt hat.
 * @author Kung
 */
public interface Component extends Serializable  {
	// define mandatory getter for every component of a computer
	public int getID();
	public String getMakerID();
	public boolean isDefect();

}
