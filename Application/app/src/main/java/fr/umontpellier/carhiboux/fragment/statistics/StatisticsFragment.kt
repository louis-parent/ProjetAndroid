package fr.umontpellier.carhiboux.fragment.statistics

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.activity.MainActivity
import fr.umontpellier.carhiboux.bundle.BundleKeys
import fr.umontpellier.carhiboux.chart.IndexAxisMonthFormatter
import fr.umontpellier.carhiboux.chart.IntegerValueFormatter
import fr.umontpellier.carhiboux.entity.enumeration.AnnouncementType
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory
import java.text.DateFormatSymbols
import java.time.LocalDateTime

class StatisticsFragment : Fragment()
{
    private var userId : Long? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        userId = arguments?.getLong(BundleKeys.USER)

        if(userId == null)
        {
            if((activity as MainActivity).isConnectedUser())
            {
                userId = (activity as MainActivity).getConnectedUser()?.requireId()
            }
            else
            {
                findNavController().navigate(R.id.to_login, bundleOf(
                    BundleKeys.REDIRECT to (R.id.to_statistics to bundleOf())
                ))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view: View = inflater.inflate(R.layout.statistics_fragment, container, false)

        initializeMessageByMonthChart(view)
        initializeAnnouncementByType(view)

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeMessageByMonthChart(view: View)
    {
        val messagePerMonth = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

        val now = LocalDateTime.now()
        val twelveMonthAgo = now.minusMonths(12)

        RepositoryFactory.getPreferredFactory().getMessageRepository().readIf({
            it.destination == userId && it.dateTime <= now && it.dateTime >= twelveMonthAgo
        }) { messages ->
            for(message in messages)
            {
                val index = if(message.dateTime.monthValue < twelveMonthAgo.monthValue) {
                    (message.dateTime.monthValue - 1) + (twelveMonthAgo.monthValue - 1)
                } else {
                    (message.dateTime.monthValue - 1) - (twelveMonthAgo.monthValue - 1)
                }

                messagePerMonth[index]++
            }

            val entries = mutableListOf<BarEntry>()

            for(index in messagePerMonth.indices)
            {
                val monthIndex = ((twelveMonthAgo.monthValue - 1) + index) % 12
                val month = DateFormatSymbols().months[monthIndex]

                entries.add(BarEntry(index.toFloat(), messagePerMonth[index].toFloat(), month))
            }

            val chart = view.findViewById<BarChart>(R.id.statistics_message_by_month)
            chart.data = BarData(BarDataSet(entries, ""))
            customizeBarChart(chart)
            chart.invalidate()
        }
    }

    private fun initializeAnnouncementByType(view: View)
    {
        RepositoryFactory.getPreferredFactory().getAnnouncementRepository().readIf({
            it.author == userId
        }) { announcements ->
            val rentAmount = announcements.filter {
                it.type == AnnouncementType.RENT
            }.size

            val sellAmount = announcements.filter {
                it.type == AnnouncementType.SELL
            }.size

            val entries = mutableListOf<PieEntry>()

            entries.add(PieEntry(rentAmount.toFloat(), view.context.getString(R.string.to_rent)))
            entries.add(PieEntry(sellAmount.toFloat(), view.context.getString(R.string.to_sell)))

            val chart = view.findViewById<PieChart>(R.id.statistics_announcement_by_type)
            chart.data = PieData(PieDataSet(entries, view.context.getString(R.string.announcement_type)))
            customizePieChart(chart)
            chart.invalidate()
        }
    }

    private fun customizeBarChart(chart: BarChart)
    {
        chart.description.isEnabled = false
        chart.axisLeft.setDrawLabels(false)
        chart.axisRight.setDrawLabels(false)
        chart.legend.isEnabled = false
        chart.data.setValueFormatter(IntegerValueFormatter())
        chart.xAxis.valueFormatter = IndexAxisMonthFormatter()
        chart.setFitBars(true)
    }

    private fun customizePieChart(chart: PieChart)
    {
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.data.setValueFormatter(IntegerValueFormatter())
    }
}