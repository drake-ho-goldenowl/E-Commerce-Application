package com.goldenowl.ecommerceapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.data.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject


@HiltViewModel
class ShopViewModel @Inject constructor(private val productRepository: ProductRepository) :
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
    val product : LiveData<Product> = statusIdProduct.flatMapLatest {
        productRepository.getProductFlow(it)
    }.asLiveData()

    fun setProduct(idProduct: String){
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
        for (color in product.value!!.colors) {
            for (size in color.sizes) {
                if (size.quantity > 0) {
                    sizes.add(size.size)
                }
            }
        }
        return sizes.toMutableList()
    }


    fun getAllColor(): MutableList<String> {
        val colors: MutableSet<String> = mutableSetOf()
        for (color in product.value!!.colors) {
            colors.add(color.color!!)
        }
        return colors.toMutableList()
    }

    companion object {
        const val TAG = "ShopViewModel"
    }
}

//class ShopViewModelFactory(
//    private val productDao: ProductDao,
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(ShopViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return ShopViewModel(productDao) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
