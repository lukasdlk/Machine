/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package machine;

import java.io.File;
import java.util.Random;

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
    
    public byte memory[] = new byte[WORD_SIZE*BLOCK_SIZE*BLOCKS];
    public final byte memoryBuffer[] = new byte[WORD_SIZE];
    
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
    
    public static byte intToByte(int integer) {
        return (byte)integer;
    }
    
    public void loader(String filename) throws Exception {
        /*File file = new File(filename);
        if (!file.canRead())
            throw new Exception("Cannot read the file.");
                */
        int from = 4*BLOCK_SIZE*WORD_SIZE;
        System.out.println("From "+from);
        for(int i=0; i<40; i++) {
            memory[from+i] = intToByte(10+i);
        }
        shuffle(memory, from, from+40, 10);
        for(int i=0; i<BLOCK_SIZE; i++) {
            memory[i*WORD_SIZE+1] = 9;
            memory[i*WORD_SIZE+2] = (byte)((int)(memory[from+i])/10);
            memory[i*WORD_SIZE+3] = (byte)((int)(memory[from+i])%10);
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
            int random = randomGenerator.nextInt(to-from-i+1);
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
        } catch(Exception e) {
            System.err.println(e);
        }
        //machine.shuffle(machine.memory, from, from+40, 10);
        System.out.println("WOW");
        for(int i=0; i<40; i++) {
           System.out.println(machine.memory[from+i]);
        }
        System.out.println("WOOF");
        for(int i=0; i<40; i++) {
           System.out.println(machine.memory[i]);
        }
    }
    
}
