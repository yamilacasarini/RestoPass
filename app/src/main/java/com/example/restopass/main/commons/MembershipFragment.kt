package com.example.restopass.main.commons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restopass.R
import kotlinx.android.synthetic.main.fragment_membership.*
import kotlinx.android.synthetic.main.view_membership_item.view.*

class MembershipFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView

    private val viewModel: MembershipViewModel = MembershipViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_membership, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewManager = LinearLayoutManager(this.context)
        val viewAdapter = MembershipAdapter(viewModel.membershipsList, MembershipListener { membership ->
            membership!!.restaurantsVisibility = if (membership.restaurantsVisibility == View.GONE) View.VISIBLE else View.GONE
            membershipRecyclerView.getChildAt(viewModel.membershipsList.indexOfFirst {
                it.type == membership.type
            }).apply {
                restaurantsList.visibility = membership.restaurantsVisibility
                if (restaurantsList.visibility == View.GONE) {
                    membershipExpandButton.setImageResource(R.drawable.ic_arrow_down_24dp)
                } else {
                    membershipExpandButton.setImageResource(R.drawable.ic_arrow_up_24dp)
                }

            }

        })

        recyclerView = membershipRecyclerView.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}