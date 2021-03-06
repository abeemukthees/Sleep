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

class DeleteAllEventRecords(
    private val repository: Repository, ioScheduler: Scheduler,
    computationScheduler: Scheduler
) : UseCase(ioScheduler, computationScheduler) {

    override fun buildUseCaseObservable(action: Action, state: State): Observable<Action> {
        return repository.deleteAllEventRecords().toObservable()
    }

    fun deleteAllEventRecordSideEffect(actions: Observable<Action>, state: StateAccessor<State>): Observable<Action> =
        actions.ofType(EventAction.DeleteAllEventRecords::class.java)
            .switchMap { execute(it, state()) }
}