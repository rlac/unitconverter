package au.id.rlac.util.android.delegates


import android.app.Activity
import android.view.View
import android.view.ViewGroup
import kotlin.properties.ReadOnlyProperty

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
public fun Activity.viewById<T : View>(id: Int): ReadOnlyProperty<Any, T> = ViewDelegates.ViewVal(id)

/**
 * Delegate property for views with the [ViewHolder] trait.
 *
 * class MyViewHolder(val view: View) : ViewHolder {
 *   val myTextView: TextView by viewById(R.id.my_text_view)
 * }
 *
 * @param id The id of the view to find.
 * @return a delegate property that finds a child of the view property by id when first accessed.
 */
public fun ViewDelegates.ViewHolder.viewById<T : View>(id: Int): ReadOnlyProperty<Any, T> = ViewDelegates.ViewVal(id)

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
public fun ViewGroup.viewById<T : View>(id: Int): ReadOnlyProperty<Any, T> = ViewDelegates.ViewVal(id)

public object ViewDelegates {
  public interface ViewHolder {
    val view: View
  }

  public fun viewById<T : View>(v: View, id: Int): ReadOnlyProperty<Any, T> = ViewVal(id, v)

  internal class ViewVal<T : View>(val id: Int, val v: View? = null) : ReadOnlyProperty<Any, T> {
    var foundView: T? = null

    override fun get(thisRef: Any, desc: PropertyMetadata): T {
      @suppress("UNCHECKED_CAST")
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
