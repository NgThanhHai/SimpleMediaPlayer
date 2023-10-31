package com.example.simplemediaplayer.ui.activities

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.simplemediaplayer.R
import com.example.simplemediaplayer.adapters.PlayListRecyclerViewAdapter
import com.example.simplemediaplayer.databinding.ActivityMainBinding
import com.example.simplemediaplayer.ui.fragments.FullPlaylistFragment
import com.example.simplemediaplayer.ui.fragments.MainSessionFragment
import com.example.simplemediaplayer.viewmodels.MainActivityViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainActivityViewModel
    private var isViewingFullList = false
    var rvPlaylistAdapter = PlayListRecyclerViewAdapter()
    private val manager: FragmentManager = supportFragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        window.decorView.setOnApplyWindowInsetsListener { view, windowInsets ->
            windowInsetsController?.hide(WindowInsetsCompat.Type.systemBars())
            view.onApplyWindowInsets(windowInsets)
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE)
        openMainSessionFragment()
        openFullPlaylistFragment()
    }

    private fun openMainSessionFragment() {
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.add(R.id.mainSessionContainer, MainSessionFragment(), MAIN_SESSION_FRAGMENT_TAG)
        transaction.commit()
    }

    private fun openFullPlaylistFragment() {
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.add(R.id.container, FullPlaylistFragment(), FULL_PLAYLIST_FRAGMENT_TAG)
        transaction.commit()
    }

    fun animationOpenFullList() {
        binding.motionLayout.transitionToEnd()
        isViewingFullList = true
    }

    fun animationCloseFullList() {
        binding.motionLayout.transitionToStart()
        isViewingFullList = false
    }

    fun isInViewingFullListMode() = isViewingFullList

    companion object {
        private const val MAIN_SESSION_FRAGMENT_TAG = "MAIN_SESSION_FRAGMENT_TAG"
        private const val FULL_PLAYLIST_FRAGMENT_TAG = "FULL_PLAYLIST_FRAGMENT_TAG"
    }

    override fun onBackPressed() {
        if(isInViewingFullListMode()) {
            animationCloseFullList()
        } else {
            super.onBackPressed()
        }
    }
}