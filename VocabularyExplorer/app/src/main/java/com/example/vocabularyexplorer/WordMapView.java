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

    private OnNodeClickListener onNodeClickListener;
    private OnMapChangeListener onMapChangeListener;

    private static final float CIRCLE_RADIUS = 650f;

    public interface OnNodeClickListener {
        void onNodeClick(Word word);
    }

    public void setOnNodeClickListener(OnNodeClickListener listener) {
        this.onNodeClickListener = listener;
    }

    public interface OnMapChangeListener {
        void onMapChanged(int visibleNodeCount);
    }

    public void setOnMapChangeListener(OnMapChangeListener listener) {
        this.onMapChangeListener = listener;
    }

    public static class WordNode {
        Word data;
        float x, y;
        boolean expanded = true;
        int parentIndex = 0;
        float relatedness; // 0.0 (most related/center) to 1.0 (least related)
        RectF lastDrawnRect = new RectF();

        public WordNode(Word data, float x, float y, float relatedness) {
            this.data = data;
            this.x = x;
            this.y = y;
            this.relatedness = relatedness;
        }

        public WordNode(Word data, float x, float y, int parentIndex, float relatedness) {
            this(data, x, y, relatedness);
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
        notifyMapChanged();
    }

    void notifyMapChanged() {
        if (onMapChangeListener != null) {
            int visibleCount = 0;
            for (WordNode node : nodes) {
                if (node.relatedness <= (1.0f - relatednessThreshold)) {
                    visibleCount++;
                }
            }
            onMapChangeListener.onMapChanged(visibleCount);
        }
    }

    public void zoomIn() {
        scaleFactor = Math.min(scaleFactor * 1.2f, 5.0f);
        invalidate();
    }

    public void zoomOut() {
        scaleFactor = Math.max(scaleFactor / 1.2f, 0.1f);
        invalidate();
    }

    public void returnToCenter() {
        scaleFactor = 1.0f;
        translateX = getWidth() / 2f - 1000f;
        translateY = getHeight() / 2f - 1000f;
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
            loadDefaultData(getContext());
        } else {
            nodes.clear();
            nodes.add(new WordNode(new Word(word, ""), 1000, 1000, 0.0f));
        }
        invalidate();
        notifyMapChanged();
    }

    private void init(Context context) {
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, new GestureListener());

        float density = context.getResources().getDisplayMetrics().density;

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

        loadDefaultData(context);
        
        translateX = 0; 
        translateY = 0;
    }

    private void loadDefaultData(Context context) {
        nodes.clear();
        // Relatedness: 0.0 = core, higher = more specific/less related
        nodes.add(new WordNode(new Word("Medicine", ""), 1000, 1000, 0.0f));

        Word nurse = new Word("maskihkîwiskwêw", "1. nurse\n2. woman doctor\n3. medicine woman\n4. a medicine woman or nurse");
        nurse.setCreePhrase1("maskihkîwiskwêw anima.");
        nurse.setEnglishPhrase1("That is a nurse.");
        nodes.add(new WordNode(nurse, 900, 200, 3, 0.8f));

        Word healer = new Word("maskihkîwiyiniw", "1. medicine man, herbalist\n2. shaman");
        nodes.add(new WordNode(healer, 600, 700, 0, 0.4f));

        Word medicine = new Word("maskihkiy", "1. medicine\n2. herb, plant\n3. medicinal root");
        medicine.setSyllabics("ᒪᐢᑭᐦᑭᐩ");
        medicine.setAdvancedLabel("NI-2");
        medicine.setIPATranscription("/ˈmʌs.kɪh.kiː/");
        medicine.setCreePhrase1(context.getString(R.string.maskihkiy_cree_phrase1));
        medicine.setEnglishPhrase1(context.getString(R.string.maskihkiy_english_phrase1));
        medicine.setCreePhrase2(context.getString(R.string.maskihkiy_cree_phrase2));
        medicine.setEnglishPhrase2(context.getString(R.string.maskihkiy_english_phrase2));
        medicine.setMorphologyImage(R.drawable.maskihkiy_word_parts);
        medicine.setMorphology(context.getString(R.string.maskihkiy_morphology));
        nodes.add(new WordNode(medicine, 1400, 1000, 0, 0.2f));

        Word tea = new Word("maskihkîwâpoy", "1. tea, herbal tea, infused tea\n2. liquid medicine");
        tea.setCreePhrase1("niyâlan maskihkîwâpoy.");
        tea.setEnglishPhrase1("Five herbal teas.");
        nodes.add(new WordNode(tea, 1100, 1450, 0, 0.5f));

        Word bag = new Word("maskimot", "1. bag, sack\n2. luggage\n3. medicine bag");
        bag.setSyllabics("ᒪᐢᑭᒧᐟ");
        bag.setAdvancedLabel("NI-1");
        bag.setIPATranscription("/ˈmʌs.kɪ.mʊt/");
        bag.setCreePhrase1(context.getString(R.string.maskimot_cree_phrase1));
        bag.setEnglishPhrase1(context.getString(R.string.maskimot_english_phrase1));
        bag.setCreePhrase2(context.getString(R.string.maskimot_cree_phrase2));
        bag.setEnglishPhrase2(context.getString(R.string.maskimot_english_phrase2));
        bag.setMorphologyImage(R.drawable.maskimot_word_parts);
        bag.setMorphology(context.getString(R.string.maskimot_morphology));
        nodes.add(new WordNode(bag, 400, 1700, 0, 0.9f));
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
            node.lastDrawnRect.set(node.x - dotRadius, node.y - dotRadius, node.x + dotRadius, node.y + dotRadius);
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

        boolean showDefinitions = !node.data.getDefinitions().isEmpty() && node.expanded && scaleFactor > 0.7f;
        boolean isCenterNode = node.data.getDefinitions().isEmpty() && node.relatedness == 0.0f;

        textPaint.setTextSize(titleSize);
        float maxTextWidth = textPaint.measureText(node.data.getTitle());
        float totalHeight = titleSize + padding * 2;

        String[] definitions = node.data.getDefinitions().isEmpty() ? new String[0] : node.data.getDefinitions().split("\n");

        if (showDefinitions) {
            textPaint.setTextSize(defSize);
            for (String def : definitions) {
                maxTextWidth = Math.max(maxTextWidth, textPaint.measureText(def));
                totalHeight += defSize + lineSpacing;
            }
        }

        float width = maxTextWidth + padding * 2.5f;
        float height = totalHeight;

        node.lastDrawnRect.set(node.x - width / 2, node.y - height / 2, node.x + width / 2, node.y + height / 2);
        
        canvas.drawRoundRect(node.lastDrawnRect, cornerRadius, cornerRadius, boxPaint);
        
        Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setColor(Color.BLACK);
        stroke.setStrokeWidth(1.5f * density);
        canvas.drawRoundRect(node.lastDrawnRect, cornerRadius, cornerRadius, stroke);

        textPaint.setTextSize(titleSize);
        textPaint.setFakeBoldText(isCenterNode);
        
        if (isCenterNode) {
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(node.data.getTitle(), node.x, node.y - height / 2 + padding + titleSize * 0.8f, textPaint);
        } else {
            textPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(node.data.getTitle(), node.x - width / 2 + padding, node.y - height / 2 + padding + titleSize * 0.8f, textPaint);
        }
        textPaint.setFakeBoldText(false);
        textPaint.setTextAlign(Paint.Align.LEFT); // Reset for definitions

        if (showDefinitions) {
            textPaint.setTextSize(defSize);
            float currentY = node.y - height / 2 + padding + titleSize + lineSpacing + defSize * 0.8f;
            for (String def : definitions) {
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

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (onNodeClickListener != null) {
                float mapX = (e.getX() - translateX) / scaleFactor;
                float mapY = (e.getY() - translateY) / scaleFactor;

                for (WordNode node : nodes) {
                    if (node.relatedness <= (1.0f - relatednessThreshold)) {
                        if (node.lastDrawnRect.contains(mapX, mapY)) {
                            onNodeClickListener.onNodeClick(node.data);
                            return true;
                        }
                    }
                }
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            returnToCenter();
        }
    }
}
