package au.id.rlac.util.android.delegates


import android.app.Activity
import android.view.View
import android.view.ViewGroup
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Delegate property for views on Activity classes.
 *
 * class MyActivity : Activity() {
 *   val myTextView: TextView by viewById(R.id.my_text_view)
 * }
 *
 * @param id The id of the view to find.
 * @return a delegate property that finds the view in the Activity by id when first accessed.
 */
fun <T : View> Activity.viewById(id: Int): ReadOnlyProperty<Any, T> = ViewDelegates.ViewVal(id)

/**
 * Delegate property for views within a ViewGroup.
 *
 * class MyCustomViewGroup(c: Context, a: AttributeSet?) : FrameLayout(c, a, 0) {
 *   val myTextView: TextView by viewById(R.id.my_text_view)
 * }
 *
 * @param id The id of the view to find.
 * @return a delegate property that finds a view under the ViewGroup by id when first accessed
 */
fun <T : View> ViewGroup.viewById(id: Int): ReadOnlyProperty<Any, T> = ViewDelegates.ViewVal(id)

object ViewDelegates {
  interface ViewHolder {
    val view: View

    fun <T : View> viewById(id: Int): ReadOnlyProperty<Any, T> = ViewVal(id, view)
  }

  fun <T : View> viewById(v: View, id: Int): ReadOnlyProperty<Any, T> = ViewVal(id, v)

  internal class ViewVal<T : View>(val id: Int, val v: View? = null) : ReadOnlyProperty<Any, T> {
    var foundView: T? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
      @Suppress("UNCHECKED_CAST")
      if (foundView == null) {
        foundView =
            if (v != null) v.findViewById(id) as T
            else when (thisRef) {
              is Activity -> thisRef.findViewById(id)
              is ViewHolder -> thisRef.view.findViewById(id)
              is ViewGroup -> thisRef.findViewById(id)
              else -> throw IllegalArgumentException("Class containing view delegate is not a supported type.")
            } as T
      }

      return foundView!!
    }
  }
}
