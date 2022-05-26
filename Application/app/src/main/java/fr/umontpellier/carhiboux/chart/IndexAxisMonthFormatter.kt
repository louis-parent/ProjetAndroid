package fr.umontpellier.carhiboux.chart

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.DateFormatSymbols

class IndexAxisMonthFormatter : IndexAxisValueFormatter(DateFormatSymbols().months)
{
}