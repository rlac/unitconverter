package au.id.rlac.unitconverter.app

import android.app.Fragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v13.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import au.id.rlac.unitconverter.R
import au.id.rlac.unitconverter.converter.UnitConverter
import au.id.rlac.util.android.delegates.viewById
import au.id.rlac.util.android.isLollipop

class MainActivity : AppCompatActivity() {
  val tabs: TabLayout by viewById(R.id.tabs)
  val pager: ViewPager by viewById(R.id.pager)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main_activity)

    val adapter = TabPageAdapter(getFragmentManager())
    pager.setAdapter(adapter)
    tabs.setTabMode(TabLayout.MODE_SCROLLABLE)
    tabs.setupWithViewPager(pager)

    fun onPageChanged(position: Int) {
      val theme = UnitConverter.Theme(
          this@MainActivity,
          adapter.positionToConverter(position))

      tabs.setBackgroundColor(theme.colorPrimary)
      if (isLollipop) getWindow().setStatusBarColor(theme.colorPrimaryDark)
    }

    pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
      override fun onPageSelected(position: Int) {
        onPageChanged(position)
        Preferences.lastConverter = adapter.positionToConverter(position)
      }
    })

    if (savedInstanceState == null) {
      pager.setCurrentItem(adapter.converterToPosition(Preferences.lastConverter))
    }

    onPageChanged(pager.getCurrentItem())
  }

  inner class TabPageAdapter(val fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment =
        ConverterFragment(positionToConverter(position))

    override fun getCount(): Int = 5

    override fun getPageTitle(position: Int): CharSequence =
        getString(positionToConverter(position).displayNameResId)

    fun converterToPosition(uc: UnitConverter) =
        when (uc) {
          UnitConverter.LENGTH -> 0
          UnitConverter.ENERGY -> 1
          UnitConverter.MASS -> 2
          UnitConverter.TEMPERATURE -> 3
          UnitConverter.VOLUME -> 4
          else -> throw IndexOutOfBoundsException()
        }

    fun positionToConverter(position: Int): UnitConverter =
        when (position) {
          0 -> UnitConverter.LENGTH
          1 -> UnitConverter.ENERGY
          2 -> UnitConverter.MASS
          3 -> UnitConverter.TEMPERATURE
          4 -> UnitConverter.VOLUME
          else -> throw IndexOutOfBoundsException()
        }
  }
}

