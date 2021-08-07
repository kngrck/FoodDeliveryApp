package com.example.fooddeliveryapp.ui.restaurantlisting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.model.entity.Restaurant

class RestaurantListingAdapter : RecyclerView.Adapter<RestaurantListingAdapter.ViewHolder>() {

    private lateinit var restaurantList: List<Restaurant>
    private var listenerI: IRestaurantOnClick? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val containerFrameLayout: FrameLayout = view.findViewById(R.id.containerFrameLayout)
        private val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        private val locationTextView: TextView = view.findViewById(R.id.locationTextView)
        private val imageView: AppCompatImageView = view.findViewById(R.id.imageView)

        fun bind(restaurant: Restaurant, listenerI: IRestaurantOnClick?) {
            nameTextView.text = restaurant.name
            locationTextView.text = restaurant.district
//            Glide.with(imageView.context)
//                .load(item.image).into(imageView);

            containerFrameLayout.setOnClickListener {
                listenerI?.onClick(restaurant)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_item_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = restaurantList[position]
        holder.bind(item, listenerI)
    }

    override fun getItemCount(): Int = restaurantList.size

    fun setData(restaurantList: List<Restaurant>) {
        this.restaurantList = restaurantList
        notifyDataSetChanged()
    }

    fun addListener(listenerI: IRestaurantOnClick?) {
        this.listenerI = listenerI
    }

}