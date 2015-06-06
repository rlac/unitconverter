package au.id.rlac.unitconverter.app

import android.app.Application
import kotlin.properties.Delegates

class UnitConverterApplication : Application() {
  companion object {
    val instance: UnitConverterApplication get() = instanceVar
    private var instanceVar: UnitConverterApplication by Delegates.notNull()
  }

  override fun onCreate() {
    super.onCreate()
    instanceVar = this
  }
}