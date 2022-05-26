package fr.umontpellier.carhiboux.fragment.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.activity.MainActivity
import fr.umontpellier.carhiboux.bundle.BundleKeys
import fr.umontpellier.carhiboux.entity.user.ParticularUser
import fr.umontpellier.carhiboux.entity.user.ProfessionalUser
import fr.umontpellier.carhiboux.entity.user.User
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory

class ProfileFragment : Fragment()
{
    private var user : User? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        
        val id = arguments?.get(BundleKeys.USER) as Long?
        when
        {
            id != null -> {
                RepositoryFactory.getPreferredFactory().getUserRepository().read(id) {
                    user = it
                }
            }
            (activity as MainActivity).isConnectedUser() -> {
                user = (activity as MainActivity).getConnectedUser()
            }
            else -> {
                findNavController().navigate(R.id.to_announcement_list)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view : View = inflater.inflate(R.layout.profile_fragment, container, false)

        initializeProfile(view)

        view.findViewById<Button>(R.id.profile_become_premium).setOnClickListener {
            findNavController().navigate(R.id.from_profile_to_payment, bundleOf(
                BundleKeys.AMOUNT to ProfessionalUser.getPremiumPrice(),

                BundleKeys.CALLBACK to { validated: Boolean ->
                    run {
                        if (validated) {
                            val professional = user as ProfessionalUser
                            professional.becomePremium().save {
                                (activity as MainActivity).connectUser(professional)
                            }
                        }
                    }
                },

                BundleKeys.REDIRECT to (
                    R.id.to_profile to bundleOf(BundleKeys.USER to user?.requireId())
                )
            ))
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun initializeProfile(view: View = requireView())
    {
        view.findViewById<ImageView>(R.id.profile_premium_icon).visibility = View.INVISIBLE
        view.findViewById<Button>(R.id.profile_become_premium).visibility = View.INVISIBLE

        if(user?.isProfessional == true)
        {
            val professional = user as ProfessionalUser

            if(professional.isPremium)
            {
                view.findViewById<ImageView>(R.id.profile_premium_icon).visibility = View.VISIBLE
            }
            else
            {
                view.findViewById<Button>(R.id.profile_become_premium).visibility = View.VISIBLE
            }

            view.findViewById<TextView>(R.id.profile_name).text = professional.name

            val siret = view.findViewById<TextView>(R.id.profile_siret)
            siret.text = professional.siret
            siret.visibility = View.VISIBLE
        }
        else if(user?.isProfessional == false)
        {
            val particular = user as ParticularUser

            view.findViewById<TextView>(R.id.profile_siret).visibility = View.INVISIBLE

            view.findViewById<TextView>(R.id.profile_name).text = "" + particular.civility + ". " + particular.firstName + " " + particular.lastName
        }

        view.findViewById<TextView>(R.id.profile_email).text = user?.email
        view.findViewById<TextView>(R.id.profile_phone).text = user?.phone
        view.findViewById<TextView>(R.id.profile_address).text = user?.address.toString()
    }
}