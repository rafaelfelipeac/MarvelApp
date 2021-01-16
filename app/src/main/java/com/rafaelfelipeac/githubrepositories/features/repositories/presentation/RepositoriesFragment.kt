package com.rafaelfelipeac.githubrepositories.features.repositories.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rafaelfelipeac.githubrepositories.core.extension.gone
import com.rafaelfelipeac.githubrepositories.core.extension.viewBinding
import com.rafaelfelipeac.githubrepositories.core.extension.visible
import com.rafaelfelipeac.githubrepositories.core.plataform.Constants.isNetworkConnected
import com.rafaelfelipeac.githubrepositories.core.plataform.base.BaseFragment
import com.rafaelfelipeac.githubrepositories.databinding.FragmentRepositoriesBinding
import com.rafaelfelipeac.githubrepositories.features.repositories.domain.model.Repository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

var CURRENT_PAGE = 1
var LANGUAGE = "language:kotlin"
var SORT = "star"

class RepositoriesFragment : BaseFragment() {

    private val viewModel by lazy { viewModelProvider.repositoriesViewModel() }

    private var binding by viewBinding<FragmentRepositoriesBinding>()

    private var repositoriesAdapter = RepositoriesAdapter()

    private var isFirstPage = true
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return FragmentRepositoriesBinding.inflate(inflater, container, false).run {
            binding = this
            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLayout()

        viewModel.getRepositories(LANGUAGE, SORT, CURRENT_PAGE)

        observeViewModel()
    }

    private fun setLayout() {
        binding.repositoriesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1) && !isLoading) {
                    isLoading = true

                    // load the next page
                    viewModel.getRepositories(LANGUAGE, SORT, CURRENT_PAGE)

                    binding.repositoriesListLoader.visible()
                    binding.repositoriesList.scrollToPosition(repositoriesAdapter.itemCount - 1)
                }
            }
        })
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.repositories.collect {
                isLoading = false

                setList(it)

                CURRENT_PAGE += 1

                binding.repositoriesPlaceholder.gone()
                binding.repositoriesListLoader.gone()
                binding.repositoriesProgressBar.gone()
            }
        }

        lifecycleScope.launch {
            viewModel.error.collect {
                isLoading = false

                if ((!isNetworkConnected && !binding.repositoriesList.isVisible) ||
                    (!isNetworkConnected && binding.repositoriesProgressBar.isVisible)) {
                    binding.repositoriesPlaceholder.visible()
                }

                binding.repositoriesListLoader.gone()
                binding.repositoriesProgressBar.gone()
            }
        }
    }

    private fun setList(repositories: List<Repository>) {
        repositoriesAdapter.setItems(repositories)

        if (isFirstPage) {
            isFirstPage = false

            binding.repositoriesList.apply {
                setHasFixedSize(true)

                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = repositoriesAdapter
            }
        }
    }
}
