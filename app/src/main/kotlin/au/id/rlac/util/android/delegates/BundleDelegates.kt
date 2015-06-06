package au.id.rlac.util.android.delegates

import android.app.Fragment
import android.os.Bundle
import android.os.Parcelable
import au.id.rlac.util.android.delegates
import au.id.rlac.util.android.delegates.CachedKeyValueVal
import au.id.rlac.util.android.delegates.propertyNameKey
import java.io.Serializable
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import android.support.v4.app.Fragment as SupportFragment

/**
 * Delegates to a value provided by the Fragment arguments.
 *
 * @param key Called to retrieve the Bundle key. By default this is the property name.
 * @param default The default value. By default this is null and throws NoDefaultValueException.
 */
public fun Fragment.argument<V>(key: (PropertyMetadata) -> String = propertyNameKey,
                                      default: V = null): ReadOnlyProperty<Any?, V> =
    BundleDelegates.bundleVal(bundleFromArguments, key, default)

/**
 * Delegates to the result of a Fragment argument passed to a value mapping function.
 */
public fun Fragment.argument<BundleV, V>(key: (PropertyMetadata) -> String = propertyNameKey,
                                               default: V = null,
                                               map: (BundleV) -> V): ReadOnlyProperty<Any?, V> =
    BundleDelegates.mapBundleVal<BundleV, V>(bundleFromArguments, key, default, map)

/**
 * Delegates to a value provided by the support library Fragment arguments.
 *
 * @param key Called to retrieve the Bundle key. By default this is the property name.
 * @param default The default value. By default this is null and throws NoDefaultValueException.
 */
public fun SupportFragment.argument<V>(key: (PropertyMetadata) -> String = propertyNameKey,
                                             default: V = null): ReadOnlyProperty<Any?, V> =
    BundleDelegates.bundleVal(bundleFromArguments, key, default)

/**
 * Delegates to the result of a Fragment argument passed to a value mapping function.
 */
public fun SupportFragment.argument<BundleV, V>(key: (PropertyMetadata) -> String = propertyNameKey,
                                                      default: V = null,
                                                      map: (BundleV) -> V): ReadOnlyProperty<Any?, V> =
    BundleDelegates.mapBundleVal<BundleV, V>(bundleFromArguments, key, default, map)

private val bundleFromArguments: (Any?) -> android.os.Bundle =
    { thisRef ->
      when (thisRef) {
        is android.app.Fragment -> thisRef.getArguments()
        is android.support.v4.app.Fragment -> thisRef.getArguments()
        else -> throw IllegalArgumentException()
      }
    }

/**
 * Property delegates backed by a Bundle.
 *
 * Extension functions for Fragments (framework and support library versions) also allow using
 * these to add properties for argument values.
 */
public object BundleDelegates {

  internal fun mapBundleVal<BundleV, V>(bundle: (Any?) -> android.os.Bundle,
                                        key: (PropertyMetadata) -> String = propertyNameKey,
                                        default: V = null,
                                        map: (BundleV) -> V): ReadOnlyProperty<Any?, V> =
      @suppress("UNCHECKED_CAST")
      (CachedKeyValueVal(bundle, key, default, contains, { k -> map(read(k) as BundleV) }))

  internal fun bundleVal<V>(bundle: (Any?) -> Bundle,
                            key: (PropertyMetadata) -> String = propertyNameKey,
                            default: V = null): ReadOnlyProperty<Any?, V> =
      @suppress("UNCHECKED_CAST")
      CachedKeyValueVal(bundle, key, default, contains, read as Bundle.(String) -> V)

  /**
   * Delegates to a value in a bundle.
   *
   * @param bundle The bundle the value is in.
   * @param key Called to retrieve the Bundle key. By default this is the property name.
   * @param default The default value. By default throws NoDefaultValueException.
   */
  public fun bundleVal<V>(bundle: Bundle,
                          key: (PropertyMetadata) -> String = propertyNameKey,
                          default: V = null): ReadOnlyProperty<Any?, V> =
      @suppress("UNCHECKED_CAST")
      CachedKeyValueVal({ bundle }, key, default, contains, read as Bundle.(String) -> V)

  /**
   * Delegates to a variable in a bundle.
   *
   * Bundle variables support the following types:
   *  - String
   *  - Short
   *  - Int
   *  - Long
   *  - Float
   *  - Boolean
   *  - Byte
   *  - Char
   *  - CharSequence
   *  - Parcelable
   *  - Serializable
   *
   * Using this with an unsupported type will result in an [IllegalArgumentException] being thrown when
   * set is called.
   *
   * @param The bundle the value is in.
   * @param key Called to retrieve the Bundle key. By default this is the property name.
   * @param default The default value. By default throws NoDefaultValueException.
   */
  public fun bundleVar<V>(bundle: Bundle,
                          key: (PropertyMetadata) -> String = propertyNameKey,
                          default: V = null): ReadWriteProperty<Any?, V> =
      @suppress("UNCHECKED_CAST")
      (delegates.KeyValueVar({ bundle }, key, default, contains, read as Bundle.(String) -> V, put))

  private val contains: Bundle.(String) -> Boolean = { k -> containsKey(k) }
  private val read: Bundle.(String) -> Any = { k -> get(k) }
  private val put: Bundle.(String, Any) -> Unit = { key, value ->
    when (value) {
      is String -> putString(key, value)
      is Short -> putShort(key, value)
      is Int -> putInt(key, value)
      is Long -> putLong(key, value)
      is Float -> putFloat(key, value)
      is Boolean -> putBoolean(key, value)
      is Byte -> putByte(key, value)
      is Char -> putChar(key, value)
      is CharSequence -> putCharSequence(key, value)
      is Parcelable -> putParcelable(key, value)
      is Serializable -> putSerializable(key, value)
      else -> throw IllegalArgumentException("Type is not supported by BundleDelegates.")
    }
  }
}
