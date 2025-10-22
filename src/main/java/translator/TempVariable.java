package translator;
/*
 * Create new temp variables for code generation
 */
public class TempVariable {
    private static int counter = 0;
    private int tempNumber;

    public TempVariable () 
    {
        counter = 0;
        tempNumber = counter;
    }

    public void newTemp() 
    {
        counter++;
        tempNumber = counter;
    }

    public String printTemp() 
    {
        return "t"+tempNumber;
    }
}