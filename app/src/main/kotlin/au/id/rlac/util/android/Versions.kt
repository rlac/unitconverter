package au.id.rlac.util.android

import android.os.Build

val isLollipop: Boolean get() = Build.VERSION.SDK_INT >= 21
val isJellyBeanMR1: Boolean get() = Build.VERSION.SDK_INT >= 17
