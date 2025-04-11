package com.fpis.money.views.fragments.records

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.fpis.money.models.Transaction
import android.app.DatePickerDialog
import java.util.*

class RecordFragment : Fragment() {

    private lateinit var recordViewModel: RecordViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecordRecyclerViewAdapter
    private lateinit var searchInput: EditText
    private var allTransactionsList: List<Transaction> = emptyList()
    private lateinit var sortSpinner: Spinner
    private val sortOptions = arrayListOf("Newest first", "Oldest first", "Pick a date")
    private lateinit var spinnerAdapter: ArrayAdapter<String>

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

        sortSpinner = view.findViewById(R.id.sort_spinner)

        val sortOptions = arrayListOf("Newest first", "Oldest first", "Pick a date")
        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, sortOptions)
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item)
        sortSpinner.adapter = spinnerAdapter

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = parent.getItemAtPosition(position).toString()
                if (selected == "Pick a date") {
                    showDatePickerDialog()
                } else {
                    sortAndUpdateTransactions(selected)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

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
            sortAndUpdateTransactions(sortSpinner.selectedItem.toString())
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

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                Toast.makeText(requireContext(), "Selected date: $selectedDate", Toast.LENGTH_SHORT).show()

                filterTransactionsByDate(selectedYear, selectedMonth, selectedDay)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun filterTransactionsByDate(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val startOfNextDay = calendar.timeInMillis

        val filteredTransactions = allTransactionsList.filter {
            it.date in startOfDay until startOfNextDay
        }

        adapter.updateData(filteredTransactions)
    }

    private fun sortAndUpdateTransactions(option: String) {
        val sortedList = when (option) {
            "Newest first" -> allTransactionsList.sortedByDescending { it.date }
            "Oldest first" -> allTransactionsList.sortedBy { it.date }
            else -> allTransactionsList
        }

        val query = searchInput.text.toString()
        val filtered = if (query.isEmpty()) sortedList
        else sortedList.filter {
            it.category.contains(query, ignoreCase = true) ||
                    it.subCategory.contains(query, ignoreCase = true) ||
                    it.notes.contains(query, ignoreCase = true)
        }

        adapter.updateData(filtered)
    }
}