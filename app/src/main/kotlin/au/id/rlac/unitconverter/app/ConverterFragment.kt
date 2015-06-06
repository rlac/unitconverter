package au.id.rlac.unitconverter.app

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import au.id.rlac.unitconverter.R
import au.id.rlac.util.nucleus.BaseFragment
import au.id.rlac.unitconverter.converter.Measure
import au.id.rlac.unitconverter.converter.UnitConverter
import au.id.rlac.unitconverter.widget.BindableSpinnerAdapter
import au.id.rlac.unitconverter.widget.PadView
import au.id.rlac.util.android.delegates.ViewDelegates
import au.id.rlac.util.android.delegates.argument
import au.id.rlac.util.android.delegates.viewById
import au.id.rlac.util.android.isLollipop
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.InlineOption.ONLY_LOCAL_RETURN
import nucleus.factory.RequiresPresenter as requiresPresenter

requiresPresenter(ConverterPresenter::class)
class ConverterFragment : BaseFragment<ConverterPresenter.View, ConverterPresenter>(), ConverterPresenter.View {

  companion object {
    fun invoke(uc: UnitConverter): ConverterFragment {
      val fragment = ConverterFragment()
      val args = Bundle()
      fragment.setArguments(args)

      args.putInt("unitConverter", uc.ordinal())

      return fragment
    }
  }

  // number formats for general use, and force showing sep after pressing dec separator button
  val numFormat = NumberFormat.getInstance()
  val numFormatInputWithSeparator = NumberFormat.getInstance()
  init {
    if (numFormat is DecimalFormat) {
      numFormat.setMaximumFractionDigits(4)
    }
    if (numFormatInputWithSeparator is DecimalFormat) {
      numFormatInputWithSeparator.setDecimalSeparatorAlwaysShown(true)
      // fraction digits set in view binding method
    }
  }

  // unit converter set for the fragment as an argument
  val unitConverter: UnitConverter by argument(map = { ucOrdinal: Int ->
    UnitConverter.values()[ucOrdinal]
  })

  class ViewReference(override val view: View) : ViewDelegates.ViewHolder {
    val padView: PadView by viewById(R.id.padview)
    val fromSpinner: Spinner by viewById(R.id.spinner_from_unit)
    val toSpinner: Spinner by viewById(R.id.spinner_to_unit)
    val fromQtyText: TextView by viewById(R.id.text_from_qty)
    val toQtyText: TextView by viewById(R.id.text_to_qty)
    val switch: View by viewById(R.id.button_switch)
  }

  var views: ViewReference? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super<BaseFragment>.onCreate(savedInstanceState)
    presenter.unitConverter = unitConverter
  }

  override fun onCreateView(inflater: LayoutInflater,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {

    val views = with (inflater.cloneInContext(ContextThemeWrapper(inflater.getContext(), unitConverter.theme))) {
      ViewReference(inflate(R.layout.fragment_converter, container, false))
    }

    views.padView.padViewListener = object : PadView.PadViewListener {
      override fun onValueClicked(value: Int) {
        presenter.appendDigit(value)
      }

      override fun onDecClicked() {
        presenter.appendDec()
      }

      override fun onClearClicked() {
        overlayReveal { presenter.clear() }
      }
    }

    views.fromSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
      override fun onNothingSelected(parent: AdapterView<*>?) {
      }

      override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        presenter.changeFrom(parent.getItemAtPosition(position) as Measure)
      }
    })

    views.toSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
      override fun onNothingSelected(parent: AdapterView<*>?) {
      }

      override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        presenter.changeTo(parent.getItemAtPosition(position) as Measure)
      }
    })

    views.switch.setOnClickListener {
      val rotate = RotateAnimation(
          0f, 180f,
          Animation.RELATIVE_TO_SELF, .5f,
          Animation.RELATIVE_TO_SELF, .5f)
      rotate.setDuration(125)

      views.switch.startAnimation(rotate)

      presenter.swap()
    }

    this.views = views
    return views.view
  }

  override fun onDestroyView() {
    super<BaseFragment>.onDestroyView()
    views = null
  }

  override fun onShowInput(input: ConverterPresenter.UserInput) {
    if (input.isDecEntered) {
      // ensure trailing 0s are displayed by setting fraction digits
      numFormatInputWithSeparator.setMinimumFractionDigits(input.bigDecimalValue.scale())
    }

    views?.fromQtyText?.setText(
        if (input.isDecEntered) numFormatInputWithSeparator.format(input.bigDecimalValue)
        else numFormat.format(input.bigDecimalValue))
  }

  override fun onShowResult(result: BigDecimal) {
    views?.toQtyText?.setText(numFormat.format(result))
  }

  override fun onShowToAndFromOptions(from: List<Measure>, selectedFrom: Measure,
                                    to: List<Measure>, selectedTo: Measure) {

    val selectedFromIndex = from.indexOf(selectedFrom)
    val selectedToIndex = to.indexOf(selectedTo)

    if (selectedFromIndex == -1) throw IllegalArgumentException()
    if (selectedToIndex == -1) throw IllegalArgumentException()

    with (views) {
      if (this == null) return

      val bind = fun(m: Measure, tv: TextView) {
        tv.setText(m.displayStringId)
      }
      val id = fun(m: Measure): Long = m.ordinal().toLong()

      fromSpinner.setAdapter(BindableSpinnerAdapter(
          context = getActivity(), bind = bind, getId = id, items = from))
      toSpinner.setAdapter(BindableSpinnerAdapter(
          context = getActivity(), bind = bind, getId = id, items = to))

      fromSpinner.setSelection(selectedFromIndex)
      toSpinner.setSelection(selectedToIndex)
    }
  }

  /**
   * Overlay a reveal animation over the converter display (not the keypad), then fades it out.
   * When the reveal animation (but before fade) has completed the 'complete' function is invoked.
   *
   * Written using the clear reveal code in the Calculator app from Lollipop as a reference on the
   * approach to take.
   *
   * @param complete function that is invoked when the reveal animation has completed.
   */
  private inline fun overlayReveal(inlineOptions(ONLY_LOCAL_RETURN) complete: () -> Unit) {
    if (!isLollipop) {
      complete()

    } else {
      val displayView = getView().findViewById(R.id.layout_display)

      val overlay = displayView.getOverlay() as ViewGroupOverlay
      val displayRect = Rect()
      val revealColor = UnitConverter.Theme(getView().getContext(), unitConverter).colorPrimary

      displayView.getDrawingRect(displayRect)

      val revealView = View(getView().getContext())
      revealView.setBottom(displayRect.bottom)
      revealView.setTop(displayRect.top)
      revealView.setLeft(displayRect.left)
      revealView.setRight(displayRect.right)
      revealView.setBackgroundColor(revealColor)

      overlay.add(revealView)

      val revealCenterX = revealView.getRight()
      val revealCenterY = revealView.getBottom()
      val revealRadius = Math.hypot(revealView.getRight().toDouble(),
          revealView.getBottom().toDouble())

      val revealAnimator = ViewAnimationUtils.createCircularReveal(
          revealView, revealCenterX, revealCenterY, 0f, revealRadius.toFloat())
      revealAnimator.setDuration(500L)

      val alphaAnimator = ObjectAnimator.ofFloat(revealView, View.ALPHA, 0f)
      alphaAnimator.setDuration(200L)

      val animatorSet = AnimatorSet()
      animatorSet.play(revealAnimator).before(alphaAnimator)
      animatorSet.setInterpolator(AccelerateDecelerateInterpolator())

      revealAnimator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
          complete()
        }
      })

      animatorSet.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
          overlay.remove(revealView)
        }
      })

      animatorSet.start()
    }
  }
}