package au.id.rlac.util.android

import android.os.Parcel
import android.os.Parcelable
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.InlineOption.ONLY_LOCAL_RETURN

/* Extension methods to add read and write methods for additional types to Parcel. */

public fun Parcel.writeBigInteger(bi: BigInteger) {
  with (bi.toByteArray()) {
    writeInt(size())
    writeByteArray(this)
  }
}

public fun Parcel.readBigInteger(): BigInteger =
    with (ByteArray(readInt())) {
      readByteArray(this)
      BigInteger(this)
    }

public fun Parcel.writeNullableBigInteger(bi: BigInteger?) {
  if (bi == null) writeByte(0)
  else {
    writeByte(1)
    writeBigInteger(bi)
  }
}

public fun Parcel.readNullableBigInteger(): BigInteger? =
    if (readByte() == 0.toByte()) null
    else readBigInteger()

public fun Parcel.writeBigDecimal(bd: BigDecimal) {
  writeBigInteger(bd.unscaledValue())
  writeInt(bd.scale())
}

public fun Parcel.readBigDecimal(): BigDecimal = BigDecimal(readBigInteger(), readInt())

public fun Parcel.writeNullableBigDecimal(bd: BigDecimal?) {
  if (bd == null) writeByte(0)
  else {
    writeByte(1)
    writeBigDecimal(bd)
  }
}

public fun Parcel.readNullableBigDecimal(): BigDecimal? =
    if (readByte() == 0.toByte()) null
    else readBigDecimal()

public fun Parcel.writeBoolean(b: Boolean) {
  writeByte(if (b) 1 else 0)
}

public fun Parcel.readBoolean(): Boolean = readByte() == 1.toByte()

public fun Parcel.writeNullableBoolean(b: Boolean?) {
  if (b == null) writeByte(2)
  else writeBoolean(b)
}

public fun Parcel.readNullableBoolean(): Boolean? =
    when (readByte()) {
      0.toByte() -> false
      1.toByte() -> true
      else -> null
    }

/**
 * Creates a [Parcelable.Creator], reducing boilerplate code when implementing the CREATOR field of
 * a [Parcelable].
 *
 * data class MyParcelable(var someProperty: String) : Parcelable {
 *   override fun describeContents(): Int = 0
 *   override fun writeToParcel(dest: Parcel, flags: Int) {
 *     dest.writeString(someProperty)
 *   }
 *
 *   class object {
 *     platformStatic val CREATOR = creator { MyParcelable(it.readString() }
 *   }
 * }
 */
public inline fun creator<reified T : Parcelable>(
    inlineOptions(ONLY_LOCAL_RETURN) createFromParcel: (Parcel) -> T?): Parcelable.Creator<T> =
    object : Parcelable.Creator<T> {
      override fun createFromParcel(source: Parcel): T? = createFromParcel(source)
      override fun newArray(size: Int): Array<out T?> = arrayOfNulls(size)
    }
