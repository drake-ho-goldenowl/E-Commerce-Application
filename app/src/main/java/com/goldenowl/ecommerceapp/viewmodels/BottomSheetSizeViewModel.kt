package com.goldenowl.ecommerceapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.data.Favorite
import com.goldenowl.ecommerceapp.data.FavoriteDao
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.data.Size
import kotlinx.coroutines.launch

class BottomSheetSizeViewModel(private val product: Product, private val favoriteDao: FavoriteDao) : BaseViewModel() {

    fun createFavorite(product: Product, color: String, size: String): Favorite {
        val sizeSelect = getSizeOfColor(product.colors[0].sizes,size) ?: Size()
        return Favorite(
            product.id,
            product.title,
            product.brandName,
            product.images[1],
            product.createdDate,
            product.salePercent,
            product.isPopular,
            product.numberReviews,
            product.reviewStars,
            product.categoryName,
            color,
            sizeSelect
        )
    }


    private fun getSizeOfColor(sizes : List<Size>, select : String): Size? {
        for(size in sizes){
            if(select == size.size){
                return size
            }
        }
        return null
    }
    fun insertFavorite(product: Product, color: String, size: String) {
        val favorite = createFavorite(product,color,size)
        viewModelScope.launch {
            favoriteDao.insert(favorite)
        }
    }


    fun getAllSize(): List<String>{
        val sizes : MutableSet<String> = mutableSetOf()
        for (color in product.colors){
            for(size in color.sizes){
                if(size.size != null && size.quantity!! > 0){
                    sizes.add(size.size)
                }
            }
        }
        return sizes.toList()
    }

}

class BottomSheetSizeViewModelFactory(
    private val product: Product,
    private val favoriteDao: FavoriteDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BottomSheetSizeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BottomSheetSizeViewModel(product,favoriteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}