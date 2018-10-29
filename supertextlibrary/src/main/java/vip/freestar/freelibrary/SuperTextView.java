package vip.freestar.freelibrary;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * 描述：
 * 作者：一颗浪星
 * 日期：2018/4/30
 * github：
 */
public class SuperTextView extends AppCompatTextView {

    //default value
    public static final int ICON_DIR_LEFT = 1, ICON_DIR_TOP = 2, ICON_DIR_RIGHT = 3, ICON_DIR_BOTTOM = 4;

    //icon
    private int mIconHeight;
    private int mIconWidth;
    private int mIconDirection;

    //corner
    private float mCornerRadius;
    private float mCornerRadiusTopLeft;
    private float mCornerRadiusTopRight;
    private float mCornerRadiusBottomLeft;
    private float mCornerRadiusBottomRight;

    //BorderWidth
    private float mBorderDashWidth = 0;
    private float mBorderDashGap = 0;

    private int mBorderWidthNormal = 0;

    //BorderColor
    private int mBorderColorNormal;
    private int mBorderColorSelected = -1;

    //Background
    private int mBackgroundColorNormal;
    private int mBackgroundColorSelected = -1;

    private GradientDrawable mBackgroundNormal;
    private GradientDrawable mBackgroundSelected;

    // Text
    private int mTextColorNormal;
    private int mTextColorSelected;

    //Icon
    private Drawable mIcon = null;
    private Drawable mIconNormal;
    private Drawable mIconSelected;

    //typeface
    private String mTypefacePath;


    private float mBorderRadii[] = new float[8];

    /**
     * Cache the touch slop from the context that created the view.
     */
    private Context mContext;

    /**
     * 是否设置对应的属性
     */
    private String mTextSelected;
    private String mTextNormal;

    public SuperTextView(Context context) {
        this(context, null);
    }

    public SuperTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttributeSet(context, attrs);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);

        if (mIconSelected != null) {
            mIcon = selected ? mIconSelected : mIconNormal;
            setIcon();
        }

        if (mTextSelected != null) {
            setText(selected ? mTextSelected : mTextNormal);
        }

    }

    /**
     * 初始化控件属性
     *
     * @param context
     * @param attrs
     */
    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (context == null || attrs == null) {
            setup();
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SuperTextView);
        //corner
        mCornerRadius = a.getDimensionPixelSize(R.styleable.SuperTextView_corner_radius, -1);
        mCornerRadiusTopLeft = a.getDimensionPixelSize(R.styleable.SuperTextView_corner_radius_top_left, 0);
        mCornerRadiusTopRight = a.getDimensionPixelSize(R.styleable.SuperTextView_corner_radius_top_right, 0);
        mCornerRadiusBottomLeft = a.getDimensionPixelSize(R.styleable.SuperTextView_corner_radius_bottom_left, 0);
        mCornerRadiusBottomRight = a.getDimensionPixelSize(R.styleable.SuperTextView_corner_radius_bottom_right, 0);
        //border
        mBorderDashWidth = a.getDimensionPixelSize(R.styleable.SuperTextView_border_dash_width, 0);
        mBorderDashGap = a.getDimensionPixelSize(R.styleable.SuperTextView_border_dash_gap, 0);
        mBorderWidthNormal = a.getDimensionPixelSize(R.styleable.SuperTextView_border_width_normal, 0);

        mBorderColorNormal = a.getColor(R.styleable.SuperTextView_border_color_normal, Color.TRANSPARENT);
        mBorderColorSelected = a.getColor(R.styleable.SuperTextView_border_color_selected, mBorderColorSelected);
        //icon
        mIconNormal = a.getDrawable(R.styleable.SuperTextView_icon_src_normal);
        mIconSelected = a.getDrawable(R.styleable.SuperTextView_icon_src_selected);

        mIconWidth = a.getDimensionPixelSize(R.styleable.SuperTextView_icon_width, 0);
        mIconHeight = a.getDimensionPixelSize(R.styleable.SuperTextView_icon_height, 0);
        mIconDirection = a.getInt(R.styleable.SuperTextView_icon_direction, ICON_DIR_LEFT);

        //text
        mTextColorNormal = a.getColor(R.styleable.SuperTextView_text_color_normal, getCurrentTextColor());
        mTextColorSelected = a.getColor(R.styleable.SuperTextView_text_color_selected, mTextColorSelected);

        mTextNormal = a.getString(R.styleable.SuperTextView_text_normal);
        mTextSelected = a.getString(R.styleable.SuperTextView_text_selected);

        //background
        mBackgroundColorNormal = a.getColor(R.styleable.SuperTextView_background_color_normal, Color.TRANSPARENT);
        mBackgroundColorSelected = a.getColor(R.styleable.SuperTextView_background_color_selected, mBackgroundColorSelected);

        //typeface
        mTypefacePath = a.getString(R.styleable.SuperTextView_text_typeface);

        a.recycle();

        //setup
        setup();

    }

    /**
     * 设置
     */
    private void setup() {

        //未设置图片大小
        if (mIconHeight == 0 && mIconWidth == 0) {
            if (mIcon != null) {
                mIconWidth = mIcon.getIntrinsicWidth();
                mIconHeight = mIcon.getIntrinsicHeight();
            }
        }

        mIcon = mIconNormal;
        //设置ICON
        setIcon();

        //设置文本字体样式
        setTypeface();

        initDrawable();

        if (mTextNormal != null) {
            setText(mTextNormal);
        }
    }

    public void initDrawable() {
        int[][] states = new int[2][];
        //normal, selected  有状态要求的放前面 否则不生效
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{};

        // 有颜色
        if (mBackgroundColorSelected != -1 || mBorderColorSelected != -1) {
            //设置圆角
            mBackgroundNormal = new GradientDrawable();
            mBackgroundSelected = new GradientDrawable();
            setRadius();

            Drawable drawable = getBackground();
            StateListDrawable stateBackground;
            if (drawable != null && drawable instanceof StateListDrawable) {
                stateBackground = (StateListDrawable) drawable;
            } else {
                stateBackground = new StateListDrawable();
            }

            mBackgroundNormal.setColor(mBackgroundColorNormal);
            mBackgroundSelected.setColor(mBackgroundColorSelected);
            mBackgroundNormal.setStroke(mBorderWidthNormal, mBorderColorNormal, mBorderDashWidth, mBorderDashGap);
            mBackgroundSelected.setStroke(mBorderWidthNormal, mBorderColorSelected, mBorderDashWidth, mBorderDashGap);

            stateBackground.addState(states[0], mBackgroundSelected);
            stateBackground.addState(states[1], mBackgroundNormal);
            setBackground(stateBackground);
        } else if (mBackgroundColorNormal != 0 || mBorderColorNormal != 0) {
            mBackgroundNormal = new GradientDrawable();
            //设置圆角
            setRadius();
            mBackgroundNormal.setColor(mBackgroundColorNormal);
            mBackgroundNormal.setStroke(mBorderWidthNormal, mBorderColorNormal, mBorderDashWidth, mBorderDashGap);
            setBackground(mBackgroundNormal);
        }

        if (mTextColorSelected != 0) {
            //设置文本颜色
            int[] colors = new int[]{mTextColorSelected, mTextColorNormal};
            ColorStateList textColorStateList = new ColorStateList(states, colors);
            setTextColor(textColorStateList);
        }

    }

    /************************
     * Typeface
     ************************/

    public SuperTextView setTypeface(String typefacePath) {
        this.mTypefacePath = typefacePath;
        setTypeface();
        return this;
    }

    private void setTypeface() {
        if (!TextUtils.isEmpty(mTypefacePath)) {
            AssetManager assetManager = mContext.getAssets();
            Typeface typeface = Typeface.createFromAsset(assetManager, mTypefacePath);
            setTypeface(typeface);
        }
    }

    private void setIcon() {
        setIcon(mIcon, mIconWidth, mIconHeight, mIconDirection);
    }

    public SuperTextView setIconNormal(Drawable icon) {
        this.mIconNormal = icon;
        this.mIcon = icon;
        setIcon();
        return this;
    }

    private void setIcon(Drawable drawable, int width, int height, int direction) {
        if (drawable != null) {
            if (width != 0 && height != 0) {
                drawable.setBounds(0, 0, width, height);
            }
            switch (direction) {
                case ICON_DIR_LEFT:
                    setCompoundDrawables(drawable, null, null, null);
                    break;
                case ICON_DIR_TOP:
                    setCompoundDrawables(null, drawable, null, null);
                    break;
                case ICON_DIR_RIGHT:
                    setCompoundDrawables(null, null, drawable, null);
                    break;
                case ICON_DIR_BOTTOM:
                    setCompoundDrawables(null, null, null, drawable);
                    break;
            }
        }
    }

    /*********************
     * border
     *********************/

    public void setBorderWidth(int normal) {
        this.mBorderWidthNormal = normal;
//        setBorder();
    }

    private void setRadiusRadii() {
        mBackgroundNormal.setCornerRadii(mBorderRadii);
        if (mBackgroundSelected != null) {
            mBackgroundSelected.setCornerRadii(mBorderRadii);
        }
    }

    private void setRadius() {
        if (mCornerRadius >= 0) {
            mBorderRadii[0] = mCornerRadius;
            mBorderRadii[1] = mCornerRadius;
            mBorderRadii[2] = mCornerRadius;
            mBorderRadii[3] = mCornerRadius;
            mBorderRadii[4] = mCornerRadius;
            mBorderRadii[5] = mCornerRadius;
            mBorderRadii[6] = mCornerRadius;
            mBorderRadii[7] = mCornerRadius;
            setRadiusRadii();
            return;
        }

        if (mCornerRadius < 0) {
            mBorderRadii[0] = mCornerRadiusTopLeft;
            mBorderRadii[1] = mCornerRadiusTopLeft;
            mBorderRadii[2] = mCornerRadiusTopRight;
            mBorderRadii[3] = mCornerRadiusTopRight;
            mBorderRadii[4] = mCornerRadiusBottomRight;
            mBorderRadii[5] = mCornerRadiusBottomRight;
            mBorderRadii[6] = mCornerRadiusBottomLeft;
            mBorderRadii[7] = mCornerRadiusBottomLeft;
            setRadiusRadii();
        }
    }

    private float dp2px(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

}
