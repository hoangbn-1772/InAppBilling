package com.example.billingpjsample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.SkuDetails
import com.example.billingpjsample.billing.BillingProvider
import com.example.billingpjsample.skulist.row.SkuRowData
import kotlinx.android.synthetic.main.item_product.view.*

class SkusAdapter(
    @Nullable private var products: List<SkuRowData>,
    @NonNull private val billingProvider: BillingProvider,
    @NonNull private val onProductClicked: (SkuRowData) -> Unit
) : RecyclerView.Adapter<SkusAdapter.SkusViewHolder>() {

    fun updateProducts(products: List<SkuRowData>) {
        this.products = products
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkusViewHolder =
        SkusViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        )

    override fun getItemCount(): Int = if (products.isNullOrEmpty()) 0 else products.size


    override fun onBindViewHolder(holder: SkusViewHolder, position: Int) {
        holder.bind(products[position])
    }

    inner class SkusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(skuRowData: SkuRowData) {
            with(itemView) {
                title?.text = skuRowData.title
                description?.text = skuRowData.description
                price?.text = skuRowData.price
                state_button?.isEnabled = true

                when (skuRowData.sku) {
                    "gas" -> sku_icon?.setImageResource(R.drawable.gas_icon)
                    "premium" -> sku_icon?.setImageResource(R.drawable.premium_icon)
                    "gold_monthly" -> {
                    }
                    "gold_yearly" -> sku_icon?.setImageResource(R.drawable.gold_icon)
                    else -> {
                    }
                }
            }
        }
    }
}
