/*
 *  © 2019 by Patrick Matthew Chan
 *  File: PlayerState.java
 *  Package: uart_try
 *  Description: The PlayerState class.
 */
package brick_breaker;
import java.lang.reflect.Field;//optional,for toString shortcut
/* @author Patrick Matthew J. Chan [LBYCP24-EQ1]*/
public class PlayerState {
    //fields
    //int pNo = 0;
    int conType = 0;
    int lasX = 128;   //left analog stick
    int lasY = 128;     //128  <-- neutral position (0 to 255)
    int rasX = 128;   //right analog stick
    int rasY = 128;
    boolean fbA = false;
    boolean fbB = false;
    boolean fbX = false;
    boolean fbY = false;
    boolean dpD = false;
    boolean dpR = false;
    boolean dpL = false;
    boolean dpU = false;
    boolean btnStart = false;
    boolean btnSelect = false;
    boolean stickL = false;
    boolean stickR = false;
    boolean shL = false;    //shoulder button
    boolean shR = false;
    boolean trigL = false;    //trigger button
    boolean trigR = false;

    
    //methods
    public static int readAsUnsignedByte(byte input){
        if(input<0){
            return input + 256;
        } else {
            return input;
        }
    }
    
    public static int getBit(byte input,int position){ //LSB is posn 0
       return (input >> position) & 1;
    }
    
    static boolean boolFrBit(int input){   //1/0 to true/false  [logic similar to C]
        return input!=0;
    }
    
    static boolean boolFrByte(byte input,int position){   //1/0 to true/false  [logic similar to C]
        return boolFrBit(getBit(input,position));
    }
    
    void type1(byte[] byteBuffer){
        //pNo = byteBuffer[2] >> 4;
        conType 	= byteBuffer[2] & 0b00001111;
        lasX 		= readAsUnsignedByte(byteBuffer[3]);   //left analog stick
        lasY 		= readAsUnsignedByte(byteBuffer[4]);        //value from 0 to 255.
        rasX 		= readAsUnsignedByte(byteBuffer[5]);   //right analog stick
        rasY 		= readAsUnsignedByte(byteBuffer[6]);
        fbA 		= boolFrByte(byteBuffer[7],7);
        fbB 		= boolFrByte(byteBuffer[7],6);
        fbX 		= boolFrByte(byteBuffer[7],5);
        fbY 		= boolFrByte(byteBuffer[7],4);
        dpD 		= boolFrByte(byteBuffer[7],3);
        dpR 		= boolFrByte(byteBuffer[7],2);
        dpL 		= boolFrByte(byteBuffer[7],1);
        dpU 		= boolFrByte(byteBuffer[7],0);
        btnStart 	= boolFrByte(byteBuffer[8],7);
        btnSelect 	= boolFrByte(byteBuffer[8],6);
        stickL 		= boolFrByte(byteBuffer[8],5);
        stickR 		= boolFrByte(byteBuffer[8],4);
        shL 		= boolFrByte(byteBuffer[8],3);    //shoulder button
        shR 		= boolFrByte(byteBuffer[8],2);
        trigL 		= boolFrByte(byteBuffer[8],1);    //trigger button
        trigR 		= boolFrByte(byteBuffer[8],0);
    }
    
    //reqByteNum = requested Byte Number.
    void type2(int reqByteNum,byte[] byteBuffer){
        //pNo = byteBuffer[2] >> 4;
        conType 	= byteBuffer[2] & 0b00001111;
        switch (reqByteNum){
            case 3:
                lasX 		= readAsUnsignedByte(byteBuffer[3]);   //left analog stick
                break;
                
            case 4:
                lasY 		= readAsUnsignedByte(byteBuffer[3]);
                break;
                
            case 5:
                rasX 		= readAsUnsignedByte(byteBuffer[3]);   //right analog stick
                break;
                
            case 6:
                rasY 		= readAsUnsignedByte(byteBuffer[3]);
                break;
                
            case 7:
                fbA 		= boolFrByte(byteBuffer[3],7);
                fbB 		= boolFrByte(byteBuffer[3],6);
                fbX 		= boolFrByte(byteBuffer[3],5);
                fbY 		= boolFrByte(byteBuffer[3],4);
                dpD 		= boolFrByte(byteBuffer[3],3);
                dpR 		= boolFrByte(byteBuffer[3],2);
                dpL 		= boolFrByte(byteBuffer[3],1);
                dpU 		= boolFrByte(byteBuffer[3],0);
                break;
                
            case 8:
                btnStart 	= boolFrByte(byteBuffer[3],7);
                btnSelect 	= boolFrByte(byteBuffer[3],6);
                stickL 		= boolFrByte(byteBuffer[3],5);
                stickR 		= boolFrByte(byteBuffer[3],4);
                shL 		= boolFrByte(byteBuffer[3],3);    //shoulder button
                shR 		= boolFrByte(byteBuffer[3],2);
                trigL 		= boolFrByte(byteBuffer[3],1);    //trigger button
                trigR 		= boolFrByte(byteBuffer[3],0);
                break;
                                
            case 1:
            case 2:
            case 9:
            default:
                //case 1,2 & 9 onwards are currently invalid
                System.err.println("Type 2 for player "+(byteBuffer[2] >> 4)+
                        " -- invalid [requested byte ("+reqByteNum+
                        ")]!");
                break;
        }
    }
    
    
    boolean readButton(CM button){
        switch(button){
            case A_FACE_BUTTON:
                return fbA;
                
            case B_FACE_BUTTON:
                return fbB;
                
            case X_FACE_BUTTON:
                return fbX;
                
            case Y_FACE_BUTTON:
                return fbY;
                
            case UP_DPAD:
                return dpU;
                
            case DOWN_DPAD:
                return dpD;
                
            case LEFT_DPAD:
                return dpL;
                
            case RIGHT_DPAD:
                return dpR;
                
            case START_BUTTON:
                return btnStart;
                
            case SELECT_BUTTON:
                return btnSelect;
                
            case LEFT_STICK_BUTTON:
                return stickL;
                
            case RIGHT_STICK_BUTTON:
                return stickR;
                
            case LEFT_SHOULDER:
                return shL;
                
            case RIGHT_SHOULDER:
                return shR;
                
            case LEFT_TRIGGER:
                return trigL;
                
            case RIGHT_TRIGGER:
                return trigR;
                
            default:
                System.err.println("Oi, ["+button+"] is not a Button. It's a Stick Direction!");
                return false;       
        }
    }
    
    
    int readAnalogAxis(CM axis){
        switch(axis){
            case LEFT_ANALOG_STICK_X:
                return lasX;
                
            case LEFT_ANALOG_STICK_Y:
                return lasY;
                
            case RIGHT_ANALOG_STICK_X:
                return rasX;
                
            case RIGHT_ANALOG_STICK_Y:
                return rasY;
                
            default:
                System.err.println("Hey, ["+axis+"] is not an Analog Axis. It is just a simple button!");
                return 127;
        }
    }
    
    static CM readStickChange(PlayerState oldP, PlayerState newP){
        //System.out.println("readStickChange:");
        //System.out.println("oldP = " + oldP);
        //System.out.println("newP = " + newP);
        //System.out.println("--------------------------");
        //System.out.println("--------------------------");
        if(oldP.lasX != newP.lasX){
            return CM.LEFT_ANALOG_STICK_X;
        } else if(oldP.lasY != newP.lasY){
            return CM.LEFT_ANALOG_STICK_Y;
        } else if(oldP.rasX != newP.rasX){
            return CM.RIGHT_ANALOG_STICK_X;
        } else if(oldP.rasY != newP.rasY){
            return CM.RIGHT_ANALOG_STICK_Y;
        } else {
            return null;
        }
    }
    
    static CM readBtnPressed(PlayerState oldP, PlayerState newP){
        //System.out.println("readBtnPressed:");
        //System.out.println("oldP = " + oldP);
        //System.out.println("newP = " + newP);
        //System.out.println("--------------------------");
        //System.out.println("--------------------------");
        for (CM key : CM.values()) {
            switch(key){
                case  LEFT_ANALOG_STICK_X:
                case  LEFT_ANALOG_STICK_Y:
                case  RIGHT_ANALOG_STICK_X:
                case  RIGHT_ANALOG_STICK_Y:
                    break;  //break switch (NOT for each)
                default:
                    if(newP.readButton(key) && !oldP.readButton(key)){
                        return key;
                    }
                    break;
            }
        }
        return null;
    }
    
    
    public PlayerState copy(){
        PlayerState out = new PlayerState();
        out.conType             = this.conType; 	
        out.lasX 		= this.lasX; 		   //left analog stick
        out.lasY 		= this.lasY; 		        //value from 0 to 255.
        out.rasX 		= this.rasX; 		   //right analog stick
        out.rasY 		= this.rasY; 		
        out.fbA 		= this.fbA; 		
        out.fbB 		= this.fbB; 		
        out.fbX 		= this.fbX; 		
        out.fbY 		= this.fbY; 		
        out.dpD 		= this.dpD; 		
        out.dpR 		= this.dpR; 		
        out.dpL 		= this.dpL; 		
        out.dpU 		= this.dpU; 		
        out.btnStart            = this.btnStart; 	
        out.btnSelect           = this.btnSelect; 
        out.stickL 		= this.stickL; 	
        out.stickR 		= this.stickR; 	
        out.shL 		= this.shL; 		//shoulder button
        out.shR 		= this.shR; 		
        out.trigL 		= this.trigL; 	  //trigger button
        out.trigR 		= this.trigR;
        /////
        return out;
    } 
    
    
    // <editor-fold defaultstate="collapsed" desc="toString shortcut">
    //++toString shortcut
    @Override
    public String toString() {
        //System.out.println("PlayerState toString invoked.");  //debug
        StringBuilder result = new StringBuilder();
        for (Field f: getClass().getDeclaredFields()) {
            //System.out.println("f = " + f);   //debug
            //System.out.println("result = " + result.toString());  //debug
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
