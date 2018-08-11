package com.w.animshopbutton

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.Html
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_item_list_layout.view.*

class MainActivity : AppCompatActivity() {

    private var menuList = arrayListOf<MenuBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        menuList.add(MenuBean(-1, 0, "八刀汤", 1 * 10, true))
        for (i in 0..50) {
            menuList.add(MenuBean(i, i, "八刀汤", i * 10, i % 2 != 0))
        }
        recyListView.adapter = EasyAdapter(R.layout.menu_item_list_layout, { itemView, item ->
            itemView.apply {
                val isHasExtraInfo = item.isMultiSpe

                titleTv.text = "${try {
                    itemView.getTag(R.id.easyadapter_position).toString().toInt() + 1
                } catch (e: Exception) {
                }}.${item.name}"
                priceTv.text = "${item.money}元${if (isHasExtraInfo) "(多规格商品)" else ""}"

                menuImg.setOnClickListener {
                    DetailActivity.start(this@MainActivity, menuImg, item)
                }

                addMenuImg.setFixView(isHasExtraInfo)//固定视图
                addMenuImg.setGoodsMinNum(if (isHasExtraInfo) {//如果有属性和配菜就是多规格商品
                    item.amount ?: 0
                } else {
                    0
                })
                addMenuImg.setGoodsNum(item.amount ?: 0)
                addMenuImg.addOnAmountChangeListener { view: View, amount: Int ->
                    item.amount = amount
                    when (view.id) {
                        R.id.btnIncrease -> {//加号
                            if (isHasExtraInfo) {
                                menuImg.performClick()
                                Snackbar.make(addMenuImg, "多规格商品请进入详情选规格", Snackbar.LENGTH_SHORT).show()
                            } else {
//                                addCartAnim?.addGoodToCar(itemView.menuImg, (parentFragment?.parentFragment?.parentFragment as MainFragment).cartImg)//要解耦
//                                cartDish.number = 1
//                                cartViewModel.addCart(cartDish)
//                                isSelfOperate = true
                            }
                        }
                        R.id.btnDecrease -> {//减号 不会有多规格的商品 虽有不会有多条购物车id
//                            cartViewModel.editCart(cartViewModel.getIdByItemId(cartDish.itemId), cartDish)
//                            isSelfOperate = true
                        }
                    }
                }
                addMenuImg.setOnAmountStateListener { state ->
                    when (state) {
                        AmountView.LEAST -> {
                            if (isHasExtraInfo)//如果有属性和配菜就是多规格商品
                            {
                                Snackbar.make(addMenuImg, "多规格商品请在购物车删除", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }, menuList)
    }
}
