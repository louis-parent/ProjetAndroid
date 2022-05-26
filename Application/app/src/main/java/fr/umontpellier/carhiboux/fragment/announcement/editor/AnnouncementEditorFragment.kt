package fr.umontpellier.carhiboux.fragment.announcement.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.activity.MainActivity
import fr.umontpellier.carhiboux.adapter.EnumAdapter
import fr.umontpellier.carhiboux.bundle.BundleKeys
import fr.umontpellier.carhiboux.entity.announcement.Announcement
import fr.umontpellier.carhiboux.entity.enumeration.AnnouncementType
import fr.umontpellier.carhiboux.entity.enumeration.Energy
import fr.umontpellier.carhiboux.entity.enumeration.Gearbox
import fr.umontpellier.carhiboux.entity.util.Address
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory

class AnnouncementEditorFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private var announcement : Announcement? = null

    private lateinit var announcementTypeAdapter : EnumAdapter<AnnouncementType>
    private lateinit var energyAdapter : EnumAdapter<Energy>
    private lateinit var gearboxAdapter : EnumAdapter<Gearbox>

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        announcement = arguments?.get(BundleKeys.ANNOUNCEMENT) as Announcement?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view : View = inflater.inflate(R.layout.announcement_editor_fragment, container, false)

        val typeSpinner = view.findViewById<Spinner>(R.id.announcement_editor_type)

        typeSpinner.onItemSelectedListener = this
        EnumAdapter(AnnouncementType.plainValues()).also {
            typeSpinner.adapter = it
        }.also {
            announcementTypeAdapter = it
        }

        EnumAdapter(Energy.plainValues()).also {
            view.findViewById<Spinner>(R.id.announcement_editor_energy).adapter = it
        }.also {
            energyAdapter = it
        }

        EnumAdapter(Gearbox.plainValues()).also {
            view.findViewById<Spinner>(R.id.announcement_editor_gearbox).adapter = it
        }.also {
            gearboxAdapter = it
        }

        if(announcement != null)
        {
            view.findViewById<EditText>(R.id.announcement_editor_brand).setText(announcement!!.brand)
            view.findViewById<EditText>(R.id.announcement_editor_model).setText(announcement!!.model)
            view.findViewById<Spinner>(R.id.announcement_editor_type).setSelection(announcementTypeAdapter.getItemIndex(announcement!!.type)!!)
            view.findViewById<EditText>(R.id.announcement_editor_price).setText(announcement!!.price.toString())
            view.findViewById<EditText>(R.id.announcement_editor_year).setText(announcement!!.year.toString())
            view.findViewById<EditText>(R.id.announcement_editor_kilometers).setText(announcement!!.kilometers.toString())
            view.findViewById<CheckBox>(R.id.announcement_editor_technical_control).isChecked = announcement!!.technicalControl
            view.findViewById<Spinner>(R.id.announcement_editor_energy).setSelection(energyAdapter.getItemIndex(announcement!!.energy)!!)
            view.findViewById<Spinner>(R.id.announcement_editor_gearbox).setSelection(gearboxAdapter.getItemIndex(announcement!!.gearbox)!!)
            view.findViewById<EditText>(R.id.announcement_editor_color_exterior).setText(announcement!!.exteriorColor)
            view.findViewById<EditText>(R.id.announcement_editor_interior_color).setText(announcement!!.interiorColor)
            view.findViewById<EditText>(R.id.announcement_editor_places).setText(announcement!!.places.toString())
            view.findViewById<EditText>(R.id.announcement_editor_doors).setText(announcement!!.doors.toString())
            view.findViewById<EditText>(R.id.announcement_editor_power).setText(announcement!!.power.toString())
            view.findViewById<EditText>(R.id.announcement_editor_din).setText(announcement!!.din.toString())
            view.findViewById<EditText>(R.id.announcement_editor_co2).setText(announcement!!.co2.toString())
            view.findViewById<EditText>(R.id.announcement_editor_consumption).setText(announcement!!.consumption.toString())
            view.findViewById<EditText>(R.id.announcement_editor_former_owner_count).setText(announcement!!.formerOwnerCount.toString())
            view.findViewById<EditText>(R.id.announcement_editor_address).setText(announcement!!.address.address)
            view.findViewById<EditText>(R.id.announcement_editor_zip).setText(announcement!!.address.zip)
            view.findViewById<EditText>(R.id.announcement_editor_city).setText(announcement!!.address.city)
            view.findViewById<EditText>(R.id.announcement_editor_country).setText(announcement!!.address.country)
        }

        view.findViewById<Button>(R.id.announcement_editor_validate).setOnClickListener {
            validateEdition()
        }

        return view
    }

    private fun getCurrentAnnouncement() : Announcement
    {
        return Announcement(
            requireView().findViewById<Spinner>(R.id.announcement_editor_type).selectedItem as AnnouncementType,
            requireView().findViewById<EditText>(R.id.announcement_editor_brand).text.toString(),
            requireView().findViewById<EditText>(R.id.announcement_editor_model).text.toString(),
            requireView().findViewById<EditText>(R.id.announcement_editor_price).text.toString().toDouble(),
            Address(
                requireView().findViewById<EditText>(R.id.announcement_editor_address).text.toString(),
                requireView().findViewById<EditText>(R.id.announcement_editor_zip).text.toString(),
                requireView().findViewById<EditText>(R.id.announcement_editor_city).text.toString(),
                requireView().findViewById<EditText>(R.id.announcement_editor_country).text.toString()
            ),
            requireView().findViewById<EditText>(R.id.announcement_editor_kilometers).text.toString().toInt(),
            requireView().findViewById<EditText>(R.id.announcement_editor_year).text.toString().toInt(),
            requireView().findViewById<CheckBox>(R.id.announcement_editor_technical_control).isChecked,
            requireView().findViewById<Spinner>(R.id.announcement_editor_energy).selectedItem as Energy,
            requireView().findViewById<Spinner>(R.id.announcement_editor_gearbox).selectedItem as Gearbox,
            requireView().findViewById<EditText>(R.id.announcement_editor_color_exterior).text.toString(),
            requireView().findViewById<EditText>(R.id.announcement_editor_interior_color).text.toString(),
            requireView().findViewById<EditText>(R.id.announcement_editor_doors).text.toString().toInt(),
            requireView().findViewById<EditText>(R.id.announcement_editor_places).text.toString().toInt(),
            requireView().findViewById<EditText>(R.id.announcement_editor_former_owner_count).text.toString().toInt(),
            requireView().findViewById<EditText>(R.id.announcement_editor_power).text.toString().toInt(),
            requireView().findViewById<EditText>(R.id.announcement_editor_din).text.toString().toInt(),
            requireView().findViewById<EditText>(R.id.announcement_editor_co2).text.toString().toDouble(),
            requireView().findViewById<EditText>(R.id.announcement_editor_consumption).text.toString().toDouble(),
            (activity as MainActivity).getConnectedUser()!!.requireId(),
            false,
            announcement?.id
        )
    }

    private fun validateEdition()
    {
        val newAnnouncement = getCurrentAnnouncement()

        if(announcement?.id != null)
        {
            RepositoryFactory.getPreferredFactory().getAnnouncementRepository().update(announcement!!.requireId(), newAnnouncement) {
                validateAnnouncementId(announcement!!.requireId())
            }
        }
        else
        {
            RepositoryFactory.getPreferredFactory().getAnnouncementRepository().create(newAnnouncement) {
                validateAnnouncementId(it)
            }
        }
    }

    private fun validateAnnouncementId(announcementId : Long?)
    {
        if(announcementId != null)
        {
            requireActivity().runOnUiThread {
                findNavController().navigate(R.id.from_announcement_editor_to_announcement_details, bundleOf(BundleKeys.ANNOUNCEMENT to announcementId))
            }
        }
        else
        {
            requireActivity().runOnUiThread {
                Toast.makeText(context, "Check fields", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onItemSelected(adapter: AdapterView<*>?, view: View?, index: Int, id: Long)
    {
        if((adapter?.selectedItem as AnnouncementType) == AnnouncementType.RENT)
        {
            requireView().findViewById<TextView>(R.id.announcement_editor_price_units).setText(R.string.euro_per_day)
        }
        else
        {
            requireView().findViewById<TextView>(R.id.announcement_editor_price_units).setText(R.string.euro)
        }
    }

    override fun onNothingSelected(adapter: AdapterView<*>?)
    {
    }
}