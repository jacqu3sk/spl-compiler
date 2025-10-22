package translator;
/*
 * Create new Labels for code generation
 */
public class Label {
    private static int counter = 0;
    private int labelNumber;

    public Label() 
    {
        labelNumber = counter;
    }

    public void newLabel() 
    {
        counter++;
        labelNumber = counter;
    }

    public String printLabel() 
    {
        return "L"+labelNumber;
    }
}