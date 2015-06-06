package au.id.rlac.unitconverter.converter

import android.content.Context
import au.id.rlac.unitconverter.R
import au.id.rlac.util.android.use
import java.math.BigDecimal
import java.util.*

/**
 * Converts a numeric value for one [Measure] to the equivalent value for another [Measure].
 *
 * A [UnitConverter] has a base [Measure] along with a set of [Conversion] objects that can convert
 * values to and from the base [Measure] to their own [Measure].
 *
 * @property base The base [Measure] all [converters] work with.
 * @property displayNameResId Display name for the unit converter.
 * @property theme The theme associated with this unit converter.
 * @param converters Converters that convert to the [base]. Each [Conversion] is adds its [Measure]
 * as a supported type to this unit converter.
 */
public enum class UnitConverter(private val base: Measure,
                                val displayNameResId: Int,
                                val theme: Int,
                                vararg converters: Conversion) {

  TEMPERATURE(
      Measure.CELSIUS,
      R.string.temperature,
      R.style.TemperatureTheme,
      Conversion.fahrenheit),

  LENGTH(
      Measure.METRE,
      R.string.length,
      R.style.LengthTheme,
      Conversion(Measure.CENTIMETRE, BigDecimal(0.01)),
      Conversion(Measure.KILOMETRE, BigDecimal(1000.0)),
      Conversion(Measure.INCH, BigDecimal(0.0254)),
      Conversion(Measure.FEET, BigDecimal(0.3048)),
      Conversion(Measure.YARD, BigDecimal(0.9144)),
      Conversion(Measure.MILE, BigDecimal(1609.344))),

  MASS(
      Measure.GRAM,
      R.string.mass,
      R.style.MassTheme,
      Conversion(Measure.OUNCE, BigDecimal(28.3495)),
      Conversion(Measure.STONE, BigDecimal(6350.29)),
      Conversion(Measure.POUND, BigDecimal(453.592)),
      Conversion(Measure.KILOGRAM, BigDecimal(1000.0))),

  VOLUME(
      Measure.LITRE,
      R.string.volume,
      R.style.VolumeTheme,
      Conversion(Measure.MILLILITRE, BigDecimal(0.001)),
      Conversion(Measure.FLUIDOUNCE, BigDecimal(0.0295735)),
      Conversion(Measure.CUP, BigDecimal(0.236588)),
      Conversion(Measure.PINT, BigDecimal(0.473176)),
      Conversion(Measure.GALLON, BigDecimal(3.78541))),

  ENERGY(
      Measure.KILOJOULE,
      R.string.energy,
      R.style.EnergyTheme,
      Conversion(Measure.KILOCALORIE, BigDecimal(4.184)));

  public class Theme private constructor(val colorPrimary: Int, val colorPrimaryDark: Int) {
    companion object {
      fun invoke(context: Context, uc: UnitConverter): Theme {
        with(context.getResources().newTheme()) {
          setTo(context.getTheme())
          applyStyle(uc.theme, true)

          return obtainStyledAttributes(R.styleable.Theme) use {
            Theme(it.getColor(R.styleable.Theme_colorPrimary, 0),
                it.getColor(R.styleable.Theme_colorPrimaryDark, 0))
          }
        }
      }
    }
  }

  private val converterMap: Map<Measure, Conversion> = Collections.unmodifiableMap(
      with(HashMap<Measure, Conversion>()) {
        converters forEach { c -> put(c.targetMeasurement, c) }
        put(base, Conversion(base, BigDecimal.ONE))
        this
      })

  /**
   * @param forSystem If set filters the list to measurements for this system only.
   * @return the list of units supported by this converter's convert method.
   */
  public fun supportedUnits(forSystem: MeasurementSystem? = null): List<Measure> =
      with(converterMap map { it.key } sortBy { it.weight }) {
        if (forSystem == null) this
        else filter { it.system equals forSystem }
      }

  /**
   * Convert the value of a unit to its equivalent value as another measure.
   *
   * @param from The unit type to convert from.
   * @param to The unit type to convert to.
   * @param value The value of the unit to convert from.
   * @return the converted value.
   * @throws IllegalArgumentException if the from or to unit is not supported by the converter.
   */
  public fun convert(from: Measure, to: Measure, value: BigDecimal): BigDecimal {
    val fromConverter = converterMap[from]
    val toConverter = converterMap[to]

    if (fromConverter == null) throw IllegalArgumentException("from unit ${from} not supported.")
    if (toConverter == null) throw IllegalArgumentException("to unit ${to} not supported.")

    return toConverter fromBase (fromConverter toBase value)
  }
}
