package com.fpis.money.views.fragments.records

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R

class RecordFragment : Fragment() {

    private lateinit var recordViewModel: RecordViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecordRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recordViewModel = ViewModelProvider(this).get(RecordViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_record_list, container, false)

        recyclerView = view.findViewById(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = RecordRecyclerViewAdapter(emptyList())
        recyclerView.adapter = adapter

        recordViewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            Log.d("RecordFragment", "Transactions received: ${transactions.size}")
            if (transactions.isNotEmpty()) {
                adapter.updateData(transactions)
            } else {
                Log.d("RecordFragment", "No transactions found")
            }
        }


        return view
    }
}