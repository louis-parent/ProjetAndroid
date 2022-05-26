package fr.umontpellier.carhiboux.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.bundle.SearchFilters
import fr.umontpellier.carhiboux.entity.announcement.Announcement
import fr.umontpellier.carhiboux.entity.user.User
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory

class AnnouncementListAdapter(activity: Activity, userId: Long?, filters: SearchFilters? = null) : BaseAdapter()
{
    private var items : List<Announcement> = listOf()
    private var user : User? = null

    init {
        RepositoryFactory.getPreferredFactory().getAnnouncementRepository().readIf({
            filters?.match(it) ?: true
        }) { list ->
            items = list.sortedBy {
                !it.isEnhance
            }

            activity.runOnUiThread {
                notifyDataSetChanged()
            }
        }

        if(userId != null)
        {
            RepositoryFactory.getPreferredFactory().getUserRepository().read(userId) {
                user = it

                activity.runOnUiThread {
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getCount(): Int
    {
        return items.size
    }

    override fun getItem(i: Int): Any
    {
        return items[i]
    }

    override fun getItemId(i: Int): Long
    {
        return items[i].requireId()
    }

    @SuppressLint("SetTextI18n")
    override fun getView(i: Int, view: View?, group: ViewGroup?): View
    {
        val v : View = view ?: LayoutInflater.from(group?.context).inflate(R.layout.announcement_list_item, group, false)
        val item : Announcement = getItem(i) as Announcement

        v.findViewById<TextView>(R.id.announcement_item_title).text = item.brand + " " + item.model
        v.findViewById<TextView>(R.id.announcement_item_subtitle).text = "" + item.kilometers + " " + group?.context?.getString(R.string.km) + " | " + item.year + " | " + item.energy + " | " + item.gearbox + " box | " + item.places
        v.findViewById<TextView>(R.id.announcement_item_price).text = item.price.toString()
        v.findViewById<TextView>(R.id.announcement_item_place).text = item.address.zip.subSequence(0, 2)

        v.findViewById<TextView>(R.id.announcement_item_enhance_label).isVisible = item.isEnhance
        v.findViewById<ImageView>(R.id.announcement_item_enhance_icon).isVisible = item.isEnhance

        v.findViewById<ImageView>(R.id.announcement_item_type_icon).setImageResource(item.type.icon)
        v.findViewById<TextView>(R.id.announcement_item_price_unit).setText(item.type.unit)
        v.findViewById<TextView>(R.id.announcement_item_type).setText(item.type.stringId())

        if(user != null)
        {
            val favoriteIcon = v.findViewById<ImageView>(R.id.announcement_item_favorite)

            user!!.isFavorite(item.requireId()) { favorite ->
                initFavorite(favorite, favoriteIcon)
            }

            favoriteIcon.setOnClickListener {
                user!!.toggleFavorite(item.requireId()) { enabled ->
                    initFavorite(enabled, favoriteIcon)
                }
            }

        }

        return v
    }

    private fun initFavorite(isFavorite: Boolean, favoriteIcon: ImageView)
    {
        if (isFavorite)
        {
            favoriteIcon.setImageResource(R.drawable.ic_baseline_favorite_24)
        }
        else
        {
            favoriteIcon.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }
    }
}