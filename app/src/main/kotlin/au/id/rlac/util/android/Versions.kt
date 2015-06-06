package au.id.rlac.util.android

import android.os.Build

public val isLollipop: Boolean get() = Build.VERSION.SDK_INT >= 21
public val isJellyBeanMR1: Boolean get() = Build.VERSION.SDK_INT >= 17
