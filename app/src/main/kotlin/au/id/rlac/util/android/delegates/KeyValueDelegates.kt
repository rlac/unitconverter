package au.id.rlac.util.android.delegates


internal val propertyNameKey: (PropertyMetadata) -> String = { it.name }

/**
 * Non-caching key-value backed read-only property.
 */
internal open class KeyValueVal<TRef, Val, Key, Dict>(val dict: (thisRef: TRef) -> Dict,
                                                      val key: (PropertyMetadata) -> Key,
                                                      val default: Val,
                                                      val contains: Dict.(Key) -> Boolean,
                                                      val read: Dict.(Key) -> Val) :
    kotlin.properties.ReadOnlyProperty<TRef, Val> {

  override fun get(thisRef: TRef, desc: PropertyMetadata): Val {
    val key = key(desc)
    val dictionary = dict(thisRef)

    return if (dictionary.contains(key)) dictionary.read(key)
    else default ?: throw IllegalArgumentException()
  }
}

/**
 * Non-caching key-value backed read/write property.
 */
internal open class KeyValueVar<TRef, Val, Key, Dict>(dict: (ref: TRef) -> Dict,
                                                      key: (PropertyMetadata) -> Key,
                                                      default: Val,
                                                      contains: Dict.(Key) -> Boolean,
                                                      read: Dict.(Key) -> Val,
                                                      val write: Dict.(Key, Val) -> Unit) :
    KeyValueVal<TRef, Val, Key, Dict>(dict, key, default, contains, read),
    kotlin.properties.ReadWriteProperty<TRef, Val> {

  override fun set(thisRef: TRef, desc: PropertyMetadata, value: Val) {
    dict(thisRef).write(key(desc), value)
  }
}

/**
 * Performs get once only and then returns the cached value.
 */
internal open class CachedKeyValueVal<TRef, Val, Key, Dict>(dict: (ref: TRef) -> Dict,
                                                            key: (PropertyMetadata) -> Key,
                                                            default: Val,
                                                            contains: Dict.(Key) -> Boolean,
                                                            read: Dict.(Key) -> Val) :
    KeyValueVal<TRef, Val, Key, Dict>(dict, key, default, contains, read) {

  var cached: Val = null

  override fun get(thisRef: TRef, desc: PropertyMetadata): Val =
      if (cached != null) cached
      else synchronized(this) {
        if (cached == null) {
          cached = super.get(thisRef, desc)
        }

        cached
      }
}
