package fr.umontpellier.carhiboux.view

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView

class AdapterTextView(str: String?, context: Context) : AppCompatTextView(context)
{
    init
    {
        text = str
        setPadding(16, 16, 16, 16)
    }
}