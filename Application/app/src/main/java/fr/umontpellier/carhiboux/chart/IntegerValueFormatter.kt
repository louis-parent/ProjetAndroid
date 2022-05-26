package fr.umontpellier.carhiboux.chart

import com.github.mikephil.charting.formatter.ValueFormatter

class IntegerValueFormatter : ValueFormatter()
{
    override fun getFormattedValue(value: Float): String
    {
        return value.toInt().toString()
    }
}