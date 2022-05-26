package fr.umontpellier.carhiboux.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.activity.MainActivity
import java.lang.IllegalStateException

class ConfirmDialogFragment(private val message : Int) : DialogFragment()
{
    private var listener : ConfirmDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setMessage(message)
            builder.setPositiveButton(R.string.confirm) { _, _ ->
                listener?.onDialogConfirmationClick()
            }
            builder.setNegativeButton(R.string.cancel) { _, _ ->
                listener?.onDialogCancelClick()
            }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun setConfirmListener(listener: ConfirmDialogListener)
    {
        this.listener = listener
    }
}