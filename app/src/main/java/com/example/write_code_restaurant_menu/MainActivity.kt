package com.example.write_code_restaurant_menu

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import com.example.write_code_restaurant_menu.R.*
import com.example.write_code_restaurant_menu.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlin.collections.MutableList as MutableList1

class MainActivity : AppCompatActivity(), DialogListener {
    private lateinit var binding: ActivityMainBinding
//    private val orderList: MutableList1<Pair<String, Double>> = mutableListOf()
    private val orderList: MutableList1<ShoppingBagItem> = mutableListOf()
    private var cardlist: List<CardItem> = listOf()
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //  botões da sessões
        binding.buttonAllSections.setOnClickListener {
            scrollVerticalToView(binding.textSectionAppetizerList)
            applyStyleToButton(binding.buttonAllSections)
            removeStyleFromButtons(binding.buttonAllSections)
        }
        binding.buttonSectionAppetizer.setOnClickListener {
            scrollVerticalToView(binding.textSectionAppetizerList)
            applyStyleToButton(binding.buttonSectionAppetizer)
            removeStyleFromButtons(binding.buttonSectionAppetizer)
        }
        binding.buttonSectionMainDishes.setOnClickListener {
            scrollVerticalToView(binding.textSectionMainDishesList)
            applyStyleToButton(binding.buttonSectionMainDishes)
            removeStyleFromButtons(binding.buttonSectionMainDishes)
        }
        binding.buttonSectionPastaAndRisotto.setOnClickListener {
            scrollVerticalToView(binding.textSectionPastaAndRisottoList)
            applyStyleToButton(binding.buttonSectionPastaAndRisotto)
            removeStyleFromButtons(binding.buttonSectionPastaAndRisotto)
        }
        binding.buttonSectionDesserts.setOnClickListener {
            scrollVerticalToView(binding.textSectionDessertsList)
            applyStyleToButton(binding.buttonSectionDesserts)
            removeStyleFromButtons(binding.buttonSectionDesserts)
        }
        binding.buttonSectionDrinks.setOnClickListener {
            scrollVerticalToView(binding.textSectionDrinksList)
            applyStyleToButton(binding.buttonSectionDrinks)
            removeStyleFromButtons(binding.buttonSectionDrinks)
        }
        val bottomSheet = findViewById<LinearLayout>(R.id.bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.apply {
            peekHeight=220
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.buttonFinalizeOrder.setOnClickListener {
            var message = "🟢 Seu pedido foi enviado para o balcão do restaurante"
            if (orderList.isEmpty()){
                message = "🟡 ️Selecione um ou mais itens do cardágio para finalizar o pedido"
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
        binding.buttonResume.setOnClickListener{
            if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED){
                bottomSheetBehavior.apply {
                    peekAvailableContext()
                    this.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }else{
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        var index = 0
        mapIdsToList()
        for (i in cardlist){
            val checkbox = binding.root.findViewById<CheckBox>(i.checkboxCard)
            val textId = binding.root.findViewById<TextView>(i.textIdDish)
            val textPrice = binding.root.findViewById<TextView>(i.textPrice)
            val textQuantity = binding.root.findViewById<TextView>(i.textQuantity)
            val buttonIncrease = binding.root.findViewById<AppCompatImageButton>(i.buttonIncrease)
            val buttonDecrease = binding.root.findViewById<AppCompatImageButton>(i.buttonDecrease)
            val buttonAddComment = binding.root.findViewById<AppCompatButton>(i.buttonAddComment)
            val dish = MenuPrices.getByIndex(index)
            index++
            textId.text = "ID: ${dish?.id}"
            textPrice.text = "R$ ${dish?.price.toString().replace(".",",0")}"
            buttonAddComment.setOnClickListener {
                val dialog = AddDialogFragment(buttonAddComment)
                dialog.show(supportFragmentManager, dialog.tag)
                checkbox.setOnClickListener{
                    addItemOrderList(checkbox,textId,textPrice , dialog.commentEditText)
                }
            }
            checkbox.setOnClickListener{
                addItemOrderList(checkbox,textId,textPrice ,null)
            }
            buttonIncrease.setOnClickListener {
                increaseQuantity(textQuantity)
                calculateValuePerUnit(textId, textPrice, textQuantity)
            }
            buttonDecrease.setOnClickListener {
                decreaseQuantity(textQuantity)
                calculateValuePerUnit(textId, textPrice, textQuantity)
            }

        }
    }
    override fun onCommentSubmitted(comment: String, view: AppCompatButton) {
        if (comment.isNotEmpty()) {
            view.setCompoundDrawablesWithIntrinsicBounds(
                drawable.commented,
                0,
                0,
                0
            )
        } else {
            view.setCompoundDrawablesWithIntrinsicBounds(
                drawable.add_comment,
                0,
                0,
                0
            )
        }
    }
    private fun scrollVerticalToView(view: View) {
        val yPosition = view.y
        binding.productsScrollview.smoothScrollTo(0, yPosition.toInt())
    }
    private fun applyStyleToButton(button: AppCompatButton) {
        button.setBackgroundResource(drawable.button_chip_selected)
        button.setTextColor(Color.WHITE)
    }
    private fun removeStyleFromButtons(excludeButton: AppCompatButton) {
        val parent = excludeButton.parent as ViewGroup
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child is AppCompatButton && child !== excludeButton) {
                child.setBackgroundResource(drawable.button_chip)
                child.setTextColor(Color.RED)
            }
        }
    }
    private fun increaseQuantity(textQuantity: TextView) {
        var intQuantity = textQuantity.text.toString().toInt()
        intQuantity += 1
        textQuantity.setText(intQuantity.toString())

    }
    private fun decreaseQuantity(textQuantity: TextView) {
        var intQuantity = textQuantity.text.toString().toInt()
        if (intQuantity > 1) {
            intQuantity -= 1
            textQuantity.setText(intQuantity.toString())
        }
    }
    private fun calculateValuePerUnit(idView: TextView, priceView: TextView, quantityView: TextView) {
        val id = stringSlicing(idView)
        val price = MenuPrices.getPriceById(id)
        val quantity = stringSlicing(quantityView).toFloatOrNull() ?: 0.00f
        val totalValuePerQuantity = quantity * price
        if (quantity > 0) {
            priceView.setText("R$ ${totalValuePerQuantity.toString().replace(".", ",0")}")
            val index = orderList.indexOfFirst { it.id == id }
            if (index != -1) {
                orderList[index] = orderList[index].copy(price = totalValuePerQuantity)
                updateView()
            }
        }
    }
    private fun addItemOrderList(viewCheckbox: CheckBox, viewTextId: TextView, viewTextPrice: TextView, viewComment: EditText?) {
        val id = stringSlicing(viewTextId)
        val price = stringSlicing(viewTextPrice).replace(',', '.').toDouble()
        val comment =  viewComment?.text.let {
            it.toString()
        }
        if(viewCheckbox.isChecked){
            orderList.add(ShoppingBagItem(id, price, comment))
        }else {
            orderList.removeIf { it.id == id }
        }
        updateView()
    }
    private fun stringSlicing(view: TextView): String {
        val text = view.text.toString()
        val startIndex = text.indexOfFirst { it.isDigit() }
        return text.subSequence(startIndex, text.length).toString()
    }
    private fun updateView(){
        if(orderList.isEmpty()){
            binding.textTotalBill.setText("Total: R$ 0,00")
        }else {
            val totalSum: Double = orderList.sumOf{ it.price }
            binding.textTotalBill.setText("Total: R$ ${totalSum.toString().replace(".",",0")}")
        }
        addTextView()
    }
    private fun addTextView() {
        val textViewContainer = binding.layoutResume
        if(textViewContainer.childCount > 1){
            textViewContainer.removeViews(1, textViewContainer.childCount-1)
        }
        for (i in orderList){
            val newTextView = TextView(this)
            var comment = "\n📝: ${i.comment}"
            if (i.comment == "null") {
                comment = ""
            }
            newTextView.text = "ID: ${i.id} - R$ ${i.price.toString().replace(".",",0")} ${comment}"
            textViewContainer.addView(newTextView)
        }
    }
    private fun mapIdsToList(){
        cardlist = listOf(
            CardItem(
                textIdDish = R.id.text_id_dish_1,
                textQuantity = R.id.text_quantity_1,
                textPrice = R.id.text_card_price_1,
                checkboxCard = R.id.checkbox_card_1,
                buttonAddComment = R.id.button_add_comment_1,
                buttonIncrease = R.id.img_button_increase_1,
                buttonDecrease = R.id.img_button_decrease_1,
            ),
            CardItem(
                textIdDish = R.id.text_id_dish_2,
                textQuantity = R.id.text_quantity_2,
                textPrice = R.id.text_card_price_2,
                checkboxCard = R.id.checkbox_card_2,
                buttonAddComment = R.id.button_add_comment_2,
                buttonIncrease = R.id.img_button_increase_2,
                buttonDecrease = R.id.img_button_decrease_2
            ),
            CardItem(
                textIdDish = R.id.text_id_dish_3,
                textQuantity = R.id.text_quantity_3,
                textPrice = R.id.text_card_price_3,
                checkboxCard = R.id.checkbox_card_3,
                buttonAddComment = R.id.button_add_comment_3,
                buttonIncrease = R.id.img_button_increase_3,
                buttonDecrease = R.id.img_button_decrease_3
            ),
            CardItem(
                textIdDish = R.id.text_id_dish_4,
                textQuantity = R.id.text_quantity_4,
                textPrice = R.id.text_card_price_4,
                checkboxCard = R.id.checkbox_card_4,
                buttonAddComment = R.id.button_add_comment_4,
                buttonIncrease = R.id.img_button_increase_4,
                buttonDecrease = R.id.img_button_decrease_4
            ),
            CardItem(
                textIdDish = R.id.text_id_dish_5,
                textQuantity = R.id.text_quantity_5,
                textPrice = R.id.text_card_price_5,
                checkboxCard = R.id.checkbox_card_5,
                buttonAddComment = R.id.button_add_comment_5,
                buttonIncrease = R.id.img_button_increase_5,
                buttonDecrease = R.id.img_button_decrease_5
            ),
            CardItem(
                textIdDish = R.id.text_id_dish_6,
                textQuantity = R.id.text_quantity_6,
                textPrice = R.id.text_card_price_6,
                checkboxCard = R.id.checkbox_card_6,
                buttonAddComment = R.id.button_add_comment_6,
                buttonIncrease = R.id.img_button_increase_6,
                buttonDecrease = R.id.img_button_decrease_6
            ),
            CardItem(
                textIdDish = R.id.text_id_dish_7,
                textQuantity = R.id.text_quantity_7,
                textPrice = R.id.text_card_price_7,
                checkboxCard = R.id.checkbox_card_7,
                buttonAddComment = R.id.button_add_comment_7,
                buttonIncrease = R.id.img_button_increase_7,
                buttonDecrease = R.id.img_button_decrease_7
            ),
            CardItem(
                textIdDish = R.id.text_id_dish_8,
                textQuantity = R.id.text_quantity_8,
                textPrice = R.id.text_card_price_8,
                checkboxCard = R.id.checkbox_card_8,
                buttonAddComment = R.id.button_add_comment_8,
                buttonIncrease = R.id.img_button_increase_8,
                buttonDecrease = R.id.img_button_decrease_8
            ),
            CardItem(
                textIdDish = R.id.text_id_dish_9,
                textQuantity = R.id.text_quantity_9,
                textPrice = R.id.text_card_price_9,
                checkboxCard = R.id.checkbox_card_9,
                buttonAddComment = R.id.button_add_comment_9,
                buttonIncrease = R.id.img_button_increase_9,
                buttonDecrease = R.id.img_button_decrease_9
            ),
            CardItem(
                textIdDish = R.id.text_id_dish_10,
                textQuantity = R.id.text_quantity_10,
                textPrice = R.id.text_card_price_10,
                checkboxCard = R.id.checkbox_card_10,
                buttonAddComment = R.id.button_add_comment_10,
                buttonIncrease = R.id.img_button_increase_10,
                buttonDecrease = R.id.img_button_decrease_10
            ),
            CardItem(
                textIdDish = R.id.text_id_dish_11,
                textQuantity = R.id.text_quantity_11,
                textPrice = R.id.text_card_price_11,
                checkboxCard = R.id.checkbox_card_11,
                buttonAddComment = R.id.button_add_comment_11,
                buttonIncrease = R.id.img_button_increase_11,
                buttonDecrease = R.id.img_button_decrease_11
            ),
            CardItem(
                textIdDish = R.id.text_id_dish_12,
                textQuantity = R.id.text_quantity_12,
                textPrice = R.id.text_card_price_12,
                checkboxCard = R.id.checkbox_card_12,
                buttonAddComment = R.id.button_add_comment_12,
                buttonIncrease = R.id.img_button_increase_12,
                buttonDecrease = R.id.img_button_decrease_12
            ),
            CardItem(
                textIdDish = R.id.text_id_dish_13,
                textQuantity = R.id.text_quantity_13,
                textPrice = R.id.text_card_price_13,
                checkboxCard = R.id.checkbox_card_13,
                buttonAddComment = R.id.button_add_comment_13,
                buttonIncrease = R.id.img_button_increase_13,
                buttonDecrease = R.id.img_button_decrease_13
            ),
            CardItem(
                textIdDish = R.id.text_id_dish_14,
                textQuantity = R.id.text_quantity_14,
                textPrice = R.id.text_card_price_14,
                checkboxCard = R.id.checkbox_card_14,
                buttonAddComment = R.id.button_add_comment_14,
                buttonIncrease = R.id.img_button_increase_14,
                buttonDecrease = R.id.img_button_decrease_14
            ),
            CardItem(
                textIdDish = R.id.text_id_dish_15,
                textQuantity = R.id.text_quantity_15,
                textPrice = R.id.text_card_price_15,
                checkboxCard = R.id.checkbox_card_15,
                buttonAddComment = R.id.button_add_comment_15,
                buttonIncrease = R.id.img_button_increase_15,
                buttonDecrease = R.id.img_button_decrease_15
            ),
            CardItem(
                textIdDish = R.id.text_id_dish_16,
                textQuantity = R.id.text_quantity_16,
                textPrice = R.id.text_card_price_16,
                checkboxCard = R.id.checkbox_card_16,
                buttonAddComment = R.id.button_add_comment_16,
                buttonIncrease = R.id.img_button_increase_16,
                buttonDecrease = R.id.img_button_decrease_16
            ),
        )
    }
}