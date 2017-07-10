package biz.eventually.atpl.ui.questions

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.view.*
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
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.pwittchen.swipe.library.Swipe
import com.github.pwittchen.swipe.library.SwipeListener
import com.squareup.picasso.Picasso
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_questions.*


class QuestionsActivity : BaseActivity<QuestionsManager>() {

    private var mTopic: Topic? = null
    private var mQuestions = mutableListOf<Question>()

    private var mCurrentQuestion: Int = 0
    private var mShowAnswer = false
    private var mAnswerIndexTick = -1

    private var transparentColor: Int = 0x00000000
    private var mTimer: CountDownTimer? = null
    private var isLight: Boolean = true

    private var mHadChange = false
    private var mHasToken = true

    private var mTimeLength: Long = 1000

    private var mSwipe : Swipe? = null

    // answer ticked results for stat
    private val mStatistic = mutableMapOf<Int, Int>()

    private val mMime = "text/html"
    private val mEncoding = "utf-8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_questions)
        AtplApplication.component.inject(this)

        mTimeLength = getLong(applicationContext, PREF_TIMER, mTimeLength)

        // has Token ?
        PrefsGetString(this@QuestionsActivity, PREF_TOKEN)?.let {
            question_care.visibility = View.VISIBLE
            question_dontcare.visibility = View.VISIBLE

            question_follow.visibility = View.VISIBLE
            question_follow_label.visibility = View.VISIBLE
        } ?: kotlin.run { mHasToken = false }

        mTopic = intent.extras.getParcelable<Topic>(IntentIdentifier.TOPIC)
        val startFirst = intent.extras.getBoolean(IntentIdentifier.TOPIC_STARRED, false)

        mTopic?.apply {
            rotateloading.start()
            manager.getQuestions(id, startFirst, { t -> questionsLoaded(t) }, { loadError() })

            supportActionBar?.title = name
        }

        question_answer_1.setOnClickListener { onAnswerClick(it, 0) }
        question_answer_2.setOnClickListener { onAnswerClick(it, 1) }
        question_answer_3.setOnClickListener { onAnswerClick(it, 2) }
        question_answer_4.setOnClickListener { onAnswerClick(it, 3) }

        question_answer_1_rdo.setOnClickListener { onAnswerClick(question_answer_1, 0) }
        question_answer_2_rdo.setOnClickListener { onAnswerClick(question_answer_2, 1) }
        question_answer_3_rdo.setOnClickListener { onAnswerClick(question_answer_3, 2) }
        question_answer_4_rdo.setOnClickListener { onAnswerClick(question_answer_4, 3) }

        question_previous.setOnClickListener {
            val isGood = mQuestions[mCurrentQuestion].answers[mAnswerIndexTick].good

            // local stats
            mStatistic.put(mQuestions[mCurrentQuestion].id, if (isGood) 1 else 0)

            // server following
            if (question_follow.isChecked && mAnswerIndexTick > -1) {
                mHadChange = true
                manager.updateFollow(mQuestions[mCurrentQuestion].id, isGood)
            }

            if (mCurrentQuestion >= 1) mCurrentQuestion -= 1
            displayQuestion()
        }

        question_next.setOnClickListener {
            mTopic?.questions?.let {
                val isGood = mQuestions[mCurrentQuestion].answers[mAnswerIndexTick].good
                // local stats
                mStatistic.put(mQuestions[mCurrentQuestion].id, if (isGood) 1 else 0)

                if (question_follow.isChecked && mAnswerIndexTick > -1) {
                    mHadChange = true
                    manager.updateFollow(mQuestions[mCurrentQuestion].id, isGood)
                }

                if (mCurrentQuestion < it.count() - 1) mCurrentQuestion += 1
                displayQuestion()
            }
        }

        question_last.setOnClickListener {
            if (mAnswerIndexTick > -1) {
                val isGood = mQuestions[mCurrentQuestion].answers[mAnswerIndexTick].good
                // local stats
                mStatistic.put(mQuestions[mCurrentQuestion].id, if (isGood) 1 else 0)

                // if follow request
                if (question_follow.isChecked) {
                    mHadChange = true
                    manager.updateFollow(mQuestions[mCurrentQuestion].id, isGood)
                }

                it.visibility = View.GONE
                // show stats of result
            }

            showLocalStats()
        }

        mSwipe = Swipe()
        mSwipe?.setListener(object : SwipeListener {
            override fun onSwipedLeft(event: MotionEvent) {
                question_next.performClick()
            }

            override fun onSwipedRight(event: MotionEvent) {
                question_previous.performClick()
            }

            override fun onSwipingLeft(event: MotionEvent) {}
            override fun onSwipingRight(event: MotionEvent) {}
            override fun onSwipingUp(event: MotionEvent) {}
            override fun onSwipedUp(event: MotionEvent) {}
            override fun onSwipingDown(event: MotionEvent) {}
            override fun onSwipedDown(event: MotionEvent) {}
        })

        question_label.setBackgroundColor(Color.TRANSPARENT)

        question_follow.setOnCheckedChangeListener { _, isChecked ->
            question_last.visibility = if (isChecked && mCurrentQuestion == mQuestions.size - 1) View.VISIBLE else View.GONE
        }

        question_label.settings.javaScriptEnabled = false
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        mSwipe?.dispatchTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
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

        if (mHadChange) {
            setResult(Activity.RESULT_OK, Intent())
        } else {
            setResult(Activity.RESULT_CANCELED, Intent())
        }

        super.onBackPressed()
    }

    private fun showLocalStats() {

        val result = mStatistic.entries.filter { it.value > 0 }.sumBy { it.value }.toDouble()
        val percent = if (result == 0.0) 0 else ((result / mStatistic.size.toDouble()) * 100).toInt()

        var title = getString(R.string.result_title_good)
        val message = getString(R.string.result_value, percent)
        var warningType = SweetAlertDialog.SUCCESS_TYPE

        when {
            percent < 75 -> {
                title = getString(R.string.result_title_bad)
                warningType = SweetAlertDialog.ERROR_TYPE
            }
            percent < 85 -> {
                title = getString(R.string.result_title_warning)
                warningType = SweetAlertDialog.WARNING_TYPE
            }
            else -> SweetAlertDialog.SUCCESS_TYPE
        }

        SweetAlertDialog(this, warningType)
                .setTitleText(title)
                .setContentText(message)
                .setConfirmClickListener({ finish() })
                .show()
    }

    fun shuffleQuestions() {
        mQuestions = shuffle(mQuestions) as MutableList<Question>
        mCurrentQuestion = 0
        displayQuestion()
    }

    private fun questionsLoaded(topic: Topic): Unit {

        mTopic = topic
        mQuestions = topic.questions.toMutableList()

        // shuffle answer
        mQuestions.forEach { q ->
            q.answers = shuffle(q.answers.toMutableList())
        }

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
        mTimer?.cancel()
        mAnswerIndexTick = -1

        if (question_imgs.childCount > 0) {
            question_imgs.removeAllViews()
        }

        mShowAnswer = false

        initAnswerCardDisplay()
        resetCheckbox()

        // check following crash report ???
        if (mQuestions.size > 0 && mCurrentQuestion >= 0 && mCurrentQuestion <= mQuestions.size) {
            mQuestions[mCurrentQuestion].apply {
                question_label.setBackgroundColor(transparentColor)
                question_label.loadDataWithBaseURL(null, label, mMime, mEncoding, null)
                println(label)

                for (i in 0..answers.count() - 1) {
                    when (i) {
                        0 -> question_answer_1_text.text = answers[i].value
                        1 -> question_answer_2_text.text = answers[i].value
                        2 -> question_answer_3_text.text = answers[i].value
                        3 -> question_answer_4_text.text = answers[i].value
                    }
                }

                if (mHasToken) {
                    displayFollowAndFocus()
                    attachFocusListener()
                }

                displayFollowCount()

                img?.forEach { img ->
                    val imgContainer = ImageView(applicationContext)
                    Picasso.with(applicationContext)
                            .load(BuildConfig.API_ATPL_IMG + img)
                            .into(imgContainer)

                    question_imgs.addView(imgContainer)
                }

                launchCountDown()
            }
        }

        mQuestions.isNotEmpty().apply {
            question_range.text = "${mCurrentQuestion + 1} / ${mQuestions.count()}"
        }

        question_previous.visibility = if (mCurrentQuestion > 0) View.VISIBLE else View.GONE

        mTopic?.questions?.let {
            question_next.visibility = if (mCurrentQuestion < it.count() - 1) View.VISIBLE else View.GONE
            question_last.visibility = if (mCurrentQuestion == it.count() - 1) View.VISIBLE else View.GONE
        }
    }

    private fun launchCountDown() {

        question_time.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorGrey))

        mTimer = object : CountDownTimer(60000, mTimeLength) {
            override fun onFinish() {
                question_time.text = ""
                question_label.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorAccent))
            }

            override fun onTick(millisUntilFinished: Long) {

                question_time.text = "${millisUntilFinished / 1000}"

                if (millisUntilFinished < 10000) {
                    when (isLight) {
                        true -> {
                            question_label.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorGreyLight))
                            question_time.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorPrimaryDark))
                        }
                        false -> {
                            question_label.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorLight))
                            question_time.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorAccent))
                        }
                    }
                    isLight = !isLight
                }
            }
        }.start()
    }

    private fun attachFocusListener() {

        question_care.setOnClickListener {
            mHadChange = true

            question_care.isEnabled = false
            question_care.setImageDrawable(ContextCompat.getDrawable(this@QuestionsActivity, R.drawable.ic_cached_black))
            question_care.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorGrey))

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
            mHadChange = true

            question_dontcare.isEnabled = false
            question_dontcare.setImageDrawable(ContextCompat.getDrawable(this@QuestionsActivity, R.drawable.ic_cached_black))
            question_dontcare.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorGrey))

            manager.updateFocus(mQuestions[mCurrentQuestion].id, false, this::onFocusSaves, this::onSavinError)

            displayFollowAndFocus()
        }
    }

    private fun onFocusSaves(state: Boolean?) {
        mQuestions[mCurrentQuestion].focus = state

        question_care.isEnabled = true
        question_care.setImageDrawable(ContextCompat.getDrawable(this@QuestionsActivity, R.drawable.ic_star))

        question_dontcare.isEnabled = true
        question_dontcare.setImageDrawable(ContextCompat.getDrawable(this@QuestionsActivity, R.drawable.ic_visibility_off))

        displayFollowAndFocus()
    }

    private fun onSavinError() {
        Alerter.create(this)
                .setTitle(getString(R.string.dialog_title_error))
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

    private fun displayFollowCount() {

        val good = mQuestions[mCurrentQuestion].follow.good
        val wrong = mQuestions[mCurrentQuestion].follow.wrong

        if (good == 0 && wrong == 0) {
            question_good_img.visibility = View.GONE
            question_good_value.visibility = View.GONE

            question_wrong_img.visibility = View.GONE
            question_wrong_value.visibility = View.GONE
        } else {
            question_good_img.visibility = View.VISIBLE
            question_good_value.visibility = View.VISIBLE
            question_good_value.text = good.toString()

            question_wrong_img.visibility = View.VISIBLE
            question_wrong_value.visibility = View.VISIBLE
            question_wrong_value.text = wrong.toString()

            if (good > wrong) {
                question_good_img.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorAccent))
                question_wrong_img.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorGrey))
            } else if (good < wrong) {
                question_good_img.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorGrey))
                question_wrong_img.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorAccent))
            } else {
                question_good_img.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorGrey))
                question_wrong_img.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorGrey))
            }
        }
    }

    private fun initAnswerCardDisplay() {
        question_answer_1.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.cardview_light_background))
        question_answer_2.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.cardview_light_background))
        question_answer_3.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.cardview_light_background))
        question_answer_4.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.cardview_light_background))
    }

    private fun onAnswerClick(view: View, index: Int) {

        mShowAnswer = !mShowAnswer
        mAnswerIndexTick = if (mShowAnswer) index else -1

        if (mShowAnswer) {
            resetCheckbox()
            checkOneBox(view as CardView, true)
            showAnswer()
            mTimer?.cancel()
        } else {
            initAnswerCardDisplay()
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
        mQuestions[mCurrentQuestion].answers.let {
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
