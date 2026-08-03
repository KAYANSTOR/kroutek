package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.AppDatabase
import com.example.models.PointOfSale
import com.example.models.Wallet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class PosWalletViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    
    val pointsOfSale = database.pointOfSaleDao().getAllPointsOfSale()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val wallets = database.walletDao().getAllWallets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addPointOfSale(name: String, location: String, isActive: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val pos = PointOfSale(
                id = UUID.randomUUID().toString(),
                name = name,
                location = location,
                isActive = isActive,
                balance = 0.0,
                createdAt = System.currentTimeMillis()
            )
            database.pointOfSaleDao().insertPointOfSale(pos)
        }
    }

    fun addWallet(name: String, type: String, connectionNumber: String, conditions: String, isActive: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val wallet = Wallet(
                id = UUID.randomUUID().toString(),
                name = name,
                type = type,
                connectionNumber = connectionNumber,
                conditions = conditions,
                isActive = isActive,
                balance = 0.0,
                createdAt = System.currentTimeMillis()
            )
            database.walletDao().insertWallet(wallet)
        }
    }
}
