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
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Debug;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class PictureView extends View {

    //number of line and column where affect the images
    private static  int column = 5;
    private static  int line = 5;

    private Paint mPaint;

    private ArrayList<String> listImages;

    private ArrayList<BitmapDrawable> listBitmap;

    //dimension of the screen
    private int width;
    private int height;


    //scale
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScale = 1f;

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

        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGesture());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //temporary variable to fix the screen
        int templeft=0;
        int temptop=0;
        int tempWidth = (int) ((width/column)*mScale);
        int tempHeight = (int) ((height/line)*mScale);

        //Log.d("canvas", "Draw");
        for(BitmapDrawable bD : listBitmap){

            bD.setBounds(templeft,temptop,tempWidth,tempHeight);
            bD.draw(canvas);

             if(tempWidth >= (width/column)*(column-1)){
                tempHeight += height/ line;
                temptop += height/ line;
            }

            if(tempWidth < (width/column)*(column-1)){
                templeft += width/ column;
                tempWidth += width/ column;
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

            if(mScale < 0.95f && mScale > 0.5f){
                column =6;
                line = 6;
            }
            else if(mScale < 0.5f && mScale > 0){
                column =7;
                line = 7;
            }
            invalidate();
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);

        /*switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_MOVE:
                for(BitmapDrawable bD : listBitmap){
                    //Rect bot = bD.getBounds();
                    //bD.setBounds(Math.round(bot.left*mScale),Math.round(bot.top*mScale),Math.round(bot.right*mScale),Math.round(bot.bottom*mScale));
                    //tempWidth *= mScale;
                    //tempHeight *= mScale;

                }
                //Log.d("Editable", "MOOVE");
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:

                invalidate();
                break;
        }*/
        return true;
    }
}
