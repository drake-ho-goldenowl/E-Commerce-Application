package com.goldenowl.ecommerceapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.data.*
import kotlinx.coroutines.launch

class BottomSheetSizeViewModel(
    private val product: Product,
    private val favoriteDao: FavoriteDao,
    private val productDao: ProductDao
) : BaseViewModel() {

    private fun createFavorite(product: Product, color: String, size: String): Favorite {
        val sizeSelect = getSizeOfColor(product.colors[0].sizes, size) ?: Size()
        return Favorite(
            product.id,
            sizeSelect.size,
            sizeSelect.quantity,
            sizeSelect.price,
            product.title,
            product.brandName,
            product.images[0],
            product.createdDate,
            product.salePercent,
            product.isPopular,
            product.numberReviews,
            product.reviewStars,
            product.categoryName,
            color,
        )
    }


    private fun getSizeOfColor(sizes: List<Size>, select: String): Size? {
        for (size in sizes) {
            if (select == size.size) {
                return size
            }
        }
        return null
    }

    fun insertFavorite(product: Product, color: String, size: String) {
        val favorite = createFavorite(product, color, size)
        viewModelScope.launch {
            favoriteDao.insert(favorite)
            product.isFavorite = true
            fetchProduct(product)
        }
    }

    private fun fetchProduct(product: Product) {
        viewModelScope.launch {
            productDao.update(product)
        }
    }


    fun getAllSize(): List<String> {
        val sizes: MutableSet<String> = mutableSetOf()
        for (color in product.colors) {
            for (size in color.sizes) {
                if (size.quantity > 0) {
                    sizes.add(size.size)
                }
            }
        }
        return sizes.toList()
    }

}

class BottomSheetSizeViewModelFactory(
    private val product: Product,
    private val favoriteDao: FavoriteDao,
    private val productDao: ProductDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BottomSheetSizeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BottomSheetSizeViewModel(product, favoriteDao, productDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}