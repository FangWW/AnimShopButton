package com.w.animshopbutton

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityOptionsCompat
import android.text.Html
import android.view.View
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_item_list_layout.view.*

class DetailActivity : AppCompatActivity() {

    companion object {
        fun start(context: AppCompatActivity, view: View, menuBean: MenuBean) {
            val imagePair = android.support.v4.util.Pair.create(view, "menuImg")
            val compat = ActivityOptionsCompat.makeSceneTransitionAnimation(context, imagePair)
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("bean", menuBean)
            context.startActivity(intent, compat.toBundle())
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        closeImg.setOnClickListener { onBackPressed() }
        var menuBean = intent.getParcelableExtra<MenuBean>("bean")
        titleTv.text = menuBean.name
        priceTv.text = "${menuBean.money}元"
        amountView.setGoodsMinNum(1)
        amountView.setGoodsNum(menuBean.amount)
        if (menuBean.isMultiSpe)
            Snackbar.make(priceTv, "多规格商品请进入详情选规格", Snackbar.LENGTH_SHORT).show()
    }
}
