package com.example.billingpjsample.skulist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.billingpjsample.R
import com.example.billingpjsample.SkusAdapter
import com.example.billingpjsample.billing.BillingProvider
import com.example.billingpjsample.skulist.row.SkuRowData
import kotlinx.android.synthetic.main.fragment_acquire.*

class AcquireFragment : DialogFragment() {

    private lateinit var products: List<SkuRowData>

    private lateinit var mAdapter: SkusAdapter

    private lateinit var mBillingProvider: BillingProvider

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mBillingProvider = this.activity as BillingProvider
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewRoot = inflater.inflate(R.layout.fragment_acquire, container, false)
        setWaitScreen(true)
        return viewRoot
    }

    private fun setWaitScreen(set: Boolean) {
        if (set) {
            list_rv?.visibility = View.GONE
        } else {
            list_rv?.visibility = View.VISIBLE
        }
    }

    private fun onManagerReady(billingProvider: BillingProvider) {
        mAdapter = SkusAdapter(products, mBillingProvider) {
            productSelected(it)
        }
        list_rv?.apply {
            adapter = mAdapter
        }
    }

    private fun handleManagerAndUiReady() {
        displayAnErrorIfNeeded()
    }
    private fun displayAnErrorIfNeeded() {
        if (activity == null || activity!!.isFinishing()) {
            return
        }

        error_textview?.setVisibility(View.VISIBLE)
        error_textview?.setText(getText(R.string.error_codelab_not_finished))
    }


    private fun productSelected(skuRowData: SkuRowData) {

    }
}
