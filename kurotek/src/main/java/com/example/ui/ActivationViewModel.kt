package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.CoreContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ActivationViewModel - Manages state and logic for the activation flow
 * Handles the 3-step activation process: version selection → phone → network/key
 */
class ActivationViewModel(
    private val authViewModel: AuthViewModel,
    private val repository: com.example.database.CardRepository
) : ViewModel() {

    // UI State
    private val _selectedStep = MutableStateFlow(0)
    val selectedStep: StateFlow<Int> = _selectedStep.asStateFlow()

    private val _phone = MutableStateFlow<String>("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _networkName = MutableStateFlow<String>("")
    val networkName: StateFlow<String> = _networkName.asStateFlow()

    private val _activationKey = MutableStateFlow<String>("")
    val activationKey: StateFlow<String> = _activationKey.asStateFlow()

private val _isTrial = MutableStateFlow(false)
    val isTrial: StateFlow<Boolean> = _isTrial.asStateFlow()

    private val _showKeyField = MutableStateFlow(true)
    val showKeyField: StateFlow<Boolean> = _showKeyField.asStateFlow()
    }

    // Actions for UI events
    fun onTrialSelected(isTrial: Boolean) {
        _isTrial.value = isTrial
        _showKeyField.value = !isTrial
    }

    fun onPhoneChanged(phone: String) {
        _phone.value = phone
    }

    fun onNetworkChanged(network: String) {
        _networkName.value = network
    }

    fun onKeyChanged(key: String) {
        _activationKey.value = key
    }

    fun goToPreviousStep() {
        val current = _selectedStep.value
        if (current > 0) {
            _selectedStep.value = current - 1
        }
    }

    fun goToNextStep() {
        val current = _selectedStep.value
        if (current < 2) {  // Only 3 steps (0, 1, 2)
            _selectedStep.value = current + 1
        }
    }

    fun activate(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val currentStep = _selectedStep.value
        if (currentStep != 2) return // Can only activate from step 2

        _isLoading.value = true
        _errorMessage.value = ""

        viewModelScope.launch {
            try {
                // Validate inputs
                val phone = _phone.value
                val networkName = _networkName.value
                val activationKey = _activationKey.value
                val isTrial = _isTrial.value

                if (phone.isBlank()) {
                    _errorMessage.value = "يرجى إدخال رقم الهاتف"
                    _isLoading.value = false
                    return@launch
                }

                if (networkName.isBlank()) {
                    _errorMessage.value = "يرجى إدخال اسم الشبكة"
                    _isLoading.value = false
                    return@launch
                }

                // For trial version, key might not be required
                if (!isTrial && activationKey.isBlank()) {
                    _errorMessage.value = "يرجى إدخال رمز التفعيل"
                    _isLoading.value = false
                    return@launch
                }

                // Simulate processing delay
                delay(800)

                if (isTrial) {
                    // For trial version, just proceed to success
                    // In a real app, you might want to record trial start date, etc.
                    onSuccess()
                } else {
                    // For full version, activate the license
                    authViewModel.activateLicense(activationKey) { success, message ->
                        _isLoading.value = false
                        if (success) {
                            // Mark initial setup as complete
                            authViewModel.setInitialLoginDone(true)
                            onSuccess()
                        } else {
                            _errorMessage.value = message
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "حدث خطأ غير متوقع: ${e.localizedMessage}"
                _isLoading.value = false
            }
        }
    }

    // Reset form to initial state
    fun reset() {
        _selectedStep.value = 0
        _phone.value = ""
        _networkName.value = ""
        _activationKey.value = ""
        _isTrial.value = false
        // showKeyField will update automatically via init block
        _isLoading.value = false
        _errorMessage.value = ""
    }
}

class ActivationViewModelFactory(
    private val authViewModel: AuthViewModel,
    private val repository: com.example.database.CardRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActivationViewModel(authViewModel, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}