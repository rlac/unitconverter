package au.id.rlac.unitconverter.converter

import au.id.rlac.unitconverter.R

/**
 * Units of measure available for conversion. Each [UnitConverter] supports a subset of these.
 *
 * @property displayStringId String resource id.
 * @property system Whether this is a US or a Metric unit.
 * @property weight Sort weighting (ascending). Sorting for matching weights is undefined.
 */
enum class Measure(val displayStringId: Int, val system: MeasurementSystem, val weight: Int) {
  // temperature
  CELSIUS(R.string.celsius, MeasurementSystem.METRIC, 0),
  FAHRENHEIT(R.string.fahrenheit, MeasurementSystem.US, 0),

  // length
  METRE(R.string.metres, MeasurementSystem.METRIC, 1),
  CENTIMETRE(R.string.centimetres, MeasurementSystem.METRIC, 0),
  KILOMETRE(R.string.kilometres, MeasurementSystem.METRIC, 2),
  INCH(R.string.inches, MeasurementSystem.US, 0),
  FEET(R.string.feet, MeasurementSystem.US, 1),
  YARD(R.string.yards, MeasurementSystem.US, 2),
  MILE(R.string.miles, MeasurementSystem.US, 3),

  // mass
  GRAM(R.string.grams, MeasurementSystem.METRIC, 0),
  KILOGRAM(R.string.kilograms, MeasurementSystem.METRIC, 1),
  OUNCE(R.string.ounces, MeasurementSystem.US, 0),
  STONE(R.string.stone, MeasurementSystem.US, 1),
  POUND(R.string.pounds, MeasurementSystem.US, 2),

  // volume
  LITRE(R.string.litres, MeasurementSystem.METRIC, 1),
  MILLILITRE(R.string.millilitres, MeasurementSystem.METRIC, 0),
  FLUIDOUNCE(R.string.fluid_ounces, MeasurementSystem.US, 0),
  CUP(R.string.cups, MeasurementSystem.US, 1),
  PINT(R.string.pints, MeasurementSystem.US, 2),
  GALLON(R.string.gallons, MeasurementSystem.US, 3),

  // energy
  KILOCALORIE(R.string.kilocalories, MeasurementSystem.US, 0),
  KILOJOULE(R.string.kilojoules, MeasurementSystem.METRIC, 0)
}
