package fr.umontpellier.carhiboux.fragment.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.adapter.EnumAdapter
import fr.umontpellier.carhiboux.entity.enumeration.Civility

class ParticularRegisterFragment : Fragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.particular_register_fragment, container, false)

        view.findViewById<Spinner>(R.id.register_civility).adapter = EnumAdapter(Civility.values())

        return view
    }
}