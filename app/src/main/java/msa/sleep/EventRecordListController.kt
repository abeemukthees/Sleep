package msa.sleep

import android.annotation.SuppressLint
import android.widget.TextView
import com.airbnb.epoxy.*
import msa.domain.statemachine.EventState
import msa.sleep.base.BaseEpoxyHolder
import msa.sleep.common.LoadingItemModel_
import timber.log.Timber
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Abhi Muktheeswarar.
 */

class EventRecordListController : TypedEpoxyController<EventState>() {


    @AutoModel
    lateinit var loadingItemModel: LoadingItemModel_

    fun setState(state: EventState) {
        setData(state)
    }

    override fun buildModels(data: EventState) {

        Timber.d(data.eventRecords.toString())

        loadingItemModel.addIf(data.loading, this)

        data.eventRecords?.forEach { eventRecord ->

            eventRecordItem {
                id(eventRecord.id)
                eventRecordId(eventRecord.id)
                startDate(eventRecord.startDate)
                endDate(eventRecord.endDate)
                durationInMilliSeconds(eventRecord.durationInMilliSeconds)
            }
        }
    }
}

@EpoxyModelClass(layout = R.layout.item_event)
abstract class EventRecordItemModel : EpoxyModelWithHolder<EventRecordItemModel.EventRecordViewHolder>() {

    @EpoxyAttribute
    open var eventRecordId: Int = -1

    @EpoxyAttribute
    lateinit var startDate: Date

    @EpoxyAttribute
    lateinit var endDate: Date

    @EpoxyAttribute
    open var durationInMilliSeconds: Long = 0

    @SuppressLint("SetTextI18n")
    override fun bind(holder: EventRecordViewHolder) {
        super.bind(holder)
        Timber.d("$eventRecordId, $durationInMilliSeconds")
        holder.dateTextView.text = DateFormat.getDateInstance().format(startDate)

        val formattedDuration = String.format(
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(durationInMilliSeconds),
            TimeUnit.MILLISECONDS.toMinutes(durationInMilliSeconds) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(durationInMilliSeconds)),
            TimeUnit.MILLISECONDS.toSeconds(durationInMilliSeconds) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationInMilliSeconds))
        )

        holder.durationTextView.text = formattedDuration

        holder.startTimeTextView.text = DateFormat.getTimeInstance().format(startDate)
        holder.endTimeTextView.text = DateFormat.getTimeInstance().format(endDate)
    }

    class EventRecordViewHolder : BaseEpoxyHolder() {

        val dateTextView by bind<TextView>(R.id.text_date)
        val durationTextView by bind<TextView>(R.id.text_duration)
        val startTimeTextView by bind<TextView>(R.id.text_startTime)
        val endTimeTextView by bind<TextView>(R.id.text_endTime)

    }
}