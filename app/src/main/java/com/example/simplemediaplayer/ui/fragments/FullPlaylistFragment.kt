package com.example.simplemediaplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.simplemediaplayer.databinding.FragmentFullPlaylistBinding
import com.example.simplemediaplayer.ui.activities.MainActivity
import com.example.simplemediaplayer.ui.dialogs.LoadingDialog


class FullPlaylistFragment : Fragment() {

    private lateinit var binding: FragmentFullPlaylistBinding
    private lateinit var rvLayoutManager: LinearLayoutManager
    private lateinit var loadingDialog: LoadingDialog
    private var smoothScroller: RecyclerView.SmoothScroller? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFullPlaylistBinding.inflate(inflater, container, false)
        loadingDialog = LoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListSong()
        initEvent()

    }
    private fun initEvent() {
        (requireActivity() as MainActivity).viewModel.getSongListSource().observe(viewLifecycleOwner) {
            it?.let {
                (requireActivity() as MainActivity).rvPlaylistAdapter.addAll(it)
                binding.rvPlaylist.adapter = (requireActivity() as MainActivity).rvPlaylistAdapter
            }
        }

        (requireActivity() as MainActivity).viewModel.loadingLiveData().observe(viewLifecycleOwner) {
            if (it) {
                loadingDialog.show(parentFragmentManager, "")
            } else {
                loadingDialog.dismiss()
            }
        }


        smoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        (requireActivity() as MainActivity).apply {
            rvPlaylistAdapter.onItemClick = { data ->
                viewModel.onSelectTrack(data)

                if(isInViewingFullListMode()) {
                    animationCloseFullList()
                }

                val nextPosition = rvPlaylistAdapter.nextTrackOnQueue
                if(nextPosition > 0) {
                    smoothScroller?.let {
                        it.targetPosition = nextPosition
                        rvLayoutManager.startSmoothScroll(it)
                    }
                }
            }
        }
    }

    private fun initListSong() {
        binding.rvPlaylist.apply {
            setItemViewCacheSize(200)
            hasFixedSize()
            rvLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            layoutManager = rvLayoutManager
        }
    }
}