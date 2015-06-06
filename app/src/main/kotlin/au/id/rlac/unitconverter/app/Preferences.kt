package au.id.rlac.unitconverter.app

import android.content.Context
import android.content.SharedPreferences
import au.id.rlac.unitconverter.converter.Measure
import au.id.rlac.unitconverter.converter.MeasurementSystem
import au.id.rlac.unitconverter.converter.UnitConverter
import kotlin.properties.Delegates

/**
 * Application preferences and persisted user state.
 */
object Preferences {
  val sharedPrefs: SharedPreferences by Delegates.lazy {
    UnitConverterApplication.instance.getSharedPreferences("prefs", Context.MODE_PRIVATE)
  }

  private fun lastMeasureKey(uc: UnitConverter, system: MeasurementSystem) =
      "last_measure_${uc.ordinal()}${system.ordinal()}"

  /**
   * Save the most recently used measurement for a unit converter and measurement system.
   */
  fun saveLastMeasure(uc: UnitConverter, preferred: Measure) {
    sharedPrefs.edit().putInt(lastMeasureKey(uc, preferred.system), preferred.ordinal()).apply()
  }

  /**
   * Retrieve the most recently used measurement for a unit converter and measurement system.
   */
  fun readLastMeasure(uc: UnitConverter, default: Measure): Measure {
    with (sharedPrefs.getInt(lastMeasureKey(uc, default.system), -1)) {
      if (this == -1) return default
      else return uc.supportedUnits(default.system).firstOrNull { it.ordinal() == this } ?: default
    }
  }

  /**
   * The most recently used unit converter.
   */
  var lastConverter: UnitConverter
    get() = UnitConverter.values()[sharedPrefs.getInt("last_converter", UnitConverter.LENGTH.ordinal())]
    set(value) = sharedPrefs.edit().putInt("last_converter", value.ordinal()).apply()

  /**
   * The most recently used 'from' measurement system.
   */
  var lastFromSystem: MeasurementSystem
    get() = MeasurementSystem.values()[sharedPrefs.getInt("last_from_system", MeasurementSystem.US.ordinal())]
    set(value) = sharedPrefs.edit().putInt("last_from_system", value.ordinal()).apply()
}