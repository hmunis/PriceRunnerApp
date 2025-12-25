package com.example.pricerunner

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pricerunner.databinding.CardDesignBinding
import com.squareup.picasso.Picasso

class RecyclerAdapter(
    private val productList: List<Information>
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardDesignBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size

    class ViewHolder(
        private val binding: CardDesignBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(product: Information) {
            binding.tvProductName.text = product.title
            binding.tvProductPrice.text = product.price
            binding.tvSellerName.text = product.seller
            
            if (product.image.isNotEmpty()) {
                Picasso.get()
                    .load(product.image)
                    .fit()
                    .centerCrop()
                    .into(binding.imageViewProduct)
            }
        }
    }
}
