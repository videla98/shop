package com.example.shop.feature_authentication.presentation.login

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ShareCompat.getCallingActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.shop.R
import com.example.shop.feature_authentication.data.LogOutWorker
import com.example.shop.databinding.FragmentLogInBinding
import com.example.shop.ui.authentication.digest
import com.example.shop.core.presentation.ShopActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration

@AndroidEntryPoint
class LogInFragment : Fragment() {
    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LogInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSignUp.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_logInFragment_to_signUpFragment)
        }

        binding.btnLogIn.setOnClickListener {
            lifecycleScope.launch {
                val userId = viewModel.getUserIdIfExists(
                    binding.etUsername.text.toString(),
                    digest(binding.etPassword.text.toString())
                )
                userId?.let {
                    viewModel.setActiveUser(it)
                    startLogOutWorker()
                    if (getCallingActivity(requireActivity()) == null) {
                        openMainActivity()
                    } else {
                        requireActivity().setResult(RESULT_OK)
                        requireActivity().finish()
                    }
                } ?: run {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LogInFragment.context,
                            "Wrong username and password combination",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun startLogOutWorker() {
        val logOutRequest = OneTimeWorkRequestBuilder<LogOutWorker>()
            .setInitialDelay(Duration.ofSeconds(30))
            .build()
        val workManager = WorkManager.getInstance(this.requireContext())
        workManager
            .beginUniqueWork(
                "logout",
                ExistingWorkPolicy.REPLACE,
                logOutRequest
            )
            .enqueue()
    }

    private fun openMainActivity() {
        val intent = Intent(this.context, ShopActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        this.activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}