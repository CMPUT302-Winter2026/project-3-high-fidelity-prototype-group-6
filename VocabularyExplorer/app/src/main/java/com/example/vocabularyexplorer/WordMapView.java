package com.example.vocabularyexplorer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class WordMapView extends View {

    private float scaleFactor = 1.0f;
    private float translateX = 0f;
    private float translateY = 0f;
    private float relatednessThreshold = 0.0f; // 0.0 = show all, 1.0 = show only center

    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;

    private List<WordNode> nodes = new ArrayList<>();
    private Paint boxPaint;
    private Paint textPaint;
    private Paint linePaint;
    private Paint circlePaint;

    private static final float CIRCLE_RADIUS = 650f;

    public static class WordNode {
        String title;
        List<String> definitions;
        float x, y;
        boolean expanded = true;
        int parentIndex = 0;
        float relatedness; // 0.0 (most related/center) to 1.0 (least related)

        public WordNode(String title, List<String> definitions, float x, float y, float relatedness) {
            this.title = title;
            this.definitions = definitions;
            this.x = x;
            this.y = y;
            this.relatedness = relatedness;
        }

        public WordNode(String title, List<String> definitions, float x, float y, int parentIndex, float relatedness) {
            this(title, definitions, x, y, relatedness);
            this.parentIndex = parentIndex;
        }
    }

    public WordMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setRelatednessThreshold(float threshold) {
        this.relatednessThreshold = threshold;
        invalidate();
    }

    /**
     * Updates the center word of the map.
     * If the word is "Medicine", it restores all associated nodes.
     * Otherwise, it clears them and only shows the searched word.
     */
    public void setCenterWord(String word) {
        if (word == null || word.isEmpty()) return;

        if (word.equalsIgnoreCase("Medicine")) {
            loadDefaultData();
        } else {
            nodes.clear();
            nodes.add(new WordNode(word, new ArrayList<>(), 1000, 1000, 0.0f));
        }
        invalidate();
    }

    private void init(Context context) {
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, new GestureListener());

        float density = getContext().getResources().getDisplayMetrics().density;

        boxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boxPaint.setColor(Color.parseColor("#EEEEEE"));
        boxPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(2f * density);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.parseColor("#80C8E6C9"));
        circlePaint.setStyle(Paint.Style.FILL);

        loadDefaultData();
        
        translateX = 0; 
        translateY = 0;
    }

    private void loadDefaultData() {
        nodes.clear();
        // Relatedness: 0.0 = core, higher = more specific/less related
        nodes.add(new WordNode("Medicine", new ArrayList<>(), 1000, 1000, 0.0f));

        List<String> defs1 = new ArrayList<>();
        defs1.add("1. nurse");
        defs1.add("2. woman doctor");
        defs1.add("3. medicine woman");
        defs1.add("4. a medicine woman or nurse");
        nodes.add(new WordNode("maskihkîwiskwêw", defs1, 900, 200, 3, 0.8f));

        List<String> defs2 = new ArrayList<>();
        defs2.add("1. medicine man, herbalist");
        defs2.add("2. shaman");
        nodes.add(new WordNode("maskihkîwiyiniw", defs2, 600, 700, 0, 0.4f));

        List<String> defs3 = new ArrayList<>();
        defs3.add("1. medicine");
        defs3.add("2. herb, plant");
        defs3.add("3. medicinal root");
        nodes.add(new WordNode("maskihkiy", defs3, 1400, 1000, 0, 0.2f));

        List<String> defs4 = new ArrayList<>();
        defs4.add("1. tea, herbal tea, infused tea");
        defs4.add("2. liquid medicine");
        nodes.add(new WordNode("maskihkîwâpoy", defs4, 1100, 1450, 0, 0.5f));

        List<String> defs5 = new ArrayList<>();
        defs5.add("1. bag, sack");
        defs5.add("2. luggage");
        defs5.add("3. medicine bag");
        nodes.add(new WordNode("maskimot", defs5, 400, 1700, 0, 0.9f));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(translateX, translateY);
        canvas.scale(scaleFactor, scaleFactor, 0, 0);

        canvas.drawCircle(1000, 1000, CIRCLE_RADIUS, circlePaint);

        for (int i = 1; i < nodes.size(); i++) {
            WordNode node = nodes.get(i);
            if (node.relatedness <= (1.0f - relatednessThreshold)) {
                WordNode parent = nodes.get(node.parentIndex);
                // Only draw line if parent is also visible
                if (parent.relatedness <= (1.0f - relatednessThreshold)) {
                    canvas.drawLine(parent.x, parent.y, node.x, node.y, linePaint);
                }
            }
        }

        for (WordNode node : nodes) {
            if (node.relatedness <= (1.0f - relatednessThreshold)) {
                drawNode(canvas, node);
            }
        }

        canvas.restore();
    }

    private void drawNode(Canvas canvas, WordNode node) {
        float density = getContext().getResources().getDisplayMetrics().density;

        if (scaleFactor < 0.4f) {
            float dotRadius = 35 * density;
            canvas.drawCircle(node.x, node.y, dotRadius, boxPaint);

            Paint dotStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
            dotStroke.setStyle(Paint.Style.STROKE);
            dotStroke.setColor(Color.BLACK);
            dotStroke.setStrokeWidth(2f * density);
            canvas.drawCircle(node.x, node.y, dotRadius, dotStroke);
            return;
        }

        float padding = 16 * density;
        float titleSize = 20 * density;
        float defSize = 16 * density;
        float lineSpacing = 6 * density;
        float cornerRadius = 25 * density;

        boolean showDefinitions = !node.definitions.isEmpty() && node.expanded && scaleFactor > 0.7f;
        boolean isCenterNode = node.definitions.isEmpty() && node.relatedness == 0.0f;

        textPaint.setTextSize(titleSize);
        float maxTextWidth = textPaint.measureText(node.title);
        float totalHeight = titleSize + padding * 2;

        if (showDefinitions) {
            textPaint.setTextSize(defSize);
            for (String def : node.definitions) {
                maxTextWidth = Math.max(maxTextWidth, textPaint.measureText(def));
                totalHeight += defSize + lineSpacing;
            }
        }

        float width = maxTextWidth + padding * 2.5f;
        float height = totalHeight;

        RectF rect = new RectF(node.x - width / 2, node.y - height / 2, node.x + width / 2, node.y + height / 2);
        
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, boxPaint);
        
        Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setColor(Color.BLACK);
        stroke.setStrokeWidth(1.5f * density);
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, stroke);

        textPaint.setTextSize(titleSize);
        textPaint.setFakeBoldText(isCenterNode);
        
        if (isCenterNode) {
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(node.title, node.x, node.y - height / 2 + padding + titleSize * 0.8f, textPaint);
        } else {
            textPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(node.title, node.x - width / 2 + padding, node.y - height / 2 + padding + titleSize * 0.8f, textPaint);
        }
        textPaint.setFakeBoldText(false);
        textPaint.setTextAlign(Paint.Align.LEFT); // Reset for definitions

        if (showDefinitions) {
            textPaint.setTextSize(defSize);
            float currentY = node.y - height / 2 + padding + titleSize + lineSpacing + defSize * 0.8f;
            for (String def : node.definitions) {
                canvas.drawText(def, node.x - width / 2 + padding, currentY, textPaint);
                currentY += defSize + lineSpacing;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
            invalidate();
            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            translateX -= distanceX;
            translateY -= distanceY;
            invalidate();
            return true;
        }
    }
}
