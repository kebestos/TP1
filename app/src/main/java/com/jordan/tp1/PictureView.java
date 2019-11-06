package com.jordan.tp1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Debug;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Scroller;

import androidx.core.widget.ScrollerCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class PictureView extends View {
    
    //number of line and column where affect the images
    private static  int column = 1;
    private static  int line = 1;

    private Paint mPaint;

    private ArrayList<String> listImages;

    private ArrayList<BitmapDrawable> listBitmap;

    //dimension of the screen
    private int width;
    private int height;

    //scale
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScale = 1f;


    //Scroll
    private PointF mTouch = new PointF(); // Touch point
    //private float initialTouchX; // start touch point
    //private Scroller mScroller;
    //private float deltaX;
    //private float deltaY;
    //private float initialTouchY;

    private static int MIN_SWIPE_DISTANCE_X = 100;
    private static int MIN_SWIPE_DISTANCE_Y = 100;

    private static int MAX_SWIPE_DISTANCE_X = 1000;
    private static int MAX_SWIPE_DISTANCE_Y = 1000;

    private GestureDetector mGestureDetector;

    //initialize the view
    public PictureView(Context context) {
        super(context);

        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);

        //get list of filePath of the images
        listImages = getAllShownImagesPath((Activity) context);
        //set a list of bitmap with the list of images, and compress
        listBitmap = saveAndCompressShownImage();

        //get the dimension of the screen
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        //Zoom
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGesture());

        //Scroll
        mGestureDetector = new GestureDetector(context, new DetectSwipeGestureListener());

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //temporary variable to fix the screen
        int templeft=0;
        int temptop=0;
        int tempWidth = (int) ((width/column)*mScale);
        int tempHeight = (int) ((height/line)*mScale);
        int tempWidthDeux = width/column;


        //Log.d("canvas", "Draw");
        for(BitmapDrawable bD : listBitmap){

            bD.setBounds(templeft,temptop,tempWidth,tempHeight);
            bD.draw(canvas);

            if(tempWidth >=((width/column)) * (column -1)){
                tempHeight += height/line;
                temptop += height/line;
            }

            if(tempWidth < ((width/column)) * (column -1)){
                templeft += width/column;
                tempWidth += width/column;
            }
            else{
                tempWidth = (int) ((width/column)*mScale);
                templeft = 0;
            }
        }
    }

    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }

    @SuppressLint("WrongThread")
    private ArrayList<BitmapDrawable> saveAndCompressShownImage(){
        ArrayList<BitmapDrawable> bitmaplist = new ArrayList<BitmapDrawable>();

        for(String fileP : listImages){
            Bitmap bitmap = null;

            File f = new File(fileP);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);

                ByteArrayOutputStream boas = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, boas);
                BitmapDrawable bD = new BitmapDrawable(bitmap);
                bitmaplist.add(bD);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return bitmaplist;
    }

    public class ScaleGesture extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScale *= detector.getScaleFactor();
            Log.d("Editable", "scaale" + mScale);

            if(mScale<0.4f){
                mScale = 0.4f;
            }
            else if(mScale > 1.f){
                mScale = 1.f;
            }

            if(mScale < 0.4f && mScale > 0){
                column =7;
                line = 7;
            }
            else if(mScale < 0.5f && mScale > 0.4f){
                column =6;
                line = 6;
            }
            else if(mScale < 0.6f && mScale > 0.5f){
                column =5;
                line = 5;
            }
            else if(mScale < 0.7f && mScale > 0.6f ){
                column =4;
                line = 4;
            }
            else if(mScale < 0.8f && mScale > 0.7f ){
                column =3;
                line = 3;
            }
            else if(mScale < 0.9f && mScale > 0.8f ){
                column =2;
                line = 2;
            }
            else if(mScale < 1f && mScale > 0.90f ){
                column =1;
                line = 1;
            }
            invalidate();
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    /*public boolean doTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("DEBUG", "doTouchEvent: ACTION DOWN");
                //Log.d("DEBUG", "doTouchEvent->initialTouch = " + initialTouchX);
                getParent().requestDisallowInterceptTouchEvent(true);
                initialTouchX = event.getX();
                initialTouchY = event.getY();
                this.postInvalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("DEBUG", "doTouchEvent: ACTION MOVE");
                mTouch.x = event.getX();
                float currentY = event.getY();
                //Log.d("DEBUG", "doTouchEvent->mTouch.x = " + mTouch.x);
                //Log.d("DEBUG", "doTouchEvent->mTouch.y = " + mTouch.y);
                deltaY = currentY - initialTouchY;
                if(initialTouchY < currentY){
                    Log.d("DEBUG", "SCROLL BAS");
                    scrollBy(0,(int)deltaY);
                }
                else{
                    Log.d("DEBUG", "SCROLL HAUT");
                    scrollBy(0,(int)deltaY * (-1));
                }
                this.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                Log.d("DEBUG", "doTouchEvent: ACTION UP");
                getParent().requestDisallowInterceptTouchEvent(false);
                this.postInvalidate();
                break;

            default :
                return super.onTouchEvent(event);
        }
        return true;
    }*/

    public class DetectSwipeGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float deltaY = e1.getY() - e2.getY();

            float deltaYabs = Math.abs(deltaY);

            if(deltaYabs >= MIN_SWIPE_DISTANCE_Y && deltaYabs <= MAX_SWIPE_DISTANCE_Y){
                if(deltaY > 0){
                    scrollBy(0,100);
                }
                else{
                    scrollBy(0,-100);
                }
            }
            return true;
        }
    }




}
