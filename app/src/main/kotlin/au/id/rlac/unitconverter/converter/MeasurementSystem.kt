package au.id.rlac.unitconverter.converter

/**
 * Measurement systems supported by the application.
 */
enum class MeasurementSystem {
  US,
  METRIC;

  fun opposite() = if (this.equals(US)) METRIC else US
}
