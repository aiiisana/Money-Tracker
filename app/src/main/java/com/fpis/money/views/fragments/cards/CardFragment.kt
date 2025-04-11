import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R
import com.fpis.money.models.Card
import com.fpis.money.views.fragments.cards.AddCardFragment
import com.fpis.money.views.fragments.cards.EditCardFragment
import com.fpis.money.views.fragments.cards.MyCardRecyclerViewAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CardFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyCardRecyclerViewAdapter
    private val cardList = mutableListOf<Card>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_card_list, container, false)
        recyclerView = view.findViewById(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = MyCardRecyclerViewAdapter(cardList) { position ->
            showEditCardFragment(position)
        }
        recyclerView.adapter = adapter

        view.findViewById<View>(R.id.button_add)?.setOnClickListener {
            showAddCardFragment()
        }

        loadUserCardsFromFirestore()

        return view
    }

    private fun loadUserCardsFromFirestore() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        if (currentUser != null) {
            db.collection("users")
                .document(currentUser.uid)
                .collection("cards")
                .get()
                .addOnSuccessListener { result ->
                    val cards = result.mapNotNull { it.toObject(Card::class.java) }

                    cardList.clear()
                    cardList.addAll(cards)

                    adapter.updateCards(cards)
                }
                .addOnFailureListener { exception ->
                    Log.e("CardFragment", "Error loading cards: ${exception.message}")
                }
        } else {
            Log.e("CardFragment", "User not authenticated")
        }
    }

    private fun showAddCardFragment() {
        val addCardFragment = AddCardFragment.newInstance()
        parentFragmentManager.beginTransaction()
            .add(R.id.fragment_container, addCardFragment)
            .hide(this)
            .addToBackStack("addCard")
            .commit()
    }

    private fun showEditCardFragment(position: Int) {
        val cardId = cardList.getOrNull(position)?.id ?: return
        val editCardFragment = EditCardFragment.newInstance(cardId)
        parentFragmentManager.beginTransaction()
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