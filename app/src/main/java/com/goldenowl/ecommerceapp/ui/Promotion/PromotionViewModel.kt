package com.goldenowl.ecommerceapp.ui.Promotion

import com.goldenowl.ecommerceapp.data.PromotionRepository
import com.goldenowl.ecommerceapp.data.TypeSort
import com.goldenowl.ecommerceapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PromotionViewModel @Inject constructor(
    private val promotionRepository: PromotionRepository,
) : BaseViewModel() {
    val promotions = promotionRepository.promotions
    val promotion = promotionRepository.promotion

    init {
        fetchData()
    }

    fun fetchData() {
        promotionRepository.fetchData()
    }

    fun getPromotion(id: String) {
        promotionRepository.getPromotion(id)
    }

    fun filterPromotion(filterPromotion: FilterPromotion, typeSort: TypeSort) {
        promotionRepository.filterPromotion(filterPromotion,typeSort)
    }
}

enum class FilterPromotion(val value: String){
    DATE("endDate"),
    PERCENT("salePercent")
}