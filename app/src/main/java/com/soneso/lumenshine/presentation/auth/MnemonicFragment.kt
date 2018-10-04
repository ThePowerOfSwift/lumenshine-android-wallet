package com.soneso.lumenshine.presentation.auth


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.soneso.lumenshine.R
import com.soneso.lumenshine.presentation.customViews.LsEditText
import com.soneso.lumenshine.presentation.customViews.LsTextView
import com.soneso.lumenshine.presentation.util.setOnTextChangeListener
import com.soneso.lumenshine.presentation.util.showInfoDialog
import com.soneso.lumenshine.util.LsException
import com.soneso.lumenshine.util.Resource
import kotlinx.android.synthetic.main.fragment_mnemonic.*
import kotlinx.android.synthetic.main.list_item_mnemonic.view.*
import kotlinx.android.synthetic.main.view_mnemonic_confirm.*
import kotlinx.android.synthetic.main.view_mnemonic_words.*


/**
 * A simple [Fragment] subclass.
 *
 */
class MnemonicFragment : AuthFragment() {

    private lateinit var quizHelper: MnemonicQuizHelper
    private val editTexts = ArrayList<LsEditText>()
    private val wordTexts = ArrayList<LsTextView>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_mnemonic, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeForLiveData()
        setupListeners()

        authViewModel.fetchMnemonic()
    }

    private fun subscribeForLiveData() {

        authViewModel.liveMnemonic.observe(this, Observer {
            renderMnemonic(it ?: return@Observer)
        })
        authViewModel.liveMnemonicConfirmation.observe(this, Observer {
            renderMnemonicConfirmation(it ?: return@Observer)
        })
    }

    private fun renderMnemonicConfirmation(resource: Resource<Boolean, LsException>) {

        when (resource.state) {
            Resource.FAILURE -> {
                hideProgressDialog()
                showErrorSnackbar(resource.failure())
            }
            Resource.LOADING -> {
                showProgressDialog()
            }
            Resource.SUCCESS -> {
                hideProgressDialog()
            }
        }
    }

    private fun setupListeners() {

        mnemonic_confirm.setOnClickListener {
            //            if (quizHelper.checkCurrentWord(mnemonic_answer.text.toString())) {
//                if (quizHelper.isCompleted()) {
//                    onQuizCompleted()
//                } else {
            showQuestion()
//                }
//            } else {
//                resetQuiz()
//            }
        }
        mnemonic_more_info_button.setOnClickListener {
            activity?.showInfoDialog()
        }
        finish_setup.setOnClickListener {
            checkWords()
        }
        go_back_button.setOnClickListener {
            resetQuiz()
        }
    }

    private fun renderMnemonic(resource: Resource<String, LsException>) {

        when (resource.state) {
            Resource.SUCCESS -> {

                val mnemonic = resource.success()
                quizHelper = MnemonicQuizHelper(mnemonic)
                mnemonic_view.text = getString(R.string.mnemonic_description, mnemonic)
                val mnemonics = mnemonic.split(" ")
                for (i in 0 until mnemonics.size) {
                    val mnemonicView = LayoutInflater.from(context).inflate(R.layout.list_item_mnemonic, null)
                    val m = mnemonics[i]
                    mnemonicView.mnemonic_order.text = (i + 1).toString()
                    mnemonicView.mnemonic_text.text = m
                    mnemonic_container.addView(mnemonicView)
                }
                mnemonic_confirm.visibility = View.VISIBLE
            }
            Resource.LOADING -> {
            }
            Resource.FAILURE -> {

                showErrorSnackbar(resource.failure())
            }
        }
    }

    /**
     * check all the introduced numbers
     */
    private fun checkWords() {

        for (i in 0..3) {
            if (editTexts[i].text.isEmpty()) {
                editTexts[i].showError(true)
                return
            }
            if (!quizHelper.checkWord(wordTexts[i].text.toString(), editTexts[i].text.toString().toInt()))
                break
        }
        if (quizHelper.isCompleted()) {
            onQuizCompleted()
        } else {
            showError(true)
        }
    }

    /**
     * changes mnemonic words textColor to red and the editTexts border to red to
     */
    private fun showError(show: Boolean) {
        val red = ContextCompat.getColor(context!!, R.color.red)
        val dark = ContextCompat.getColor(context!!, R.color.text_dark)
        mnemonic_word_1.setTextColor(red)
        for (i in 0..3) {
            wordTexts[i].setTextColor(if (show) red else dark)
            editTexts[i].showError(show)
        }
        mnemonic_error_text.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    private fun showQuestion() {
        mnemonic_list.visibility = View.GONE
        mnemonic_confirmation.visibility = View.VISIBLE
        val words = quizHelper.getRandomWords()

        fillViewArrays()
        for (i in 0..3) {
            wordTexts[i].text = words[i]
        }

//        mnemonic_view.visibility = View.GONE
//        mnemonic_question.text = getString(R.string.mnemonic_question, quizHelper.generatePositionToAsk() + 1)
//        mnemonic_answer.text = SpannableStringBuilder()
//        question_layout.fadeIn()
    }

    /**
     * adds all mnemonic textViews and editText to arrays
     */
    private fun fillViewArrays() {
        wordTexts.add(mnemonic_word_1)
        wordTexts.add(mnemonic_word_2)
        wordTexts.add(mnemonic_word_3)
        wordTexts.add(mnemonic_word_4)
        editTexts.add(mnemonic_position_1)
        editTexts.add(mnemonic_position_2)
        editTexts.add(mnemonic_position_3)
        editTexts.add(mnemonic_position_4)
        mnemonic_position_1.setOnTextChangeListener { showError(false) }
        mnemonic_position_2.setOnTextChangeListener { showError(false) }
        mnemonic_position_3.setOnTextChangeListener { showError(false) }
        mnemonic_position_4.setOnTextChangeListener { showError(false) }
    }

    private fun resetQuiz() {
        for (e in editTexts) {
            e.text.clear()
        }
        quizHelper.reset()
        mnemonic_list.visibility = View.VISIBLE
        mnemonic_confirmation.visibility = View.GONE
    }

    private fun onQuizCompleted() {
        authViewModel.confirmMnemonic()
    }


    companion object {
        const val TAG = "MnemonicFragment"

        fun newInstance() = MnemonicFragment()
    }
}
