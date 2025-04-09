package com.fpis.money.views.fragments.cards

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import com.fpis.money.R
import com.fpis.money.views.activities.MainActivity
import com.fpis.money.views.fragments.cards.placeholder.PlaceholderContent
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_card_list, container, false)

        // Set up RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Create a smaller sample list (just 3 items for testing)
        val sampleItems = PlaceholderContent.ITEMS.take(5)

        // Set up adapter with edit card callback
        recyclerView.adapter = MyCardRecyclerViewAdapter(sampleItems) { position ->
            // Handle edit card action
            showEditCardFragment(position)
        }

        // Set up the add button to show AddCardFragment
        val addButton = view.findViewById<View>(R.id.button_add)
        addButton?.setOnClickListener {
            try {
                showAddCardFragment()
            } catch (e: Exception) {
                Log.e("CardFragment", "Error showing AddCardFragment: ${e.message}")
                e.printStackTrace()
            }
        }

        return view
    }

    private fun showAddCardFragment() {
        // Create a new instance of AddCardFragment
        val addCardFragment = AddCardFragment.newInstance()

        // Get the fragment manager from the activity
        val fragmentManager = parentFragmentManager

        // Add the AddCardFragment to the container and hide the current fragment
        fragmentManager.beginTransaction()
            .add(R.id.fragment_container, addCardFragment)
            .hide(this)
            .addToBackStack("addCard")
            .commit()
    }

    private fun showEditCardFragment(position: Int) {
        // Create a new instance of EditCardFragment with the card ID
        // In a real app, you would get the actual card ID from your data source
        val cardId = "card_id_$position"
        val editCardFragment = EditCardFragment.newInstance(cardId)

        // Get the fragment manager from the activity
        val fragmentManager = parentFragmentManager

        // Add the EditCardFragment to the container and hide the current fragment
        fragmentManager.beginTransaction()
            .add(R.id.fragment_container, editCardFragment)
            .hide(this)
            .addToBackStack("editCard")
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = CardFragment()
    }
}
