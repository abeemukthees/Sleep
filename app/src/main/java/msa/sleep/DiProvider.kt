package msa.sleep

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import msa.data.DataStoreFactory
import msa.data.EventRecordRepository
import msa.domain.repository.Repository
import msa.domain.statemachine.AppStateMachine
import msa.domain.usecases.DeleteAllEventRecords
import msa.domain.usecases.GetEventRecord
import msa.domain.usecases.GetEventRecords
import msa.domain.usecases.InsertEventRecord
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

/**
 * Created by Abhi Muktheeswarar.
 */

val appModule = module {

    single { DataStoreFactory(get()) }
    single<Repository> { EventRecordRepository(get()) }
    single(name = "ioScheduler") { Schedulers.io() }
    single(name = "computationScheduler") { Schedulers.computation() }
    single(name = "postExecutionScheduler") { AndroidSchedulers.mainThread() }
}

val stateMachineModule = module {

    single { AppStateMachine(get(), get(), get()) }
}

val useCaseModule = module {

    factory { GetEventRecords(get(), get(name = "ioScheduler"), get(name = "computationScheduler")) }
    factory { InsertEventRecord(get(), get(name = "ioScheduler"), get(name = "computationScheduler")) }
    factory { GetEventRecord(get(), get(name = "ioScheduler"), get(name = "computationScheduler")) }
    factory { DeleteAllEventRecords(get(), get(name = "ioScheduler"), get(name = "computationScheduler")) }
}

val viewModelModule = module {

    viewModel { EventViewModel(get(), get(name = "postExecutionScheduler")) }
}