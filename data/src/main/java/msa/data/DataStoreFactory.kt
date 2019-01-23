package msa.data

import android.content.Context
import androidx.room.Room
import msa.data.local.EventRecordDatabase
import msa.data.local.LocalDataStore

/**
 * Created by Abhi Muktheeswarar.
 */

class DataStoreFactory(applicationContext: Context) {

    val localDataStore: LocalDataStore

    init {
        val eventRecordDatabase = Room.databaseBuilder(
            applicationContext,
            EventRecordDatabase::class.java, "EventRecordDb"
        ).enableMultiInstanceInvalidation().build()

        localDataStore = LocalDataStore(eventRecordDatabase)
    }
}