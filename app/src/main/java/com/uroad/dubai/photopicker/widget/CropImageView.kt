package com.uroad.dubai.photopicker.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.widget.ImageView
import com.uroad.dubai.R

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

@SuppressLint("AppCompatCustomView")
class CropImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : ImageView(context, attrs, defStyle) {

    companion object {

        /******************************** 图片缩放位移控制的参数  */
        private const val MAX_SCALE = 4.0f  //最大缩放比，图片缩放后的大小与中间选中区域的比值
        private const val NONE = 0   // 初始化
        private const val DRAG = 1   // 拖拽
        private const val ZOOM = 2   // 缩放
        private const val ROTATE = 3 // 旋转
        private const val ZOOM_OR_ROTATE = 4  // 缩放或旋转

    }

    private val styles = arrayOf(Style.RECTANGLE, Style.CIRCLE)

    private var mMaskColor = -0x51000000   //暗色
    /**
     * 返回焦点框边框颜色
     */
    private var focusColor = -0x557f7f80
    private var mBorderWidth = 1         //焦点边框的宽度（画笔宽度）
    private var mFocusWidth = 250         //焦点框的宽度
    private var mFocusHeight = 250        //焦点框的高度
    private var mDefaultStyleIndex = 0    //默认焦点框的形状

    private var mStyle = styles[mDefaultStyleIndex]
    private val mBorderPaint = Paint()
    private val mFocusPath = Path()
    private val mFocusRect = RectF()

    private var mImageWidth: Int = 0
    private var mImageHeight: Int = 0
    private var mRotatedImageWidth: Int = 0
    private var mRotatedImageHeight: Int = 0
    private var mMatrix = Matrix()      //图片变换的matrix
    private val savedMatrix = Matrix() //开始变幻的时候，图片的matrix
    private val pA = PointF()          //第一个手指按下点的坐标
    private val pB = PointF()          //第二个手指按下点的坐标
    private val midPoint = PointF()    //两个手指的中间点
    private val doubleClickPos = PointF()  //双击图片的时候，双击点的坐标
    private var mFocusMidPoint = PointF()  //中间View的中间点
    private var mode = NONE            //初始的模式
    private var doubleClickTime: Long = 0   //第二次双击的时间
    private var rotation = 0.0        //手指旋转的角度，不是90的整数倍，可能为任意值，需要转换成level
    private var oldDist = 1f          //双指第一次的距离
    private var sumRotateLevel = 0     //旋转的角度，90的整数倍
    private var mMaxScale = MAX_SCALE//程序根据不同图片的大小，动态得到的最大缩放比
    private var isInited = false   //是否经过了 onSizeChanged 初始化
    private var mSaving = false    //是否正在保存
    private var disposable: Disposable? = null
    /**
     * 图片保存完成的监听
     */
    private var mListener: OnBitmapSaveListener? = null
    /**
     * @return 获取当前图片显示的矩形区域
     */
    private val imageMatrixRect: RectF
        get() {
            val rectF = RectF()
            rectF.set(0f, 0f, drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat())
            mMatrix.mapRect(rectF)
            return rectF
        }

    /**
     * 返回焦点框宽度
     */
    /**
     * 设置焦点框的宽度
     */
    var focusWidth: Int
        get() = mFocusWidth
        set(width) {
            mFocusWidth = width
            initImage()
        }

    /**
     * 获取焦点框的高度
     */
    /**
     * 设置焦点框的高度
     */
    var focusHeight: Int
        get() = mFocusHeight
        set(height) {
            mFocusHeight = height
            initImage()
        }

    /**
     * 返回阴影颜色
     */
    /**
     * 设置阴影颜色
     */
    var maskColor: Int
        get() = mMaskColor
        set(color) {
            mMaskColor = color
            invalidate()
        }

    /**
     * 返回焦点框边框绘制宽度
     */
    val borderWidth: Float
        get() = mBorderWidth.toFloat()

    /**
     * 获取焦点框的形状
     */
    /**
     * 设置焦点框的形状
     */
    var focusStyle: Style
        get() = mStyle
        set(style) {
            this.mStyle = style
            invalidate()
        }

    /******************************** 中间的FocusView绘图相关的参数  */
    enum class Style {
        RECTANGLE, CIRCLE
    }

    init {
        mFocusWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mFocusWidth.toFloat(), resources.displayMetrics).toInt()
        mFocusHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mFocusHeight.toFloat(), resources.displayMetrics).toInt()
        mBorderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mBorderWidth.toFloat(), resources.displayMetrics).toInt()
        val a = context.obtainStyledAttributes(attrs, R.styleable.CropImageView)
        mMaskColor = a.getColor(R.styleable.CropImageView_cropMaskColor, mMaskColor)
        focusColor = a.getColor(R.styleable.CropImageView_cropBorderColor, focusColor)
        mBorderWidth = a.getDimensionPixelSize(R.styleable.CropImageView_cropBorderWidth, mBorderWidth)
        mFocusWidth = a.getDimensionPixelSize(R.styleable.CropImageView_cropFocusWidth, mFocusWidth)
        mFocusHeight = a.getDimensionPixelSize(R.styleable.CropImageView_cropFocusHeight, mFocusHeight)
        mDefaultStyleIndex = a.getInteger(R.styleable.CropImageView_cropStyle, mDefaultStyleIndex)
        mStyle = styles[mDefaultStyleIndex]
        a.recycle()

        //只允许图片为当前的缩放模式
        scaleType = ImageView.ScaleType.MATRIX
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        initImage()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        initImage()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        initImage()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        initImage()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        isInited = true
        initImage()
    }

    /**
     * 初始化图片和焦点框
     */
    private fun initImage() {
        val d = drawable
        if (!isInited || d == null) return
        mode = NONE
        mMatrix = imageMatrix
        mRotatedImageWidth = d.intrinsicWidth
        mImageWidth = mRotatedImageWidth
        mRotatedImageHeight = d.intrinsicHeight
        mImageHeight = mRotatedImageHeight
        //计算出焦点框的中点的坐标和上、下、左、右边的x或y的值
        val viewWidth = width
        val viewHeight = height
        val midPointX = (viewWidth / 2).toFloat()
        val midPointY = (viewHeight / 2).toFloat()
        mFocusMidPoint = PointF(midPointX, midPointY)
        if (mStyle == Style.CIRCLE) {
            val focusSize = Math.min(mFocusWidth, mFocusHeight)
            mFocusWidth = focusSize
            mFocusHeight = focusSize
        }
        mFocusRect.left = mFocusMidPoint.x - mFocusWidth / 2
        mFocusRect.right = mFocusMidPoint.x + mFocusWidth / 2
        mFocusRect.top = mFocusMidPoint.y - mFocusHeight / 2
        mFocusRect.bottom = mFocusMidPoint.y + mFocusHeight / 2
        //适配焦点框的缩放比例（图片的最小边不小于焦点框的最小边）
        val fitFocusScale = getScale(mImageWidth, mImageHeight, mFocusWidth, mFocusHeight, true)
        mMaxScale = fitFocusScale * MAX_SCALE
        //适配显示图片的ImageView的缩放比例（图片至少有一边是铺满屏幕的显示的情形）
        val fitViewScale = getScale(mImageWidth, mImageHeight, viewWidth, viewHeight, false)
        //确定最终的缩放比例,在适配焦点框的前提下适配显示图片的ImageView，
        //方案：首先满足适配焦点框，如果还能适配显示图片的ImageView，则适配它，即取缩放比例的最大值。
        //采取这种方案的原因：有可能图片很长或者很高，适配了ImageView的时候可能会宽/高已经小于焦点框的宽/高
        val scale = if (fitViewScale > fitFocusScale) fitViewScale else fitFocusScale
        //图像中点为中心进行缩放
        mMatrix.setScale(scale, scale, (mImageWidth / 2).toFloat(), (mImageHeight / 2).toFloat())
        val mImageMatrixValues = FloatArray(9)
        mMatrix.getValues(mImageMatrixValues) //获取缩放后的mImageMatrix的值
        val transX = mFocusMidPoint.x - (mImageMatrixValues[2] + mImageWidth * mImageMatrixValues[0] / 2)  //X轴方向的位移
        val transY = mFocusMidPoint.y - (mImageMatrixValues[5] + mImageHeight * mImageMatrixValues[4] / 2) //Y轴方向的位移
        mMatrix.postTranslate(transX, transY)
        imageMatrix = mMatrix
        invalidate()
    }

    /**
     * 计算边界缩放比例 isMinScale 是否最小比例，true 最小缩放比例， false 最大缩放比例
     */
    private fun getScale(bitmapWidth: Int, bitmapHeight: Int, minWidth: Int, minHeight: Int, isMinScale: Boolean): Float {
        val scaleX = minWidth.toFloat() / bitmapWidth
        val scaleY = minHeight.toFloat() / bitmapHeight
        return if (isMinScale) {
            if (scaleX > scaleY) scaleX else scaleY
        } else {
            if (scaleX < scaleY) scaleX else scaleY
        }
    }

    /**
     * 绘制焦点框
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (Style.RECTANGLE == mStyle) {
            mFocusPath.addRect(mFocusRect, Path.Direction.CCW)
            canvas.save()
            canvas.clipRect(0, 0, width, height)
            canvas.clipPath(mFocusPath)
            canvas.drawColor(mMaskColor)
            canvas.restore()
        } else if (Style.CIRCLE == mStyle) {
            val radius = Math.min((mFocusRect.right - mFocusRect.left) / 2, (mFocusRect.bottom - mFocusRect.top) / 2)
            mFocusPath.addCircle(mFocusMidPoint.x, mFocusMidPoint.y, radius, Path.Direction.CCW)
            canvas.save()
            canvas.clipRect(0, 0, width, height)
            canvas.clipPath(mFocusPath)
            canvas.drawColor(mMaskColor)
            canvas.restore()
        }
        mBorderPaint.color = focusColor
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.strokeWidth = mBorderWidth.toFloat()
        mBorderPaint.isAntiAlias = true
        canvas.drawPath(mFocusPath, mBorderPaint)
        mFocusPath.reset()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mSaving || null == drawable) {
            return super.onTouchEvent(event)
        }
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN  //第一个点按下
            -> {
                savedMatrix.set(mMatrix)   //以后每次需要变换的时候，以现在的状态为基础进行变换
                pA.set(event.x, event.y)
                pB.set(event.x, event.y)
                mode = DRAG
            }
            MotionEvent.ACTION_POINTER_DOWN  //第二个点按下
            -> {
                if (event.actionIndex > 1) return true
                pA.set(event.getX(0), event.getY(0))
                pB.set(event.getX(1), event.getY(1))
                midPoint.set((pA.x + pB.x) / 2, (pA.y + pB.y) / 2)
                oldDist = spacing(pA, pB)
                savedMatrix.set(mMatrix)  //以后每次需要变换的时候，以现在的状态为基础进行变换
                if (oldDist > 10f) mode = ZOOM_OR_ROTATE//两点之间的距离大于10才有效
            }
            MotionEvent.ACTION_MOVE -> {
                if (mode == ZOOM_OR_ROTATE) {
                    val pC = PointF(event.getX(1) - event.getX(0) + pA.x, event.getY(1) - event.getY(0) + pA.y)
                    val a = spacing(pB.x, pB.y, pC.x, pC.y).toDouble()
                    val b = spacing(pA.x, pA.y, pC.x, pC.y).toDouble()
                    val c = spacing(pA.x, pA.y, pB.x, pB.y).toDouble()
                    if (a >= 10) {
                        val cosB = (a * a + c * c - b * b) / (2.0 * a * c)
                        val angleB = Math.acos(cosB)
                        val pid4 = Math.PI / 4
                        //旋转时，默认角度在 45 - 135 度之间
                        mode = if (angleB > pid4 && angleB < 3 * pid4) ROTATE
                        else ZOOM
                    }
                }
                if (mode == DRAG) {
                    mMatrix.set(savedMatrix)
                    mMatrix.postTranslate(event.x - pA.x, event.y - pA.y)
                    fixTranslation()
                    imageMatrix = mMatrix
                } else if (mode == ZOOM) {
                    val newDist = spacing(event.getX(0), event.getY(0), event.getX(1), event.getY(1))
                    if (newDist > 10f) {
                        mMatrix.set(savedMatrix)
                        // 这里之所以用 maxPostScale 矫正一下，主要是防止缩放到最大时，继续缩放图片会产生位移
                        val tScale = Math.min(newDist / oldDist, maxPostScale())
                        if (tScale != 0f) {
                            mMatrix.postScale(tScale, tScale, midPoint.x, midPoint.y)
                            fixScale()
                            fixTranslation()
                            imageMatrix = mMatrix
                        }
                    }
                } else if (mode == ROTATE) {
                    val pC = PointF(event.getX(1) - event.getX(0) + pA.x, event.getY(1) - event.getY(0) + pA.y)
                    val a = spacing(pB.x, pB.y, pC.x, pC.y).toDouble()
                    val b = spacing(pA.x, pA.y, pC.x, pC.y).toDouble()
                    val c = spacing(pA.x, pA.y, pB.x, pB.y).toDouble()
                    if (b > 10) {
                        val cosA = (b * b + c * c - a * a) / (2.0 * b * c)
                        var angleA = Math.acos(cosA)
                        val ta = (pB.y - pA.y).toDouble()
                        val tb = (pA.x - pB.x).toDouble()
                        val tc = (pB.x * pA.y - pA.x * pB.y).toDouble()
                        val td = ta * pC.x + tb * pC.y + tc
                        if (td > 0) {
                            angleA = 2 * Math.PI - angleA
                        }
                        rotation = angleA
                        mMatrix.set(savedMatrix)
                        mMatrix.postRotate((rotation * 180 / Math.PI).toFloat(), midPoint.x, midPoint.y)
                        imageMatrix = mMatrix
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                if (mode == DRAG) {
                    if (spacing(pA, pB) < 50) {
                        var now = System.currentTimeMillis()
                        if (now - doubleClickTime < 500 && spacing(pA, doubleClickPos) < 50) {
                            doubleClick(pA.x, pA.y)
                            now = 0
                        }
                        doubleClickPos.set(pA)
                        doubleClickTime = now
                    }
                } else if (mode == ROTATE) {
                    var rotateLevel = Math.floor((rotation + Math.PI / 4) / (Math.PI / 2)).toInt()
                    if (rotateLevel == 4) rotateLevel = 0
                    mMatrix.set(savedMatrix)
                    mMatrix.postRotate((90 * rotateLevel).toFloat(), midPoint.x, midPoint.y)
                    if (rotateLevel == 1 || rotateLevel == 3) {
                        val tmp = mRotatedImageWidth
                        mRotatedImageWidth = mRotatedImageHeight
                        mRotatedImageHeight = tmp
                    }
                    fixScale()
                    fixTranslation()
                    imageMatrix = mMatrix
                    sumRotateLevel += rotateLevel
                }
                mode = NONE
            }
        }
        //解决部分机型无法拖动的问题
        ViewCompat.postInvalidateOnAnimation(this)
        return true
    }

    /**
     * 修正图片的缩放比
     */
    private fun fixScale() {
        val imageMatrixValues = FloatArray(9)
        mMatrix.getValues(imageMatrixValues)
        val currentScale = Math.abs(imageMatrixValues[0]) + Math.abs(imageMatrixValues[1])
        val minScale = getScale(mRotatedImageWidth, mRotatedImageHeight, mFocusWidth, mFocusHeight, true)
        mMaxScale = minScale * MAX_SCALE

        //保证图片最小是占满中间的焦点空间
        if (currentScale < minScale) {
            val scale = minScale / currentScale
            mMatrix.postScale(scale, scale)
        } else if (currentScale > mMaxScale) {
            val scale = mMaxScale / currentScale
            mMatrix.postScale(scale, scale)
        }
    }

    /**
     * 修正图片的位移
     */
    private fun fixTranslation() {
        val imageRect = RectF(0f, 0f, mImageWidth.toFloat(), mImageHeight.toFloat())
        mMatrix.mapRect(imageRect)  //获取当前图片（缩放以后的）相对于当前控件的位置区域，超过控件的上边缘或左边缘为负
        var deltaX = 0f
        var deltaY = 0f
        if (imageRect.left > mFocusRect.left) {
            deltaX = -imageRect.left + mFocusRect.left
        } else if (imageRect.right < mFocusRect.right) {
            deltaX = -imageRect.right + mFocusRect.right
        }
        if (imageRect.top > mFocusRect.top) {
            deltaY = -imageRect.top + mFocusRect.top
        } else if (imageRect.bottom < mFocusRect.bottom) {
            deltaY = -imageRect.bottom + mFocusRect.bottom
        }
        mMatrix.postTranslate(deltaX, deltaY)
    }

    /**
     * 获取当前图片允许的最大缩放比
     */
    private fun maxPostScale(): Float {
        val imageMatrixValues = FloatArray(9)
        mMatrix.getValues(imageMatrixValues)
        val curScale = Math.abs(imageMatrixValues[0]) + Math.abs(imageMatrixValues[1])
        return mMaxScale / curScale
    }

    /**
     * 计算两点之间的距离
     */
    private fun spacing(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val x = x1 - x2
        val y = y1 - y2
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    /**
     * 计算两点之间的距离
     */
    private fun spacing(pA: PointF, pB: PointF): Float {
        return spacing(pA.x, pA.y, pB.x, pB.y)
    }

    /**
     * 双击触发的方法
     */
    private fun doubleClick(x: Float, y: Float) {
        val p = FloatArray(9)
        mMatrix.getValues(p)
        val curScale = Math.abs(p[0]) + Math.abs(p[1])
        val minScale = getScale(mRotatedImageWidth, mRotatedImageHeight, mFocusWidth, mFocusHeight, true)
        if (curScale < mMaxScale) {
            //每次双击的时候，缩放加 minScale
            val toScale = Math.min(curScale + minScale, mMaxScale) / curScale
            mMatrix.postScale(toScale, toScale, x, y)
        } else {
            val toScale = minScale / curScale
            mMatrix.postScale(toScale, toScale, x, y)
            fixTranslation()
        }
        imageMatrix = mMatrix
    }

    /**
     * @param expectWidth     期望的宽度
     * @param exceptHeight    期望的高度
     * @param isSaveRectangle 是否按矩形区域保存图片
     * @return 裁剪后的Bitmap
     */
    private fun getCropBitmap(expectWidth: Int, exceptHeight: Int, isSaveRectangle: Boolean): Bitmap? {
        if (expectWidth <= 0 || exceptHeight < 0) return null
        var srcBitmap: Bitmap? = (drawable as BitmapDrawable).bitmap
        srcBitmap = rotate(srcBitmap, sumRotateLevel * 90)  //最好用level，因为角度可能不是90的整数
        return if (srcBitmap != null) makeCropBitmap(srcBitmap, mFocusRect, imageMatrixRect, expectWidth, exceptHeight, isSaveRectangle)
        else null
    }

    /**
     * @param bitmap  要旋转的图片
     * @param degrees 选择的角度（单位 度）
     * @return 旋转后的Bitmap
     */
    private fun rotate(bitmap: Bitmap?, degrees: Int): Bitmap? {
        if (degrees != 0 && bitmap != null) {
            val matrix = Matrix()
            matrix.setRotate(degrees.toFloat(), bitmap.width.toFloat() / 2, bitmap.height.toFloat() / 2)
            try {
                val rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                if (bitmap != rotateBitmap) {
                    return rotateBitmap
                }
            } catch (ex: OutOfMemoryError) {
                ex.printStackTrace()
            }
        }
        return bitmap
    }

    /**
     * @param bitmap          需要裁剪的图片
     * @param focusRect       中间需要裁剪的矩形区域
     * @param imageMatrixRect 当前图片在屏幕上的显示矩形区域
     * @param expectWidth     希望获得的图片宽度，如果图片宽度不足时，拉伸图片
     * @param exceptHeight    希望获得的图片高度，如果图片高度不足时，拉伸图片
     * @param isSaveRectangle 是否希望按矩形区域保存图片
     * @return 裁剪后的图片的Bitmap
     */
    private fun makeCropBitmap(bitmap: Bitmap, focusRect: RectF, imageMatrixRect: RectF, expectWidth: Int, exceptHeight: Int, isSaveRectangle: Boolean): Bitmap {
        val scale = imageMatrixRect.width() / bitmap.width
        var left = ((focusRect.left - imageMatrixRect.left) / scale).toInt()
        var top = ((focusRect.top - imageMatrixRect.top) / scale).toInt()
        var width = (focusRect.width() / scale).toInt()
        var height = (focusRect.height() / scale).toInt()

        if (left < 0) left = 0
        if (top < 0) top = 0
        if (left + width > bitmap.width) width = bitmap.width - left
        if (top + height > bitmap.height) height = bitmap.height - top

        try {
            var newBitmap = Bitmap.createBitmap(bitmap, left, top, width, height)
            if (expectWidth != width || exceptHeight != height) {
                newBitmap = Bitmap.createScaledBitmap(bitmap, expectWidth, exceptHeight, true)
                if (mStyle == Style.CIRCLE && !isSaveRectangle) {
                    //如果是圆形，就将图片裁剪成圆的
                    val length = Math.min(expectWidth, exceptHeight)
                    val radius = length / 2
                    val circleBitmap = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(circleBitmap)
                    val bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                    val paint = Paint()
                    paint.shader = bitmapShader
                    canvas.drawCircle(expectWidth / 2f, exceptHeight / 2f, radius.toFloat(), paint)
                    newBitmap = circleBitmap
                }
            }
            return newBitmap
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        }
        return bitmap
    }

    /**
     * @param folder          希望保存的文件夹
     * @param expectWidth     希望保存的图片宽度
     * @param exceptHeight    希望保存的图片高度
     * @param isSaveRectangle 是否希望按矩形区域保存图片
     */
    fun saveBitmapToFile(folder: File, expectWidth: Int, exceptHeight: Int, isSaveRectangle: Boolean) {
        if (mSaving) return
        mSaving = true
        val croppedImage = getCropBitmap(expectWidth, exceptHeight, isSaveRectangle)
        croppedImage?.let {
            var outputFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
            var saveFile = createFile(folder, "IMG_", ".jpg")
            if (mStyle == Style.CIRCLE && !isSaveRectangle) {
                outputFormat = Bitmap.CompressFormat.PNG
                saveFile = createFile(folder, "IMG_", ".png")
            }
            val finalOutputFormat = outputFormat
            val finalSaveFile = saveFile
            saveOutput(it, finalOutputFormat, finalSaveFile)
        }
    }

    /**
     * 根据系统时间、前缀、后缀产生一个文件
     */
    private fun createFile(folder: File, prefix: String, suffix: String): File {
        if (!folder.exists() || !folder.isDirectory) folder.mkdirs()
        try {
            val nomedia = File(folder, ".nomedia")  //在当前文件夹底下创建一个 .nomedia 文件
            if (!nomedia.exists()) nomedia.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)
        val filename = prefix + dateFormat.format(Date(System.currentTimeMillis())) + suffix
        return File(folder, filename)
    }

    /**
     * 将图片保存在本地
     */
    private fun saveOutput(croppedImage: Bitmap, outputFormat: Bitmap.CompressFormat, saveFile: File) {
        disposable = Observable.fromCallable {
            val outputStream = context.contentResolver.openOutputStream(Uri.parse("file://$saveFile"))
            if (outputStream != null) {
                croppedImage.compress(outputFormat, 90, outputStream)
                outputStream.close()
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            mSaving = false
            if (!croppedImage.isRecycled) croppedImage.recycle()
            mListener?.onBitmapSaveSuccess(saveFile)
        }, {
            mListener?.onBitmapSaveError(saveFile)
        }, {}, {
            mListener?.onBitmapSaveStart()
        })
    }

    /**
     * 设置焦点框边框颜色
     */
    fun setBorderColor(color: Int) {
        focusColor = color
        invalidate()
    }

    /**
     * 设置焦点边框宽度
     */
    fun setBorderWidth(width: Int) {
        mBorderWidth = width
        invalidate()
    }

    override fun onDetachedFromWindow() {
        disposable?.dispose()
        super.onDetachedFromWindow()
    }

    interface OnBitmapSaveListener {
        fun onBitmapSaveStart()
        fun onBitmapSaveSuccess(file: File)

        fun onBitmapSaveError(file: File)
    }

    fun setOnBitmapSaveListener(listener: OnBitmapSaveListener) {
        mListener = listener
    }
}