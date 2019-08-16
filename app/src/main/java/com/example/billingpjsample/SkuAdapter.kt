package com.example.billingpjsample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.SkuDetails
import kotlinx.android.synthetic.main.item_product.view.*

class SkuAdapter(
    @Nullable private var products: List<SkuDetails>,
    @NonNull private val onProductClicked: (SkuDetails) -> Unit,
    @NonNull private val onLoadRewardedProduct: (SkuDetails) -> Unit
) : RecyclerView.Adapter<SkuAdapter.SkuViewHolder>() {

    fun updateProducts(products: List<SkuDetails>) {
        this.products = products
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkuViewHolder =
        SkuViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        )

    override fun getItemCount(): Int = if (products.isNullOrEmpty()) 0 else products.size


    override fun onBindViewHolder(holder: SkuViewHolder, position: Int) {
        holder.bind(products[position])
    }

    inner class SkuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(skuDetails: SkuDetails) {
            with(itemView) {
                title?.text = skuDetails.title
                description?.text = skuDetails.description
                price?.text = skuDetails.price
                btn_state?.text = this.context.getString(R.string.button_buy)

                when (skuDetails.sku) {
                    "gas" -> sku_icon?.setImageResource(R.drawable.gas_icon)
                    "premium" -> sku_icon?.setImageResource(R.drawable.premium_icon)
                    "gold_monthly" -> {
                    }
                    "gold_yearly" -> sku_icon?.setImageResource(R.drawable.gold_icon)
                    else -> {
                    }
                }

                btn_state?.setOnClickListener { onProductClicked(skuDetails) }

                btn_ads?.setOnClickListener { onLoadRewardedProduct(skuDetails) }
            }
        }
    }
}
