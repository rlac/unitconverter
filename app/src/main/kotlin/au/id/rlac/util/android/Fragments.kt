package au.id.rlac.util.android

import android.app.Fragment

fun Fragment?.isRemovingOrParentRemoving(): Boolean =
    this != null && (isRemoving() || (isJellyBeanMR1 && getParentFragment().isRemovingOrParentRemoving()))
