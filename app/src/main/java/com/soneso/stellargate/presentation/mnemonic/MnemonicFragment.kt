package com.soneso.stellargate.presentation.mnemonic


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.soneso.stellargate.R
import com.soneso.stellargate.presentation.MainActivity
import com.soneso.stellargate.presentation.auth.AuthFragment
import kotlinx.android.synthetic.main.fragment_mnemonic.*
import kotlinx.android.synthetic.main.layout_flipper_mnemonic_question.view.*


/**
 * A simple [Fragment] subclass.
 *
 */
class MnemonicFragment : AuthFragment() {

    private lateinit var mnemonic: String
    private lateinit var mnemonicViewModel: MnemonicViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mnemonic = arguments?.getString(ARG_MNEMONIC) ?: ""
        mnemonicViewModel = ViewModelProviders.of(this)[MnemonicViewModel::class.java]
        mnemonicViewModel.init(mnemonic)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_mnemonic, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mnemonic_value.text = mnemonic

        mnemonic_button.setOnClickListener {
            val currentFlipperView = mnemonic_flipper.currentView
            when {
                mnemonicViewModel.isMnemonicCurrentlyPresented() -> {
                    mnemonicViewModel.randomizeQuestion()
                }
                mnemonicViewModel.isAnswerCorrect(currentFlipperView.mnemonic_answer.text.trim()) -> {
                    mnemonicViewModel.randomizeQuestion()
                }
                else -> {
                    Toast.makeText(context, "You're wrong! Please repeat!", Toast.LENGTH_SHORT).show()
                    mnemonicViewModel.resetQuiz()
                    mnemonic_flipper.showNext()
                    for (index in (1 until mnemonic_flipper.childCount).reversed()) {
                        mnemonic_flipper.removeViewAt(index)
                    }
                }
            }
        }

        mnemonicViewModel.liveCurrentQuestion.observe(this, Observer {
            val currentQuestion = it ?: return@Observer
            if (currentQuestion >= resources.getInteger(R.integer.mnemonic_question_count)) {
                success()
            } else {
                val flipperPage = LayoutInflater.from(context).inflate(R.layout.layout_flipper_mnemonic_question, mnemonic_flipper, false)
                flipperPage.mnemonic_question.text = getString(R.string.mnemonic_question, mnemonicViewModel.currentWordIndex + 1)
                flipperPage.mnemonic_answer.setText("")
                mnemonic_flipper.addView(flipperPage, mnemonic_flipper.childCount)
                mnemonic_flipper.showNext()
            }
        })
    }

    private fun success() {
        MainActivity.startInstance(context ?: return)
        activity?.finishAffinity()
    }

    companion object {
        const val TAG = "MnemonicFragment"
        private const val ARG_MNEMONIC = "$TAG.ARG_MNEMONIC"

        fun newInstance(mnemonic: String): MnemonicFragment {
            val instance = MnemonicFragment()
            val args = Bundle()
            args.putString(ARG_MNEMONIC, mnemonic)
            instance.arguments = args
            return instance
        }
    }
}
