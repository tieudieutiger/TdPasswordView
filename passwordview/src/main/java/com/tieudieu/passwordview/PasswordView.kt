package com.tieudieu.passwordview

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.CycleInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.password_input_slot_view.view.*

import kotlinx.android.synthetic.main.password_view.view.*


class PasswordView : RelativeLayout {

    companion object {

        private const val PASSWORD_LENGTH_DEFAULT = 6
    }

    private var attrs: AttributeSet? = null
    private var styleAttr: Int = 0

    private var textColor: Int = 0
    private var hintColor: Int = 0
    private var passwordLength = PASSWORD_LENGTH_DEFAULT
    private lateinit var inputType: InputType
    private lateinit var passwordType: PasswordType

    private var password: String = ""
    private var keyboardVisible = false
    private var shakeAnimationDoing = false
    private var onPasswordEnterListener: OnPasswordEnterListener? = null


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

        inflate(context, R.layout.password_view, this)

        var typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.TdPasswordInput, styleAttr, 0
        )

        textColor = typedArray.getColor(R.styleable.TdPasswordInput_tdpi_text_color, Color.BLACK)
        hintColor = typedArray.getColor(R.styleable.TdPasswordInput_tdpi_hint_color, Color.LTGRAY)
        passwordLength = typedArray.getInt(R.styleable.TdPasswordInput_tdpi_password_length, PASSWORD_LENGTH_DEFAULT)

        var inputTypeValue = typedArray.getInteger(R.styleable.TdPasswordInput_tdpi_input_type, InputType.NUMBER.attrValue)
        inputType = InputType.fromValue(inputTypeValue)

        var passwordTypeValue = typedArray.getInteger(R.styleable.TdPasswordInput_tdpi_password_type, PasswordType.PASSWORD.attrValue)
        passwordType = PasswordType.fromValue(passwordTypeValue)

        typedArray.recycle()

    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        initView()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (!isInEditMode) {

            addKeyboardVisibilityListener(
                    this,
                    object : OnKeyboardVisibilityListener {

                        override fun onVisibilityChange(isVisible: Boolean) {
                            if (keyboardVisible != isVisible) {
                                keyboardVisible = isVisible
                                if (!keyboardVisible && edt_input_password_real.isFocused) {
                                    edt_input_password_real.clearFocus()
                                }
                            }
                        }
                    }
            )

            edt_input_password_real.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
                showInput(password, hasFocus)
            }

            edt_input_password_real.addTextChangedListener(

                    object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {
                        }

                        override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                            if (s == null) return

                            if (!shakeAnimationDoing) {

                                if (before > 0) {

                                    // Remove last char
                                    if (password.isNotEmpty())
                                        password = password.subSequence(0, password.length - 1).toString()

                                } else if (password.length < passwordLength && s.isNotEmpty() && charIsValid(s[s.length - 1])) {

                                    val unicodeChar = s[s.length - 1]

                                    password += unicodeChar

                                    if (password.length == passwordLength && onPasswordEnterListener != null)
                                        onPasswordEnterListener!!.onPasswordEntered(getPassword())

                                } else
                                    shake()

                                showInput(password, true)
                            }
                        }

                    }
            )

        }
    }

    /**
     * Private functions
     */

    private fun initView() {

        password = ""

        layout_input_password.setPasswordType(passwordType)
        layout_input_password.setPasswordLength(passwordLength)
        layout_input_password.initView()

        btn_password_view_on_focus.setOnClickListener {

            // select endPosition
            edt_input_password_real.setSelection(edt_input_password_real.text.toString().length)

            // focus real edt
            edt_input_password_real.requestFocus()

            if (!keyboardVisible)
                showKeyboard(context)

        }
    }

    private fun showKeyboard(context: Context) {

        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun addKeyboardVisibilityListener(
            rootLayout: View, onKeyboardVisibilityListener: OnKeyboardVisibilityListener) {

        rootLayout.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            rootLayout.getWindowVisibleDisplayFrame(r)
            val screenHeight = rootLayout.rootView.height

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            val keypadHeight = screenHeight - r.bottom

            val isVisible = keypadHeight > screenHeight * 0.15 // 0.15 ratio is perhaps enough to determine keypad height.
            onKeyboardVisibilityListener.onVisibilityChange(isVisible)
        }
    }

    private fun showInput(value: String, hasFocus: Boolean) {

        // onEntered on [length -1]
        if (value.isNotEmpty()) {

            val letter = value[value.length - 1]
            layout_input_password.onEntered(value.length - 1, letter)
        }

        // onInput on [length]
        layout_input_password.onInput(value.length)

        // view focus
        layout_input_password.onViewFocus(hasFocus, value.length)

    }

    private fun shake() {

        shakeView(layout_input_password)
    }

    private fun shakeView(view: View) {

        shakeAnimationDoing = true

        view.animate()
                .translationX(-15f).translationX(15f)
                .setDuration(30)
                .setInterpolator(CycleInterpolator((150 / 30).toFloat()))
                .setDuration(150)
                .withEndAction { shakeAnimationDoing = false }
                .start()
    }

    private fun charIsValid(unicodeChar: Char): Boolean {

        return if (inputType == InputType.NUMBER)
            Character.isDigit(unicodeChar)
        else
            true
    }

    private fun getPassword(): String? = if (password.length == passwordLength) password else null


    /**
     * Public functions
     */

    fun setOnPasswordEnterListener(listener: OnPasswordEnterListener) {

        this.onPasswordEnterListener = listener
    }

    fun setPassword(password: String): Boolean {

        if (passwordLength <= 0 || password.length != passwordLength)
            return false

        this.password = password

        edt_input_password_real.setText(password)

        return layout_input_password.setPassword(password)
    }

    /**
     * Listeners
     */

    interface OnKeyboardVisibilityListener {

        fun onVisibilityChange(isVisible: Boolean)

    }

    interface OnPasswordEnterListener {

        fun onPasswordEntered(password: String?)
    }

    /**
     * Input type
     */

    enum class InputType {

        NUMBER,
        FREE;

        val value: String
            get() {

                return when (this) {
                    NUMBER -> "number"
                    FREE -> "free"
                }
                throw IllegalArgumentException("Not value available for this DateFormat: " + this)
            }

        val attrValue: Int
            get() {

                return when (this) {
                    NUMBER -> 1
                    FREE -> 2
                }

                throw IllegalArgumentException("Not value available for this DateFormat: " + this)
            }

        companion object {

            fun fromValue(value: Int): InputType {

                when (value) {

                    1 -> return NUMBER
                    2 -> return FREE
                }
                throw IllegalArgumentException("This value is not supported for DateFormat: $value")
            }
        }
    }

    /**
     * PasswordType
     */

    enum class PasswordType {

        PASSWORD,
        OTP;

        val value: String
            get() {

                return when (this) {
                    PASSWORD -> "password"
                    OTP -> "otp"
                }
                throw IllegalArgumentException("Not value available for this DateFormat: " + this)
            }

        val attrValue: Int
            get() {

                return when (this) {
                    PASSWORD -> 1
                    OTP -> 2
                }

                throw IllegalArgumentException("Not value available for this DateFormat: " + this)
            }

        companion object {

            fun fromValue(value: Int): PasswordType {

                when (value) {

                    1 -> return PASSWORD
                    2 -> return OTP
                }
                throw IllegalArgumentException("This value is not supported for DateFormat: $value")
            }
        }
    }

}