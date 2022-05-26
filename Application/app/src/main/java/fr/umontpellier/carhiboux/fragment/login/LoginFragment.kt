package fr.umontpellier.carhiboux.fragment.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.activity.MainActivity
import fr.umontpellier.carhiboux.bundle.BundleKeys
import fr.umontpellier.carhiboux.entity.user.ParticularUser
import fr.umontpellier.carhiboux.entity.user.ProfessionalUser
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory

class LoginFragment : Fragment()
{
    private var redirect : Pair<Int, Bundle?>? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        redirect = arguments?.get(BundleKeys.REDIRECT) as Pair<Int, Bundle?>?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.login_fragment, container, false)

        view.findViewById<Button>(R.id.login_validate).setOnClickListener {
            val email = view.findViewById<EditText>(R.id.login_email).text.toString()
            val password = view.findViewById<EditText>(R.id.login_password).text.toString()
            connect(email, password)
        }

        view.findViewById<TextView>(R.id.login_register).setOnClickListener {
            findNavController().navigate(R.id.from_login_to_register, bundleOf(BundleKeys.REDIRECT to redirect))
        }

        return view
    }

    private fun connect(email: String, password: String)
    {
        RepositoryFactory.getPreferredFactory().getUserRepository().readIf ({
            it.email == email && it.password == password
        }) { users ->
            if(users.isNotEmpty())
            {
                val user = users[0]

                if(user.isProfessional)
                {
                    (activity as MainActivity).connectUser(user as ProfessionalUser)
                }
                else
                {
                    (activity as MainActivity).connectUser(user as ParticularUser)
                }
                requireActivity().runOnUiThread {
                    if(redirect != null)
                    {
                        findNavController().navigate(redirect!!.first, redirect!!.second)
                    }
                    else
                    {
                        findNavController().navigate(R.id.to_announcement_list)
                    }
                }
            }
        }
    }
}