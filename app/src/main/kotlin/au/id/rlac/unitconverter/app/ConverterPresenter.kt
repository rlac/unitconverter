package au.id.rlac.unitconverter.app

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import au.id.rlac.unitconverter.converter.Measure
import au.id.rlac.unitconverter.converter.MeasurementSystem
import au.id.rlac.unitconverter.converter.UnitConverter
import au.id.rlac.util.android.*
import nucleus.presenter.Presenter
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.plus
import kotlin.math.times
import kotlin.platform.platformStatic
import kotlin.properties.Delegates

class ConverterPresenter : Presenter<ConverterPresenter.View>() {

  interface View {
    fun onShowInput(input: UserInput)
    fun onShowResult(result: BigDecimal)
    fun onShowToAndFromOptions(from: List<Measure>, selectedFrom: Measure,
                               to: List<Measure>, selectedTo: Measure)
  }

  private companion object {
    val stateUnitConverter = "unitConverter"
    val stateInput = "input"
    val stateConvertFrom = "convertFrom"
    val stateTo = "to"
    val stateFrom = "from"
  }

  private var maybeUnitConverter: UnitConverter? = null
  public var unitConverter: UnitConverter
    get() = maybeUnitConverter ?: throw IllegalStateException()
    set(value) {
      if (value equals maybeUnitConverter) {
        // do nothing
      } else {
        maybeUnitConverter = value
        from = Preferences.readLastMeasure(value, value.supportedUnits(convertFrom).first())
        to = Preferences.readLastMeasure(value,
            value.supportedUnits(convertFrom.opposite()).first())
      }
    }

  private var convertFrom: MeasurementSystem by Delegates.notNull()
  private var from: Measure by Delegates.notNull()
  private var to: Measure by Delegates.notNull()
  private var input: UserInput by Delegates.notNull()

  override fun onCreate(savedState: Bundle?) {
    super<Presenter>.onCreate(savedState)

    if (savedState == null) {
      input = UserInput()
      convertFrom = Preferences.lastFromSystem
      // unit converter is unknown until set by view
      // to and from are not known until the unit converter is set

    } else {
      input = savedState.getParcelable(stateInput)
      convertFrom = MeasurementSystem.values()[savedState.getInt(stateConvertFrom)]
      unitConverter = UnitConverter.values()[savedState.getInt(stateUnitConverter)]
      to = Measure.values()[savedState.getInt(stateTo)]
      from = Measure.values()[savedState.getInt(stateFrom)]
    }
  }

  override fun onSave(state: Bundle) {
    super<Presenter>.onSave(state)
    state.putInt(stateUnitConverter, unitConverter.ordinal())
    state.putParcelable(stateInput, input)
    state.putInt(stateTo, to.ordinal())
    state.putInt(stateFrom, from.ordinal())
    state.putInt(stateConvertFrom, convertFrom.ordinal())
  }

  override fun onTakeView(view: View) {
    super<Presenter>.onTakeView(view)

    if (maybeUnitConverter == null)
      throw IllegalStateException("Unit converter must be set before view is attached")

    bindMeasurements()
    calculateAndUpdateView()
  }

  private fun bindMeasurements() {
    getView()?.onShowToAndFromOptions(
        unitConverter.supportedUnits(convertFrom), from,
        unitConverter.supportedUnits(convertFrom.opposite()), to)
  }

  private fun calculateAndUpdateView() {
    getView()?.onShowInput(input)
    getView()?.onShowResult(unitConverter.convert(from, to, input.bigDecimalValue))
  }

  /** Update the 'To' measurement. */
  public fun changeTo(to: Measure) {
    if (this.to equals to) return
    this.to = to
    Preferences.saveLastMeasure(unitConverter, to)
    calculateAndUpdateView()
  }

  /** Update the 'From' measurement. */
  public fun changeFrom(from: Measure) {
    if (this.from equals from) return
    this.from = from
    Preferences.saveLastMeasure(unitConverter, from)
    calculateAndUpdateView()
  }

  /** Append a digit to the user input if possible. */
  public fun appendDigit(digit: Int) {
    input.append(digit)
    calculateAndUpdateView()
  }

  /** Append a decimal separator to the user input if possible. */
  public fun appendDec() {
    input.appendDec()
    calculateAndUpdateView()
  }

  /** Clear user input. */
  public fun clear() {
    input.clear()
    calculateAndUpdateView()
  }

  /** Swap the 'To' and 'From' unit system. */
  public fun swap() {
    convertFrom = convertFrom.opposite()
    Preferences.lastFromSystem = convertFrom

    with (from) {
      from = to
      to = this
    }

    bindMeasurements()
    calculateAndUpdateView()
  }

  class UserInput(private var value: BigDecimal = BigDecimal.ZERO,
                  private var hasDec: Boolean = false) : Parcelable {

    val bigDecimalValue: BigDecimal get() = value
    val isDecEntered: Boolean get() = hasDec

    fun append(appendVal: Int) {
      value = if (hasDec) value + BigDecimal.valueOf(appendVal.toLong(), value.scale() + 1)
      else value * BigDecimal.TEN + BigDecimal.valueOf(appendVal.toLong())
    }

    fun appendDec() {
      hasDec = true
    }

    fun clear() {
      value = BigDecimal.ZERO
      hasDec = false
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
      dest.writeBigDecimal(value)
      dest.writeBoolean(hasDec)
    }

    companion object {
      platformStatic val CREATOR = creator { UserInput(it.readBigDecimal(), it.readBoolean()) }
    }
  }
}