package com.rafaelfelipeac.marvelapp.features.details.presentation

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.rafaelfelipeac.marvelapp.R
import com.rafaelfelipeac.marvelapp.core.extension.gone
import com.rafaelfelipeac.marvelapp.core.extension.viewBinding
import com.rafaelfelipeac.marvelapp.core.extension.visible
import com.rafaelfelipeac.marvelapp.core.plataform.base.BaseFragment
import com.rafaelfelipeac.marvelapp.databinding.FragmentDetailsBinding
import com.rafaelfelipeac.marvelapp.features.details.domain.model.CharacterDetail
import com.rafaelfelipeac.marvelapp.features.details.domain.model.DetailInfo
import com.rafaelfelipeac.marvelapp.features.main.MainFragmentDirections

class DetailsFragment : BaseFragment() {

    private var isFirstPageComics = true
    private var isFirstPageSeries = true

    private var offsetComics = 0
    private var offsetSeries = 0

    private var isLoadingComics = false
    private var isLoadingSeries = false

    private var binding by viewBinding<FragmentDetailsBinding>()

    var viewModel: DetailsViewModel? = null

    private var comicsAdapter = DetailsInfoAdapter()
    private var seriesAdapter = DetailsInfoAdapter()

    private var characterId: Long = 0L

    private var listAsGrid = false

    var characterDetail: CharacterDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        characterId = arguments?.let { DetailsFragmentArgs.fromBundle(it).characterId } ?: 0L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setScreen()

        setHasOptionsMenu(false)

        return FragmentDetailsBinding.inflate(inflater, container, false).run {
            binding = this
            binding.root
        }
    }

    private fun setScreen() {
        showBackArrow()
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel == null) {
            viewModel = viewModelProvider.detailsViewModel()
        }

        setLayout()

        showList()

        viewModel?.getDetails(characterId)
        viewModel?.getDetailsComics(characterId, offsetComics)
        viewModel?.getDetailsSeries(characterId, offsetSeries)

        observeViewModel()
    }

    private fun setComics(comics: List<DetailInfo>?) {
        comicsAdapter.setItems(comics)
        comicsAdapter.clickListener = { character ->
            val action = MainFragmentDirections.mainToDetail()
            action.characterId = character.id
            navController?.navigate(action)
        }

        if (isFirstPageComics) {
            isFirstPageComics = false

            binding.detailsCharacterComicsList.apply {
                setHasFixedSize(true)

                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

                adapter = comicsAdapter
            }
        }
    }

    private fun setSeries(series: List<DetailInfo>?) {
        seriesAdapter.setItems(series)
        seriesAdapter.clickListener = { character ->
            val action = MainFragmentDirections.mainToDetail()
            action.characterId = character.id
            navController?.navigate(action)
        }

        if (isFirstPageSeries) {
            isFirstPageSeries = false

            binding.detailsCharacterSeriesList.apply {
                setHasFixedSize(true)

                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

                adapter = seriesAdapter
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_grid, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuGrid -> {
                listAsGrid = !listAsGrid

//                refreshList()

                return true
            }
        }

        return false
    }

    private fun showList() {
//        binding.charactersList.visible()
//        binding.charactersPlaceholder.gone()
//        binding.charactersListLoader.gone()
//        binding.charactersProgressBar.gone()
    }

    private fun showPlaceholder() {
//        if ((!binding.charactersList.isVisible || binding.charactersProgressBar.isVisible)) {
//            binding.charactersPlaceholder.visible()
//        }
//
//        binding.charactersListLoader.gone()
//        binding.charactersProgressBar.gone()
    }

    private fun setLayout() {
        binding.detailsFavorite.setOnClickListener {
            viewModel?.favoriteCharacter(
                characterDetail?.id!!,
                characterDetail?.name!!,
                characterDetail?.thumbnail?.getUrl()!!
            )
        }

        binding.detailsCharacterComicsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollHorizontally(1) && !isLoadingComics) {
                    isLoadingComics = true

                    // load the next page
                    viewModel?.getDetailsComics(characterId, offsetComics)

                    binding.detailsCharacterComicsListLoader.visible()
                    binding.detailsCharacterComicsList.scrollToPosition(comicsAdapter.itemCount - 1)
                }
            }
        })

        binding.detailsCharacterSeriesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollHorizontally(1) && !isLoadingSeries) {
                    isLoadingSeries = true

                    // load the next page
                    viewModel?.getDetailsSeries(characterId, offsetSeries)

                    binding.detailsCharacterSeriesListLoader.visible()
                    binding.detailsCharacterSeriesList.scrollToPosition(seriesAdapter.itemCount - 1)
                }
            }
        })
    }

    private fun observeViewModel() {
        viewModel?.details?.observe(viewLifecycleOwner) {
            characterDetail = it

            setTitle(it?.name!!)
            binding.detailsFavorite.visible()
            binding.detailsCharacterDescription.text = if (it.description.isNotEmpty()) {
                it.description
            } else {
                getString(R.string.details_no_description)
            }
            binding.detailsCharacterImage.load(it.thumbnail.getUrl())
        }

        viewModel?.comics?.observe(viewLifecycleOwner) {
            if (it?.size!! > 0) {
                binding.detailsCharacterComicsListLoader.gone()
                isLoadingComics = false

                offsetComics += 20

                binding.detailsCharacterComics.visible()
                setComics(it)
            }
        }

        viewModel?.series?.observe(viewLifecycleOwner) {
            if (it?.size!! > 0) {
                binding.detailsCharacterSeriesListLoader.gone()
                isLoadingSeries = false

                offsetSeries += 20

                binding.detailsCharacterSeries.visible()
                setSeries(it)
            }
        }

        viewModel?.savedFavorite?.observe(viewLifecycleOwner) {
            Snackbar.make(requireView(), getString(R.string.details_added_favorites), Snackbar.LENGTH_SHORT).show()
        }

        viewModel?.error?.observe(viewLifecycleOwner) {

        }

        viewModel?.errorComics?.observe(viewLifecycleOwner) {

        }

        viewModel?.errorSeries?.observe(viewLifecycleOwner) {

        }
    }
}
