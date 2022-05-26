package fr.umontpellier.carhiboux.fragment.payment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.adapter.ArrayAdapter
import fr.umontpellier.carhiboux.bundle.BundleKeys
import java.text.DateFormatSymbols
import java.time.LocalDate

class PaymentFragment : Fragment()
{
    private var amount: Double = 0.0
    private var redirection : Pair<Int, Bundle?>? = null
    private var callback : ((Boolean) -> Unit?)? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        amount = (arguments?.get(BundleKeys.AMOUNT) as Double? ?: 0) as Double
        redirection = arguments?.get(BundleKeys.REDIRECT) as Pair<Int, Bundle?>?
        callback = arguments?.get(BundleKeys.CALLBACK) as ((Boolean) -> Unit)?
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.payment_fragment, container, false)

        view.findViewById<TextView>(R.id.payment_amount).text = amount.toString()
        view.findViewById<Spinner>(R.id.payment_card_expiration_month).adapter = ArrayAdapter(DateFormatSymbols().months)

        val years = mutableListOf<String>()
        val currentYear = LocalDate.now().year

        for(year in currentYear until currentYear + 25)
        {
            years.add(year.toString())
        }

        view.findViewById<Spinner>(R.id.payment_card_expiration_year).adapter = ArrayAdapter(years.toTypedArray())

        view.findViewById<FloatingActionButton>(R.id.payment_validate).setOnClickListener {
            if(callback != null)
            {
                callback?.invoke(true)
            }

            if(redirection != null)
            {
                findNavController().navigate(redirection!!.first, redirection!!.second)
            }
            else
            {
                findNavController().navigate(R.id.to_announcement_list)
            }
        }

        return view
    }
}