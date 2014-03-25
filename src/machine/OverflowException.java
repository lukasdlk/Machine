/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machine;


public class OverflowException extends MachineException {
    private static final long serialVersionUID = 1L;

    public OverflowException() {
    }

    public OverflowException(String string) {
        super(string);
    }

    public OverflowException(Throwable thrwbl) {
        super(thrwbl);
    }

    public OverflowException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }
    
    
}
