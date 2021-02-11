package com.rafaelfelipeac.kotlinrepo.features.favorite.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rafaelfelipeac.kotlinrepo.core.extension.viewBinding
import com.rafaelfelipeac.kotlinrepo.databinding.FragmentFavoriteBinding

class FavoriteFragment : Fragment() {

    private var binding by viewBinding<FragmentFavoriteBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentFavoriteBinding.inflate(inflater, container, false).run {
            binding = this
            binding.root
        }
    }
}
