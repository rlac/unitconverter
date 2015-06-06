package au.id.rlac.util.nucleus

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import au.id.rlac.util.android.isRemovingOrParentRemoving
import nucleus.factory.PresenterFactory
import nucleus.manager.PresenterManager
import nucleus.presenter.Presenter

/**
 * Helper class that manages the lifecycle events for a [Fragment] or [Activity] [Presenter].
 *
 * The Fragment or Activity using the helper must call the appropriate lifecycle methods during
 * its lifecycle - [create], [resume], [pause], [save], and [destroy].
 *
 * @param factory Constructs a new presenter instance if one is not found in the manager.
 * @param manager Presenter instance manager.
 */
public class NucleusHelper<ViewType : Any, PresenterType : Presenter<ViewType>>
(private val manager: PresenterManager,
 private val factory: PresenterFactory<PresenterType>) {

  private companion object {
    val presenterStateKey = "presenter_state"
  }

  private var viewRef: ViewType? = null
  private var maybePresenter: PresenterType? = null

  /**
   * Get the presenter instance, available after [create] has been called.
   *
   * @throws IllegalStateException if [create] has not been called.
   */
  public val presenter: PresenterType
    get() = maybePresenter ?: throw IllegalStateException("Create not yet called")

  /**
   * Initialize the presenter or reattach to an existing presenter instance for this view.
   *
   * @param state Saved state instance from the view class if available.
   * @param viewRef The view reference. Must be a [Fragment] or [Activity].
   * @throws IllegalArgumentException if [viewRef] is unsupported (not a [Fragment] or [Activity]).
   * @throws IllegalStateException if [create] has already been called on this helper instance.
   */
  public fun create(viewRef: ViewType, state: Bundle?) {
    if (this.viewRef != null) throw IllegalStateException("Create already called")

    when (viewRef) {
      is Fragment -> Unit
      is Activity -> Unit
      else -> throw IllegalArgumentException("View must be a Fragment or Activity")
    }

    this.viewRef = viewRef
    val presenterState = state?.getBundle(presenterStateKey)
    maybePresenter = manager.provide<PresenterType>(factory, presenterState)
  }

  /**
   * Attach the view for the helper to the presenter.
   *
   * @throws IllegalStateException if [create] has not been called.
   */
  public fun resume() {
    with (viewRef) {
      if (this == null) throw IllegalStateException("Create not called")
      presenter.takeView(this)
    }
  }

  /**
   * Drop the view for the helper from the presenter.
   *
   * @throws IllegalStateException if [create] has not been called.
   */
  public fun pause() {
    presenter.dropView()
  }

  /**
   * If the view class is permanently being destroyed (finishing or being removed), destroys the
   * presenter.
   *
   * @throws IllegalStateException if [create] has not been called or the attached view is not
   * supported.
   */
  public fun destroy() {
    if (isViewGoingAway()) manager.destroy(presenter)
  }

  /**
   * Requests the presenter manager save the presenters state to a [Bundle].
   *
   * This method should usually be called when the view is saving instance state.
   *
   * @param state Bundle to save presenter state to.
   */
  public fun save(state: Bundle) {
    state.putBundle(presenterStateKey, manager.save(presenter))
  }

  private fun isViewGoingAway(): Boolean =
      with (viewRef!!) {
        when (this) {
          is Fragment -> isRemovingOrParentRemoving()
          is Activity -> isFinishing()
          else -> throw IllegalStateException()
        }
      }
}