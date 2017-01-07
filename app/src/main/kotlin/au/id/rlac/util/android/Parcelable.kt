package au.id.rlac.util.android

import android.os.Parcel
import android.os.Parcelable
import java.math.BigDecimal
import java.math.BigInteger

/* Extension methods to add read and write methods for additional types to Parcel. */

fun Parcel.writeBigInteger(bi: BigInteger) {
  with (bi.toByteArray()) {
    writeInt(size)
    writeByteArray(this)
  }
}

fun Parcel.readBigInteger(): BigInteger =
    with (ByteArray(readInt())) {
      readByteArray(this)
      BigInteger(this)
    }

fun Parcel.writeNullableBigInteger(bi: BigInteger?) {
  if (bi == null) writeByte(0)
  else {
    writeByte(1)
    writeBigInteger(bi)
  }
}

fun Parcel.readNullableBigInteger(): BigInteger? =
    if (readByte() == 0.toByte()) null
    else readBigInteger()

fun Parcel.writeBigDecimal(bd: BigDecimal) {
  writeBigInteger(bd.unscaledValue())
  writeInt(bd.scale())
}

fun Parcel.readBigDecimal(): BigDecimal = BigDecimal(readBigInteger(), readInt())

fun Parcel.writeNullableBigDecimal(bd: BigDecimal?) {
  if (bd == null) writeByte(0)
  else {
    writeByte(1)
    writeBigDecimal(bd)
  }
}

fun Parcel.readNullableBigDecimal(): BigDecimal? =
    if (readByte() == 0.toByte()) null
    else readBigDecimal()

fun Parcel.writeBoolean(b: Boolean) {
  writeByte(if (b) 1 else 0)
}

fun Parcel.readBoolean(): Boolean = readByte() == 1.toByte()

fun Parcel.writeNullableBoolean(b: Boolean?) {
  if (b == null) writeByte(2)
  else writeBoolean(b)
}

fun Parcel.readNullableBoolean(): Boolean? =
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
 *   companion object {
 *     @JvmStatics val CREATOR = creator { MyParcelable(it.readString() }
 *   }
 * }
 */
inline fun <reified T : Parcelable> creator(
    crossinline createFromParcel: (Parcel) -> T?): Parcelable.Creator<T> =
    object : Parcelable.Creator<T> {
      override fun createFromParcel(source: Parcel): T? = createFromParcel(source)
      override fun newArray(size: Int): Array<out T?> = arrayOfNulls(size)
    }
