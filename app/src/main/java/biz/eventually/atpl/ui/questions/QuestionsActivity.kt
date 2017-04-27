package biz.eventually.atpl.ui.questions

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.view.*
import android.widget.CheckBox
import biz.eventually.atpl.AtplApplication
import biz.eventually.atpl.R
import biz.eventually.atpl.common.IntentIdentifier
import biz.eventually.atpl.common.StateIdentifier
import biz.eventually.atpl.data.model.Topic
import biz.eventually.atpl.data.network.Question
import biz.eventually.atpl.ui.BaseActivity
import biz.eventually.atpl.ui.source.QuestionsManager
import biz.eventually.atpl.utils.getHtml
import biz.eventually.atpl.utils.shuffle
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.activity_questions.*

class QuestionsActivity : BaseActivity<QuestionsManager>() {

    private var mTopic: Topic? = null
    private var mQuestions = mutableListOf<Question>()

    private var mCurrentQuestion: Int = 0
    private var mShowAnswer = false
    private var mIndexTick = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)
        AtplApplication.component.inject(this)
        ButterKnife.bind(this)

        mTopic = intent.extras.getParcelable<Topic>(IntentIdentifier.TOPIC)
        mTopic?.apply {
            rotateloading.start()
            manager.getQuestions(id) { t ->
                mTopic = t
                mQuestions = t.questions as MutableList<Question>
                rotateloading.stop()
                displayQuestion()
            }
        }

        question_answer_1.setOnClickListener { onAnswerClick(it, 0) }
        question_answer_2.setOnClickListener { onAnswerClick(it, 1) }
        question_answer_3.setOnClickListener { onAnswerClick(it, 2) }
        question_answer_4.setOnClickListener { onAnswerClick(it, 3) }

        question_previous.setOnClickListener {
            if (mCurrentQuestion >= 1) mCurrentQuestion -= 1
            displayQuestion()
        }

        question_next.setOnClickListener {
            mTopic?.questions?.let {
                if (mCurrentQuestion < it.count() - 1) mCurrentQuestion += 1
                displayQuestion()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_shuffle, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_shuffle -> {
                shuffleQuestions()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.let {
            it.putParcelable(StateIdentifier.TOPIC, mTopic)
            it.putInt(StateIdentifier.QUEST_CURRENT, mCurrentQuestion)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState?.let {
            mTopic = it.getParcelable(StateIdentifier.TOPIC)
            mQuestions = mTopic?.questions as MutableList<Question>
            mCurrentQuestion = it.getInt(StateIdentifier.QUEST_CURRENT)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun shuffleQuestions() {
        mQuestions = shuffle(mQuestions) as MutableList<Question>
        mCurrentQuestion = 0
        displayQuestion()
    }

    fun displayQuestion() {
        mShowAnswer = false
        resetCheckbox()

        mQuestions[mCurrentQuestion].apply {
            question_label.text = getHtml(label)

            for (i in 0..answers.count() - 1) {
                when (i) {
                    0 -> question_answer_1_text.text = answers[i].value
                    1 -> question_answer_2_text.text = answers[i].value
                    2 -> question_answer_3_text.text = answers[i].value
                    3 -> question_answer_4_text.text = answers[i].value
                }
            }
        }

        mQuestions.isNotEmpty().apply {
            question_range.text = "${mCurrentQuestion+1} / ${mQuestions.count()}"
        }

        question_previous.visibility = if (mCurrentQuestion > 0) View.VISIBLE else View.GONE

        mTopic?.questions?.let {
            question_next.visibility = if (mCurrentQuestion < it.count() - 1) View.VISIBLE else View.GONE
        }

        initCheckboxes()
    }

    fun initCheckboxes() {
        question_answer_1.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.cardview_light_background))
        question_answer_2.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.cardview_light_background))
        question_answer_3.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.cardview_light_background))
        question_answer_4.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.cardview_light_background))
    }

    fun onAnswerClick(view: View, index: Int) {

        mIndexTick = index
        mShowAnswer = !mShowAnswer

        if (mShowAnswer) {
            resetCheckbox()
            checkOneBox(view as CardView, true)
            showAnswer()
        } else {
            initCheckboxes()
            resetCheckbox()
        }

    }

    private fun resetCheckbox() {
        checkOneBox(question_answer_1, false)
        checkOneBox(question_answer_2, false)
        checkOneBox(question_answer_3, false)
        checkOneBox(question_answer_4, false)
    }

    private fun checkOneBox(card: CardView, check: Boolean ) {

        // get the LinearLayout inside the CardView
        val group = (card as ViewGroup).getChildAt(0) as ViewGroup
        var box: CheckBox? = null

        (0..(group.childCount - 1)).forEach { i ->
            if (group.getChildAt(i) is CheckBox) {
                box = group.getChildAt(i) as CheckBox
            }
        }

        box?.let { it.isChecked = check }
    }

    private fun showAnswer() {
        mTopic?.questions?.get(mCurrentQuestion)?.answers?.let {
            for (i in 0..it.count() - 1) {
                if (it[i].good) {
                    val bckg = if (it[i].good) ContextCompat.getDrawable(applicationContext, R.drawable.answer_right) else  ContextCompat.getDrawable(applicationContext, R.drawable.answer_wrong)
                    when (i) {
                        0 -> question_answer_1.background = bckg
                        1 -> question_answer_2.background = bckg
                        2 -> question_answer_3.background = bckg
                        3 -> question_answer_4.background = bckg
                    }
                }
            }
        }
    }
}
