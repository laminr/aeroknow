package biz.eventually.atpl.ui.questions

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import biz.eventually.atpl.AtplApplication
import biz.eventually.atpl.BuildConfig
import biz.eventually.atpl.R
import biz.eventually.atpl.common.IntentIdentifier
import biz.eventually.atpl.common.StateIdentifier
import biz.eventually.atpl.data.model.Topic
import biz.eventually.atpl.data.network.Question
import biz.eventually.atpl.ui.BaseActivity
import biz.eventually.atpl.ui.source.QuestionsManager
import biz.eventually.atpl.utils.*
import com.squareup.picasso.Picasso
import com.vicpin.krealmextensions.delete
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_questions.*


class QuestionsActivity : BaseActivity<QuestionsManager>() {

    private var mTopic: Topic? = null
    private var mQuestions = mutableListOf<Question>()

    private var mCurrentQuestion: Int = 0
    private var mShowAnswer = false
    private var mIndexTick = -1

    private var transparentColor: Int = 0x00000000
    private var mTimer: CountDownTimer? = null
    private var isLight: Boolean = true

    private var mTimeLength: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)
        AtplApplication.component.inject(this)

        mTimeLength = getLong(applicationContext, PREF_TIMER, mTimeLength)

        mTopic = intent.extras.getParcelable<Topic>(IntentIdentifier.TOPIC)
        mTopic?.apply {
            rotateloading.start()
            manager.getQuestions(id, { t -> questionsLoaded(t) }, { loadError() })

            supportActionBar?.title = name
        }

        question_answer_1.setOnClickListener { onAnswerClick(it, 0) }
        question_answer_2.setOnClickListener { onAnswerClick(it, 1) }
        question_answer_3.setOnClickListener { onAnswerClick(it, 2) }
        question_answer_4.setOnClickListener { onAnswerClick(it, 3) }

        question_previous.setOnClickListener {
            if (question_follow.isChecked && mIndexTick > -1) {
                manager.updateFollow(
                        mQuestions[mCurrentQuestion].id,
                        mQuestions[mCurrentQuestion].answers[mIndexTick].good
                )
            }

            if (mCurrentQuestion >= 1) mCurrentQuestion -= 1
            displayQuestion()
        }

        question_next.setOnClickListener {
            mTopic?.questions?.let {
                if (question_follow.isChecked && mIndexTick > -1) {
                    manager.updateFollow(
                            mQuestions[mCurrentQuestion].id,
                            mQuestions[mCurrentQuestion].answers[mIndexTick].good
                    )
                }

                if (mCurrentQuestion < it.count() - 1) mCurrentQuestion += 1
                displayQuestion()
            }
        }

        question_last.setOnClickListener {
            manager.updateFollow(
                    mQuestions[mCurrentQuestion].id,
                    mQuestions[mCurrentQuestion].answers[mIndexTick].good
            )
        }

        question_label.setBackgroundColor(Color.TRANSPARENT)

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

    private fun questionsLoaded(topic: Topic): Unit {

        mTopic = topic
        mQuestions = topic.questions.orderByFollowAndFocus()
        rotateloading.stop()

        if (mQuestions.size > 0) {
            displayQuestion()
        }
    }

    private fun loadError() {
        rotateloading.stop()
    }

    fun displayQuestion() {
/*
        mQuestions[mCurrentQuestion].let {
            mFocus = Focus().queryFirst { query -> query.equalTo("questionId", it.id) }
            mFollow = FollowDb().queryFirst { query -> query.equalTo("questionId", it.id) }
        }
*/
        mIndexTick = -1

        if (question_imgs.childCount > 0) {
            question_imgs.removeAllViews()
        }

        mShowAnswer = false
        resetCheckbox()

        mQuestions[mCurrentQuestion].apply {
            question_label.setBackgroundColor(transparentColor)
            question_label.loadData(label, "text/html; charset=utf-8", "UTF-8")

            for (i in 0..answers.count() - 1) {
                when (i) {
                    0 -> question_answer_1_text.text = answers[i].value
                    1 -> question_answer_2_text.text = answers[i].value
                    2 -> question_answer_3_text.text = answers[i].value
                    3 -> question_answer_4_text.text = answers[i].value
                }
            }

            displayFollowAndFocus()
            attachFocusListener()

            img?.forEach { img ->
                val imgContainer = ImageView(applicationContext)
                Picasso.with(applicationContext)
                        .load(BuildConfig.API_ATPL_IMG + img)
                        .into(imgContainer)

                question_imgs.addView(imgContainer)
            }

            launchCountDown()
        }

        mQuestions.isNotEmpty().apply {
            question_range.text = "${mCurrentQuestion + 1} / ${mQuestions.count()}"
        }

        question_previous.visibility = if (mCurrentQuestion > 0) View.VISIBLE else View.GONE

        mTopic?.questions?.let {
            question_next.visibility = if (mCurrentQuestion < it.count() - 1) View.VISIBLE else View.GONE
            question_last.visibility = if (question_follow.isChecked && mCurrentQuestion == it.count() - 1) View.VISIBLE else View.GONE
        }

        initCheckboxes()
    }

    private fun launchCountDown() {

        mTimer = object : CountDownTimer(60000, mTimeLength) {
            override fun onFinish() {
                question_label.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorAccent))
            }

            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished < 10000) {
                    when (isLight) {
                        true -> question_label.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorGreyLight))
                        false -> question_label.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorLight))
                    }
                    isLight = !isLight
                }
            }
        }.start()
    }

    private fun attachFocusListener() {

        question_care.setOnClickListener {
            it.visibility = View.GONE
            manager.updateFocus(mQuestions[mCurrentQuestion].id, true, this::onFocusSaves, this::onSavinError)

/*            when (mQuestions[mCurrentQuestion].focus) {
                true -> mQuestions[mCurrentQuestion].focus = null
                else -> mQuestions[mCurrentQuestion].focus = true
            }*/

            /*
            if (mFocus == null) {
                mFocus = Focus(-1, mTopic?.id ?: 0, mQuestions[mCurrentQuestion].id, true)
                mFocus?.save()
            } else {
                when(mFocus?.care) {
                    true -> {
                        mFocus = null
                        Focus().delete { query -> query.equalTo("questionId", mQuestions[mCurrentQuestion].id) }
                    }
                    false -> {
                        mFocus?.care = true
                        mFocus?.save()
                    }
                }
            }
            */
            displayFollowAndFocus()
        }

        question_dontcare.setOnClickListener {
            it.visibility = View.GONE
            manager.updateFocus(mQuestions[mCurrentQuestion].id, false, this::onFocusSaves, this::onSavinError)

            displayFollowAndFocus()
        }
    }

    private fun onFocusSaves(state: Boolean?) {
        mQuestions[mCurrentQuestion].focus = state
        displayFollowAndFocus()
    }

    private fun onSavinError() {
        Alerter.create(this)
                .setTitle(getString(R.string.error_dialog_title))
                .setText(getString(R.string.question_focus_error))
                .setBackgroundColor(R.color.colorAccent)
                .show()

        displayFollowAndFocus()
    }

    private fun displayFollowAndFocus() {

        question_dontcare.visibility = View.VISIBLE
        question_care.visibility = View.VISIBLE

        when (mQuestions[mCurrentQuestion].focus) {
            null -> {
                question_care.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorGrey))
                question_dontcare.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorGrey))
            }
            false -> {
                question_care.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorGrey))
                question_dontcare.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorAccent))
            }
            true -> {
                question_care.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorAccent))
                question_dontcare.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorGrey))
            }
        }
    }

    private fun initCheckboxes() {
        question_answer_1.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.cardview_light_background))
        question_answer_2.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.cardview_light_background))
        question_answer_3.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.cardview_light_background))
        question_answer_4.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.cardview_light_background))
    }

    private fun onAnswerClick(view: View, index: Int) {

        mShowAnswer = !mShowAnswer
        mIndexTick = if (mShowAnswer) index else -1

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

    private fun checkOneBox(card: CardView, check: Boolean) {

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
                    val bckg = if (it[i].good) ContextCompat.getDrawable(applicationContext, R.drawable.answer_right) else ContextCompat.getDrawable(applicationContext, R.drawable.answer_wrong)
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
