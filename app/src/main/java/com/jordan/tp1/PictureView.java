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
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class PictureView extends View {

    //nombre max d'image sur une ligne
    private static final int MAX_POINTERS = 7;
    //private Pointer[] mPointers = new Pointer[MAX_POINTERS];

    private Paint mPaint;

    private ArrayList<String> listImages;

    //dimension de l'écran
    private int width;
    private int height;

    /*class Pointer {
        float x = 0;
        float y = 0;
        int index = -1;
        int id = -1;
    }
*/
    //init la view
    public PictureView(Context context) {
        super(context);
        /*for (int i = 0; i<MAX_POINTERS; i++) {
            mPointers[i] = new Pointer();
        }*/
        listImages = getAllShownImagesPath((Activity) context);
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);

        //recup les dimensions de l'écran
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap = null;

        //varialble temporaire pour gérer l'affichage
        int templeft=0;
        int tempWidth = width/MAX_POINTERS;
        int tempHeight = height/MAX_POINTERS;


            //for(String fileP : listImages){

                File f = new File(listImages.get(0));
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                //inresample

                try {
                    //creation du bitmap en fonction du filePath
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
                    BitmapDrawable bD = new BitmapDrawable(bitmap);

                    //setPosition et largeur hauteru des images
                    //reste à placer plusieurs images en fonction des dimension de l'ecran à l'aide des variable temporaire
                    bD.setBounds(100,100,200,200);

                    bD.draw(canvas);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            //}
    }

    //recup une liste contenant le filePath de chaque image
    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }
}
