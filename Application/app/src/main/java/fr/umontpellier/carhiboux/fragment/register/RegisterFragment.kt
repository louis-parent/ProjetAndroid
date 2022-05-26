package fr.umontpellier.carhiboux.fragment.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.bundle.BundleKeys
import fr.umontpellier.carhiboux.entity.enumeration.Civility
import fr.umontpellier.carhiboux.entity.user.ParticularUser
import fr.umontpellier.carhiboux.entity.user.ProfessionalUser
import fr.umontpellier.carhiboux.entity.user.User
import fr.umontpellier.carhiboux.entity.util.Address
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory

class RegisterFragment : Fragment()
{
    private var redirect : Pair<Int, Bundle?>? = null

    private var particularFragment : ParticularRegisterFragment = ParticularRegisterFragment()
    private var professionalFragment: ProfessionalRegisterFragment = ProfessionalRegisterFragment()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        redirect = arguments?.get(BundleKeys.REDIRECT) as Pair<Int, Bundle?>?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view : View = inflater.inflate(R.layout.register_fragment, container, false)

        setFragment(particularFragment)

        view.findViewById<Switch>(R.id.register_toggle).setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
            {
                setFragment(professionalFragment)
            }
            else
            {
                setFragment(particularFragment)
            }
        }

        view.findViewById<Button>(R.id.register_validate).setOnClickListener {
            RepositoryFactory.getPreferredFactory().getUserRepository().create(getUser()) { user ->
                if(user != null)
                {
                    findNavController().navigate(R.id.to_login, bundleOf(
                        BundleKeys.REDIRECT to redirect
                    ))
                }
                else
                {
                    Toast.makeText(context, "Incorrect fields, try again", Toast.LENGTH_LONG).show()
                }
            }
        }

        view.findViewById<TextView>(R.id.register_login).setOnClickListener {
            findNavController().navigate(R.id.from_register_to_login, bundleOf(BundleKeys.REDIRECT to redirect))
        }

        return view
    }

    private fun setFragment(fragment : Fragment)
    {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.register_fragment, fragment)
        transaction.commit()
    }

    private fun getUser() : User
    {
        if(requireView().findViewById<Switch>(R.id.register_toggle).isChecked)
        {
            return ProfessionalUser(
                requireView().findViewById<EditText>(R.id.register_email).text.toString(),
                requireView().findViewById<EditText>(R.id.register_password).text.toString(),
                Address(
                    requireView().findViewById<EditText>(R.id.register_address).text.toString(),
                    requireView().findViewById<EditText>(R.id.register_zipcode).text.toString(),
                    requireView().findViewById<EditText>(R.id.register_city).text.toString(),
                    requireView().findViewById<EditText>(R.id.register_country).text.toString()
                ),
                requireView().findViewById<EditText>(R.id.register_phone).text.toString(),
                requireView().findViewById<EditText>(R.id.register_name).text.toString(),
                requireView().findViewById<EditText>(R.id.register_siret).text.toString(),
                false
            )
        }
        else
        {
            return ParticularUser(
                requireView().findViewById<EditText>(R.id.register_email).text.toString(),
                requireView().findViewById<EditText>(R.id.register_password).text.toString(),
                Address(
                    requireView().findViewById<EditText>(R.id.register_address).text.toString(),
                    requireView().findViewById<EditText>(R.id.register_zipcode).text.toString(),
                    requireView().findViewById<EditText>(R.id.register_city).text.toString(),
                    requireView().findViewById<EditText>(R.id.register_country).text.toString()
                ),
                requireView().findViewById<EditText>(R.id.register_phone).text.toString(),
                requireView().findViewById<Spinner>(R.id.register_civility).selectedItem as Civility,
                requireView().findViewById<EditText>(R.id.register_firstname).text.toString(),
                requireView().findViewById<EditText>(R.id.register_lastname).text.toString()
            )
        }
    }
}