/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machine;


public class CastException extends MachineException {
    private static final long serialVersionUID = 1L;

    public CastException() {
    }

    public CastException(String string) {
        super(string);
    }

    public CastException(Throwable thrwbl) {
        super(thrwbl);
    }

    public CastException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }
    
    
}
