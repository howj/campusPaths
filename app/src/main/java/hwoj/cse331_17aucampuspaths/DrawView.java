package hwoj.cse331_17aucampuspaths;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import java.util.*;

import hw8.*;

/**
 * Created by howj on 12/8/2017.
 */

public class DrawView extends AppCompatImageView {

    private boolean drawCircle = false;
    private boolean drawEndCircle = false;
    private boolean drawPath = false;

    // X Y coords for our start building
    private float startX;
    private float startY;

    // X Y coords for our end building
    private float endX;
    private float endY;

    // List for drawing the path
    List<Float> coordsList;

    public DrawView(Context context) {
        super(context);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();

        if (drawCircle) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5.f);
            paint.setColor(Color.RED);
            canvas.drawCircle(startX, startY, 10.f, paint);
        }

        if (drawEndCircle) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5.f);
            paint.setColor(Color.CYAN);
            canvas.drawCircle(endX, endY, 10.f, paint);
        }

        if (drawPath) {
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(5.f);
            paint.setColor(Color.MAGENTA);

            if (coordsList.size() >= 4) {
                float startX = coordsList.get(0);
                float startY = coordsList.get(1);

                for (int i = 2; i + 1 < coordsList.size(); i += 2) {
                    float endX = coordsList.get(i);
                    float endY = coordsList.get(i + 1);

                    // draw the line
                    canvas.drawLine(startX, startY, endX, endY, paint);

                    startX = endX;
                    startY = endY;
                }
            }
        }
    }

    public void toggleDrawCircle(double x, double y, boolean first, boolean newBuilding) {
        startX = (float) (x * 0.25);
        startY = (float) (y * 0.25);
        if (first) {
            drawCircle = !drawCircle;
            this.invalidate();
        } else {
            drawCircle = !drawCircle;
            this.invalidate();
            if (newBuilding) {
                drawCircle = !drawCircle;
                this.invalidate();
            }
        }
    }

    public void toggleDrawEndCircle(double x, double y, boolean first, boolean newBuilding) {
        endX = (float) (x * 0.25);
        endY = (float) (y * 0.25);
        if (first) {
            drawEndCircle = !drawEndCircle;
            this.invalidate();
        } else {
            drawEndCircle = !drawEndCircle;
            this.invalidate();
            if (newBuilding) {
                drawEndCircle = !drawEndCircle;
                this.invalidate();
            }
        }
    }

    public void drawShortestPath(Map<Coordinate2D, Double> pathMap, double startX, double startY) {
        List<Coordinate2D> coordinates = new ArrayList<Coordinate2D>(pathMap.keySet());
        coordinates = coordinates.subList(1, coordinates.size());
        coordsList = new ArrayList<Float>();
        coordsList.add((float) (startX * 0.25));
        coordsList.add((float) (startY * 0.25));

        for (Coordinate2D c : coordinates) {
            // Add the coords to the list
            coordsList.add((float) (c.getX() * 0.25));
            coordsList.add((float) (c.getY() * 0.25));
        }

        // When done, callBack
        drawPath = !drawPath;
        this.invalidate();
    }

    public void clear() {
        drawCircle = false;
        drawEndCircle = false;
        drawPath = false;
        this.invalidate();
    }
}
