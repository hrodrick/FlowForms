package com.rootstrap.flowforms.example

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.rootstrap.flowforms.core.common.StatusCodes.BASIC_EMAIL_FORMAT_UNSATISFIED
import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.MATCH_UNSATISFIED
import com.rootstrap.flowforms.core.common.StatusCodes.MIN_LENGTH_UNSATISFIED
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.field.FieldStatus
import com.rootstrap.flowforms.core.form.FormStatus
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.CONFIRMATION
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.CONFIRM_PASSWORD
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.EMAIL
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.MIN_PASSWORD_LENGTH
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.NAME
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.NEW_PASSWORD
import com.rootstrap.flowforms.example.databinding.LayoutSimpleSignUpFormBinding
import com.rootstrap.flowforms.util.bind
import com.rootstrap.flowforms.util.repeatOnLifeCycleScope

class SignUpFormActivity : AppCompatActivity() {

    private lateinit var viewModel: SignUpViewModel
    private lateinit var binding: LayoutSimpleSignUpFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutSimpleSignUpFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        binding.lifecycleOwner = this
        binding.formModel = viewModel.formModel
        binding.continueButton.setOnClickListener {
            Toast.makeText(this, R.string.account_registered, Toast.LENGTH_SHORT).show()
        }

        listenStatusChanges()
        bindFields()
    }

    private fun listenStatusChanges() {
        viewModel.form.fields.value.let {
            repeatOnLifeCycleScope(
                { it[NAME]?.status?.collect(::onNameStatusChange) },
                { it[EMAIL]?.status?.collect(::onEmailStatusChange) },
                { it[NEW_PASSWORD]?.status?.collect(::onPasswordStatusChange) },
                { it[CONFIRM_PASSWORD]?.status?.collect(::onConfirmPasswordChange) },
                { viewModel.form.status.collect(::onFormStatusChange) }
            )
        }
    }

    private fun bindFields() {
        binding.apply {
            viewModel.form.bind(lifecycleScope,
                nameInputEditText to NAME,
                emailInputEditText to EMAIL,
                passwordInputEditText to NEW_PASSWORD,
                confirmPasswordInputEditText to CONFIRM_PASSWORD
            )
            viewModel.form.bind(this@SignUpFormActivity, lifecycleScope,
                viewModel.formModel.confirm to CONFIRMATION
            )
        }
    }

    private fun onNameStatusChange(status: FieldStatus) {
        when (status.code) {
            CORRECT, UNMODIFIED -> binding.nameInputLayout.error = null
            else -> binding.nameInputLayout.error = getString(R.string.required_field)
        }
    }

    private fun onEmailStatusChange(status: FieldStatus) {
        when (status.code) {
            CORRECT, UNMODIFIED -> binding.emailInputLayout.error = null
            BASIC_EMAIL_FORMAT_UNSATISFIED -> binding.emailInputLayout.error = getString(R.string.invalid_email)
            else -> binding.emailInputLayout.error = getString(R.string.required_field)
        }
    }

    private fun onPasswordStatusChange(status: FieldStatus) {
        when (status.code) {
            CORRECT, UNMODIFIED -> binding.passwordInputLayout.error = null
            MIN_LENGTH_UNSATISFIED -> binding.passwordInputLayout.error = getString(R.string.min_length, MIN_PASSWORD_LENGTH)
            else -> binding.passwordInputLayout.error = getString(R.string.required_field)
        }
    }

    private fun onConfirmPasswordChange(status: FieldStatus) {
        when (status.code) {
            CORRECT, UNMODIFIED -> binding.confirmPasswordInputLayout.error = null
            MIN_LENGTH_UNSATISFIED -> binding.confirmPasswordInputLayout.error = getString(R.string.min_length, MIN_PASSWORD_LENGTH)
            MATCH_UNSATISFIED -> binding.confirmPasswordInputLayout.error = getString(R.string.password_match)
            else -> binding.confirmPasswordInputLayout.error = getString(R.string.required_field)
        }
    }

    private fun onFormStatusChange(status: FormStatus) {
        when (status.code) {
            CORRECT -> binding.continueButton.isEnabled = true
            else -> binding.continueButton.isEnabled = false
        }
    }

}
