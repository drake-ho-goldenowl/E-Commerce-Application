package com.goldenowl.ecommerceapp.ui.Profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.data.UserManager
import com.goldenowl.ecommerceapp.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userManager = UserManager.getInstance(this.requireContext())
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.appBarLayout.topAppBar.title = "My Profile"
        if (!userManager.isLogged()) {
            changeFragment(ProfileNoLoginFragment())
            return binding.root
        }
        changeFragment(ProfileLoginFragment())
        return binding.root
    }

    private fun changeFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.host_fragment_profile, fragment)
        transaction.addToBackStack(null)
        transaction.setReorderingAllowed(true)
        transaction.commit()
    }

    companion object {
        const val TAG = "PROFILE_FRAGMENT"
    }
}