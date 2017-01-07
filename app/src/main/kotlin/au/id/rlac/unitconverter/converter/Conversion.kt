package au.id.rlac.unitconverter.converter

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Converts a single measure to and from another measure from a base unit of measure.
 *
 * @property targetMeasurement The measurement converted to and from.
 * @property fromBase Function that converts from the base measurement to the target measurement.
 * @property toBase Function that converts to the base measurement from the target measurement.
 */
class Conversion private constructor(val targetMeasurement: Measure,
                                     val fromBase: (BigDecimal) -> BigDecimal,
                                     val toBase: (BigDecimal) -> BigDecimal) {

  companion object {
    /**
     * Create a 1:N converter.
     *
     * @param target The unit of measure that is converted to/from the base unit.
     * @param n The conversion ratio to and from the base unit.
     */
    operator fun invoke(target: Measure, n: BigDecimal) =
        if (n.compareTo(BigDecimal.ZERO) == 0) throw IllegalArgumentException("Cannot map to 0")
        else Conversion(
            target,
            { y: BigDecimal -> y.divide(n, RoundingMode.HALF_EVEN) },
            { y: BigDecimal -> y.multiply(n) })

    /**
     * Converts fahrenheit to and from celsius.
     */
    val fahrenheit: Conversion by lazy {
      val nine = BigDecimal(9)
      val five = BigDecimal(5)
      val thirtyTwo = BigDecimal(32)

      Conversion(Measure.FAHRENHEIT,
          { c -> c.multiply(nine).divide(five, RoundingMode.HALF_EVEN).add(thirtyTwo) },
          { f -> f.subtract(thirtyTwo).multiply(five).divide(nine, RoundingMode.HALF_EVEN) })
    }
  }
}
