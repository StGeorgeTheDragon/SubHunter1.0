package msm.programming.subhunter10;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.Window;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.util.Log;
import android.widget.ImageView;
import java.util.Random;


public class SubHunter extends Activity {

    int numberHorizontalPixels;
    int numberVerticalPixels;
    int blockSize;
    int gridWidth = 30;
    int gridHeight;
    float horizontalTouched = -100;
    float verticalTouched = -100;
    int subHorizontalPosition;
    int subVerticalPosition;
    boolean hit = false;
    int shotsTaken;
    int distanceFromSub;
    boolean debugging = true;


    //Here are all the objects(instances) of the classes that we need to do some drawing
    ImageView gameView;
    Bitmap blankBitmap;
    Canvas canvas;
    Paint paint;

            /*
            Android runs this code just before the player sees the app.
            This makes it a good place to add the code for one time setup phase
             */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Get the display size of the device's screen resolution
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Initialize our size based variable based on the screen resolution
        numberHorizontalPixels = size.x;
        numberVerticalPixels = size.y;
        blockSize = numberHorizontalPixels / gridWidth;
        gridHeight = numberVerticalPixels / blockSize;

        //Initialize all the objects ready for drawing
        blankBitmap = Bitmap.createBitmap(numberHorizontalPixels, numberVerticalPixels, Bitmap.Config.ARGB_8888);

        canvas = new Canvas(blankBitmap);
        gameView = new ImageView(this);
        paint = new Paint();

        //Tell android to set our drawing as the view for this app
        setContentView(gameView);

        Log.d("Debugging","In onCreate");
        newGame();
        draw();
    }
    /*
    This code will execute when a new games needs to be started.
    It will happen when the app is first started and after the player wins a game.
     */
    void newGame()
    {
        Random random = new Random();
        subHorizontalPosition = random.nextInt(gridWidth);
        subVerticalPosition = random.nextInt(gridHeight);
        shotsTaken = 0;
        Log.d("Debugging","In onCreate");
    }

    /*
    Here we will do the drawing. The grid lines, the hud and the touch indicator.
     */
    void draw()
    {
        gameView.setImageBitmap(blankBitmap);

        //wipe the screen with a white color
        canvas.drawColor(Color.argb(255,255,255,255));

        //Change the paint color to black
        paint.setColor(Color.argb(255,0,0,0));

        //Draw vertical lines of grid
        for(int i =0; i < gridWidth; i++){
            canvas.drawLine(blockSize*i,0,blockSize*i,numberVerticalPixels,paint);
        }

        //draw the horizontal lines of the grid
        for(int i =0; i < gridHeight; i++){
            canvas.drawLine(0,blockSize*i,numberHorizontalPixels,blockSize*i,paint);
        }
        //Draw the players shot
        canvas.drawRect(horizontalTouched*blockSize,verticalTouched*blockSize,
                (horizontalTouched*blockSize)+blockSize,(verticalTouched*blockSize)+blockSize,paint);
        //draw the sub
       // canvas.drawRect(subHorizontalPosition*blockSize,subVerticalPosition*blockSize,
       //         (subHorizontalPosition*blockSize)+blockSize,(subVerticalPosition*blockSize)+blockSize,paint);
        //Re-size the text appropriate for the score and distance text
        paint.setTextSize(blockSize*2);
        paint.setColor(Color.argb(255,0,0,255));
        canvas.drawText("Shots Taken: "+shotsTaken+" Distance: "+distanceFromSub,blockSize,blockSize*1.75f,paint);


        Log.d("Debugging","In onCreate");
        if(debugging) {
            printDebuggingText();
        }
    }
    /*
    This part of the code will handle detecting that the player has tapped the screen.
     */
    @Override public boolean onTouchEvent(MotionEvent motionEvent)
    {
        Log.d("Debugging","In onCreate");


        //Has the player removed their finger from the screen?
        if((motionEvent.getAction() & MotionEvent.ACTION_MASK)==MotionEvent.ACTION_UP)
        {
            //Process the player's shot by passing the coordinates of the player's finger to takeShot
            takeShot(motionEvent.getX(), motionEvent.getY());
        }

    return true;
    }
    /*
    The code here will execute when the player taps the screen.
    It will calculate the distance from the sub and decide hit or miss
     */
    void takeShot(float touchX, float touchY)
    {
        Log.d("Debugging","In onCreate");
        shotsTaken++;
        //Convert the float screen coordinates into int grid coordinates
        horizontalTouched = (int)touchX/blockSize;
        verticalTouched = (int)touchY/blockSize;
        //Did it hit the sub?
        hit = horizontalTouched == subHorizontalPosition && verticalTouched == subVerticalPosition;
        //How far away was horizontally and vertically was the shot from the sub
        int horizontalGap = (int)horizontalTouched - subHorizontalPosition;
        int verticalGap = (int)verticalTouched - subVerticalPosition;
        //Use pythagoras's theorem to get the distance travelled in a straight line
        distanceFromSub = (int)Math.sqrt(((horizontalGap*horizontalGap)+ (verticalGap*verticalGap)));
            if(hit)
            boom();
                else {
                    //Sub is moved in a random direction up to 2 squares
                    int max = 2;
                Random random = new Random();
                int rand = random.nextInt(max) * (random.nextBoolean()? -1:1);
                subVerticalPosition = subVerticalPosition + rand;
                subHorizontalPosition = subHorizontalPosition + rand;
                    draw();
            }

    }
    //This code says "BOOM!!!"
    void boom()
    {
    gameView.setImageBitmap(blankBitmap);
    //wipe the screen with red color
        canvas.drawColor(Color.argb(255,255,0,0));

        //Draw some huge white text
        paint.setColor(Color.argb(255,0,255,255));
        paint.setTextSize(blockSize*5);
        canvas.drawText("BOOM!!", blockSize*4,blockSize*14,paint);
        //draw some prompt restarting
        paint.setTextSize(blockSize*2);
        canvas.drawText("Take a shot to start again", blockSize*8,blockSize*18,paint);
        //start new game
        newGame();
    }
    //This code prints debugging text
    void printDebuggingText()
    {
    paint.setTextSize(blockSize);
    canvas.drawText("numberHorizontalPixels = "+numberHorizontalPixels,50,blockSize*3,paint);
        canvas.drawText("numberVerticalPixels = "+numberVerticalPixels,50,blockSize*4,paint);
        canvas.drawText("blockSize = "+blockSize,50,blockSize*5,paint);
        canvas.drawText("gridWidth = "+gridWidth,50,blockSize*6,paint);
        canvas.drawText("gridHeight = "+gridHeight,50,blockSize*7,paint);
        canvas.drawText("horizontalTouched = "+horizontalTouched,50,blockSize*8,paint);
        canvas.drawText("verticalTouched = "+verticalTouched,50,blockSize*9,paint);
        canvas.drawText("subHorizontalPosition = "+subHorizontalPosition,50,blockSize*10,paint);
        canvas.drawText("subVerticalPosition = "+subVerticalPosition,50,blockSize*11,paint);
        canvas.drawText("hit = "+hit,50,blockSize*12,paint);
        canvas.drawText("shotsTaken = "+shotsTaken,50,blockSize*13,paint);
        canvas.drawText("debugging = "+debugging,50,blockSize*14,paint);
    }
    /*
    Log.d("numberHorizontalPixels", " " + numberHorizontalPixels);
    Log.d("numberVerticalPixels", " "+numberVerticalPixels);
    Log.d("blockSize", " " +blockSize);
    Log.d("gridWidth", " "+gridWidth);
    Log.d("gridHeight", " " +gridHeight);
    Log.d("horizontalTouched", " "+horizontalTouched);
    Log.d("verticalTouched", " "+verticalTouched);
    Log.d("subHorizontalPosition", " " +subHorizontalPosition);
    Log.d("subVerticalPosition", " "+subVerticalPosition);
    Log.d("hit", " " +hit);
    Log.d("shotsTaken", ""+shotsTaken);
    Log.d("debugging", ""+debugging);
    Log.d("distanceFromSub", ""+distanceFromSub);
     */
}