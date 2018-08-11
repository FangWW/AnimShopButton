/*林尽水源，便得一山，山有小口，仿佛若有光。*/
package com.w.animshopbutton

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.transition.ChangeBounds
import android.transition.Transition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.w.lib.R

/**
 * 自定义组件：购买数量，带减少增加按钮
 * @Author: FangJW
 * @Date: 2018/6/5 10:59
 */
class AmountView @SuppressLint("ResourceType")
constructor(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs), View.OnClickListener {
    private val llAmount: LinearLayout
    private var amount: Int = 0 //购买数量
    private var goodsStorage = 999 //商品库存
    private var minNum: Int = 0 //最少购买数量
    private var fixView: Boolean = false //最少购买数量

    private var mListeners = arrayListOf<(view: View, amount: Int) -> Unit>()

    private val etAmount: TextView
    private val btnDecrease: Button
    private val btnIncrease: Button

    private var isAniming = false

    companion object {
        //减少到最少
        const val LEAST = 1
    }

    private var onAmountStateListener: ((state: Int) -> Unit)? = null

    private var startWidth: Int = 0

    private var tvWidth: Int

    constructor(context: Context) : this(context, null)

    init {

        LayoutInflater.from(context).inflate(R.layout.view_amount, this)
        llAmount = findViewById<View>(R.id.llAmount) as LinearLayout
        etAmount = findViewById<View>(R.id.etAmount) as TextView
        btnDecrease = findViewById<View>(R.id.btnDecrease) as Button
        btnIncrease = findViewById<View>(R.id.btnIncrease) as Button
        btnDecrease.setOnClickListener(this)
        btnIncrease.setOnClickListener(this)
        etAmount.setOnClickListener(this)

        val obtainStyledAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.AmountView)
        val btnWidth = obtainStyledAttributes.getDimensionPixelSize(R.styleable.AmountView_btnWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
        tvWidth = obtainStyledAttributes.getDimensionPixelSize(R.styleable.AmountView_tvWidth, 80)
        val tvTextSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.AmountView_tvTextSize, 0)
        val btnTextSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.AmountView_btnTextSize, 0)
        obtainStyledAttributes.recycle()

        val btnParams = LinearLayout.LayoutParams(btnWidth, LinearLayout.LayoutParams.MATCH_PARENT)
        btnDecrease.layoutParams = btnParams
        btnIncrease.layoutParams = btnParams
        if (btnTextSize != 0) {
            btnDecrease.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnTextSize.toFloat())
            btnIncrease.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnTextSize.toFloat())
        }

        val textParams = LinearLayout.LayoutParams(tvWidth, LinearLayout.LayoutParams.MATCH_PARENT)
        etAmount.layoutParams = textParams
        if (tvTextSize != 0) {
            etAmount.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvTextSize.toFloat())
        }

        closeStyle()

        val changeBounds = ChangeBounds()
        changeBounds.duration = 300
        changeBounds.addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(p0: Transition?) {
                isAniming = false
            }

            override fun onTransitionResume(p0: Transition?) {
                isAniming = true
            }

            override fun onTransitionPause(p0: Transition?) {
                isAniming = false
            }

            override fun onTransitionCancel(p0: Transition?) {
                isAniming = false
            }

            override fun onTransitionStart(p0: Transition?) {
                isAniming = true
            }

        })

        mListeners.add { _: View, amount: Int ->
            if (amount == 0 || amount == 1) {
                TransitionManager.beginDelayedTransition(parent as ViewGroup, changeBounds)
                switchStyle()
            }
        }
    }

    fun getAmount(): Int = amount

    private fun switchStyle() {
        val params = layoutParams as ViewGroup.LayoutParams
        if (startWidth == 0) {
            startWidth = params.width
        }
        if (amount == 0) {//amount==0 是关闭状态
            params.width = startWidth
            closeStyle()
        } else {
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT
            openStyle()
        }
        //                params.topMargin = 100;
        layoutParams = params
    }

    private fun closeStyle() {
        btnDecrease.setTextColor(Color.WHITE)
        btnIncrease.setTextColor(Color.WHITE)
        btnDecrease.text = "+"
        btnIncrease.text = "+"
        llAmount.showDividers = SHOW_DIVIDER_NONE
        llAmount.background = context.getDrawable(R.drawable.amount_close_selector)
    }

    private fun openStyle() {
        llAmount.background = context.getDrawable(R.drawable.amount_open_selector)
        btnDecrease.setTextColor(resources.getColor(R.color.color_a7a7a7))
        btnIncrease.setTextColor(resources.getColor(R.color.color_f44b9b))
        btnDecrease.text = "-"
        btnIncrease.text = "+"
        llAmount.showDividers = SHOW_DIVIDER_MIDDLE
    }

    /**
     * adapter里面调用的时候 会重复添加  故改成这样子写法
     * 2个回掉  一个默认的 一个给使用者的
     */
    @Synchronized
    fun addOnAmountChangeListener(onAmountChangeListener: (view: View, amount: Int) -> Unit) {
        if (mListeners.size == 2) {
            mListeners[1] = onAmountChangeListener
        } else {
            mListeners.add(onAmountChangeListener)
        }
    }

    fun setGoodsStorage(goodsStorage: Int) {
        this.goodsStorage = goodsStorage
    }

    /**
     * 最少数量
     */
    fun setGoodsMinNum(minNum: Int) {
        this.minNum = minNum
        setGoodsNum(minNum)
    }

    /**
     * 永远是+号
     */
    fun setFixView(fix: Boolean) {
        this.fixView = fix
    }

    /**
     * -1 是售完
     * -2 是固定选择视图
     */
    fun setGoodsNum(num: Int) {
        var goodsNum = num
        if (fixView && num == 0) {
            goodsNum = -2
        }
        if (goodsNum < minNum && goodsNum != -1 && goodsNum != -2) {
            goodsNum = minNum
        }
        amount = goodsNum
        when (goodsNum) {
            -1 -> {
                var width = etAmount.layoutParams.width
                if (width != tvWidth * 2) {
                    etAmount.layoutParams.width = tvWidth * 2
                }
//            etAmount.setBackgroundColor(Color.GRAY)
                btnDecrease.visibility = View.GONE
                btnIncrease.visibility = View.GONE
                switchStyle()
                etAmount.text = context.getString(R.string.amount_none)
                etAmount.setTextColor(Color.BLACK)
            }
            -2 -> {
                var width = etAmount.layoutParams.width
                if (width != tvWidth * 2) {
                    etAmount.layoutParams.width = tvWidth * 2
                }
//            etAmount.setBackgroundColor(Color.GRAY)
                btnDecrease.visibility = View.GONE
                btnIncrease.visibility = View.GONE
                switchStyle()
                etAmount.text = "选规格"
                etAmount.setTextColor(Color.WHITE)
                llAmount.background = context.getDrawable(R.drawable.amount_close_selector)
            }
            else -> {
                var width = etAmount.layoutParams.width
                if (width != tvWidth) {
                    etAmount.layoutParams.width = tvWidth
                }
                btnDecrease.visibility = View.VISIBLE
                btnIncrease.visibility = View.VISIBLE
                switchStyle()
                etAmount.text = goodsNum.toString()
                etAmount.setTextColor(Color.BLACK)
            }
        }
    }


    override fun onClick(v: View) {
        if (isAniming) return
        val i = v.id
        if (i == R.id.btnDecrease && amount != 0) {//amount==0 是关闭状态
            if (amount > minNum) {
                amount--
                etAmount.text = amount.toString()
                mListeners.forEach {
                    it(btnDecrease, amount)
                }
            } else {
                onAmountStateListener?.let { it(LEAST) }
            }
        } else {//if (i == R.id.btnIncrease)R.id.etAmount
            if (amount != -2 && i == R.id.etAmount) {
                return
            }
            if (fixView) {//不变换界面
                mListeners.forEach {
                    it(btnIncrease, amount)
                }
                return
            }
            if (amount < goodsStorage) {
                amount++
                etAmount.text = amount.toString()
                mListeners.forEach {
                    it(btnIncrease, amount)
                }
            }
        }
    }

    fun setOnAmountStateListener(onAmountStateListener: (state: Int) -> Unit) {
        this.onAmountStateListener = onAmountStateListener
    }
}
