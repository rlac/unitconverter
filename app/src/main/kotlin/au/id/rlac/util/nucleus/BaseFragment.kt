package au.id.rlac.util.nucleus

import android.app.Fragment
import android.os.Bundle
import au.id.rlac.util.nucleus.NucleusHelper
import nucleus.factory.ReflectionPresenterFactory
import nucleus.manager.PresenterManager
import nucleus.presenter.Presenter

public abstract class BaseFragment<ViewType, PresenterType : Presenter<ViewType>> : Fragment() {
  private var presenterHelper = NucleusHelper<ViewType, PresenterType>(
      PresenterManager.getInstance(), ReflectionPresenterFactory.fromViewClass(javaClass))
  val presenter: PresenterType get() = presenterHelper.presenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super<Fragment>.onCreate(savedInstanceState)
    @Suppress("UNCHECKED_CAST")
    presenterHelper.create(this as ViewType, savedInstanceState)
  }

  override fun onResume() {
    super<Fragment>.onResume()
    presenterHelper.resume()
  }

  override fun onPause() {
    super<Fragment>.onPause()
    presenterHelper.pause()
  }

  override fun onDestroy() {
    super<Fragment>.onDestroy()
    presenterHelper.destroy()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super<Fragment>.onSaveInstanceState(outState)
    presenterHelper.save(outState)
  }
}