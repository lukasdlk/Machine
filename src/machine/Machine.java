/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package machine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adomas
 */
public class Machine {
    
    public final static int WORD_SIZE = 4;
    public final static int BLOCK_SIZE = 10;
    public final static int BLOCKS = 70;
    public final static int USER_MEMORY_BLOCKS = 50;
    public final static int PAGE_TABLE_BLOCKS = 10;
    
    public final byte memory[] = new byte[WORD_SIZE*BLOCK_SIZE*BLOCKS];
    public final byte memoryBuffer[] = new byte[WORD_SIZE];

    public final byte PLR[] = new byte[WORD_SIZE];
    public final byte AX[] = new byte[WORD_SIZE];
    public final byte BX[] = new byte[WORD_SIZE];
    public final byte IC[] = new byte[2];
    public byte C;
    
    public byte MODE;
    public byte CH1;
    public byte CH2;
    public byte CH3;
    public byte IOI;
    public byte PI;
    public byte SI;
    public byte TI;
    
    /*
     * @param args the command line arguments
     */
    
    public int getNextAvaibleBlockIndex() {
        throw new UnsupportedOperationException("Not implemented.");
    }
    
    public void readWord(int realAddress) {
        for (int i=0; i<WORD_SIZE; i++) {
            memoryBuffer[i] = memory[realAddress*WORD_SIZE+i];
        }
    }
    
    public int realAddress(byte x, byte y) {
        int block = ((int)PLR[2])*10+(int)PLR[3];
        int a2 = memory[block+(int)x*WORD_SIZE+3];
        return a2*10+(int)y;
    }
    
    public static byte intToByte(int integer) {
        return (byte)integer;
    }
    
    public void loader(String filename) throws FileNotFoundException, Exception {
        if (filename == null)
            filename = "";
        File file = new File(filename);
        if (!file.canRead())
            throw new FileNotFoundException("Cannot read the file, doh.");
        int from = 4*BLOCK_SIZE*WORD_SIZE;
        System.out.println("From "+from);
        for(int i=0; i<40; i++) {
            memory[from+i] = intToByte(10+i);
        }
        shuffle(memory, from, from+40, 10);
        for(int i=0; i<BLOCK_SIZE; i++) {
            memory[i*WORD_SIZE+3] = memory[from+i];
        }
        BufferedReader inputStream = null;
        try {
            inputStream = new BufferedReader(new FileReader(filename));
            String l;
            l = inputStream.readLine();
            if(!l.startsWith("$WOW"))
                throw new Exception("Invalid program label "+l);
            l = inputStream.readLine();
        }
        catch (IOException ex) {
            Logger.getLogger(Machine.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
    
    public void swap(byte[] memory, int from, int to) {
        byte temp = memory[from];
        memory[from] = memory[to];
        memory[to] = temp;
    }
    
    public void shuffle(byte[] memory, int from, int to, int size) {
        Random randomGenerator = new Random(System.currentTimeMillis());
        for (int i=0; i<size; i++) {
            int random = randomGenerator.nextInt(to-from-i);
            System.out.println("random "+random);
            System.out.println("Swapping "+(from+i)+" "+(from+random+i));
            swap(memory, from+i, from+random+i);
        }
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println(intToByte(512));
        Machine machine = new Machine();
        int from = 4*BLOCK_SIZE*WORD_SIZE;
        for(int i=0; i<40; i++) {
           System.out.println(machine.memory[from+i]);
        }
        try {
            machine.loader(null);
        } catch(FileNotFoundException e) {
            System.err.println(e);
        }
        //machine.shuffle(machine.memory, from, from+40, 10);
        System.out.println("WOW");
        for(int i=0; i<40; i++) {
           System.out.println(machine.memory[from+i]);
        }
        System.out.println("WOOF");
        for(int i=0; i<10; i++) {
            for(int j=0; j<4; j++)
                System.out.print(machine.memory[i*4+j]+" ");
            System.out.println();
        }
        System.out.println();
        byte x = (byte)5;
        byte y = (byte)6;
        System.out.println(machine.realAddress(x, y));
    }
    
}
