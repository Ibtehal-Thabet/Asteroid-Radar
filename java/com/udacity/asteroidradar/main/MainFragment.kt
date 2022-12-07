package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.ApiFilter
import com.udacity.asteroidradar.databinding.AsteroidItemBinding
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var viewModelAdapter:AsteroidAdapter

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.asteroids.observe(viewLifecycleOwner, Observer<List<Asteroid>> { asteroids ->
            asteroids?.apply {
                viewModelAdapter?.asteroids = asteroids
            }
        })
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setHasOptionsMenu(true)

        viewModelAdapter = AsteroidAdapter(AsteroidClick { asteroid ->
            findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
        })

        binding.asteroidRecycler.adapter = viewModelAdapter


        binding.root.findViewById<RecyclerView>(R.id.asteroid_recycler).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.updateFilter(
            when(item.itemId) {
            R.id.show_today_menu -> ApiFilter.SHOW_TODAY
            R.id.show_week_menu -> ApiFilter.SHOW_WEEK
            else -> ApiFilter.SHOW_SAVED
            }
        )
        return true
    }
}


class AsteroidClick(val block: (Asteroid) -> Unit) {
    fun onClick(asteroid: Asteroid) = block(asteroid)
}

class AsteroidAdapter(val callback: AsteroidClick) : RecyclerView.Adapter<AsteroidViewHolder>() {

    /**
     * The asteroids that our Adapter will show
     */
    var asteroids: List<Asteroid> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        val withDataBinding: AsteroidItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            AsteroidViewHolder.LAYOUT,
            parent,
            false)
        return AsteroidViewHolder(withDataBinding)
    }

    override fun getItemCount() = asteroids.size

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.asteroid = asteroids[position]
            it.asteroidCallback = callback
        }
    }

}

/**
 * ViewHolder for Asteroid items. All work is done by data binding.
 */
class AsteroidViewHolder(val viewDataBinding: AsteroidItemBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.asteroid_item
    }
}
