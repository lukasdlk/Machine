/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package machine;

import java.io.Console;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import static machine.Utils.*;

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
    public final byte memory[] = new byte[WORD_SIZE * BLOCK_SIZE * BLOCKS];
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
    
    public char X, Y;
    public int channelNumber;
    public Console console = System.console();
    public byte channelDeviceBuffer[] = new byte[40];

    /*
     * @param args the command line arguments
     */
    public int getNextAvaibleBlockIndex() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public void readWord(int realAddress) {
        for (int i = 0; i < WORD_SIZE; i++) {
            memoryBuffer[i] = memory[realAddress * WORD_SIZE + i];
        }
    }

    public int realAddress(char x, char y) throws CastException {
        int block = byteToInt(PLR[2]) * 10 + byteToInt(PLR[3]);
        int a2 = memory[block + charToInt(x) * WORD_SIZE + 3];
        return a2 * 10 + charToInt(y);
    }

    public void loader(String filename) throws FileNotFoundException, Exception {
        /*
         if (filename == null)
         filename = "";
         File file = new File(filename);
         if (!file.canRead())
         throw new FileNotFoundException("Cannot read the file, doh.");
         */
        int from = 4 * BLOCK_SIZE * WORD_SIZE;
        System.out.println("From " + from);
        for (int i = 0; i < 40; i++) {
            memory[from + i] = intToByte(10 + i);
        }
        shuffle(memory, from, from + 40, 10);
        for (int i = 0; i < BLOCK_SIZE; i++) {
            memory[i * WORD_SIZE + 3] = memory[from + i];
        }
        /*
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
         */
    }

    public void swap(byte[] memory, int from, int to) {
        byte temp = memory[from];
        memory[from] = memory[to];
        memory[to] = temp;
    }

    public void shuffle(byte[] memory, int from, int to, int size) {
        Random randomGenerator = new Random(System.currentTimeMillis());
        for (int i = 0; i < size; i++) {
            int random = randomGenerator.nextInt(to - from - i);
            System.out.println("random " + random);
            System.out.println("Swapping " + (from + i) + " " + (from + random + i));
            swap(memory, from + i, from + random + i);
        }
    }

    public static void main(String[] args) throws CastException {
        // TODO code application logic here
        Machine machine = new Machine();
        int from = 4 * BLOCK_SIZE * WORD_SIZE;
        for (int i = 0; i < 40; i++) {
            System.out.println(machine.memory[from + i]);
        }
        try {
            machine.loader(null);
        } catch (FileNotFoundException e) {
            System.err.println(e);
        } catch (Exception ex) {
            Logger.getLogger(Machine.class.getName()).log(Level.SEVERE, null, ex);
        }
        //machine.shuffle(machine.memory, from, from+40, 10);
        System.out.println("WOW");
        for (int i = 0; i < 40; i++) {
            System.out.println(machine.memory[from + i]);
        }
        System.out.println("WOOF");
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(machine.memory[i * 4 + j] + " ");
            }
            System.out.println();
        }
        System.out.println();
        char x = '5';
        char y = '9';
        //System.out.println(machine.realAddress(x, y));
        machine.commandLA(x, y);
        System.out.println("PI = " + machine.PI);
        machine.C = intToByte(1);
        machine.resetC();
        machine.setSF(true);
        System.out.println("C = " + machine.C);
        System.out.println(byteToInt(intToByte(255)));
    }

    public void resetC() {
        C = (byte) (C ^ C);
    }

    public void setSF(boolean val) {
        C = (byte) (val? (C | 0b00000100): (C^0b00000100));
    }

    public void setOF(boolean val) {
        C = (byte) (val? (C | 0b00000010): (C^0b00000010));
    }

    public void setZF(boolean val) {
        C = (byte) (val? (C | 0b00000001): (C^0b00000001));
    }

    public boolean getSF() {
        return ((C & 0b00000100) > 0) ? true : false;
    }

    public boolean getOF() {
        return ((C & 0b00000100) > 0) ? true : false;
    }

    public boolean getZF() {
        return ((C & 0b00000100) > 0) ? true : false;
    }
    public void incIC() throws CastException {
        int x=byteToInt(IC[0]);
        int y=byteToInt(IC[1]);
        int IC_int = x*10+y;
        IC_int++;
        IC[0] = intToByte(IC_int/10);
        IC[1] = intToByte(IC_int%10);
    }

    public void commandLA(char x, char y) throws CastException {
        resetC();
        incIC();
        try {
            int address = realAddress(x, y);
            for (int i = 0; i < 4; i++) {
                AX[i] = memory[address + i];
            }
            int AX_int = wordToInt(AX, 0);
            if (AX_int == 0) {
                setZF(true);
            } else if (AX_int < 0) {
                setSF(true);
            }
        } catch (ClassCastException e) {
            PI = intToByte(1);
        }
    }

    public void commandLB(char x, char y) throws CastException {
        resetC();
        incIC();
        try {
            int address = realAddress(x, y);
            for (int i = 0; i < 4; i++) {
                BX[i] = memory[address + i];
            }
            int BX_int = wordToInt(BX, 0);
            if (BX_int == 0) {
                setZF(true);
            } else if (BX_int < 0) {
                setSF(true);
            }
        } catch (ClassCastException e) {
            PI = intToByte(1);
        }
    }

    public void commandLAfB() throws CastException {
        resetC();
        incIC();
        if (byteToInt(BX[0]) + byteToInt(BX[1]) > 0) {
            PI = intToByte(1);
            return;
        }
        try {
            int address = realAddress(byteToChar(BX[2]), byteToChar(BX[3]));
            for (int i = 0; i < 4; i++) {
                AX[i] = memory[address + i];
            }
            int AX_int = wordToInt(AX, 0);
            if (AX_int == 0) {
                setZF(true);
            } else if (AX_int < 0) {
                setSF(true);
            }
        } catch (ClassCastException e) {
            PI = intToByte(1);
        }
    }

    public void commandLBfA() throws CastException {
        resetC();
        incIC();
        if (AX[0] + AX[1] > 0) {
            PI = intToByte(1);
            return;
        }
        try {
            int address = realAddress(byteToChar(AX[2]), byteToChar(AX[3]));
            for (int i = 0; i < 4; i++) {
                BX[i] = memory[address + i];
            }
            int BX_int = wordToInt(AX, 0);
            if (BX_int == 0) {
                setZF(true);
            } else if (BX_int < 0) {
                setSF(true);
            }
        } catch (ClassCastException e) {
            PI = intToByte(1);
        }
    }

    public void commandSA(char x, char y) throws CastException {
        resetC();
        incIC();
        try {
            int address = realAddress(x, y);
            for (int i = 0; i < 4; i++) {
                memory[address + i] = AX[i];
            }
            int AX_int = wordToInt(AX, 0);
            if (AX_int == 0) {
                setZF(true);
            } else if (AX_int < 0) {
                setSF(true);
            }
        } catch (ClassCastException e) {
            PI = intToByte(1);
        }
    }

    public void commandSB(char x, char y) throws CastException {
        resetC();
        incIC();
        try {
            int address = realAddress(x, y);
            for (int i = 0; i < 4; i++) {
                memory[address + i] = BX[i];
            }
            int BX_int = wordToInt(BX, 0);
            if (BX_int == 0) {
                setZF(true);
            } else if (BX_int < 0) {
                setSF(true);
            }
        } catch (ClassCastException e) {
            PI = intToByte(1);
        }
    }

    public void commandCOPA() throws CastException {
        resetC();
        incIC();
        for (int i = 0; i < 4; i++) {
            AX[i] = BX[i];
        }
        int AX_int = wordToInt(AX, 0);
        if (AX_int == 0) {
            setZF(true);
        } else if (AX_int < 0) {
            setSF(true);
        }
    }

    public void commandCOPB() throws CastException {
        resetC();
        incIC();
        for (int i = 0; i < 4; i++) {
            BX[i] = AX[i];
        }
        int BX_int = wordToInt(AX, 0);
        if (BX_int == 0) {
            setZF(true);
        } else if (BX_int < 0) {
            setSF(true);
        }
    }

    public void commandAW(char x) throws CastException {
        resetC();
        incIC();
        for (int i = 0; i < 3; i++) {
            BX[i] = intToByte(0);
        }
        BX[3] = charToByte(x);
        int BX_int = wordToInt(AX, 0);
        if (BX_int == 0) {
            setZF(true);
        } else if (BX_int < 0) {
            setSF(true);
        }
    }

    public void commandAA(char x, char y) throws CastException {
        resetC();
        incIC();
        try {
            int address = realAddress(x, y);
            int AX_int = wordToInt(AX, 0), memory_int = wordToInt(memory, address);
            try {
                AX_int = addWithOverflow(AX_int, memory_int);
            } catch (OverflowException e) {
                setOF(true);
                PI = intToByte(4);
            }
            if (AX_int == 0) {
                setZF(true);
            } else if (AX_int < 0) {
                setSF(true);
            }
            intToWord(AX_int, AX, 0);
        } catch (ClassCastException e) {
            PI = intToByte(1);
        }
    }

    public void commandAB(char x, char y) throws CastException {
        resetC();
        incIC();
        try {
            int address = realAddress(x, y);
            int BX_int = wordToInt(BX, 0), memory_int = wordToInt(memory, address);
            try {
                BX_int = addWithOverflow(BX_int, memory_int);
            } catch (OverflowException e) {
                setOF(true);
                PI = intToByte(4);
            }
            if (BX_int == 0) {
                setZF(true);
            } else if (BX_int < 0) {
                setSF(true);
            }
            intToWord(BX_int, BX, 0);
        } catch (ClassCastException e) {
            PI = intToByte(1);
        }
    }

    public void commandBA(char x, char y) throws CastException {
        resetC();
        incIC();
        try {
            int address = realAddress(x, y);
            int AX_int = wordToInt(AX, 0), memory_int = wordToInt(memory, address);
            try {
                AX_int = subWithOverflow(AX_int, memory_int);
            } catch (OverflowException e) {
                setOF(true);
                PI = intToByte(4);
            }
            if (AX_int == 0) {
                setZF(true);
            } else if (AX_int < 0) {
                setSF(true);
            }
            intToWord(AX_int, AX, 0);
        } catch (ClassCastException e) {
            PI = intToByte(1);
        }
    }

    public void commandBB(char x, char y) throws CastException {
        resetC();
        incIC();
        try {
            int address = realAddress(x, y);
            int BX_int = wordToInt(BX, 0), memory_int = wordToInt(memory, address);
            try {
                BX_int = subWithOverflow(BX_int, memory_int);
            } catch (OverflowException e) {
                setOF(true);
                PI = intToByte(4);
            }
            if (BX_int == 0) {
                setZF(true);
            } else if (BX_int < 0) {
                setSF(true);
            }
            intToWord(BX_int, BX, 0);
        } catch (ClassCastException e) {
            PI = intToByte(1);
        }
    }

    public void commandMA(char x, char y) throws CastException {
        resetC();
        incIC();
        try {
            int address = realAddress(x, y);
            int AX_int = wordToInt(AX, 0), memory_int = wordToInt(memory, address);
            try {
                AX_int = mulWithOverflow(AX_int, memory_int);
            } catch (OverflowException e) {
                setOF(true);
                PI = intToByte(4);
            }
            if (AX_int == 0) {
                setZF(true);
            } else if (AX_int < 0) {
                setSF(true);
            }
            intToWord(AX_int, AX, 0);
        } catch (ClassCastException e) {
            PI = intToByte(1);
        }
    }

    public void commandMB(char x, char y) throws CastException {
        resetC();
        incIC();
        try {
            int address = realAddress(x, y);
            int BX_int = wordToInt(BX, 0), memory_int = wordToInt(memory, address);
            try {
                BX_int = mulWithOverflow(BX_int, memory_int);
            } catch (OverflowException e) {
                setOF(true);
                PI = intToByte(4);
            }
            if (BX_int == 0) {
                setZF(true);
            } else if (BX_int < 0) {
                setSF(true);
            }
            intToWord(BX_int, BX, 0);
        } catch (ClassCastException e) {
            PI = intToByte(1);
        }
    }

    public void commandDA(char x, char y) throws CastException {
        resetC();
        incIC();
        try {
            int address = realAddress(x, y);
            int AX_int = wordToInt(BX, 0), BX_int, memory_int = wordToInt(memory, address);
            if(memory_int == 0) {
                PI = intToByte(3);
                return;
            }
            BX_int = AX_int % memory_int;
            AX_int /= memory_int;
            if (AX_int == 0) {
                setZF(true);
            } else if (AX_int < 0) {
                setSF(true);
            }
            intToWord(AX_int, AX, 0);
            intToWord(BX_int, BX, 0);
        } catch (ClassCastException e) {
            PI = intToByte(1);
        }
    }

    public void commandDECA(char x, char y) throws CastException {
        resetC();
        incIC();
        int AX_int = wordToInt(AX, 0);
        try {
            AX_int = subWithOverflow(AX_int, 1);
        } catch (OverflowException e) {
            setOF(true);
        }
        if (AX_int == 0) {
            setZF(true);
        } else if (AX_int < 0) {
            setSF(true);
        }
        intToWord(AX_int, AX, 0);
    }

    public void commandDECB(char x, char y) throws CastException {
        resetC();
        incIC();
        int BX_int = wordToInt(BX, 0);
        try {
            BX_int = subWithOverflow(BX_int, 1);
        } catch (OverflowException e) {
            setOF(true);
        }
        if (BX_int == 0) {
            setZF(true);
        } else if (BX_int < 0) {
            setSF(true);
        }
        intToWord(BX_int, BX, 0);
    }

    public void commandINCA(char x, char y) throws CastException {
        resetC();
        incIC();
        int AX_int = wordToInt(AX, 0);
        try {
            AX_int = addWithOverflow(AX_int, 1);
        } catch (OverflowException e) {
            setOF(true);
        }
        if (AX_int == 0) {
            setZF(true);
        } else if (AX_int < 0) {
            setSF(true);
        }
        intToWord(AX_int, AX, 0);
    }

    public void commandINCB(char x, char y) throws CastException {
        resetC();
        incIC();
        int BX_int = wordToInt(BX, 0);
        try {
            BX_int = addWithOverflow(BX_int, 1);
        } catch (OverflowException e) {
            setOF(true);
        }
        if (BX_int == 0) {
            setZF(true);
        } else if (BX_int < 0) {
            setSF(true);
        }
        intToWord(BX_int, BX, 0);
    }

    public void commandCA(char x, char y) throws CastException {
        resetC();
        incIC();
        try {
            int address = realAddress(x, y);
            int AX_int = wordToInt(AX, 0), memory_int = wordToInt(memory, address);
            try {
                AX_int = subWithOverflow(AX_int, memory_int);
            } catch (OverflowException e) {
                setOF(true);
                PI = intToByte(4);
            }
            if (AX_int == 0) {
                setZF(true);
            } else if (AX_int < 0) {
                setSF(true);
            }
        } catch (ClassCastException e) {
            PI = intToByte(1);
        }
    }

    public void commandCB(char x, char y) throws CastException {
        resetC();
        incIC();
        try {
            int address = realAddress(x, y);
            int BX_int = wordToInt(BX, 0), memory_int = wordToInt(memory, address);
            try {
                BX_int = subWithOverflow(BX_int, memory_int);
            } catch (OverflowException e) {
                setOF(true);
                PI = intToByte(4);
            }
            if (BX_int == 0) {
                setZF(true);
            } else if (BX_int < 0) {
                setSF(true);
            }
        } catch (ClassCastException e) {
            PI = intToByte(1);
        }
    }
    public void commandIP(char x, char y) throws CastException {
        try {
            int address = realAddress(x, y);
            X = x;
            Y = y;
            SI = intToByte(1);
        }
        catch(ClassCastException e) {
            PI = intToByte(1);
        }
    }
    public void commandOP(char x, char y) throws CastException {
        try {
            int address = realAddress(x, y);
            X = x;
            Y = y;
            SI = intToByte(2);
        }
        catch(ClassCastException e) {
            PI = intToByte(1);
        }
    }
    public void commandJP(char x, char y) throws CastException {
        IC[0] = charToByte(x);
        IC[1] = charToByte(y);
    }

    public void commandJE(char x, char y) throws CastException {
        incIC();
        if (getZF()) {
            commandJP(x, y);
        }
    }

    public void commandJL(char x, char y) throws CastException {
        incIC();
        if (!getZF() && (getSF() == getOF())) {
            commandJP(x, y);
        }
    }

    public void commandJG(char x, char y) throws CastException {
        incIC();
        if (!getZF() && (getSF() != getOF())) {
            commandJP(x, y);
        }
    }

    public void commandHALT() throws CastException {
        incIC();
        SI = intToByte(3);
    }

    public void commandGEC(char x) throws CastException {
        incIC();
        if (x < '1' || '3' < x) {
            PI = intToByte(1);
            return;
        }
        intToWord(0, BX, 0);
        if (x == '1' && getSF()) {
            BX[3] = intToByte(1);
        }
        if (x == '2' && getOF()) {
            BX[3] = intToByte(1);
        }
        if (x == '3' && getZF()) {
            BX[3] = intToByte(1);
        }
    }

    public void commandSEC(char x) throws CastException {
        incIC();
        if (x < '1' || '3' < x) {
            PI = intToByte(1);
            return;
        }
        boolean val = false;
        int BX_int = wordToInt(BX, 0);
        if (BX_int == 1) {
            val = true;
        }
        if (x == '1') {
            setSF(val);
        }
        if (x == '2' && getOF()) {
            setOF(val);
        }
        if (x == '3') {
            setZF(val);
        }
    }
    public void commandGEIC() throws CastException {
        intToWord(0, BX, 0);
        BX[2] = IC[0];
        BX[3] = IC[1];
        incIC();
    }
    public void commandSEIC() {
        IC[0] = BX[2];
        IC[1] = BX[3];
    }
    public void StartIO() throws CastException, BufferOverflowException {
        if(channelNumber==1) {
           CH1 = intToByte(1);
           String input = console.readLine("Plz enter somthing WOW: ");
           int len = input.length();
           if (len>40)
               len = 40;
           for(int i=0; i<len; i++) {
               try {
                   channelDeviceBuffer[i] = charToByte(input.charAt(i));
               } 
               catch (ClassCastException e) {
                   channelDeviceBuffer[i] = charToByte('?');
               }
           }
           if(len<40) {
               channelDeviceBuffer[len] = charToByte('#');
           }
           int startPoz = charToInt(X)*10+charToInt(Y);
           outerloopCH1:
           for(int i=0; i<BLOCK_SIZE; i++) {
               try {
                    char x = intToChar((startPoz+i)/10);
                    char y = intToChar((startPoz+i)%10);
                    int address = realAddress(x, y);
                    for(int j=0; j<4; j++) {
                        if(channelDeviceBuffer[i*WORD_SIZE+j] == '#') {
                            break outerloopCH1;
                        }
                        memory[address+j] = channelDeviceBuffer[i*WORD_SIZE+j];
                    }
               }
               catch(ClassCastException e) {
                   throw new BufferOverflowException("Do not write a poem.");
               }
           }
           CH1 = intToByte(0);
           IOI = intToByte(byteToInt(IOI)+1);
        }
        if(channelNumber==2) {
           CH2 = intToByte(1);
           int startPoz = charToInt(X)*10+charToInt(Y);
           outerloopCH2:
           for(int i=0; i<10; i++) {
               try {
                   char x = intToChar((startPoz+i)/10);
                   char y = intToChar((startPoz+i)%10);
                   int address = realAddress(x, y);
                   for(int j=0; j<4; j++) {
                        channelDeviceBuffer[i*WORD_SIZE+j] = memory[address+j];
                        if(memory[address+j] == '#') {
                            break outerloopCH2;
                        }
                    }
               }
               catch(ClassCastException e) {
                   throw new BufferOverflowException("Do not write a poem.");
               }
           }
           CH2 = intToByte(0);
           IOI = intToByte(byteToInt(IOI)+2);
        }
        if(channelNumber==3) {
            CH3 = intToByte(1);
            CH3 = intToByte(0);
            IOI = intToByte(byteToInt(IOI)+4);
        }
    }
}
