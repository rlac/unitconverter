package au.id.rlac.util.android

import android.app.Fragment

public fun Fragment?.isRemovingOrParentRemoving(): Boolean =
    this != null
        &&
        (isRemoving() || (isJellyBeanMR1 && getParentFragment().isRemovingOrParentRemoving()))
