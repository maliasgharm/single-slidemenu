package org.noandish.library.singleslidermenu

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView

class SingleCircleSliderMenuView @kotlin.jvm.JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val defX =
        context.resources.displayMetrics.widthPixels - (context.resources.displayMetrics.widthPixels / 10f)
    var isOpened = false
        private set(value) {
            field = value
            isClickable = value
        }
    private val defRadius = context.resources.displayMetrics.widthPixels / 20f

    init {
        orientation = VERTICAL
        Handler().postDelayed({ bringToFront() }, 10)
        setOnClickListener {
            closeAll()
        }
        isClickable = false
    }

    fun closeAll() {
        var timeDelay = 0L
        for (i in 0 until childCount) {
            val v = getChildAt(i)
            if (v is CardView && v.tag is Boolean && v.tag as Boolean) {
                Handler().postDelayed({
                    v.animate().x(defX).start()
                    v.tag = false
                }, timeDelay)
                timeDelay += 100L
            }
        }
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        if (child != null)
            addedView(child)
    }

    @SuppressLint("ClickableViewAccessibility", "LongLogTag", "RtlHardcoded")
    private fun addedView(view: View) {
        val cardView = CardView(context)


        val parentView = RelativeLayout(context)
        val paramsParentView =
            RelativeLayout.LayoutParams(Utils.getScreenWidth(), context.resources.displayMetrics.widthPixels / 10)
        paramsParentView.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        paramsParentView.addRule(RelativeLayout.CENTER_VERTICAL)
        val imgV = ImageView(context)
        imgV.setImageResource(R.drawable.ic_menu_black_24dp)
        val paramsImgV = RelativeLayout.LayoutParams(
            context.resources.displayMetrics.widthPixels / 14,
            context.resources.displayMetrics.widthPixels / 10
        )
        paramsImgV.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            paramsImgV.marginStart = ((context.resources.displayMetrics.widthPixels / 14) / -1.5).toInt()
        } else {
            paramsImgV.leftMargin = ((context.resources.displayMetrics.widthPixels / 14) / -1.5).toInt()
        }
        view.isClickable = false

        paramsParentView.addRule(RelativeLayout.CENTER_VERTICAL)
        parentView.addView(imgV, paramsImgV)
//        Handler().postDelayed({
//            view.layoutParams.width = Utils.getScreenWidth()
//            view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
//        }, 200)
        val viewParams = LayoutParams(Utils.getScreenWidth(), ViewGroup.LayoutParams.MATCH_PARENT)
        viewParams.gravity = Gravity.LEFT
        parentView.addView(view, viewParams)
        cardView.addView(parentView, paramsParentView)
        cardView.radius = defRadius
        var lastMoved = 0f
        var isClose = true
        if (view is ViewGroup) {
//            cardView.id = view.id
//            view.id = -1
        }
        cardView.setOnTouchListener { v, event ->
            var countIsOpened = 0
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_UP) {
                countIsOpened = countIsOpened()
            }
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    val percent = (v.x / defX)
                    val radius = (percent * defRadius)
                    v.x = event.rawX
                    isClose = event.rawX - lastMoved > if (isClose) 20 else -20
                    lastMoved = event.rawX
                    cardView.radius = radius
                    if ((countIsOpened <= 1 && cardView.tag is Boolean && (cardView.tag as Boolean))
                        || countIsOpened <= 0
                    ) changeColorBackground(percent)
                }
                MotionEvent.ACTION_UP -> {
                    if (isClose && lastMoved != 0f || (v.x >= 0 && cardView.tag is Boolean && (cardView.tag as Boolean))) {
                        v.animate().x(defX).start()

                        if ((countIsOpened <= 1 && cardView.tag is Boolean && (cardView.tag as Boolean))
                            || countIsOpened <= 0
                        ) isOpened = false
                        v.tag = false
                    } else {
                        v.animate().x(0f).start()

                        if ((countIsOpened <= 1 && cardView.tag is Boolean && (cardView.tag as Boolean))
                            || countIsOpened <= 0
                        ) isOpened = true
                        v.tag = true
                        if (view.hasOnClickListeners()) {
                            Handler().postDelayed({
                                closeAll()
                                Handler().postDelayed({
                                    view.callOnClick()
                                }, 500)
                            }, 500)
                        }
                    }
                    lastMoved = 0f
                }
            }
            return@setOnTouchListener true
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            cardView.animate().setUpdateListener {
                val percent = (cardView.x / defX)
                val radius = (percent * defRadius)
                cardView.radius = radius

                if ((countIsOpened() <= 1 && cardView.tag is Boolean && (cardView.tag as Boolean))
                    || countIsOpened() <= 0
                ) {
                    changeColorBackground(percent)
                }
                isOpened = percent == 0f
            }
        } else {
            cardView.animate().setListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    val percent = (cardView.x / defX)
                    val radius = (percent * defRadius)
                    cardView.radius = radius
                    isOpened = percent == 0f
                    if ((countIsOpened() <= 1 && cardView.tag is Boolean && (cardView.tag as Boolean))
                        || countIsOpened() <= 0
                    ) {
                        changeColorBackground(percent)
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationStart(animation: Animator?) {
                    val percent = (cardView.x / defX)
                    val radius = (percent * defRadius)
                    cardView.radius = radius
                    isOpened = percent == 0f
                    if ((countIsOpened() <= 1 && cardView.tag is Boolean && (cardView.tag as Boolean))
                        || countIsOpened() <= 0
                    ) {
                        changeColorBackground(percent)
                    }
                }

            })
        }
        cardView.x = defX
        val paramsMain = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        paramsMain.setMargins(0, 0, 0, 20)
        super.addView(cardView, paramsMain)

        cardView.setCardBackgroundColor(
            when {
                view.background == null -> {
                    Color.parseColor("#eeeeeeee")
                }
                view.background is ColorDrawable -> (view.background as ColorDrawable).color
                else -> Color.WHITE
            }
        )

        imgV.setColorFilter(
            when {
                view.background == null -> Color.BLACK
                view.background is ColorDrawable -> getBlackOrWhaiteColor((view.background as ColorDrawable).color)
                else -> Color.BLACK
            }
        )

        imgV.visibility = View.VISIBLE
        Handler().postDelayed({ imgV.bringToFront() }, 10)

    }

    private fun changeColorBackground(percent: Float) {
        if (percent in 0.0..1.0) {
            val percentR = (1f - percent)
            val valueAlpha = (percentR * 180).toInt()
            setBackgroundColor(Color.argb(valueAlpha, 0, 0, 0))
        }
    }

    private fun countIsOpened(): Int {
        var isOpened = 0
        for (i in 0 until childCount) {
            val v = getChildAt(i)
            if (v.tag is Boolean && v.tag as Boolean) {
                isOpened++
            }
        }
        return isOpened
    }

    private fun getBlackOrWhaiteColor(color: Int): Int {
        val rgb = intArrayOf(Color.red(color), Color.green(color), Color.blue(color))
        val o = Math.round(
            (rgb[0] * 299.0 +
                    rgb[1] * 587 +
                    rgb[2] * 114) / 1000
        )
        return if (o > 125) Color.BLACK else Color.WHITE
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "SingleCircleSliderMenuView"

    }
}