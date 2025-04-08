package com.fpis.money.views.fragments.cards

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.fpis.money.R
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
        val sampleItems = PlaceholderContent.ITEMS.take(3)

        recyclerView.adapter = MyCardRecyclerViewAdapter(sampleItems)


        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = CardFragment()
    }
}