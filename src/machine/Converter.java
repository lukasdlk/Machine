/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package machine;

/**
 *
 * @author adomas
 */
public class Converter {
    public static char intToChar(int integer) {
        if (integer<0 || 9<integer) {
            throw new ClassCastException("Invalid cast int->char ("+integer+").");
        }
        return (char)(integer+48);
    }
    public static int charToInt(char character) {
        if(character<'0' || character>'9') {
            throw new ClassCastException("Invalid cast char->int ("+character+").");
        }
        return (int)(character)-48;
    }
    public static byte intToByte(int integer) {
        if(integer>255) {
            throw new ClassCastException("Invalid cast int->byte");
        }
        return (byte)integer;
    }
    public static int byteToInt(byte b) {
        return (int)b;
    }
    public static char byteToChar(byte b) {
        return (char)b;
    }
    public static byte charToByte(char character) {
        if(character>((char)255)){
            throw new ClassCastException("Invalid cast char->byte");
        }
        return (byte)character;
    }
}
