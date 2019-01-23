package msa.domain.usecases

import com.freeletics.rxredux.StateAccessor
import io.reactivex.Observable
import io.reactivex.Scheduler
import msa.domain.core.Action
import msa.domain.core.State
import msa.domain.interactor.UseCase
import msa.domain.repository.Repository
import msa.domain.statemachine.EventAction

/**
 * Created by Abhi Muktheeswarar.
 */

class GetEventRecord(
    private val repository: Repository, ioScheduler: Scheduler,
    computationScheduler: Scheduler
) : UseCase(ioScheduler, computationScheduler) {

    override fun buildUseCaseObservable(action: Action, state: State): Observable<Action> {
        val id = (action as EventAction.GetEventRecordAction).id
        return repository.getEventRecord(id).toObservable().map { EventAction.EventRecordLoadedAction(it) }
    }

    fun getEventRecordSideEffect(actions: Observable<Action>, state: StateAccessor<State>): Observable<Action> =
        actions.ofType(EventAction.GetEventRecordAction::class.java)
            .switchMap { execute(it, state()) }
}