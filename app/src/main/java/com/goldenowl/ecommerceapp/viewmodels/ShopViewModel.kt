package com.goldenowl.ecommerceapp.viewmodels

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.data.FavoriteRepository
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.data.ProductRepository
import com.goldenowl.ecommerceapp.data.UserManager
import com.goldenowl.ecommerceapp.utilities.PRODUCT_FIREBASE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ShopViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository,
    private val userManager: UserManager,
    private val db: FirebaseFirestore
) :
    BaseViewModel() {
    val statusFilter = MutableStateFlow(Triple("", "", 0))
    val allCategory = productRepository.getAllCategory().asLiveData()

    //    val products: LiveData<List<Product>> = statusFilter.flatMapLatest {
//        if (it.first.isNotBlank() && it.second.isNotBlank()) {
//            productRepository.filterByCategoryAndSearch(it.second, it.first)
//        } else if (it.first.isNotBlank()) {
//            productRepository.filterByCategory(it.first)
//        } else if (it.second.isNotBlank()) {
//            productRepository.filterBySearch(it.second)
//        } else {
//            productRepository.getAll()
//        }
//    }.asLiveData()
    private var lastVisible = ""
    val products: MutableLiveData<List<Product>>
    private val source = Source.CACHE
    val total= MutableLiveData(0)

    init {
        products = statusFilter.flatMapLatest {
            if (it.first.isNotBlank() && it.second.isNotBlank()) {
                filterByCategoryAndSearch(it.second, it.first)
            } else if (it.first.isNotBlank()) {
                filterByCategory(it.first)
            } else if (it.second.isNotBlank()) {
                filterBySearch(it.second)
            } else {
                getAll()
            }
        }.asLiveData() as MutableLiveData<List<Product>>
    }

    private fun filterByCategoryAndSearch(search: String, category: String): Flow<List<Product>> {
        val result: MutableLiveData<List<Product>> = MutableLiveData()
        db.collection(PRODUCT_FIREBASE)
            .whereEqualTo("categoryName", category)
            .orderBy(ID_PRODUCT)
            .limit(LIMIT.toLong())
            .get(source)
            .addOnSuccessListener { documents ->
                val list = mutableListOf<Product>()
                for (document in documents) {
                    val product = document.toObject<Product>()
                    if (product.title.lowercase().contains(search.lowercase())) {
                        list.add(document.toObject())
                    }
                }
                lastVisible = list[list.size - 1].id
                result.postValue(list)
            }

        return result.asFlow()
    }

    private fun filterByCategory(category: String): Flow<List<Product>> {
        val result: MutableLiveData<List<Product>> = MutableLiveData()
        db.collection(PRODUCT_FIREBASE)
            .whereEqualTo("categoryName", category)
            .orderBy(ID_PRODUCT)
            .get(source)
            .addOnSuccessListener { documents ->
                val list = mutableListOf<Product>()
                for (document in documents) {
                    list.add(document.toObject())
                }
                lastVisible = list[list.size - 1].id
                result.postValue(list)
            }

        return result.asFlow()
    }

    private fun filterBySearch(search: String): Flow<List<Product>> {
        val result: MutableLiveData<List<Product>> = MutableLiveData()
        db.collection(PRODUCT_FIREBASE)
            .limit(LIMIT.toLong())
            .get(source)
            .addOnSuccessListener { documents ->
                val list = mutableListOf<Product>()
                for (document in documents) {
                    val product = document.toObject<Product>()
                    if (product.title.lowercase().contains(search.lowercase())) {
                        list.add(document.toObject())
                    }
                }
                lastVisible = list[list.size - 1].id
                result.postValue(list)
            }

        return result.asFlow()
    }

    private fun getAll(): Flow<List<Product>> {
        val result: MutableLiveData<List<Product>> = MutableLiveData()
        db.collection(PRODUCT_FIREBASE)
            .limit(LIMIT.toLong())
            .orderBy(ID_PRODUCT)
            .get(source)
            .addOnSuccessListener { documents ->

                val list = mutableListOf<Product>()
                for (document in documents) {
                    list.add(document.toObject())
                }
                lastVisible = list[list.size - 1].id
                result.postValue(list)
            }

        return result.asFlow()
    }

    fun getTotal(){
        db.collection(PRODUCT_FIREBASE)
            .get(source)
            .addOnSuccessListener { documents ->
                total.postValue(documents.size())
            }
    }

    fun loadMore(list: List<Product>) {
        db.collection(PRODUCT_FIREBASE)
            .orderBy(ID_PRODUCT)
            .startAfter(lastVisible)
            .limit(LIMIT.toLong()).get(source).addOnSuccessListener { documents ->
                val temp = mutableListOf<Product>()
                if(documents.size() != 0){
                    temp.addAll(list)
                    for (document in documents) {
                        temp.add(document.toObject())
                    }
                    lastVisible = temp[temp.size - 1].id
                    products.postValue(temp)
                }
            }
    }

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

    fun getCategory(): String {
        return statusFilter.value.first
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
        const val LIMIT = 4
        const val ID_PRODUCT = "id"
    }
}