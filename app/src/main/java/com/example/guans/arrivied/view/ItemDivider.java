package com.example.guans.arrivied.view;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by guans on 2017/7/29.
 */

public class ItemDivider extends RecyclerView.ItemDecoration {
    private ColorDrawable colorDrawable;
//    private LinearGradient linearGradient;
//    private Paint paint;

    public ItemDivider() {
        super();
//        paint=new Paint();
        colorDrawable = new ColorDrawable(Color.GRAY);
//        linearGradient=new LinearGradient(0,0,100,100,new int[]{Color.WHITE,Color.GRAY,Color.WHITE},new float[]{0,50,100}, Shader.TileMode.MIRROR);
//        paint.setShader(linearGradient);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams childParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom();
            colorDrawable.setBounds(parent.getPaddingLeft() + childParams.getMarginStart(), top, parent.getWidth() - parent.getPaddingRight() - childParams.getMarginEnd(), top + 1);
            colorDrawable.draw(c);
//            c.drawRect(parent.getPaddingLeft(),top,parent.getWidth()-parent.getPaddingRight(),top+1,paint);
        }

    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int currentPosition = parent.getChildLayoutPosition(view);
        if (currentPosition == state.getItemCount() - 1) {
            outRect.set(0, 0, 0, 0);
        } else {
            outRect.set(0, 0, 0, 1);
        }
    }
}
