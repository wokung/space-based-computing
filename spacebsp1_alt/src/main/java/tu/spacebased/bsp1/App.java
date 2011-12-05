package tu.spacebased.bsp1;


/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
    	
    	//TODO: check if transactions can be removed again
    	/*TODO: this structure of an app initializing all the containers
    	 * is quite ugly, but at least it works. */
    	
    	Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
            }
        });
    }
}
