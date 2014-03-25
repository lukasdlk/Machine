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
import static machine.Converter.*;
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
    
    public final  byte memory[] = new byte[WORD_SIZE*BLOCK_SIZE*BLOCKS];
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
    public void checkInterrupt(){
        
        if(byteToInt(TI) == 0){
            System.out.println("Program has exceeded its time limit");
            MODE = 1;
            restartTimer();
            MODE = 0;
        }
        
        if (PI != 0) {
		switch (PI) {
		case 1:
			System.out.println("PROGRAM INTERRUPT! Incorrect command");
			MODE = 1;
			stopProgram();
		case 2:
			System.out.println("PROGRAM INTERRUPT! Negative result");
			MODE = 1;
			stopProgram();
		case 3:
			System.out.println("PROGRAM INTERRUPT! Division by zero");
			MODE = 1;
			stopProgram();
                case 4:
			System.out.println("PROGRAM INTERRUPT! Program overflow!");
			MODE = 1;
			stopProgram();
		}
	}
        
        if (SI != 0) {
		switch (SI) {
		case 1:
			System.out.println("PROGRAM INTERRUPT! Data input!");
			MODE = 1;
			CH1 = 1;
			MODE = 0;
			SI = 0;
			break;
		case 2:
			System.out.println("PROGRAM INTERRUPT! Data output!");
			MODE = 1;
			CH2 = 1;
			MODE = 0;
			SI = 0;
			break;
		case 3:
			System.out.println("PROGRAM INTERRUPT! Command halt!");
			MODE = 1;
			stopProgram();
		}
	}
    }
    
    void restartTimer() {

	if (byteToInt(MODE) == 1) {
		TI = intToByte(100);
		System.out.println("Supervisor=> Timer restarted successfully. ");
	}
}
    public void stopProgram(){
        System.out.println("ate");
        System.exit(0);
    }

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
    
    public byte[] segmentAdr(String s) {
        byte by[] = new byte[2];
        char ch = s.charAt(5);
        by[0] = charToByte(ch);   
        return by;
    }
    
    public String checkLength(String s){
        if (s.length() > 4){
            s = s.substring(0,4);
        }
        if (s.length() < 4){
            while (s.length() < 4){
                s= s+" ";
            }
        }
        return s;
    }
    
    public void writeToMem(String s,byte[] x){
        char ch[] = new char[WORD_SIZE];
        byte by[] = new byte[WORD_SIZE];
        int address = realAddress(by[0], by[1]);
        ch = s.toCharArray();
        for (int i = 0; i < 4; i++){
            by[i] = (Converter.charToByte(ch[i]));
        }
        for (int i= 0;i < 4;i++){
        memory[address+ i] = by[i];       
           }
        
    }
    
    public byte[] nextAdr(byte[] x){
        int a = byteToInt(x[0]);
        int b = byteToInt(x[1]);
        b+=1;
        if (b > 9){
            a+=1;
            b=0;
        }
        if(a > 9)
            throw new IllegalArgumentException("Virtual machine has no more space");
        x[0] = intToByte(a);
        x[1] = intToByte(b);
        return x ;
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
        /*skaitymas*/
        BufferedReader inputStream = null;
        try {
            inputStream = new BufferedReader(new FileReader(filename));
            String l;
            byte dat[] = new byte[2];
            byte code[] = new byte[2];
            l = inputStream.readLine();
            if(!l.startsWith("$WOW"))
                throw new Exception("Invalid program label "+l);
            l = inputStream.readLine();
            if(!l.startsWith(".NAM"))
                throw new Exception("Invalid program label "+l);
            l = inputStream.readLine();
            if ((l.startsWith(".DAT")) || (l.startsWith("$WRT")))
                throw new Exception("Invalid program laber");           
            if(l.startsWith(".DAT")){
                if (l.length() > 5)
            throw new IllegalArgumentException("Invalid amount of memory required ");
                dat = segmentAdr(l);
                l = inputStream.readLine();
            if (!l.startsWith("$DAT")){
                throw new Exception ("Invalid program label " +l);
            }
            while(!l.startsWith("$WRT") || (l.startsWith("$END"))){
                l = inputStream.readLine();
                checkLength(l);
                writeToMem(l, dat);
                dat = nextAdr(dat);
            }
            }
       
            while(!l.startsWith("$END")){
                l = inputStream.readLine();
                checkLength(l);
                writeToMem(l, code);
                dat = nextAdr(code);
            }
        }
        catch (IOException ex) {
            Logger.getLogger(Machine.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        
        /*skaitymas*/
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
