package com.fpis.money.views.fragments.records

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.fpis.money.models.Transaction

class RecordFragment : Fragment() {

    private lateinit var recordViewModel: RecordViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecordRecyclerViewAdapter
    private lateinit var searchInput: EditText
    private var allTransactionsList: List<Transaction> = emptyList()

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

        searchInput = view.findViewById(R.id.search_input)

        // Передаем FragmentManager в адаптер
        adapter = RecordRecyclerViewAdapter(emptyList(), requireActivity().supportFragmentManager) { transaction ->
            recordViewModel.deleteTransaction(transaction)
        }
        recyclerView.adapter = adapter

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val transactionToDelete = allTransactionsList.getOrNull(position)
                if (transactionToDelete != null) {
                    recordViewModel.deleteTransaction(transactionToDelete)
                }
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                if (dX < 0) {
                    val itemView = viewHolder.itemView
                    val paint = Paint().apply {
                        color = Color.RED
                    }
                    c.drawRect(
                        itemView.right.toFloat() + dX, itemView.top.toFloat(),
                        itemView.right.toFloat(), itemView.bottom.toFloat(), paint
                    )
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)

        recordViewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            allTransactionsList = transactions
            if (transactions.isNotEmpty()) {
                adapter.updateData(transactions)
            }
        }

        searchInput.addTextChangedListener {
            val query = it.toString()
            if (query.isEmpty()) {
                adapter.updateData(allTransactionsList)
            } else {
                adapter.filter(query)
            }
        }

        return view
    }
}