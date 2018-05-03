package com.soneso.stellargate.presentation.auth


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.soneso.stellargate.R
import com.soneso.stellargate.presentation.MainActivity
import com.soneso.stellargate.presentation.general.SgViewState
import com.soneso.stellargate.presentation.general.State
import com.soneso.stellargate.presentation.util.fadeIn
import kotlinx.android.synthetic.main.fragment_mnemonic.*


/**
 * A simple [Fragment] subclass.
 *
 */
class MnemonicFragment : AuthFragment() {

    private lateinit var password: String
    private lateinit var authViewModel: AuthViewModel
    private lateinit var quizHelper: MnemonicQuizHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        password = arguments?.getString(ARG_PASSWORD) ?: ""
        authViewModel = ViewModelProviders.of(authActivity, viewModelFactory)[AuthViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_mnemonic, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeForLiveData()
        setupListeners()

        authViewModel.fetchMnemonic(password)
    }

    private fun subscribeForLiveData() {
        authViewModel.liveMnemonic.observe(this, Observer {
            renderMnemonic(it ?: return@Observer)
        })
        authViewModel.liveMnemonicConfirmation.observe(this, Observer {
            renderMnemonicConfirmation(it ?: return@Observer)
        })
    }

    private fun setupListeners() {

        mnemonic_button.setOnClickListener {
            if (quizHelper.checkCurrentWord(mnemonic_answer.text.toString())) {
                if (quizHelper.isCompleted()) {
                    onQuizCompleted()
                } else {
                    showQuestion()
                }
            } else {
                resetQuiz()
            }
        }
    }

    private fun renderMnemonic(viewState: SgViewState<String>) {

        when (viewState.state) {
            State.READY -> {

                val mnemonic = viewState.data ?: return
                quizHelper = MnemonicQuizHelper(mnemonic)
                mnemonic_view.text = getString(R.string.mnemonic_description, mnemonic)
            }
            State.LOADING -> {
            }
            State.ERROR -> {

                showErrorSnackbar(viewState.error)
            }
        }
    }

    private fun renderMnemonicConfirmation(viewState: SgViewState<Unit>) {

        when (viewState.state) {
            State.READY -> {

                MainActivity.startInstance(context!!)
                activity?.finishAffinity()
            }
            State.LOADING -> {
            }
            State.ERROR -> {

                showErrorSnackbar(viewState.error)
            }
        }
    }

    private fun showQuestion() {
        mnemonic_view.visibility = View.GONE
        mnemonic_question.text = getString(R.string.mnemonic_question, quizHelper.generatePositionToAsk() + 1)
        mnemonic_answer.text = SpannableStringBuilder()
        question_layout.fadeIn()
    }

    private fun resetQuiz() {
        quizHelper.reset()
        mnemonic_answer.text = SpannableStringBuilder()
        question_layout.visibility = View.GONE
        mnemonic_view.fadeIn()
    }

    private fun onQuizCompleted() {
        authViewModel.confirmMnemonic()
    }


    companion object {
        const val TAG = "MnemonicFragment"
        private const val ARG_PASSWORD = "$TAG.ARG_PASSWORD"

        fun newInstance(password: String): MnemonicFragment {
            val instance = MnemonicFragment()
            val args = Bundle()
            args.putString(ARG_PASSWORD, password)
            instance.arguments = args
            return instance
        }
    }
}
