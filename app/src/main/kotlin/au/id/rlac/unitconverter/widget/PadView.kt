package au.id.rlac.unitconverter.widget

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import au.id.rlac.unitconverter.R
import au.id.rlac.util.android.delegates.viewById
import java.text.DecimalFormat

/**
 * Displays a 10 key keypad with decimal separator and clear buttons.
 *
 * @property padViewListener Function called when keys on the pad have been pressed.
 */
class PadView : FrameLayout {
  constructor(context: Context) : super(context) {
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0) {
  }

  constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
  }

  interface PadViewListener {
    /**
     * Indicates a numeric key has been pressed.
     * @param value The value of the pressed key. Always between 0 and 9.
     */
    fun onValueClicked(value: Int)

    /**
     * Indicates the clear key has been pressed.
     */
    fun onClearClicked()

    /**
     * Indicates the decimal separator key has been pressed.
     */
    fun onDecClicked()
  }

  private val btn0: Button by viewById(R.id.button_0)
  private val btn1: Button by viewById(R.id.button_1)
  private val btn2: Button by viewById(R.id.button_2)
  private val btn3: Button by viewById(R.id.button_3)
  private val btn4: Button by viewById(R.id.button_4)
  private val btn5: Button by viewById(R.id.button_5)
  private val btn6: Button by viewById(R.id.button_6)
  private val btn7: Button by viewById(R.id.button_7)
  private val btn8: Button by viewById(R.id.button_8)
  private val btn9: Button by viewById(R.id.button_9)
  private val btnClr: Button by viewById(R.id.button_clr)
  private val btnDec: Button by viewById(R.id.button_decimal)

  var padViewListener: PadViewListener? = null

  val handleButtonClick = { v: View ->
    when (v.getId()) {
      R.id.button_clr -> padViewListener?.onClearClicked()
      R.id.button_decimal -> padViewListener?.onDecClicked()
      else -> padViewListener?.onValueClicked(v.getTag() as Int)
    }
  }

  init {
    LayoutInflater.from(getContext()).inflate(R.layout.view_pad, this, true)

    btn0.setOnClickListener(handleButtonClick); btn0.setTag(0)
    btn1.setOnClickListener(handleButtonClick); btn1.setTag(1)
    btn2.setOnClickListener(handleButtonClick); btn2.setTag(2)
    btn3.setOnClickListener(handleButtonClick); btn3.setTag(3)
    btn4.setOnClickListener(handleButtonClick); btn4.setTag(4)
    btn5.setOnClickListener(handleButtonClick); btn5.setTag(5)
    btn6.setOnClickListener(handleButtonClick); btn6.setTag(6)
    btn7.setOnClickListener(handleButtonClick); btn7.setTag(7)
    btn8.setOnClickListener(handleButtonClick); btn8.setTag(8)
    btn9.setOnClickListener(handleButtonClick); btn9.setTag(9)
    btnClr.setOnClickListener(handleButtonClick)
    btnDec.setOnClickListener(handleButtonClick)

    btnDec.setText(DecimalFormat().getDecimalFormatSymbols().getDecimalSeparator().toString())
  }

  override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
    super.dispatchFreezeSelfOnly(container)
  }

  override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
    super.dispatchThawSelfOnly(container)
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean =
      with (when (keyCode) {
        KeyEvent.KEYCODE_0,
        KeyEvent.KEYCODE_NUMPAD_0 -> btn0
        KeyEvent.KEYCODE_1,
        KeyEvent.KEYCODE_NUMPAD_1 -> btn1
        KeyEvent.KEYCODE_2,
        KeyEvent.KEYCODE_NUMPAD_2 -> btn2
        KeyEvent.KEYCODE_3,
        KeyEvent.KEYCODE_NUMPAD_3 -> btn3
        KeyEvent.KEYCODE_4,
        KeyEvent.KEYCODE_NUMPAD_4 -> btn4
        KeyEvent.KEYCODE_5,
        KeyEvent.KEYCODE_NUMPAD_5 -> btn5
        KeyEvent.KEYCODE_6,
        KeyEvent.KEYCODE_NUMPAD_6 -> btn6
        KeyEvent.KEYCODE_7,
        KeyEvent.KEYCODE_NUMPAD_7 -> btn7
        KeyEvent.KEYCODE_8,
        KeyEvent.KEYCODE_NUMPAD_8 -> btn8
        KeyEvent.KEYCODE_9,
        KeyEvent.KEYCODE_NUMPAD_9 -> btn9
        KeyEvent.KEYCODE_C,
        KeyEvent.KEYCODE_DEL,
        KeyEvent.KEYCODE_FORWARD_DEL -> btnClr
        KeyEvent.KEYCODE_PERIOD,
        KeyEvent.KEYCODE_NUMPAD_COMMA,
        KeyEvent.KEYCODE_NUMPAD_DOT -> btnDec
        else -> null
      }) {
        if (this != null) {
          performClick()
          return true
        }

        return false
      }
}