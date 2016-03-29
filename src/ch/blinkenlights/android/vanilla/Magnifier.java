package ch.blinkenlights.android.vanilla;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class Magnifier implements FullPlaybackActivity.arrowPositionChangedListener {

    // The magnifier uses a plain button and manipulates its background
    private ImageButton mMagnifier;
    private int mNumColorsShown;
    private Paint[] mMoodPaints;
    private float mColorWidth;
    private float mColorHeight;

   // private View mParentView;
    private FullPlaybackActivity mParentActivity;

    // The parameters for the layout needed to display the magnifier (contains a button in a relative layout)
    private RelativeLayout.LayoutParams layoutParams;

    //@SuppressWarnings("all")
    public Magnifier(float width, float height, int numColorsShown, Paint[] moodPaints, FullPlaybackActivity parentActivity)
    {
        mParentActivity = parentActivity;
        mMoodPaints = moodPaints;
        mNumColorsShown = numColorsShown;

        if (mMagnifier == null) {
            ImageButton b = (ImageButton) mParentActivity.findViewById(R.id.btn_magnifier);
            mColorHeight = height;
            mColorWidth = width / numColorsShown;

            // Make the view clickable so it eats touch events
            b.setClickable(true);
            b.setOnClickListener(null);

            layoutParams = new RelativeLayout.LayoutParams((int) width, (int) height);
            layoutParams.leftMargin = 0;
            layoutParams.topMargin = -10;


            mMagnifier = b;
            //mMagnifier.setVisibility(View.VISIBLE);
        } else {
            mMagnifier.setVisibility(View.VISIBLE);
        }
    }

    public void show()
    {
        mMagnifier.setVisibility(View.VISIBLE);
    }

    public void hide()
    {
        mMagnifier.setVisibility(View.INVISIBLE);
    }

    public void updateBackground(int centerColorIndex)
    {
        final Paint[] magnifierPaints = new Paint[mNumColorsShown];
        int paintCounter = 0;
        mMoodPaints = mParentActivity.getMoodBarColors();
        //System.out.println("THE CENTER" + centerColorIndex + " HALF = " + mNumColorsShown/2);
        for (int i = centerColorIndex - (mNumColorsShown / 2); i <= centerColorIndex + (mNumColorsShown / 2); i++)
        {
            if (i <= 0 || i >= 1000)
            {
                magnifierPaints[paintCounter] = new Paint();
                magnifierPaints[paintCounter].setColor(Color.BLACK);
            }
            else {
                magnifierPaints[paintCounter] = mMoodPaints[i];
                //System.out.println("Setting paint " + i + " to " + mMoodPaints[i]);
            }
            paintCounter++;
        }
        mMagnifier.setBackground(new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                int paintCounter = 0;
                for (float i = 0; i < mMagnifier.getWidth(); i = i + (mMagnifier.getWidth() / mNumColorsShown))
                {
                    //this
                    if (paintCounter == mNumColorsShown) return;
                    //System.out.println("i = " + i);
                    //System.out.println("width = " + mMagnifier.getWidth());
                    canvas.drawRect(i, 0 , i + (mMagnifier.getWidth() / mNumColorsShown), mMagnifier.getHeight(), magnifierPaints[paintCounter]);
                    paintCounter++;
                }
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return 0;
            }
        });
    }

    public void setPosition(float x, float y)
    {
        layoutParams.leftMargin = (int) x - 60;
        layoutParams.topMargin = (int) -40;
        //System.out.println("setting to pos " + x + " " + y);
        //System.out.println("setting to pos " + layoutParams.leftMargin + " " + layoutParams.topMargin);
        mMagnifier.setBackgroundColor(Color.BLACK);

        RelativeLayout magContainer = (RelativeLayout) mParentActivity.findViewById(R.id.magnify_holder);

        mMagnifier.setLayoutParams(layoutParams);
        SeekBar root = (SeekBar) mParentActivity.findViewById(R.id.seek_bar);
        //root.getParent().bringChildToFront(magContainer);
        //root.getParent().bringChildToFront(mMagnifier);

    }


    @Override
    public void arrowPositionChanged(int thumbPosX, int seekBarWidth, boolean updateColors) {
        // Get the color closest to arrow to use as center of magnifier
        int closestColor = thumbPosX * 1000 / seekBarWidth;
        //System.out.println("CLOSEST COL = " + closestColor);
        if (updateColors)
        {
            updateBackground(closestColor);
        }
    }
}
