package tu.spacebased.bsp1;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.KeyCoordinator;
import org.mozartspaces.capi3.LabelCoordinator;
import org.mozartspaces.core.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {   
    	//TODO: check if transactions would be useful
        TransactionReference transaction = null;
        
		ContainerReference cRef = null;
        
        URI uri = null;
		try {
			uri = new URI("xvsm://localhost:9877");
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        String containerName = "store";
        
        MzsCore core = DefaultMzsCore.newInstance();
        Capi capi = new Capi(core);
        
        try {
        		cRef = CapiUtil.lookupOrCreateContainer(
					containerName,
					uri,
					Arrays.asList(new FifoCoordinator(), new LabelCoordinator(), new KeyCoordinator()),
					null,
					capi); //transaction could be a real TransactionRefernce if
							// we need commit-style
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Entry entry = new Entry(0, LabelCoordinator.newCoordinationData("uniqueId"));
        
    	try {
			capi.write(cRef, 0, null, entry);
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	entry = new Entry("0", LabelCoordinator.newCoordinationData("uniqueWorkerId"));
        
    	try {
			capi.write(cRef, 0, null, entry);
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        	
        //capi.commitTransaction(tx);
    }
}
