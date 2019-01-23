package msa.domain.entities

import java.util.*

/**
 * Created by Abhi Muktheeswarar.
 */

data class EventRecord(val id: Int = -1, val startDate: Date, val endDate: Date, val durationInMilliSeconds: Long)

enum class EventRecordType {

    SCREEN_ON, SCREEN_OFF, MOTION
}