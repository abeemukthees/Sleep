package msa.domain.usecases

import com.freeletics.rxredux.StateAccessor
import io.reactivex.Observable
import io.reactivex.Scheduler
import msa.domain.core.Action
import msa.domain.core.State
import msa.domain.entities.EventRecord
import msa.domain.interactor.UseCase
import msa.domain.repository.Repository
import msa.domain.statemachine.EventAction
import msa.domain.statemachine.EventState
import java.util.*

/**
 * Created by Abhi Muktheeswarar.
 */

class GetEventRecords(
    private val repository: Repository, ioScheduler: Scheduler,
    computationScheduler: Scheduler
) : UseCase(ioScheduler, computationScheduler) {

    override fun buildUseCaseObservable(action: Action, state: State): Observable<Action> {
        return repository.getEventRecords()
            .map {
                val eventRecordsToProcess = it.groupBy {
                    Calendar.getInstance().apply { timeInMillis = it.startDate.time }.get(Calendar.DATE)
                }
                val processedEventRecords = arrayListOf<EventRecord>()
                for ((key, value) in eventRecordsToProcess) {

                    println("$key -> $value")
                    val largestSleep = value.sortedBy { it.durationInMilliSeconds }.last()
                    println("Largest -> $largestSleep")
                    processedEventRecords.add(largestSleep)

                }

                EventAction.EventRecordsLoadedAction(processedEventRecords)
            }
    }

    fun getEventRecordsSideEffect(actions: Observable<Action>, state: StateAccessor<State>): Observable<Action> =
        actions.ofType(EventAction.GetEventRecordsAction::class.java)
            .filter { (state() as EventState).eventRecords.isNullOrEmpty() }
            .switchMap { execute(it, state()).startWith(EventAction.LoadingEventRecordsAction) }
}