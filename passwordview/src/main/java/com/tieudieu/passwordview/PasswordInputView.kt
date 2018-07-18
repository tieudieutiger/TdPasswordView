package com.tieudieu.passwordview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import java.util.*

class PasswordInputView : LinearLayout {

    private var attrs: AttributeSet? = null
    private var styleAttr: Int = 0

    private var textColor: Int = 0
    private var hintColor: Int = 0

    private var passwordLength = 0
    private lateinit var passwordType: PasswordView.PasswordType

    private var passwordSlotViews = ArrayList<PasswordInputSlotView>()

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

        orientation = LinearLayout.HORIZONTAL

        if (context == null) return

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

        removeAllViews()

        passwordSlotViews.clear()

        if (passwordLength <= 0) return

        for (i in 0 until passwordLength) {
            addSlot()
        }

    }

    private fun addSlot() {

        var slotView = PasswordInputSlotView(context)
        slotView.setColor(textColor, hintColor)
        slotView.initView()

        passwordSlotViews.add(slotView)

        addView(slotView)
    }

    private fun parsePassSlotLetter(letter: Char): String {

        return if (passwordType == PasswordView.PasswordType.PASSWORD)
            context.getString(R.string.pass_slot)
        else
            letter.toString()
    }

    /**
     * Public functions
     */

    fun clear() {

        initView()
    }

    fun setPasswordLength(length: Int) {

        passwordLength = length
    }

    fun setPasswordType(type: PasswordView.PasswordType) {

        passwordType = type
    }

    fun setPassword(password: String): Boolean {

        if (passwordLength <= 0 || password.isEmpty() || passwordSlotViews.isEmpty() || passwordSlotViews.size != password.length)
            return false

        clear()

        for (index in 0..(passwordSlotViews.size - 1)) {
            onEntered(index, password[index])
        }

        return true

    }

    fun onViewFocus(hasFocus: Boolean, valueLength: Int) {

        if (passwordSlotViews == null || valueLength < 0 || valueLength > passwordSlotViews.size)
            return

        for (p: PasswordInputSlotView in passwordSlotViews) {
            p.setLineVisibility(View.VISIBLE)
        }

        if (hasFocus) {

            // entered on [valueLength - 1]
            if (valueLength > 0)
                passwordSlotViews[valueLength - 1].onViewFocus(false, true)

            // focus on [valueLength]
            if (valueLength < passwordSlotViews.size)
                passwordSlotViews[valueLength].onViewFocus(true)

            // lost focus on [valueLength + 1]
            if (valueLength + 1 < passwordSlotViews.size)
                passwordSlotViews[valueLength + 1].onViewFocus(false, false)

        } else if (valueLength > 0)
            passwordSlotViews[valueLength - 1].onViewFocus(false)
//        } else
//            for (p: PasswordInputSlotView in passwordSlotViews) {
//                p.onViewFocus(false, true)
//            }

    }

    fun onInput(index: Int) {

        if (passwordSlotViews == null || index < 0 || index >= passwordSlotViews.size)
            return

        passwordSlotViews[index].onInput()
    }

    /*fun onEntered(index: Int) {

        if (passwordSlotViews == null || index < 0 || index >= passwordSlotViews.size)
            return

        passwordSlotViews[index].onEntered()

    }*/

    fun onEntered(index: Int, letter: Char) {

        if (passwordSlotViews == null || index < 0 || index >= passwordSlotViews.size)
            return

        passwordSlotViews[index].onEntered(parsePassSlotLetter(letter))

    }

}