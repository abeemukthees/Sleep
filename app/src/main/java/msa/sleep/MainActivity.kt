package msa.sleep

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import msa.domain.statemachine.EventAction
import msa.domain.statemachine.EventState
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private val eventViewModel by viewModel<EventViewModel>()
    private val eventRecordListController by lazy { EventRecordListController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        button_start.setOnClickListener { startService(Intent(this, SpyService::class.java)) }
        button_stop.setOnClickListener { stopService(Intent(this, SpyService::class.java)) }


        epoxyRecyclerView.setController(eventRecordListController)
        epoxyRecyclerView.setItemSpacingDp(8)

        eventViewModel.state.observe(this, Observer {
            setupViews(it as EventState)
        })

        eventViewModel.input.accept(EventAction.GetEventRecordsAction)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_clear -> {

                eventViewModel.input.accept(EventAction.DeleteAllEventRecords)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun setupViews(state: EventState) {

        Timber.d("setupViews: $state")

        eventRecordListController.setState(state)
    }
}

