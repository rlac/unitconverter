package au.id.rlac.unitconverter.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * FrameLayout that sets its width or height to best fit while maintaining an aspect ratio.
 */
open class AspectFrameLayout : FrameLayout {
  constructor(context: Context) : super(context) {
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0) {
  }

  constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
  }

  private val targetAspect = 0.75 // 3:4 aspect ratio for 3x4 buttons

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    var width = MeasureSpec.getSize(widthMeasureSpec)
    var height = MeasureSpec.getSize(heightMeasureSpec)

    width -= getPaddingLeft() + getPaddingRight()
    height -= getPaddingTop() - getPaddingBottom()

    val initialAspect = width.toDouble() / height.toDouble()

    if (targetAspect / initialAspect - 1 > 0) {
      height = (width / targetAspect).toInt()
    } else {
      width = (height * targetAspect).toInt()
    }

    width += getPaddingLeft() + getPaddingRight()
    height += getPaddingTop() + getPaddingBottom()

    super<FrameLayout>.onMeasure(
        MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
  }
}