package com.goldenowl.ecommerceapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import com.goldenowl.ecommerceapp.databinding.FragmentLoginBinding
import com.goldenowl.ecommerceapp.utilities.PASSWORD
import com.goldenowl.ecommerceapp.utilities.USER_NAME

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater,container,false)
//        return inflater.inflate(R.layout.fragment_login, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val userName =  sharedPref.getString(USER_NAME,null)
        val userPassword = sharedPref.getString(PASSWORD,null)
        if(userName != null && userPassword != null){
            binding.txtUserName.setText(userName.toString())
            binding.txtPassword.setText(userPassword.toString())
        }

        view.findViewById<Button>(R.id.btnLogin).setOnClickListener{

//            with (sharedPref.edit()) {
//                this.putString(USER_NAME, txtUserName.text.toString())
//                this.putString(PASSWORD, txtPassword.text.toString())
//            }

            sharedPref.edit().apply{
                putString(USER_NAME,binding.txtUserName.text.toString())
                putString(PASSWORD,binding.txtPassword.text.toString())
            }.apply()


            startActivity(Intent(activity,MainActivity::class.java))
            activity?.finish()
        }

        view.findViewById<Button>(R.id.btnRegister)?.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_to_SignUp, null)
            )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}