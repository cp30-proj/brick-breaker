/*
 *  © 2019 by Patrick Matthew Chan
 *  File: UCMI.java
 *  Package: uart_try
 *  Description: The UCMI class.
 */
package brick_breaker;
import com.fazecast.jSerialComm.*;
import java.lang.reflect.Field;//optional,for toString shortcut
import java.util.InputMismatchException;
import java.util.Scanner;
import javax.swing.JOptionPane;
/* @author Patrick Matthew J. Chan */
public class UCMI { //UART Controller Manager Interface
    //fields
    //static Scanner sin = new Scanner(System.in);        //port choosing software ui is responsibility of this class now.
    boolean isPortConnected = false;
    SerialPort comPort = null;
    int curConVal = 0;
    //UART frame read
    static final int BYTE_BUFFER_SIZE = 20; 
    byte[] byteBuffer = new byte[BYTE_BUFFER_SIZE];   //NOTE: data is stored starting from "1", not "0".
    int curReadByteIdx = -1;  //byte no., based on frame format. -1 means header not detected
    static final byte HEADER_BYTE = (byte) 0b11110000;
    static final byte TRAILER_BYTE = (byte) 0b00001111;
    static final int UART_READ_SIZE = 1; //in # of bytes. //how much data in UART buffer before triggering Java event.  //default was 100;
    static final int CM_BAUD_RATE = 115200;
    PlayerState[] p = new PlayerState[5]; //[] starts with "1", not "0";
    int[] lastReqByte = new int[5]; // corresponds to PlayerState indexing
    
    
    //TODO next: convert this to be able to read from CM data frame

    //methods
    public void initPlayers(){
        for(int i=0;i<5;i++){
            p[i] = new PlayerState();
        }
    }
    
    public void clearByteBuffer(){
        for (int i = 0; i < byteBuffer.length; i++) {
            byteBuffer[i] = 0;
        }
    }
    
    public String to8bitBinary(int input){  //lets just pad if 0's to make it always 8-bit
        String out = Integer.toBinaryString(input);
        if(out.length()>8){
            out = out.substring(out.length()-8);
        } else if(out.length() < 8) {
            StringBuilder temp = new StringBuilder();
            while(out.length() + temp.length() < 8){
                temp.append('0');
            }
            temp.append(out);
            out = temp.toString();
        }
        return out;   //replace 8 with n to print last n bits.
    }

    //write
    void send(String AsciiInput){
        if(isPortConnected){
            char[] temp = AsciiInput.toCharArray();
                //assumes input is within ascii, and not unicode
            int size = temp.length;
            byte[] byteArr = new byte[size];
            for (int i = 0; i < byteArr.length; i++) {
                byteArr[i] = (byte) temp[i];                
            }
            comPort.writeBytes(byteArr, size);
        } else {
            System.err.println("please connect comPort first. [use init()]");
            System.out.println("isPortConnected = " + isPortConnected);
            System.out.println("comPort.isOpen() = " + comPort.isOpen());
        }
    }
    
    //Request Player  [Get Type 1]
    void ReqPlayer(int pNum){
        if(pNum > 0 && pNum < 5){
            send(Integer.toString(pNum));
        } else {
            System.err.println("ReqPlayer, invalid pNum:"+pNum);
        }
    }
    
    
    
    //Request Player Byte  [Get Type 2]
    void ReqPlayerByte(int pNum, int byteNum){
        if(pNum>0 && pNum<9 && byteNum>=1 && byteNum<=15){
            if(pNum<=4){
                pNum += 4;  //to fit protocol
            }
            //as of now, pagbigyan if user sends 5,6,7,8 here.
            //now, for memory
            lastReqByte[(pNum%4)+1] = byteNum;
            send(Integer.toString(pNum)+Integer.toString(byteNum));
        } else {
            System.err.print("ReqPlayerByte, invalid params:");
            System.err.print(" pNum = " + pNum);
            System.err.print(",byteNum = " + byteNum);
        }
    }
    
    
    
    //returns if connected to COM port (controller) or not
    boolean init() {
        initPlayers();
        clearByteBuffer();
        //-----init controller
        //SerialPort comPort = null;    //turned into global variable
        SerialPort ports[] = null;
        int choice = -1;
        try {
            ports = SerialPort.getCommPorts();
            StringBuilder diaMsg = new StringBuilder(9999);
            if(ports.length <= 0){
                //curConVal=512;
                isPortConnected = false;
                System.err.println("no controller detected.");
                return false;
            }
            diaMsg.append("Note: ConMgr baud rate is " + CM_BAUD_RATE + "\n\n");
            for(int i=0;i<ports.length;i++){
                comPort = ports[i];
                comPort.setBaudRate(CM_BAUD_RATE);
                String itermsg = "Choice#"+i+":\nPort desc:  " + comPort.getPortDescription() + "\n" + comPort.getDescriptivePortName() + "\n" + comPort.getSystemPortName() + /*"\n" + comPort.getBaudRate() +*/ "\n\n";
                System.out.println(itermsg);
                diaMsg.append(itermsg); 
            }
            comPort=null;
            diaMsg.append("-------------------\nchoose port.[Enter Choice#]");
            while(choice==-1){
                System.out.println("choose port.");
                try{
                    //choice = sin.nextInt();
                    String temp = JOptionPane.showInputDialog(null,diaMsg);
                    if(temp == null){
                        if(JOptionPane.showConfirmDialog(null, "Don't connect to COM port?","Quit COM port selection?",JOptionPane.YES_NO_OPTION) == 
                                JOptionPane.YES_OPTION){
                            return false;
                        } else {
                            choice = -1;
                        }
                    }
                    System.out.println("temp = " + temp);
                    choice = Integer.parseInt(temp);
                    System.out.println("choice = " + choice);
                    if(choice <0 || choice>=ports.length){
                        System.out.println("invalid input. again.\n");
                        choice = -1;
                    }
                } catch(InputMismatchException | NumberFormatException ex){
                    System.out.println("invalid input. again.\n");
                    choice = -1;
                }
            }
            comPort = ports[choice];//if none connected--array out of bounds.
            comPort.openPort();

            SerialPortPacketListener listener = new SerialPortPacketListener() {
                @Override
                public int getPacketSize() {
                    return UART_READ_SIZE;
                }

                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
                }

                @Override
                public void serialEvent(SerialPortEvent spe) {
                    //get bytes of received data
                    byte[] newData = spe.getReceivedData();
                    //System.out.println("Received data of size: " + newData.length);//size 100
                    
                    //StringBuilder temp = new StringBuilder("");
                    for (int i = 0; i < newData.length; ++i) {
                        //for each byte of received data:
                        System.out.println("newData["+i+"] = " + newData[i] + " (" + to8bitBinary(newData[i]) + ")");   //debug ish for UART rcving
                        if(curReadByteIdx == -1){
                            if(newData[i] == HEADER_BYTE){
                                curReadByteIdx = 1;
                            }
                        } else if(curReadByteIdx >= BYTE_BUFFER_SIZE){
                            //byteBuffer cannot accomodate, meaning probably error
                            //meaning header byte rcvd, but trailer not rcvd even after
                            //more than anticipated no. of bytes
                            curReadByteIdx = -1;
                        } else {
                            //end reached
                            if(newData[i] == TRAILER_BYTE){
                                // perform checksum operation:
                                byte sum = (byte)0;
                                //byteBuffer[curReadByteIdx] = "trailer byte" tho not assigned
                                System.out.println("#==============================#");       //debug
                                for (int j = 1; j < curReadByteIdx-1; j++) {//byteBuffer stats from 1.
                                    sum += byteBuffer[j];  
                                    System.out.println("#  byteBuffer["+j+"] = " + to8bitBinary(byteBuffer[j]) + "    #"); //debug
                                    //System.out.println("current sum = " + to8bitBinary(sum)); //debug
                                }
                                System.out.println("#==============================#");       //debug
                                //confirm with checksum
                                //System.out.println("sum = " + to8bitBinary(sum)); //debug
                                //System.out.println("(byteBuffer["+(curReadByteIdx-1)+"])checksum value = " + to8bitBinary(byteBuffer[curReadByteIdx-1])); //debug
                                if(byteBuffer[curReadByteIdx-1] == sum){
                                    //System.out.println("Checksum confirmed"); //debug
                                    //save data
                                    int pNo = byteBuffer[2] >> 4;
                                    //System.out.println("pNo = " + pNo);   //debug
                                    //System.out.println("p[1]:\n"+p[1].toString());
                                    //System.out.println("yo. It works.");
                                    switch(pNo){
                                        case 1:
                                        case 2:
                                        case 3:
                                        case 4:
                                            //System.out.println("yo. case works.");    //debug
                                            //type 1
                                            if (byteBuffer[1] == 1){
                                                p[pNo].type1(byteBuffer);
                                            } else if (byteBuffer[1] == 2){
                                                p[pNo].type2(lastReqByte[pNo], byteBuffer);
                                            } else {
                                                System.err.println("Invalid message type: "+pNo);
                                            }
                                            System.out.println("--------------------------------");       //debug
                                            System.out.println("p["+pNo+"]: \n" + p[pNo].toString());   //debug
                                            System.out.println("\n--------------------------------\n");   //debug
                                            break;
                                        default:
                                            System.err.println("Invalid player number: "+pNo);
                                            break;
                                    }
                                }
                                curReadByteIdx = -1;
                            } else {
                                byteBuffer[curReadByteIdx]=newData[i];
                                curReadByteIdx++;
                            }
                        }
                    }
                }
            };
            comPort.addDataListener(listener);
            isPortConnected = true;//true;

            System.out.println("Port desc:  " + comPort.getPortDescription() + "\n" + comPort.getDescriptivePortName() + "\n" + comPort.getSystemPortName() + "\n" + comPort.getBaudRate() + "\n\n\n\n");
            return true;
        } catch (Exception ex) {
            System.out.println("Exception occurred while connecting to Serial port: " + ex.toString());
            ex.printStackTrace();
            return false;
        }
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="toString shortcut">
    //++toString shortcut
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Field f: getClass().getDeclaredFields()) {
            try {
            result
            .append(f.getName())
            .append(" : ")
            .append(f.get(this))
            .append(System.getProperty("line.separator"));
            }
            catch (IllegalStateException ise) {
                result
                .append(f.getName())
                .append(" : ")
                .append("[cannot retrieve value]")
                .append(System.getProperty("line.separator"));
            }
            // nope
            catch (IllegalAccessException iae) {}
        }
        return result.toString();
    }
    // </editor-fold>
}
//Alt+Insert for constructors,Getters&Setters [after declaring fields]
