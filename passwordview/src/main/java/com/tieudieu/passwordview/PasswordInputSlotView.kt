package com.tieudieu.passwordview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.password_input_slot_view.view.*

class PasswordInputSlotView : LinearLayout {

    private var attrs: AttributeSet? = null
    private var styleAttr: Int = 0

    private var textColor: Int = 0
    private var hintColor: Int = 0

    constructor(context: Context?) : super(context) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {

        this.attrs = attrs

        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        this.attrs = attrs
        this.styleAttr = defStyleAttr

        init(context)
    }

    private fun init(context: Context?) {

        if (context == null) return

        LinearLayout.inflate(context, R.layout.password_input_slot_view, this)

        var typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.TdPasswordInput, styleAttr, 0
        )

        textColor = typedArray.getColor(R.styleable.TdPasswordInput_tdpi_text_color, Color.BLACK)
        hintColor = typedArray.getColor(R.styleable.TdPasswordInput_tdpi_hint_color, Color.LTGRAY)

        typedArray.recycle()

    }

    /**
     * Private functions
     */

    fun initView() {

        Log.d("PIS", "initView")

        tv_password_slot.setTextColor(hintColor)
        view_password_slot.setBackgroundColor(hintColor)
        view_password_slot.visibility = View.VISIBLE

        tv_password_slot.text = getPasswordSlotHint()

    }

    private fun getPasswordSlotLetter(): String = context.getString(R.string.pass_slot)

    private fun getPasswordSlotHint(): String = context.getString(R.string.pass_slot_hint)


    private fun setText(text: String?) {

        if (text != null)
            tv_password_slot.text = text
    }

    private fun setTextColor(color: Int) {

        tv_password_slot.setTextColor(color)
    }

    private fun onPass() {

        view_password_slot.setBackgroundColor(Color.TRANSPARENT)
    }

    /**
     * Public functions
     */

    fun clear() = initView()

    fun setColor(textColor: Int, hintColor: Int) {

        Log.d("PIS", "xxx-setColor")

        this.textColor = textColor
        this.hintColor = hintColor
    }

    fun setLineVisibility(visibility: Int) {

        view_password_slot.visibility = visibility
    }

    fun onViewFocus(isFocus: Boolean) {

        onViewFocus(isFocus, false)
    }

    fun onViewFocus(isFocus: Boolean, isPass: Boolean) {

        when {

            isFocus -> {

                view_password_slot.visibility = View.VISIBLE
                view_password_slot.setBackgroundColor(textColor)

            }

            isPass -> onPass()

            else -> view_password_slot.setBackgroundColor(hintColor)
        }

    }

    fun onInput() {

        setTextColor(hintColor)
        setText(getPasswordSlotHint())
    }

    /*fun onEntered() {

        onEntered(getPasswordSlotLetter())
    }*/

    fun onEntered(letter: String) {

        setTextColor(textColor)
        setText(letter)

        onPass()
    }

}