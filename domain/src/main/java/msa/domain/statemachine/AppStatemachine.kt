package msa.domain.statemachine

import com.freeletics.rxredux.reduxStore
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import msa.domain.core.Action
import msa.domain.core.BaseStateMachine
import msa.domain.core.State
import msa.domain.entities.EventRecord
import msa.domain.usecases.DeleteAllEventRecords
import msa.domain.usecases.GetEventRecords
import msa.domain.usecases.InsertEventRecord

/**
 * Created by Abhi Muktheeswarar.
 */


sealed class EventAction : Action {

    object GetEventRecordsAction : EventAction()

    data class GetEventRecordAction(val id: Int) : EventAction()

    object LoadingEventRecordsAction : EventAction()

    data class EventRecordsLoadedAction(val eventRecords: List<EventRecord>) : EventAction()

    data class EventRecordLoadedAction(val eventRecord: EventRecord) : EventAction()

    data class InsertEventRecordAction(val eventRecord: EventRecord) : EventAction()

    data class EventRecordInsertedAction(val id: Int) : EventAction()

    data class ErrorAction(val exception: Exception) : EventAction()

    object DeleteAllEventRecords : EventAction()
}

data class EventState(
    val loading: Boolean = false,
    val eventRecords: List<EventRecord>? = null,
    val exception: Exception? = null
) : State


class AppStateMachine(
    getEventRecords: GetEventRecords,
    insertEventRecord: InsertEventRecord,
    deleteAllEventRecords: DeleteAllEventRecords
) :
    BaseStateMachine<EventState> {

    init {

        println("AppStateMachine Init...")
    }

    override val input: Relay<Action> = PublishRelay.create()

    override val state: Observable<EventState> = input
        .doOnNext { println("Input Action ${it.javaClass.simpleName}") }
        .reduxStore(
            initialState = EventState(),
            sideEffects = listOf(
                getEventRecords::getEventRecordsSideEffect,
                insertEventRecord::insertEventRecordSideEffect,
                deleteAllEventRecords::deleteAllEventRecordSideEffect
            ),
            reducer = ::reducer
        )
        .distinctUntilChanged()
        .doOnNext { println("RxStore state ${it.javaClass.simpleName}") }


    override fun reducer(state: EventState, action: Action): EventState {
        return when (action) {

            is EventAction.LoadingEventRecordsAction -> state.copy(loading = true)

            is EventAction.EventRecordsLoadedAction -> state.copy(
                loading = false,
                eventRecords = action.eventRecords,
                exception = null
            )

            is EventAction.ErrorAction -> state.copy(loading = false, exception = action.exception)

            is EventAction.InsertEventRecordAction -> state.copy(loading = true)

            is EventAction.EventRecordInsertedAction -> state.copy(loading = false)

            else -> state
        }
    }
}