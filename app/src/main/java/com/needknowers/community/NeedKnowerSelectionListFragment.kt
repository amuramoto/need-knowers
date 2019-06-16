package com.needknowers.community

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_need_knower_list.view.*


data class NeedKnower(val id: String, val name: String, val imageUrl: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString() ?: throw Exception("NO ID"),
            parcel.readString() ?: throw Exception("NO NAME"),
            parcel.readString() ?: throw Exception("NO URL")) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(imageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NeedKnower> {
        override fun createFromParcel(parcel: Parcel): NeedKnower {
            return NeedKnower(parcel)
        }

        override fun newArray(size: Int): Array<NeedKnower?> {
            return arrayOfNulls(size)
        }
    }
}

class NeedKnowerSelectionListFragment : Fragment() {

    lateinit var recyclerView: RecyclerView

    val needKnowerList: ArrayList<NeedKnower> = arrayListOf(
            NeedKnower("1", "Dawson", "https://zoo.sandiegozoo.org/sites/default/files/styles/hero_mobile_560x670/public/2019-01/hero-short-pandacam_0.jpg?itok=Qdw5BiFz")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_need_knower_list, container, false)
        recyclerView = view.findViewById(R.id.need_knower_list)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        view.findViewById<Toolbar>(R.id.toolbar).apply {
            setupWithNavController(navController, appBarConfiguration)
            title = "Your Need Knower List"
        }


        initRecyclerView()
        return view
    }

    fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context!!)
        val adapter = NeedKnowerRecyclerViewAdapter(needKnowerList)
        recyclerView.need_knower_list.layoutManager = layoutManager
        recyclerView.need_knower_list.adapter = adapter
        adapter.needKnowerListCallback = object : NeedKnowerListCallback {
            override fun callback(needKnower: NeedKnower) {
                val action = NeedKnowerSelectionListFragmentDirections
                        .actionNeedKnowerSelectionListFragmentToCareTakerMainFragment(needKnower)
                findNavController().navigate(action)
            }
        }
    }
}

interface NeedKnowerListCallback {
    fun callback(needKnower: NeedKnower)
}

class NeedKnowerRecyclerViewAdapter(private val needKnowerList: ArrayList<NeedKnower> = arrayListOf()) :
        RecyclerView.Adapter<NeedKnowerRecyclerViewAdapter.ViewHolder>() {

    var needKnowerListCallback: NeedKnowerListCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_need_knower, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return needKnowerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val needKnower = needKnowerList[position]
        val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_person_outline_black_24dp)
        holder.name.text = needKnower.name
        Glide.with(holder.itemView.context)
                .setDefaultRequestOptions(requestOptions)
                .load(needKnower.imageUrl)
                .centerCrop()
                .into(holder.image)
        holder.itemView.setOnClickListener {
            needKnowerListCallback?.callback(needKnower)
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvNeedKnowerName)
        val image: ImageView = itemView.findViewById(R.id.tvNeedKnowerImageView)
    }
}