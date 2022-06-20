package com.goldenowl.ecommerceapp.viewmodels

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.data.FavoriteRepository
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.data.ProductRepository
import com.goldenowl.ecommerceapp.data.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ShopViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository,
    private val userManager: UserManager
) :
    BaseViewModel() {
    val statusFilter = MutableStateFlow(Triple("", "", 0))
    val allCategory = productRepository.getAllCategory().asLiveData()
    val products: LiveData<List<Product>> = statusFilter.flatMapLatest {
        if (it.first.isNotBlank() && it.second.isNotBlank()) {
            productRepository.filterByCategoryAndSearch(it.second, it.first)
        } else if (it.first.isNotBlank()) {
            productRepository.filterByCategory(it.first)
        } else if (it.second.isNotBlank()) {
            productRepository.filterBySearch(it.second)
        } else {
            productRepository.getAll()
        }
    }.asLiveData()

    private val statusIdProduct = MutableStateFlow("")
    val product: LiveData<Product> = statusIdProduct.flatMapLatest {
        productRepository.getProduct(it)
    }.asLiveData()

    val favorites = favoriteRepository.getAll().asLiveData()

    fun setProduct(idProduct: String) {
        statusIdProduct.value = idProduct
    }

    fun filterSort(products: List<Product>): List<Product> {
        return when (statusFilter.value.third) {
            0 -> products.sortedByDescending {
                it.isPopular
            }
            1 -> products.sortedByDescending {
                it.createdDate
            }
            2 -> products.sortedByDescending {
                it.numberReviews
            }
            3 -> products.sortedBy {
                it.colors[0].sizes[0].price
            }
            else -> {
                products.sortedByDescending {
                    it.colors[0].sizes[0].price
                }
            }
        }
    }

    fun setCategory(category: String) {
        statusFilter.value = Triple(category, statusFilter.value.second, statusFilter.value.third)
    }

    fun setSearch(search: String) {
        statusFilter.value = Triple(statusFilter.value.first, search, statusFilter.value.third)
    }

    fun setSort(select: Int) {
        statusFilter.value = Triple(statusFilter.value.first, statusFilter.value.second, select)
    }

    fun getAllSize(): MutableList<String> {
        val sizes: MutableSet<String> = mutableSetOf()
        product.value?.let {
            for (color in it.colors) {
                for (size in color.sizes) {
                    if (size.quantity > 0) {
                        sizes.add(size.size)
                    }
                }
            }
        }
        return sizes.toMutableList()
    }

    fun getAllSizeOfColor(selectColor: Int): MutableList<String> {
        val sizes: MutableSet<String> = mutableSetOf()
        product.value?.let {
            for (size in it.colors[selectColor].sizes) {
                if (size.quantity > 0) {
                    sizes.add(size.size)
                }
            }
        }
        return sizes.toMutableList()
    }


    fun getAllColor(): MutableList<String> {
        val colors: MutableSet<String> = mutableSetOf()
        product.value?.let {
            for (color in it.colors) {
                color.color?.let { str ->
                    colors.add(str)
                }
            }
        }
        return colors.toMutableList()
    }

    fun setButtonFavorite(context: Context, buttonView: View, idProduct: String) {
        if (!userManager.isLogged()) {
            buttonView.visibility = View.GONE
        } else {
            buttonView.visibility = View.VISIBLE
            viewModelScope.launch {
                val isFavorite = favoriteRepository.checkProductHaveFavorite(idProduct)
                if (isFavorite) {
                    buttonView.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.btn_favorite_active
                    )
                } else {
                    buttonView.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.btn_favorite_no_active
                    )
                }
            }
        }
    }

    companion object {
        const val TAG = "ShopViewModel"
    }
}