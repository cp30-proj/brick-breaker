package brick_breaker;
import java.awt.*;
import java.util.*;
import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import java.applet.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
/*@author Patrick Matthew Chan*/
public class BrickBreaker extends GraphicsProgram{
    /*public static void main(String[] args) {
    new BrickBreaker().start(args);
    } */   
    MyArrayList<Object> a;
    /** Width and height of application window in pixels */
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 650;
    /** Dimensions of game board (usually the same) */
    private static final int WIDTH = APPLICATION_WIDTH;
    private static final int HEIGHT = APPLICATION_HEIGHT;
    /////////////////////////////LEVEL SCREEN///////////////////////////////////
    /** Dimensions of the paddle */
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;    
    /** Offset of the paddle up from the bottom */
    private static final int PADDLE_Y_OFFSET = 80;
    /** Number of bricks per row */
    private static final int NBRICKS_PER_ROW = 10;
    /** Number of rows of bricks */
    private static final int NBRICK_ROWS = 10;
    /** Separation between bricks */
    private static final int BRICK_SEP = 4;
    /** Width of a brick */
    private static final int BRICK_WIDTH =
      (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;
    /** Height of a brick */
    private static final int BRICK_HEIGHT = 8;
    /** Radius of the ball in pixels */
    private static final int BALL_RADIUS = 10;
    /** Offset of the top brick row from the top */
    private static final int BRICK_Y_OFFSET = 70;
    /** Number of turns/lives */
    private static final int NTURNS = 3; 
    private static final int MAX_NTURNS_DRAWN = 10;
    private static final int NEW_LIFE = 10000;
    /** Mine **/
    //cheats
    private final boolean DEBUG_MODE = false;    //(0 mask) cheats enabled
        //should only work if DEBUG_MODE is true
        private final boolean DELAY_ZERO = DEBUG_MODE &&  true;
        private final boolean QUICK_RUN = DEBUG_MODE && true; //(1 mask)
            private final boolean NO_PAUSE = DEBUG_MODE && (QUICK_RUN||false);
            private final boolean AUTO_PADDLE = DEBUG_MODE && (QUICK_RUN||true);
            private final boolean START_ON_TOP = DEBUG_MODE && (QUICK_RUN||true);
            private final boolean DELAY_ONE = DEBUG_MODE && !DELAY_ZERO && (QUICK_RUN||true);
        private final boolean NO_FALL = DEBUG_MODE && true;
        private final boolean NO_PADDLE = DEBUG_MODE && true;
    //global constants
    private static final int SCORE_X = 10;
    private static final int SCORE_Y = 10;
    private static final int BALL_OFFSET = 10;
    private static final double BALL_INI_DX_MIN=1.0;
    private static final double BALL_INI_DX_MAX=3.0;
    private static final String SCORE_FONT = "SansSerif-20";
    private static final int GIncrement_ITERATION_WAIT=60;
    private static final int GComputed_ITERATION_WAIT=20;//must be less than gincrement iteration
    private static final String MSG_FONT = "SansSerif-40";
    private final int INI_DELAY=7;
    private final int SPEED_UP_RATE=1;
    private final int SPEED_INCREMENTS=6;
    private final int LIVES_X_OFFSET=20;
    private final int LIVES_Y_OFFSET=20;
    private final int LIVES_SPACING=5;
    //global GObjects  
    AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
    AudioClip bgm = MediaTools.loadAudioClip("bgm.au");
    //this can either be in BrickBreaker\build\classes\ or Brakout project folder
    private GLine left= new GLine(-1,-1,-1,HEIGHT);
    private GLine up= new GLine(-1,-1,WIDTH,-1);
    private GLine right= new GLine(WIDTH,-1,WIDTH,HEIGHT);
    private GLine down= new GLine(-1,HEIGHT,WIDTH,HEIGHT);
    private GLine dead= new GLine(-1,HEIGHT+3*BALL_RADIUS,WIDTH,HEIGHT+3*BALL_RADIUS);
    private GRect[][] brick = new GRect[NBRICK_ROWS][NBRICKS_PER_ROW];
    private GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
    private GOval ball = new GOval(2*BALL_RADIUS,2*BALL_RADIUS);
    private GLabel test;
    private GLabel GScore = new GLabel("Score: 0");
    private GLabel GIncrement = new GLabel(" ");
    private GLabel GHiScore = new GLabel("Hi-Score: 0");
    private GLabel msg;
    private GLabel livesDisp = new GLabel("Lives: ");
    private GOval[] livesBall = new GOval[MAX_NTURNS_DRAWN];
    private GLabel GMultiplier = new GLabel(" ");
    //global variables
    private boolean hasBegan=false;
    private RandomGenerator rng = RandomGenerator.getInstance();
    private double vx=0,vy=0;
    private int score=0;
    private int numBricks=0;
    private int lives=0;
    private int rewarded_score=0;   //avoid double rewarding of lives
    //private boolean nonwallCheck=false;    //true if not wall nor GLabel
    private boolean isBrick=true;
    private int GIncrementWait=-1;
    private int gameDelay=INI_DELAY;
    private int increment=0;
    private int increment2 = 0; //(temporary)
    private double multiplier=1;
    public boolean isMouseInScreen=true;
    /////////////////////////////PAUSE SCREEN///////////////////////////////////
    private GRect overlay=new GRect(WIDTH,HEIGHT);
    private final String CLICK1_FONT="SansSerif-30";
    private final String CLICK2_FONT="SansSerif-12";
    private final static int CLICK12_OFFSET=5;
    private boolean isPaused=false;
    ////////////////////////////HI-SCORE SCREEN/////////////////////////////////
    private static final int HI_SCORES=5;
    private MyArrayList<String> names=new MyArrayList<String>();
    private MyArrayList<Integer> scores=new MyArrayList<Integer>();
    private double ctrX(GObject g){
        return (WIDTH-g.getWidth())/2;
    }
    private GLabel yay = new GLabel("NEW HIGH SCORE!");
    private final static String YAY_FONT="SansSerif-30";
    private final static int YAY_Y_OFFSET=10;
    private GLabel hiScoreDisp = new GLabel("High  Scores");
    private final static int HI_SCORES_Y_OFFSET=90;
    private final static int HI_SCORES_SPACING=10;
    private final static String HI_SCORE_FONT="SansSerif-20";
    private GLabel HSNames[] = new GLabel[HI_SCORES+1];//lets just make [0] do nothing
    private GLabel HSScores[] = new GLabel[HI_SCORES+1];//lets just make [0] do nothing
    private final static int NAMES_X_OFFSET=20;
    private final static int SCORES_X_OFFSET=WIDTH-20;
    private int gameNo=1;
    private final String CLICK_FONT="SansSerif-25";
    private final int CLICK_Y_OFFSET=110;
    private boolean hasInput=false;
    ////////////////////////////////////////////////////////////////////////////
    //////////////////////----- UCMI Connection -----///////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    UCMI ucmi = new UCMI();
    ControlConfig ctrlConfig;
    /// mine other
    boolean waitingForStart = false;
    
    
    
        
    
    /////////////////////////////LEVEL SCREEN///////////////////////////////////
    //----------------------------functions-----------------------------------//    
    //display screen
    private void screenLevel(){
        setBackground(new Color(202,236,207));
        removeAll();
        startLevel();
    }
    //bricks
    private double computeXOffset() {
        return 0.5*(WIDTH-(NBRICKS_PER_ROW-1)*BRICK_SEP-BRICK_WIDTH*NBRICKS_PER_ROW);
    }
    private void setBrickColor(GRect brick, int row) {
        switch (row%10) {
            case 0:
            case 1:
                brick.setFillColor(Color.RED);
                break;
            case 2:
            case 3:
                brick.setFillColor(Color.ORANGE);
                break;
            case 4:
            case 5:
                brick.setFillColor(Color.YELLOW);
                break;
            case 6:
            case 7:
                brick.setFillColor(Color.GREEN);
                break;
            case 8:
            case 9:
                brick.setFillColor(Color.CYAN);
        }
    }
    //paddle    
    public void mouseMoved(MouseEvent me){
        /*if (hasBegan&&!isPaused){
            float x = me.getX();
            if(x < 0.5 * PADDLE_WIDTH){
                paddle.setLocation(0,paddle.getY());
            } else if (x < (WIDTH - 0.5 * PADDLE_WIDTH)){
                paddle.setLocation(me.getX()-0.5 * PADDLE_WIDTH,paddle.getY());
            } else {
                paddle.setLocation(WIDTH-PADDLE_WIDTH,paddle.getY());
            }
            if(isMouseInScreen && (x>WIDTH+3 || x<-3)){//3 is the allowance
                //System.out.println("mouse exited playing field");
                isMouseInScreen=(!isAutoPause)||false;//!isAutoPause bitmask
            } else if (!isMouseInScreen) {
                //System.out.println("mouse entered playing field");
                isMouseInScreen=true;
            }
        }*/
    }
    //pause
    public void mouseExited(MouseEvent me){
        /*if(hasBegan && isMouseInScreen && !isPaused){
            //System.out.println("mouse exited");
            isMouseInScreen=(!isAutoPause)||false;//!isAutoPause bitmask
        }*/
    }
    public void mouseEntered(MouseEvent me){
        /*if(!isMouseInScreen){
            //System.out.println("mouse entered");
            isMouseInScreen=true;
        }*/
    }
    public void mouseClicked(MouseEvent me){
        /*if(isPaused){
            float x = me.getX();
            if(!(x>WIDTH+3 || x<-3)){//3 is the allowance
                //System.out.println("mouse entered playing field");
                if(x < 0.5 * PADDLE_WIDTH){
                    paddle.setLocation(0,paddle.getY());
                } else if (x < (WIDTH - 0.5 * PADDLE_WIDTH)){
                    paddle.setLocation(me.getX()-0.5 * PADDLE_WIDTH,paddle.getY());
                } else {
                    paddle.setLocation(WIDTH-PADDLE_WIDTH,paddle.getY());
                }
                isPaused=false;
                isMouseInScreen=true;
            }
        }*/
    }
    //score
    private void resetScore(){//to zero
        score=0;
        increment=0;
        multiplier=1.0;
        rewarded_score=0;
    }
    private void incrementDone(){       
        if(GIncrementWait==-1){
            ;
        } else if(GIncrementWait==0){
            score=score+increment2;
            increment2=0;
            remove(GIncrement);
            remove(GScore);
            GScore=new GLabel("Score: " + score);
            GScore.setFont(SCORE_FONT);
            add(GScore,SCORE_X,SCORE_Y+GScore.getHeight());
            
            while(score>=rewarded_score+NEW_LIFE && hasBegan){
                lives++;
                rewarded_score+=NEW_LIFE;
                updateLives();
            }
            
            GIncrementWait--;
        } else if(GIncrementWait==GComputed_ITERATION_WAIT){
            increment=(int)(double)(increment*multiplier);
            increment2+=increment;
            increment=0;
            multiplier=1;
            remove(GMultiplier);
            remove(GIncrement);
            GIncrement=new GLabel(" +" + increment2);
            GIncrement.setFont(SCORE_FONT);
            GIncrement.setColor(new Color(0,128,0));
            add(GIncrement,SCORE_X+GScore.getWidth(),SCORE_Y+GIncrement.getHeight());
            GIncrementWait--;
        } else if (GIncrementWait==GIncrement_ITERATION_WAIT-1){
            score=score+increment2;
            increment2=0;
        } else {
            GIncrementWait--;
        }
    }
    private void updateScore(){       
        remove(GScore);
        remove(GIncrement);
        remove(GMultiplier);
        GScore=new GLabel("Score: " + score);
        GScore.setFont(SCORE_FONT);
        add(GScore,SCORE_X,SCORE_Y+GScore.getHeight());
    }
    private void updateScore(int addedValue){ 
        remove(GScore);
        remove(GIncrement);
        remove(GMultiplier);
        if(increment>0){
            multiplier+=0.05;
        }
        increment+=addedValue;
        GScore=new GLabel("Score: " + score);
        GScore.setFont(SCORE_FONT);
        add(GScore,SCORE_X,SCORE_Y+GScore.getHeight());
        if(addedValue>0){
            GIncrement=new GLabel(" +" + increment);
            GIncrement.setFont(SCORE_FONT);
            GIncrement.setColor(new Color(0,128,0));
            add(GIncrement,SCORE_X+GScore.getWidth(),SCORE_Y+GIncrement.getHeight());
            GIncrementWait=GIncrement_ITERATION_WAIT;
            if(multiplier>1){
                GMultiplier=new GLabel(" x" + (float)multiplier);
                GMultiplier.setFont(SCORE_FONT);
                GMultiplier.setColor(new Color(128,0,0));
                add(GMultiplier,GIncrement.getX()+GIncrement.getWidth(),GIncrement.getY());
            }
        }
    }
    //hi-score
    private void updateHiScore(){       
        remove(GHiScore);
        int hiscore=score;
        if(!(scores.isEmpty()) && scores.get(1)>score){
            GHiScore=new GLabel("Hi-Score: " + scores.get(1));
        } else {
            GHiScore=new GLabel("Hi-Score: " + score);
        }
        GHiScore.setFont(SCORE_FONT);
        add(GHiScore,WIDTH-GHiScore.getWidth()-SCORE_X,SCORE_Y+GHiScore.getHeight());
    }
    //message
    private double computeMsgCtrY(){
        double TOP_SPACE=(BRICK_Y_OFFSET+(BRICK_HEIGHT*NBRICK_ROWS+
                BRICK_SEP*NBRICK_ROWS));
        double WHITE_SPACE=HEIGHT-(TOP_SPACE+(PADDLE_HEIGHT+PADDLE_Y_OFFSET));
        return TOP_SPACE+0.5*WHITE_SPACE;
    }
    private void displayMessage(String s,Color c,boolean isGameOver){
        double msg_Y=0;
        msg=new GLabel(s);
        msg.setFont(MSG_FONT);
        msg.setColor(c);
        if(isGameOver){
            msg_Y=computeMsgCtrY()-0.5*msg.getHeight();
        } else{
            msg_Y=(HEIGHT-msg.getHeight())/2;
        }
        add(msg,(WIDTH-msg.getWidth())/2,msg_Y);
    }
    //lives
    private void updateLives(){
        double curX=0;
        double curY=0;
        for(int i=0;i<MAX_NTURNS_DRAWN;i++){
            remove(livesBall[i]);
        }
        if(lives>MAX_NTURNS_DRAWN){
            livesDisp.setLabel("("+lives+")");
            curX=livesDisp.getX()+livesDisp.getWidth();
            curY=livesDisp.getY()-BALL_RADIUS;
            for(int i=0;i<MAX_NTURNS_DRAWN;i++){
                add(livesBall[i],curX,curY);
                curX=curX+BALL_RADIUS+LIVES_SPACING;
            }
        } else {
            if(!livesDisp.getLabel().equals("Lives: ")){
                livesDisp.setLabel("Lives: ");
            }
            curX=livesDisp.getX()+livesDisp.getWidth();
            curY=livesDisp.getY()-BALL_RADIUS;
            for(int i=0;i<lives;i++){
                add(livesBall[i],curX,curY);
                curX=curX+BALL_RADIUS+LIVES_SPACING;
            }
        }
        //remove
    }
    private void lifeBonus(){
        if(lives>0){
            double dist=(livesDisp.getY()-GScore.getY())-2*(GScore.getHeight());
            for(int j=0;j<=dist;j++){
                livesDisp.move(0,-1);
                if(lives>MAX_NTURNS_DRAWN){
                    for(int i=0;i<MAX_NTURNS_DRAWN;i++){
                        livesBall[i].move(0,-1);
                    }
                    GHiScore.move(0,1);
                } else {
                    for(int i=0;i<lives;i++){
                        livesBall[i].move(0,-1);
                    }
                }
                pause(1);
            }
            while(lives>0){
                if(lives==MAX_NTURNS_DRAWN){
                    updateLives();
                }
                if(lives<=MAX_NTURNS_DRAWN){
                    remove(livesBall[lives-1]);
                    lives--;
                    updateScore(700);
                    pause(700);
                } else {
                    lives--;
                    updateScore(700);
                    livesDisp.setLabel("("+lives+")");
                    pause(1);
                }
            }
            GIncrementWait=GComputed_ITERATION_WAIT;
            incrementDone();
            pause(400);
            GIncrementWait=0;
            incrementDone();
            pause(500);
        }
    }
    //---------------------------initializer----------------------------------//
    private void startLevel(){
        hasBegan=false;
        removeAll();
        //wall
        add(up);
        add(down);
        add(left);
        add(right);
        dead.setVisible(false);
        add(dead);
        //bricks
        numBricks=0;
        for (int i = 0; i < NBRICK_ROWS; i++){//row
            for (int j = 0; j < NBRICKS_PER_ROW; j++){//col
                double x, y; // brick location
                brick[i][j] = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
                x = computeXOffset() + j * (BRICK_WIDTH + BRICK_SEP);
                y = BRICK_Y_OFFSET + i * (BRICK_HEIGHT + BRICK_SEP);
                brick[i][j].setFilled(true);
                setBrickColor(brick[i][j],i);
                if(y+2*BRICK_HEIGHT<dead.getY() && x+BRICK_WIDTH<right.getX()){
                    add(brick[i][j], x, y);
                    numBricks++;
                }
                if(!DELAY_ONE && !DELAY_ZERO){
                    pause(10);
                }
            }
        }        
        //score
        GIncrementWait=-1;
        updateScore();
        increment=0;
        increment2=0;
        multiplier=1;
        rewarded_score=0;
        //hi-score
        updateHiScore();
        //lives
        lives=NTURNS;
        livesDisp.setFont(SCORE_FONT);
        add(livesDisp,LIVES_X_OFFSET,HEIGHT-LIVES_Y_OFFSET-livesDisp.getHeight());
        updateLives();
        //paddle
        paddle.setFilled(true);
        add(paddle, 0.5*(WIDTH - PADDLE_WIDTH), HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
        //ball
        ball.setFilled(true);
        add(ball, 0.5*WIDTH - BALL_RADIUS , paddle.getY() - PADDLE_HEIGHT - BALL_RADIUS - BALL_OFFSET );
        if(START_ON_TOP){
            ball.setLocation(0.5*WIDTH - BALL_RADIUS,BRICK_Y_OFFSET/2);
        }
        //game delay
        gameDelay=INI_DELAY;
        //use a life
        bgm.loop();
        lifeBegin();
    }
    private void lifeBegin(){
        hasBegan=false;
        if(lives>0){
            //paddle
            if(NO_PADDLE){//cheats
                remove(paddle);
            }
            paddle.setLocation(0.5*(WIDTH - PADDLE_WIDTH), HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
            //ball
            ball.setLocation(0.5*WIDTH - BALL_RADIUS , paddle.getY() - PADDLE_HEIGHT - BALL_RADIUS - BALL_OFFSET);
            if(START_ON_TOP){
                ball.setLocation(0.5*WIDTH - BALL_RADIUS,BRICK_Y_OFFSET/2);
            }
            //ini speed
            vx = rng.nextDouble(BALL_INI_DX_MIN, BALL_INI_DX_MAX);
            if (rng.nextBoolean(0.5)){
                vx = -vx;
            }
            vy = -3.0;
            //waitforclick
            //GLabel click=new GLabel("-Click Anywhere to Begin-");
            GLabel click=new GLabel("-Press [Start] to Begin-");
            click.setFont(CLICK_FONT);
            add(click,ctrX(click),HEIGHT-click.getHeight()-CLICK_Y_OFFSET);
            //waitForClick();////////////////////////////remove this if program fails
            waitForStart(false); //^^   //cleanup false since playLevel follows after
            //use life
            lives--;
            updateLives();
            //done
            remove(click);
            hasBegan=true;
            playLevel();
        }else {            
            
            ///////////////////////
            setBackground(Color.red);
            displayMessage("Game Over",Color.WHITE,true);
            pause(400);
            screenHiScore();
        }
    }
    //------------------------playing the level-------------------------------//    
    private GObject collisionCheck(){
        //nonwallCheck=false;
        double x=ball.getX();
        double y=ball.getY();
        int d=2*BALL_RADIUS;//ball diameter
        int corner=1;//ball corner [1 2;3 4]
        //checker
        double bx=x,by=y;   //colliding boundary
        GObject coll=getElementAt(bx,by);//colliding object
        if(coll==null){
            bx=x+d;
            corner=2;
            coll=getElementAt(bx,by);
            if (coll==null){
                by=y+d;
                corner=4;
                coll=getElementAt(bx,by);
                if (coll==null){
                    bx=x;
                    corner=3;
                    coll=getElementAt(bx,by);
                }
            }
        }
        if(coll instanceof GLabel || coll instanceof GOval){
            double ax=coll.getX();
            double ay=coll.getY();
            remove(coll);
            GObject temp=collisionCheck();
            add(coll,ax, ay);
            return temp;
        }
        //the "reaction"
        //walls (also to prevent stuck)
        if((x<=0 && vx<0)||(x+d>=WIDTH && vx>0)){//vertical walls
            vx=-vx;
        }
        if(y<=0 && vy<0){
            vy=-vy;
        }
        if(NO_FALL && (y+d>=HEIGHT + 2*BALL_RADIUS && vy>0)){//cheat
            vy=-vy;
        }
        //isBrick check
        isBrick=true;
        try{
            GRect brick = (GRect)coll;
        } catch(ClassCastException e){                
            isBrick=false;
        } finally {
            if(coll==null || coll==paddle){
                isBrick=false;
            }
        }   
        //based on coll
        if(isBrick){  //brick only
            double cx=coll.getX();
            double cy=coll.getY();
            double c2x=cx+coll.getWidth();
            double c2y=cy+coll.getHeight();
            //nonwallCheck=true; //bounceClip.play();
            if((bx>=cx && bx<=cx+BALL_INI_DX_MAX) || (bx<=c2x && bx>=c2x-BALL_INI_DX_MAX)){
                //if(vx>0){
                vx=-vx;
                //}
            }
            if((by>=cy && by<=cy+3.0) || (by<=c2y && by>=c2y-3.0)){
                vy=-vy;
            }
            /*{
            test=new GLabel("x="+x+" y="+y+" bx="+bx+" by="+by+" cx="+cx+" cy="+cy+" c2x="+c2x+" c2y="+c2y+"."+coll);
            add(test,50,50);
            waitForClick();
            remove(test);
            }*/
        } else if(coll==paddle){
            if(GIncrementWait!=-1){
                GIncrementWait=GComputed_ITERATION_WAIT;
            }
            double cx=coll.getX();
            double cy=coll.getY();
            double c2x=cx+coll.getWidth();
            double c2y=cy+coll.getHeight();
            if(corner==3 || corner==4){
                if((by>=cy && by<=cy+3.0) && ((bx>=cx && bx<=cx+BALL_INI_DX_MAX) || (bx<=c2x && bx>=c2x-BALL_INI_DX_MAX))){
                    vx=-vx;
                }
                if(paddle.contains(bx, by)){
                    if(vy>0){
                        vy=-vy;
                    }
                    ball.setLocation(x,paddle.getY()-d);
                }
            } else if (paddle.contains(bx,by)){
                vy=-vy;
            }            
            /*
            double xx=x+BALL_RADIUS;
            double yy=y+BALL_RADIUS;
            //double cyy=coll.getY()+0.5*PADDLE_HEIGHT;
            if(paddle.contains(xx,yy)){
                ball.move(0, -1.5*d);
            }*/
        }
        return coll;
    }
    private void playLevel(){
        while(hasBegan && numBricks>0){
            /////////////////////////////+++++++++++ mine
            if(lf){
                moveLf();
            }
            if(rt){
                moveRt();
            }
            uartReadLoopIterate();  //clean up at screen high score
            
            ////////////////////////
            if(!isMouseInScreen && !NO_PAUSE){
                screenPause();
            }
            
            ball.move(vx, vy);
            GObject coll=collisionCheck();//collider
                     
            //reaction...++
            if(coll==dead){
                if(GIncrementWait!=-1){
                    GIncrementWait=GComputed_ITERATION_WAIT;
                    incrementDone();
                    pause(400);
                    GIncrementWait=0;
                    incrementDone();
                    pause(200);
                }
                hasBegan=false;
                lifeBegin();
            } else if (isBrick){
                GRect brick = (GRect)coll;
                if(brick.getFillColor()==Color.RED){
                    updateScore(100);
                } else if(brick.getFillColor()==Color.ORANGE){
                    updateScore(80);
                } else if(brick.getFillColor()==Color.YELLOW){
                    updateScore(60);
                } else if(brick.getFillColor()==Color.GREEN){
                    updateScore(40);
                } else if(brick.getFillColor()==Color.CYAN){
                    //setBackground(Color.BLUE);
                    updateScore(20);
                }
                    bounceClip.play();
                remove(coll);
                numBricks--;
                /*{test=new GLabel("SCORE: " + score);
                add(test,50,50);
                waitForClick();
                remove(test);}*/
            }
            //delay
            if(AUTO_PADDLE){
                paddle.setLocation(ball.getX()-PADDLE_WIDTH/2+BALL_RADIUS, paddle.getY());//hax/cheat
            }
            if(DELAY_ONE){                
                pause(1);
            } else if (!DELAY_ZERO){
                gameDelay=INI_DELAY-((int)((1-((float)numBricks/(NBRICKS_PER_ROW
                        *NBRICK_ROWS)))*(SPEED_INCREMENTS-1))*(SPEED_UP_RATE));;
                if(gameDelay<1){
                    gameDelay=1;
                }
                pause (gameDelay);                
            }
            incrementDone();
            updateHiScore();
        }
        if(numBricks==0){
            if(GIncrementWait!=-1){
                GIncrementWait=GComputed_ITERATION_WAIT;
                incrementDone();
                pause(400);
                GIncrementWait=0;
                incrementDone();
                pause(200);
            }
            hasBegan=false;
            displayMessage("Clear Bonus: +5000",Color.BLUE,false);
            updateScore(5000);
            //increment score for a while...
            GIncrementWait=GComputed_ITERATION_WAIT;
            incrementDone();
            pause(700);
            GIncrementWait=0;
            incrementDone();
            pause(500);
            remove(msg);
            //but dont include life bonus in multiplier
            lifeBonus();
            setBackground(Color.green);
            displayMessage("Congratulations!",Color.WHITE,false);
            pause(700);
            screenHiScore();
        }
    }

    

        
    
    /////////////////////////////PAUSE SCREEN///////////////////////////////////
    private void screenPause(){
        add(overlay);
        bgm.stop();
        GLabel click1=new GLabel("- PAUSED -");
        //GLabel click2=new GLabel("Click Anywhere to Resume");
        GLabel click2=new GLabel("Press [Start] to Resume");
        click1.setFont(CLICK1_FONT);
        click1.setColor(Color.WHITE);
        click2.setFont(CLICK2_FONT);
        click2.setColor(Color.WHITE);
        add(click1,ctrX(click1),(HEIGHT-click2.getHeight()-CLICK12_OFFSET
                +click1.getHeight())/2);
        add(click2,ctrX(click2),(HEIGHT+click1.getHeight()+CLICK12_OFFSET
                +click2.getHeight())/2);
        isPaused=true;
        while(isPaused){//waitforclick
            uartReadLoopIterate();
            pause(0);
        }
        //uartLoopExitResetHoldDown();
        //waitForClick();
        isPaused=false;
        isMouseInScreen=true;
        remove(overlay);
        remove(click1);
        remove(click2);
        bgm.loop();
    }
    
    
    
    
    ////////////////////////////HI-SCORE SCREEN/////////////////////////////////
    private void screenHiScore() throws HiScoreListSyncException{
        uartLoopExitResetHoldDown();
        bgm.stop();
        //HS File Read (creation if none)
        names.createList();
        scores.createList();
        String filePath=new File("").getAbsolutePath();
        FileReader in;
        BufferedReader br = null;
        try{
            in=new FileReader(filePath+"/BrickBreakerScores.dat");
            br=new BufferedReader(in);
        } catch(IOException e){
            File a=new File(filePath+"/BrickBreakerScores.dat");
            try{
                a.createNewFile();
                in=new FileReader(filePath+"/BrickBreakerScores.dat");
                br=new BufferedReader(in);
            } catch (IOException ee){
                System.err.println("FILE READ ERROR\n");
                ee.printStackTrace();
            }
        }
        MyArrayList<String> buff=new MyArrayList<>();
        buff.createList();
        String a="";
        try{
            a=br.readLine();
        } catch (IOException e){
            System.err.println("File read error");
            e.printStackTrace();
        }
        while(a!=null){
            try{
                buff.add(buff.size()+1,a);
                a=br.readLine();
            } catch (IOException e){
                System.err.println("File read error");
                e.printStackTrace();
            }
        }
        for(int i=1;i<=buff.size()/2;i++){
            names.add(i,buff.get(i));
        }
        for(int i=1;i<=buff.size()/2;i++){
            try{
                scores.add(i,Integer.parseInt(buff.get(buff.size()/2+i)));
            } catch (NumberFormatException e){
                System.err.println(buff.get(i)+"parse int error");
                scores.add(i,0);
            }
        }     
        
        
        
        setBackground(new Color(206,243,253));
        removeAll();
        if(names.size()!=scores.size()){
            throw new HiScoreListSyncException("ERROR: HiScore List Size Mismatch.");
        } else if (names.size()>HI_SCORES){
            throw new HiScoreListSyncException("ERROR: HiScore List Size Exceeds "+HI_SCORES+".");
        }
        checkHiScore(score);
    }
    //exception
    public class HiScoreListSyncException extends RuntimeException{
        public HiScoreListSyncException(String s){ 
            super(s);
        }//end constructor
    }
    //functions
    private void checkHiScore(int score){
        int size=scores.size();
        if(size==0){
            enterName(1,score);
            //return;
        } else {
            int i=size;
            for(;i>0&&score>scores.get(i);i--){//"outputs" index of higher score                
            }
            enterName(i+1,score);
            /*if(i<HI_SCORES){
                enterName(i+1,score);
                return;
            }*/
        }
        //enterName(HI_SCORES+1,score);
    }
    //.....
    String name="";
    //......
    final static int PADDLE_SPEED = 10;
    boolean lf = false,rt = false;
    @Override
    public void keyPressed(KeyEvent e) {
        //vvvv Remember: Controls indexing Starts with [0]; players indexing starts with [1].
        //String[] genControlNames = new String[]{"Up","Down","Left","Right","Start","Select","Quit"}; <--from top-((as of 10:34 pc clk))]
        int id = e.getKeyCode();
        //System.out.println("id = " + id);
        //System.out.println("pressed: "+e.getKeyText(id));
        //System.out.println("kybdCtrls[1]:"+Arrays.toString(ctrlConfig.kybdControls[1]));
        //System.out.println("kybdCtrls[2]:"+Arrays.toString(ctrlConfig.kybdControls[2]));
        
        if(id == ctrlConfig.kybdControls[1][2]){//p1 left
            lf = true;
        }
        if(id == ctrlConfig.kybdControls[1][3]){//p1 right
            rt = true;
        }
        
        //from orig (hi-score)
        ////////////////////////
        if(hasInput){
            char a=e.getKeyChar();
            //System.out.println(a);
            if(name=="<Please Type Your Name>"){
                name="";
            }
            if(a=='\n' || a=='\r'){
                hasInput=false;
            } else if (a=='\b' && !name.isEmpty()){
                name = new String(name.substring(0, name.length()-1));
            } else if (name.length()==20){
                name = new String(name.substring(0, 19) + a);
            } else if (a>=32 && a<=126){
                name = new String(name + a);
            }
        }
    }
    

    @Override
    public void keyReleased(KeyEvent e) {
        //vvvv Remember: Controls indexing Starts with [0]; players indexing starts with [1].
        //String[] genControlNames = new String[]{"Up","Down","Left","Right","Start","Select","Quit"}; <--from top-((as of 10:34 pc clk))]
        int id = e.getKeyCode();
        
        if(id == ctrlConfig.kybdControls[1][2]){//p1 left
            lf = false;
        } 
        if(id == ctrlConfig.kybdControls[1][3]){//p1 right
            rt = false;
        } 
        if(id == ctrlConfig.kybdControls[1][5]){//any select (control settings)
            actConSettings();
        } 
        if(id == ctrlConfig.kybdControls[1][4]){//any start
            actStart();
        } 
    }
    
    
    private void enterName(int index,int score){//index is where to insert entry
        yay.setVisible(false);
        yay.setFont(YAY_FONT);
        yay.setColor(new Color(0,237,0));
        add(yay,ctrX(yay),YAY_Y_OFFSET+yay.getHeight());
        hiScoreDisp.setFont(HI_SCORE_FONT);
        double fontSizeY=hiScoreDisp.getHeight();
        double curY=HI_SCORES_Y_OFFSET+fontSizeY;//current Y        
        add(hiScoreDisp,ctrX(hiScoreDisp),curY);
        curY+=HI_SCORES_SPACING+fontSizeY;
        for(int i=1;i<=names.size();i++){
            if(i<index){
                HSNames[i]=new GLabel(i+". "+names.get(i));
                HSScores[i]=new GLabel(scores.get(i)+"");
                HSNames[i].setFont(HI_SCORE_FONT);
                HSScores[i].setFont(HI_SCORE_FONT);
                add(HSNames[i],NAMES_X_OFFSET,curY);
                add(HSScores[i],SCORES_X_OFFSET-HSScores[i].getWidth(),curY);
            } else {
                HSNames[i]=new GLabel((i+1)+". "+names.get(i));
                HSScores[i]=new GLabel(scores.get(i)+"");
                HSNames[i].setFont(HI_SCORE_FONT);
                HSScores[i].setFont(HI_SCORE_FONT);
                add(HSNames[i],NAMES_X_OFFSET,curY);
                add(HSScores[i],SCORES_X_OFFSET-HSScores[i].getWidth(),curY);
            }
           
            curY+=HI_SCORES_SPACING+fontSizeY;
        }
        if(index<=HI_SCORES){
            yay.setVisible(true);
            //if(!names.isEmpty()){
            for(int i=names.size();i>=index;i--){
                curY=HSNames[i].getY();
                for(int j=0;j<=HI_SCORES_SPACING+fontSizeY;j++){
                    HSNames[i].move(0,1);
                    HSScores[i].move(0,1);
                    pause(1);
                }
            }
            //}            
            /*test=new GLabel("ENTER NAME");
            add(test,200,200);
            GCanvas canvas = getGCanvas();
            canvas.addKeyListener(this);
            hasInput=true;
            while(hasInput){
                pause(10);
            }
            remove(test);*/
            //String name="Game#"+gameNo;//"HI--PMC";//temporary
            name="<Please Type Your Name>";
            GLabel newName=new GLabel(index+". "+name);
            //gameNo++;
            GLabel newScore=new GLabel(score+"");
            newName.setFont(HI_SCORE_FONT);
            newScore.setFont(HI_SCORE_FONT);
            add(newScore,-newScore.getWidth(),curY);
            add(newName,NAMES_X_OFFSET-SCORES_X_OFFSET,curY);
            newName.setColor(new Color(206,206,0));
            newScore.setColor(new Color(206,206,0));
            for(int i=0;i<=SCORES_X_OFFSET;i++){
                newName.move(1, 0);
                newScore.move(1, 0);
                pause(1);
            }
            //entry name
            hasInput=true;
            while(hasInput){
                double x=newName.getX();
                double y=newName.getY();
                newName.setLabel(index+". "+name);
                remove(newName);
                add(newName,x,y);
                pause(10);
            }
            //done input
            names.add(index, name);
            scores.add(index, score);
            if(names.size()>HI_SCORES){
                names.remove(6);
                scores.remove(6);
                while(HSNames[5].getX()<WIDTH){
                    HSNames[5].move(1, 0);
                    HSScores[5].move(1, 0);
                    pause(1);
                }
                remove(HSNames[5]);
                remove(HSScores[5]);
            }
        }
        
        
        
        
        //HS File Overwrite
        String filePath=new File("").getAbsolutePath();
        FileWriter out;
        BufferedWriter bw = null;
        try{
            out=new FileWriter(filePath+"/BrickBreakerScores.dat");
            bw=new BufferedWriter(out);
        } catch(IOException e){
            System.err.println("FILE READ ERROR\n");
            e.printStackTrace();
        }
        MyArrayList<String> buff=new MyArrayList<>();
        buff.createList();
        for(int i=1;i<=names.size();i++){
            buff.add(i,names.get(i));
        }
        for(int i=1;i<=scores.size();i++){
            buff.add(buff.size()+1,scores.get(i).toString());
        }
        for(int i=1;i<=buff.size();i++){
            try{
                bw.append(buff.get(i));
                bw.newLine();
            } catch (IOException e){
                System.out.println("File Write Error");
                e.printStackTrace();
            }
        }
        try{//necessary to update file
            bw.flush();
            bw.close();
        } catch(IOException e){
            ;
        }//System.out.println(filePath);
        backToLevel();
    }
    private void backToLevel(){
        resetScore();
        //GLabel click=new GLabel("-Click Anywhere to Play Again-");
        GLabel click=new GLabel("-Press [Start] to Play Again-");
        click.setFont(CLICK_FONT);
        add(click,ctrX(click),HEIGHT-click.getHeight()-CLICK_Y_OFFSET);
        //waitForClick();
        waitForStart(true); //true since next input is another waitForStart
        screenLevel();
    }
    
    //"main" function
    public void init(){
        ////////////////////////////////////////////////////////////////////////////
        //////////////////////----- UCMI Connection -----///////////////////////////
        ////////////////////////////////////////////////////////////////////////////
        ucmi.init();
        if(ucmi.isPortConnected){
            ucmi.ReqPlayer(1);
            System.out.println("port connected. 1st requests sent.");
        }
        ////////////////////////////////
        /// defining controls (default)
        ///////////////////////////////
        ctrlConfig = new ControlConfig(ucmi);
        ///////
        String[] genControlNames = new String[]{"Up","Down","Left","Right","Start","Open Controls Settings"}; //please insrt new controls at end, unless wiling to change indexing in code.
        double[]  genDirGrpNo = new double[]{1.1,1.2,1.3,1.4,2,3};
        ///////
        ctrlConfig.gameControls = new String[2][]; //player indexing starts at '1'
        for (int i = 1; i < ctrlConfig.gameControls.length; i++) {
            ctrlConfig.gameControls[i] =  genControlNames;
        }
        ////////
        ctrlConfig.directionalGroupNo = new double[2][]; //player indexing starts at '1'
        for (int i = 1; i < ctrlConfig.directionalGroupNo.length; i++) {
            ctrlConfig.directionalGroupNo[i] = genDirGrpNo;
        }
        ////////
        ctrlConfig.kybdControls = new int[2][]; //player indexing starts at '1'
        ctrlConfig.kybdControls[1] = new int[]{KeyEvent.VK_UP,KeyEvent.VK_DOWN,KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT,KeyEvent.VK_SPACE,KeyEvent.VK_ESCAPE};    //P1 default
        ////////
        ctrlConfig.uartControls = new CM[2][]; //player indexing starts at '1'
        CM[] genUart = new CM[]{CM.LEFT_ANALOG_STICK_Y,CM.LEFT_ANALOG_STICK_Y,CM.LEFT_ANALOG_STICK_X,CM.LEFT_ANALOG_STICK_X,CM.A_FACE_BUTTON,CM.SELECT_BUTTON};
        for (int i = 1; i < ctrlConfig.uartControls.length; i++) {
            ctrlConfig.uartControls[i] = genUart;
        }
        ////////
        ctrlConfig.uartHolddownWaitCount = new int[ctrlConfig.gameControls.length][ctrlConfig.gameControls[1].length];
        for (int i = 1; i < ctrlConfig.uartHolddownWaitCount.length; i++) {
            for (int j = 0; j < ctrlConfig.uartHolddownWaitCount[i].length; j++) {
                ctrlConfig.uartHolddownWaitCount[i][j] = 0;
            }
        }
        
        
        ///////////////////////////////
        ///////////////////////////////
        addMouseListeners();
        GCanvas canvas = getGCanvas();
        canvas.addKeyListener(this);
        for(int i=0;i<MAX_NTURNS_DRAWN;i++){
            livesBall[i] = new GOval(BALL_RADIUS,BALL_RADIUS);
            livesBall[i].setFilled(true);
        }
        names.createList();
        scores.createList(); 
        
        //pause screen
        overlay.setFilled(true);
        overlay.setFillColor(new Color(0,0,0,200));
        
        //getting high scores list
        //HS File Read (creation if none)
        names.createList();
        scores.createList();
        String filePath=new File("").getAbsolutePath();
        FileReader in;
        BufferedReader br = null;
        try{
            in=new FileReader(filePath+"/BrickBreakerScores.dat");
            br=new BufferedReader(in);
        } catch(IOException e){
            File a=new File(filePath+"/BrickBreakerScores.dat");
            try{
                a.createNewFile();
                in=new FileReader(filePath+"/BrickBreakerScores.dat");
                br=new BufferedReader(in);
            } catch (IOException ee){
                System.err.println("FILE READ ERROR\n");
                ee.printStackTrace();
            }
        }
        MyArrayList<String> buff=new MyArrayList<>();
        buff.createList();
        String a="";
        try{
            a=br.readLine();
        } catch (IOException e){
            System.err.println("File read error");
            e.printStackTrace();
        }
        while(a!=null){
            try{
                buff.add(buff.size()+1,a);
                a=br.readLine();
            } catch (IOException e){
                System.err.println("File read error");
                e.printStackTrace();
            }
        }
        for(int i=1;i<=buff.size()/2;i++){
            names.add(i,buff.get(i));
        }
        for(int i=1;i<=buff.size()/2;i++){
            try{
                scores.add(i,Integer.parseInt(buff.get(buff.size()/2+i)));
            } catch (NumberFormatException e){
                System.err.println(buff.get(i)+"parse int error");
                scores.add(i,0);
            }
        }
    }
    public void run(){
        screenLevel();
        screenHiScore();
    }


    //additional for integration
    private static boolean isAutoPause=true;
    public void pauseGame(){
        isMouseInScreen=false;
    }
    /**
     * @param flag
     * if this is set to true, the game pauses once the mouse leaves the
     *  play area. The game can also be paused using the pauseGame() method.
     */
    public void setAutoPause(boolean flag){
        isAutoPause=flag;
    }
    @Override
    public void stop() {
        super.stop();
        //I'll just add methods here
        bgm.stop();
        bounceClip.stop();
    }
    
    
    
    ////////////////////////
    //actions
    private void actStart(){    //start or pause
        if(waitingForStart){
            waitingForStart = false;
        }
        if(isPaused){
            isPaused=false;
        }
        if(hasBegan && !isPaused){
            //System.out.println("mouse exited");
            //isMouseInScreen=(!isAutoPause)||false;//!isAutoPause bitmask
            isMouseInScreen=false;
        }
    }
    
    private void actConSettings(){  //open control settings
        if(ctrlConfig.jframe == null  || !ctrlConfig.jframe.isVisible()){
            ctrlConfig.openSettings(1, 0, 1, 2, 3, 4);
        }
    }
    
    private void waitForStart(boolean isCleanup){   //isCleanup = is it needed to reset to 0 yung hold down ctr
        waitingForStart = true;
        while(waitingForStart){
            uartReadLoopIterate();
            /*try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {}*/
        }
        if(isCleanup){
            uartLoopExitResetHoldDown();
        }
    }
    
    private void moveRt() {
        //System.out.println("rt yo");
        double curX = paddle.getX();
        if(curX + PADDLE_SPEED > (WIDTH - PADDLE_WIDTH)){
            paddle.setLocation(WIDTH-PADDLE_WIDTH,paddle.getY());
        } else {
            paddle.setLocation(curX + PADDLE_SPEED,paddle.getY());
        }
    }

    private void moveLf() {
        //System.out.println("lf ye");
        double curX = paddle.getX();
        if(curX - PADDLE_SPEED < 0.5 * PADDLE_WIDTH){
            paddle.setLocation(0,paddle.getY());
        } else {
            paddle.setLocation(curX - PADDLE_SPEED,paddle.getY());
        }
    }
    
    private void moveRt(double percentSpd) {
        int speed = (int)(percentSpd/100*PADDLE_SPEED);
        if(speed<0){
            speed = 1;
        }
        //System.out.println("rt yo");
        double curX = paddle.getX();
        if(curX + speed > (WIDTH - PADDLE_WIDTH)){
            paddle.setLocation(WIDTH-PADDLE_WIDTH,paddle.getY());
        } else {
            paddle.setLocation(curX + speed,paddle.getY());
        }
    }

    private void moveLf(double percentSpd) {
        int speed = (int)(percentSpd/100*PADDLE_SPEED);
        if(speed<0){
            speed = 1;
        }
        //System.out.println("lf ye");
        double curX = paddle.getX();
        if(curX - speed < 0.5 * PADDLE_WIDTH){
            paddle.setLocation(0,paddle.getY());
        } else {
            paddle.setLocation(curX - speed,paddle.getY());
        }
    }    
    
    private void uartReadLoopIterate() {
        ////////////////////////////////////////////////////////////////////////////
        //////////////////////----- UCMI Connection -----///////////////////////////
        ////////////////////////////////////////////////////////////////////////////
        //uart controls (PADDLE only)
        // ucmi version (both players)
        if (ucmi.isPortConnected && !ctrlConfig.disableCMInput){/////////////////BUG (seems like it)
            //---- [2],[3]    PADDLE CONTROLS
            //paddle control ("LEFT" and "RIGHT") ==> index [2] and [3]
            if(ctrlConfig.uartControls[1][2].isAnalogAxis()){
                int val1 = ucmi.p[1].readAnalogAxis(ctrlConfig.uartControls[1][2]);  //0 to 255
                val1 = val1 - 128;
                double percentage = val1;  //(val1/100)*100;      //i made maximum analog stick 100 (instead of 127)
                if(percentage > 0){
                    /*if(percentage>100){
                    percentage = 100;
                    }*/
                    moveRt(percentage);
                } else if (percentage < 0){
                    percentage = -percentage;
                    /*if(percentage>100){
                    percentage = 100;
                    }*/
                    moveLf(percentage);
                }
            } else {
                if(ucmi.p[1].readButton(ctrlConfig.uartControls[1][2])){
                    moveRt();
                }
                if(ucmi.p[1].readButton(ctrlConfig.uartControls[1][3])){
                    moveLf();
                }
                
            }
            
            
            //---- [4]-Start; [5] ConSett
            //any:  start   [NO repeat press on hold]
            if(ucmi.p[1].readButton(ctrlConfig.uartControls[1][4])){
                switch (ctrlConfig.uartHolddownWaitCount[1][4]){
                    case ControlConfig.UART_HOLD_DOWN_WAIT_TIME:
                        //actStart();   [NO repeat press on hold]
                        break;
                    case 0:
                        actStart();
                        ctrlConfig.uartHolddownWaitCount[1][4]++;
                        break;
                    default:
                        ctrlConfig.uartHolddownWaitCount[1][4]++;
                        break;
                }
            } else {ctrlConfig.uartHolddownWaitCount[1][4]=0;}
            
            
            //any: quit   [NO repeat press on hold]
            if(ucmi.p[1].readButton(ctrlConfig.uartControls[1][5])){
                switch (ctrlConfig.uartHolddownWaitCount[1][5]){
                    case ControlConfig.UART_HOLD_DOWN_WAIT_TIME:
                        //actQuit();   [NO repeat press on hold]
                        break;
                    case 0:
                        actConSettings();
                        ctrlConfig.uartHolddownWaitCount[1][5]++;
                        break;
                    default:
                        ctrlConfig.uartHolddownWaitCount[1][5]++;
                        break;
                }
            } else {ctrlConfig.uartHolddownWaitCount[1][5]=0;}
        }
    }
    
    

    private void uartLoopExitResetHoldDown() {
        //////////////////////// UCMI YEAH
        //since by this point is outside read loop
        ctrlConfig.uartHolddownWaitCount[1][4]=0;
        ctrlConfig.uartHolddownWaitCount[1][5]=0;
    }
}

