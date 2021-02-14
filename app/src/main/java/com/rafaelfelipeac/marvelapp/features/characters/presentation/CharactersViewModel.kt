package com.rafaelfelipeac.marvelapp.features.characters.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafaelfelipeac.marvelapp.core.extension.md5
import com.rafaelfelipeac.marvelapp.core.network.ResultWrapper
import com.rafaelfelipeac.marvelapp.core.plataform.Config.API_KEY
import com.rafaelfelipeac.marvelapp.core.plataform.Config.PRIVATE_KEY
import com.rafaelfelipeac.marvelapp.features.characters.domain.model.Character
import com.rafaelfelipeac.marvelapp.features.characters.domain.usecase.GetCharactersUseCase
import com.rafaelfelipeac.marvelapp.features.characters.domain.usecase.SaveFavoriteUseCase
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

open class CharactersViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase,
    private val saveFavoriteUseCase: SaveFavoriteUseCase
) : ViewModel() {

    open val characters: LiveData<List<Character>?> get() = _characters
    private val _characters = MutableLiveData<List<Character>?>()
    open val error: LiveData<Throwable> get() = _error
    private val _error = MutableLiveData<Throwable>()
    val savedFavorite: LiveData<Long?> get() = _savedFavorite
    private val _savedFavorite = MutableLiveData<Long?>()

    open fun getCharacters(offset: Int) {
        val timestamp = Date().time
        val hash = (timestamp.toString() + PRIVATE_KEY + API_KEY).md5()

        viewModelScope.launch {
            when (val response = getCharactersUseCase.invoke(API_KEY, hash, timestamp, offset)) {
                is ResultWrapper.Success -> {
                    _characters.value = response.value
                }
                is ResultWrapper.GenericError -> {
                    _error.value = response.throwable
                }
                is ResultWrapper.NetworkError -> {
                    _error.value = response.throwable
                }
            }
        }
    }

    open fun favoriteCharacter(characterId: Long, characterName: String, characterUrl: String) {
        viewModelScope.launch {
            _savedFavorite.value = saveFavoriteUseCase(characterId, characterName, characterUrl)
        }
    }
}
