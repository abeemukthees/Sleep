package msa.sleep

import io.reactivex.Scheduler
import msa.domain.statemachine.AppStateMachine
import msa.sleep.base.BaseViewModel
import timber.log.Timber

/**
 * Created by Abhi Muktheeswarar.
 */

class EventViewModel(appStateMachine: AppStateMachine, postExecutionScheduler: Scheduler) : BaseViewModel() {

    init {

        Timber.d("Init...")

        addDisposable(inputRelay.subscribe(appStateMachine.input))
        addDisposable(appStateMachine.state.observeOn(postExecutionScheduler).subscribe { state ->
            mutableState.value = state
        })
    }
}