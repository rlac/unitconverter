package au.id.rlac.unitconverter.widget

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

/**
 * Basic static collection spinner adapter that supports passing in custom binding functions.
 *
 * @property layout Item layout, must have a TextView with the id text1.
 * @property ddLayout Drop down item layout, must have a TextView with the id text1.
 * @property bind Called to bind an item to a TextView.
 * @property getId Called to request the item's id.
 * @property items The list of items to bind. The list is owned by this adapter and must not be
 * modified.
 */
class BindableSpinnerAdapter<T>(context: Context,
                                private val layout: Int = R.layout.simple_spinner_item,
                                private val ddLayout: Int = R.layout.simple_spinner_dropdown_item,
                                private val bind: (T, TextView) -> Unit,
                                private val getId: (T) -> Long,
                                private val items: List<T>) : BaseAdapter() {

  val inflater = LayoutInflater.from(context)

  override fun getCount(): Int = items.size()

  override fun getItem(position: Int): Any? = items[position]

  override fun getItemId(position: Int): Long = getId(items[position])

  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
    val view = convertView ?: inflater.inflate(layout, parent, false)
    bind(items[position], view.findViewById(R.id.text1) as TextView)
    return view
  }

  override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? {
    val view = convertView ?: inflater.inflate(ddLayout, parent, false)
    bind(items[position], view.findViewById(R.id.text1) as TextView)
    return view
  }
}