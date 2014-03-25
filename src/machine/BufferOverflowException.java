/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machine;


public class BufferOverflowException extends MachineException {
    private static final long serialVersionUID = 1L;

    public BufferOverflowException() {
    }

    public BufferOverflowException(String string) {
        super(string);
    }

    public BufferOverflowException(Throwable thrwbl) {
        super(thrwbl);
    }

    public BufferOverflowException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }
    
    
}
