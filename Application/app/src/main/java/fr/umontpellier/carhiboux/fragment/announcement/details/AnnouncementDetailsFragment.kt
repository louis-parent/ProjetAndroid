package fr.umontpellier.carhiboux.fragment.announcement.details

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.activity.MainActivity
import fr.umontpellier.carhiboux.bundle.BundleKeys
import fr.umontpellier.carhiboux.dialog.ConfirmDialogFragment
import fr.umontpellier.carhiboux.dialog.ConfirmDialogListener
import fr.umontpellier.carhiboux.entity.announcement.Announcement
import fr.umontpellier.carhiboux.entity.enumeration.AnnouncementType
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory

class AnnouncementDetailsFragment : Fragment(), ConfirmDialogListener
{
    private var announcement : Announcement? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val userId = (activity as MainActivity).getConnectedUser()?.id
        val id = arguments?.get(BundleKeys.ANNOUNCEMENT) as Long?

        if(id != null)
        {
            RepositoryFactory.getPreferredFactory().getAnnouncementRepository().read(id) {
                announcement = it

                RepositoryFactory.getPreferredFactory().getNotificationRepository().readIf({ notification ->
                    notification.data == announcement?.requireId() && !notification.seen && notification.user == userId
                }) { notifications ->
                    notifications.forEach { notification ->
                        notification.markSeen().save {}
                    }
                }
            }
        }
        else
        {
            findNavController().navigate(R.id.to_announcement_list)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view : View = inflater.inflate(R.layout.announcement_details_fragment, container, false)

        if(announcement != null)
        {
            view.findViewById<TextView>(R.id.announcement_details_title).text = announcement!!.brand + " " + announcement!!.model
            view.findViewById<TextView>(R.id.announcement_details_price).text = announcement!!.price.toString()

            if(announcement!!.type == AnnouncementType.SELL)
            {
                view.findViewById<ImageView>(R.id.announcement_details_type_icon).setImageResource(R.drawable.ic_baseline_sell_24)
                view.findViewById<TextView>(R.id.announcement_details_type).setText(R.string.sell)
                view.findViewById<TextView>(R.id.announcement_details_price_symbol).setText(R.string.euro)
            }
            else
            {
                view.findViewById<ImageView>(R.id.announcement_details_type_icon).setImageResource(R.drawable.ic_baseline_car_rental_24)
                view.findViewById<TextView>(R.id.announcement_details_type).setText(R.string.rent)
                view.findViewById<TextView>(R.id.announcement_details_price_symbol).setText(R.string.euro_per_day)
            }

            view.findViewById<TextView>(R.id.announcement_details_year).text = announcement!!.year.toString()
            view.findViewById<TextView>(R.id.announcement_details_kilometers).text = announcement!!.kilometers.toString()

            if(announcement!!.technicalControl)
            {
                view.findViewById<ImageView>(R.id.announcement_details_technical_control_icon).setImageResource(R.drawable.ic_baseline_check_24)
                view.findViewById<TextView>(R.id.announcement_details_technical_control).setText(R.string.has_technical_control)
            }
            else
            {
                view.findViewById<ImageView>(R.id.announcement_details_technical_control_icon).setImageResource(R.drawable.ic_baseline_close_24)
                view.findViewById<TextView>(R.id.announcement_details_technical_control).setText(R.string.has_not_technical_control)
            }

            view.findViewById<TextView>(R.id.announcement_details_energy).setText(announcement!!.energy.stringId())
            view.findViewById<TextView>(R.id.announcement_details_gearbox).setText(announcement!!.gearbox.stringId())
            view.findViewById<TextView>(R.id.announcement_details_exterior_color).text = announcement!!.exteriorColor
            view.findViewById<TextView>(R.id.announcement_details_interior_color).text = announcement!!.interiorColor
            view.findViewById<TextView>(R.id.announcement_details_places).text = announcement!!.places.toString()
            view.findViewById<TextView>(R.id.announcement_details_doors).text = announcement!!.doors.toString()
            view.findViewById<TextView>(R.id.announcement_details_power).text = announcement!!.power.toString()
            view.findViewById<TextView>(R.id.announcement_details_din).text = announcement!!.din.toString()
            view.findViewById<TextView>(R.id.announcement_details_co2).text = announcement!!.co2.toString()
            view.findViewById<TextView>(R.id.announcement_details_consumption).text = announcement!!.consumption.toString()

            RepositoryFactory.getPreferredFactory().getUserRepository().read(announcement!!.author) { announcer ->
                view.findViewById<TextView>(R.id.announcement_details_announcer).text = announcer?.fullName()
                view.findViewById<TextView>(R.id.announcement_details_location).text = announcer?.address?.simplified()
                view.findViewById<TextView>(R.id.announcement_details_phone).text = announcer?.phone
                view.findViewById<TextView>(R.id.announcement_details_email).text = announcer?.email
            }

            var canEdit = false
            var canChat = false

            if((activity as MainActivity).isConnectedUser())
            {
                if((activity as MainActivity).getConnectedUser()!!.requireId() == announcement!!.author)
                {
                    canEdit = true
                }
                else
                {
                    canChat = true
                }
            }

            val edit = view.findViewById<FloatingActionButton>(R.id.announcement_details_edit)
            edit.isVisible = canEdit
            edit.setOnClickListener {
                findNavController().navigate(R.id.from_announcement_details_to_announcement_editor, bundleOf(
                    BundleKeys.ANNOUNCEMENT to announcement
                ))
            }

            val delete = view.findViewById<TextView>(R.id.announcement_details_delete)
            delete.isVisible = canEdit
            delete.setOnClickListener {
                val dialog = ConfirmDialogFragment(R.string.confirm_deletion)
                dialog.setConfirmListener(this)
                dialog.show(childFragmentManager, "ConfirmDialogFragment")
            }

            val enhance = view.findViewById<TextView>(R.id.announcement_details_enhance)
            enhance.isVisible = canEdit && !announcement!!.isEnhance
            enhance.setOnClickListener {
                findNavController().navigate(R.id.from_announcement_detail_to_payment, bundleOf(
                    BundleKeys.AMOUNT to Announcement.getEnhancePrice((activity as MainActivity).getConnectedUser()),

                    BundleKeys.CALLBACK to { validated: Boolean ->
                        run {
                            if (validated) {
                                announcement!!.enhance().save {}
                            }
                        }
                    },

                    BundleKeys.REDIRECT to (
                        R.id.to_announcement_list to bundleOf()
                    )
                ))
            }

            val chat = view.findViewById<FloatingActionButton>(R.id.announcement_details_message)
            chat.isVisible = canChat
            chat.setOnClickListener {
                findNavController().navigate(R.id.from_announcement_details_to_chat, bundleOf(
                    BundleKeys.ANNOUNCEMENT to announcement
                ))
            }
        }

        return view
    }

    override fun onDialogConfirmationClick()
    {
        RepositoryFactory.getPreferredFactory().getAnnouncementRepository().delete(announcement!!.requireId()) { deleted ->
            if(deleted)
            {
                findNavController().navigate(R.id.to_announcement_list)
            }
            else
            {
                Toast.makeText(context, R.string.cannot_delete_announcement, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDialogCancelClick()
    {
    }
}