package com.goldenowl.ecommerceapp.di

import android.content.Context
import com.goldenowl.ecommerceapp.data.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideProductDao(appDatabase: AppDatabase): ProductDao {
        return appDatabase.productDao()
    }

    @Provides
    fun provideFavoriteDao(appDatabase: AppDatabase): FavoriteDao {
        return appDatabase.favoriteDao()
    }

    @Provides
    fun provideBagDao(appDatabase: AppDatabase): BagDao {
        return appDatabase.bagDao()
    }

    @Provides
    fun providePromotionDao(appDatabase: AppDatabase): PromotionDao {
        return appDatabase.promotionDao()
    }

    @Provides
    fun provideRatingProduct(appDatabase: AppDatabase): RatingProductDao{
        return appDatabase.ratingProductDao()
    }

    @Provides
    fun provideUserManager(@ApplicationContext context: Context): UserManager{
        return UserManager.getInstance(context)
    }

    @Provides
    fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient{
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("901780403692-v39fpjhl0hj5rpur16nadpeemee34psf.apps.googleusercontent.com")
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    @Provides
    fun provideFirebaseApp(@ApplicationContext context: Context): FirebaseApp? {
        return FirebaseApp.initializeApp(context)
    }
}