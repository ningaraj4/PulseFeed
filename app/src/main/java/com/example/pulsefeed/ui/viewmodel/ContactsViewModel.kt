package com.example.pulsefeed.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefeed.ui.screen.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContactsUiState(
    val contacts: List<Contact> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ContactsViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()
    
    fun loadContacts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Mock contacts for demo
            val mockContacts = listOf(
                Contact("John Doe", "+1234567890"),
                Contact("Jane Smith", "+1987654321"),
                Contact("Mike Johnson", "+1122334455")
            )
            
            kotlinx.coroutines.delay(1000)
            
            _uiState.value = _uiState.value.copy(
                contacts = mockContacts,
                isLoading = false
            )
        }
    }
    
    fun requestContactsPermission() {
        // Mock permission request
        loadContacts()
    }
    
    fun followContact(contact: Contact) {
        viewModelScope.launch {
            // TODO: Implement follow functionality with backend
            // For now, just remove from suggestions
            val updatedContacts = _uiState.value.contacts.filter { it != contact }
            _uiState.value = _uiState.value.copy(contacts = updatedContacts)
        }
    }
    
    fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }
    
    fun setError(error: String?) {
        _uiState.value = _uiState.value.copy(error = error)
    }
}
