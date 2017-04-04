package biz.eventually.atpl.ui.questions

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import android.view.View
import biz.eventually.atpl.AtplApplication
import biz.eventually.atpl.R
import biz.eventually.atpl.common.IntentIdentifier
import biz.eventually.atpl.network.model.Topic
import biz.eventually.atpl.ui.BaseActivity
import biz.eventually.atpl.ui.source.QuestionsManager
import biz.eventually.atpl.utils.getHtml
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.activity_questions.*

class QuestionsActivity : BaseActivity<QuestionsManager>() {

    private var mTopic: Topic? = null
    private var mCurrentQuestion: Int = 0

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
                rotateloading.stop()
                displayQuestion()
            }
        }

        question_answer_1.setOnClickListener { onAnswerClick(it) }
        question_answer_2.setOnClickListener { onAnswerClick(it) }
        question_answer_3.setOnClickListener { onAnswerClick(it) }
        question_answer_4.setOnClickListener { onAnswerClick(it) }

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun displayQuestion() {

        mTopic?.questions?.get(mCurrentQuestion)?.apply {
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

        question_previous.visibility = if (mCurrentQuestion > 0) View.VISIBLE else View.GONE

        mTopic?.questions?.let {
            question_next.visibility = if (mCurrentQuestion < it.count() - 1) View.VISIBLE else View.GONE
        }

        question_answer_1.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.cardview_light_background))
        question_answer_2.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.cardview_light_background))
        question_answer_3.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.cardview_light_background))
        question_answer_4.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.cardview_light_background))
    }

    fun onAnswerClick(view: View) {
        showAnswer()
    }

    private fun showAnswer() {
        mTopic?.questions?.get(mCurrentQuestion)?.answers?.let {
            for (i in 0..it.count() - 1) {
                val color = if (it[i].good) ContextCompat.getColor(applicationContext, R.color.colorAccent) else ContextCompat.getColor(applicationContext, R.color.colorSecondary)
                when (i) {
                    0 -> question_answer_1.setBackgroundColor(color)
                    1 -> question_answer_2.setBackgroundColor(color)
                    2 -> question_answer_3.setBackgroundColor(color)
                    3 -> question_answer_4.setBackgroundColor(color)
                }
            }
        }
    }
}
