package tu.spacebased.bsp1.workers;
/**
 * Es soll in jeder Abteilung (Produktion, Montage, Test, Logistik) festgehalten werden, welche(r) Mitarbeiter/-in die Arbeiten ausgeführt hat.
 * Jede(r) Mitarbeiter/-in wird dabei durch eine eigene ID identifiziert. Alle Beteiligten können dynamisch hinzugefügt und weggenommen werden. 
 * Jede(r) Mitarbeiter/-in stellt für sich ein kleines unabhängiges Programm dar (mit eigener main-Methode) mit der Ausnahme von Produzenten/-innen, 
 * die von anderen Programmen erzeugt und verwendet werden können.
 * 
 * Um die Computer zusammenzusetzen, werden die Montage-Mitarbeiter/-innen benötigt. Sie nehmen 1 CPU, 1 Mainboard, 1, 2 oder 4 Stück RAM (je nach Verfügbarkeit)
 * und eine Grafikkarte (falls vorhanden) und fertigen daraus einen Computer. Mainboards sollen dabei in FIFO-Ordnung verarbeitet werden (ältere zuerst!),
 * damit sie nicht zu lange im Lager verstauben.
 * @author Kung
 */
public class Assembler {	
	private String id;
	
	public static void main(String [] args)
	{
	}
	
	// GETTER SETTER
	public String getId(){
		return id;
	}
	public void setId(String id){
		this.id = id;
	}
}
