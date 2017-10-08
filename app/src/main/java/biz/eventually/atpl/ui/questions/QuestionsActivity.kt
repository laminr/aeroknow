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
import android.widget.TextView
import biz.eventually.atpl.AtplApplication
import biz.eventually.atpl.BuildConfig
import biz.eventually.atpl.R
import biz.eventually.atpl.common.IntentIdentifier
import biz.eventually.atpl.data.model.Question
import biz.eventually.atpl.data.model.Topic
import biz.eventually.atpl.ui.BaseActivity
import biz.eventually.atpl.ui.source.QuestionsManager
import biz.eventually.atpl.utils.*
import biz.eventually.atpl.utils.Prefields.PREF_TIMER_ENABLE
import biz.eventually.atpl.utils.Prefields.PREF_TIMER_NBR
import biz.eventually.atpl.utils.Prefields.PREF_TOKEN
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.pwittchen.swipe.library.Swipe
import com.github.pwittchen.swipe.library.SwipeListener
import com.squareup.picasso.Picasso
import com.tapadoo.alerter.Alerter
import com.vicpin.krealmextensions.queryFirst
import kotlinx.android.synthetic.main.activity_questions.*
import org.jetbrains.anko.share

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
    private var mHasToken = false

    private var mMenuShuffle: MenuItem? = null
    private var mMenuShare: MenuItem? = null

    private var mSwipe: Swipe? = null

    // answer ticked results for stat
    private val mStatistic = mutableMapOf<Int, Int>()

    private val mMime = "text/html"
    private val mEncoding = "utf-8"

    private lateinit var mQuestionCardView: List<CardView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)
        AtplApplication.component.inject(this)

        mQuestionCardView = listOf<CardView>(question_answer_1, question_answer_2, question_answer_3, question_answer_4)

        // has Token ?
        prefsGetString(this@QuestionsActivity, PREF_TOKEN)?.let {
            mHasToken = it.isNotEmpty()
            if (mHasToken) {
                question_care.visibility = View.VISIBLE
                question_dontcare.visibility = View.VISIBLE

                question_follow.visibility = View.VISIBLE
                question_follow_label.visibility = View.VISIBLE
            }
        } ?: kotlin.run { mHasToken = false }

        val topicId = intent.extras.getString(IntentIdentifier.TOPIC)
        val startFirst = intent.extras.getBoolean(IntentIdentifier.TOPIC_STARRED, false)

        mTopic = Topic().queryFirst({ query -> query.equalTo("id", topicId) })

        mTopic?.apply {
            rotateloading.start()
            manager.getQuestions(idWeb, startFirst, { t -> questionsLoaded(t) }, { loadError() })

            supportActionBar?.title = name
        }

        // Listeners
        initListeners()

        question_label.setBackgroundColor(Color.TRANSPARENT)
        question_label.settings.javaScriptEnabled = false
    }

    override fun onDestroy() {
        mTimer?.cancel()
        mTimer = null
        super.onDestroy()
    }

    private fun initListeners() {

        // initiate onClick on all Question CardView
        mQuestionCardView.forEachIndexed { index, cardView -> cardView.setOnClickListener({ onAnswerClick(it, index) }) }

        listOf<CheckBox>(
                question_answer_1_rdo,
                question_answer_2_rdo,
                question_answer_3_rdo,
                question_answer_4_rdo
        ).forEachIndexed { index, checkbox ->
            checkbox.setOnClickListener { onAnswerClick(mQuestionCardView[index], index) }
        }

        question_previous.setOnClickListener {
            mMenuShare?.isVisible = false
            if (mAnswerIndexTick > -1) {
                val isGood = mQuestions[mCurrentQuestion].answers[mAnswerIndexTick].good

                // local stats
                mStatistic.put(mQuestions[mCurrentQuestion].idWeb, if (isGood) 1 else 0)

                // server following
                if (question_follow.isChecked) {
                    mHadChange = true
                    manager.updateFollow(mQuestions[mCurrentQuestion].idWeb, isGood)
                }
            }

            if (mCurrentQuestion >= 1) mCurrentQuestion -= 1
            displayQuestion()
        }

        question_next.setOnClickListener {
            mMenuShare?.isVisible = false

            if (mAnswerIndexTick > -1 && mCurrentQuestion < mQuestions.size) {
                val isGood = mQuestions[mCurrentQuestion].answers[mAnswerIndexTick].good
                // local stats
                mStatistic.put(mQuestions[mCurrentQuestion].idWeb, if (isGood) 1 else 0)

                if (question_follow.isChecked) {
                    mHadChange = true
                    manager.updateFollow(mQuestions[mCurrentQuestion].idWeb, isGood)
                }
            }

            if (mCurrentQuestion < mQuestions.size - 1) mCurrentQuestion += 1
            displayQuestion()

        }

        question_last.setOnClickListener {
            mMenuShare?.isVisible = false

            if (mAnswerIndexTick > -1) {
                val isGood = mQuestions[mCurrentQuestion].answers[mAnswerIndexTick].good
                // local stats
                mStatistic.put(mQuestions[mCurrentQuestion].idWeb, if (isGood) 1 else 0)

                // if follow request
                if (question_follow.isChecked) {
                    mHadChange = true
                    manager.updateFollow(mQuestions[mCurrentQuestion].idWeb, isGood)
                }
            }

            it.visibility = View.GONE
            // show stats of result

            showLocalStats()
        }

        question_follow.setOnCheckedChangeListener { _, isChecked ->
            question_last.visibility = if (isChecked && mCurrentQuestion == mQuestions.size - 1) View.VISIBLE else View.GONE
        }

        mSwipe = Swipe()
        mSwipe?.setListener(
                object : SwipeListener {
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
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        mSwipe?.dispatchTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_shuffle, menu)

        mMenuShuffle = menu?.findItem(R.id.action_shuffle)
        mMenuShare = menu?.findItem(R.id.action_share)

        mMenuShuffle?.isVisible = false
        mMenuShare?.isVisible = false

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_shuffle -> {
                shuffleQuestions()
                true
            }
            R.id.action_share -> {
                val shareTxt = constructShareText()
                if (!shareTxt.isEmpty()) {
                    share(shareTxt, mTopic?.name ?: "")
                }

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun constructShareText(): String {

        val txt = StringBuilder()

        when (mCurrentQuestion) {
            in 0 until mQuestions.size -> {
                txt.append(mQuestions[mCurrentQuestion].label)
                        .append("\n")
                        .append("\n")

                mQuestions[mCurrentQuestion].answers.forEach { answer ->
                    val line = if (answer.good) "+" else "-"
                    txt.append("$line ${answer.value}")
                    txt.append("\n")
                }
            }
            else -> {
            }
        }

        return txt.toString()
    }

//    override fun onSaveInstanceState(outState: Bundle?) {
//        super.onSaveInstanceState(outState)
//        outState?.let {
//            it.putParcelable(StateIdentifier.TOPIC, mTopic)
//            it.putInt(StateIdentifier.QUEST_CURRENT, mCurrentQuestion)
//        }
//    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
//        super.onRestoreInstanceState(savedInstanceState)
//        savedInstanceState?.let {
//            mTopic = it.getParcelable(StateIdentifier.TOPIC)
//            mQuestions = mTopic?.questions as MutableList<Question>
//            mCurrentQuestion = it.getInt(StateIdentifier.QUEST_CURRENT)
//        }
//    }

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

    private fun shuffleQuestions() {
        mQuestions = shuffle(mQuestions) as MutableList<Question>
        mCurrentQuestion = 0
        displayQuestion()
    }

    private fun questionsLoaded(questions: List<Question>): Unit {

        mQuestions = questions.toMutableList()

        // shuffle answer
//        mQuestions.forEach { q ->
//            Collections.shuffle(q.answers)
//        }

        rotateloading.stop()

        if (mQuestions.size > 0) {
            mMenuShuffle?.isVisible = true
            displayQuestion()
        }

    }

    private fun loadError() {
        rotateloading.stop()
    }

    private fun displayQuestion() {

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

                val questionAnswerTextView = listOf<TextView>(
                        question_answer_1_text,
                        question_answer_2_text,
                        question_answer_3_text,
                        question_answer_4_text
                )

                for (i in 0 until answers.count()) {
                    questionAnswerTextView[i].text = answers[i].value
                }

                if (mHasToken) {
                    displayFollowAndFocus()
                    attachFocusListener()
                }

                displayFollowCount()

                imgList.forEach { img ->
                    val imgContainer = ImageView(applicationContext)
                    Picasso.with(applicationContext)
                            .load(BuildConfig.API_ATPL_IMG + img)
                            .into(imgContainer)

                    question_imgs.addView(imgContainer)
                }

                if (prefsGetValue(PREF_TIMER_ENABLE, false)) launchCountDown()
            }
        }

        mQuestions.isNotEmpty().apply {
            question_range.text = "${mCurrentQuestion + 1} / ${mQuestions.count()}"
        }

        question_previous.visibility = if (mCurrentQuestion > 0) View.VISIBLE else View.GONE

        mQuestions.let {
            question_next.visibility = if (mCurrentQuestion < it.count() - 1) View.VISIBLE else View.GONE
            question_last.visibility = if (mCurrentQuestion == it.count() - 1) View.VISIBLE else View.GONE
        }

        mMenuShare?.isVisible = true
    }

    private fun launchCountDown() {

        question_time.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorGrey))

        val seconds = (prefsGetValue(PREF_TIMER_NBR, "60").toInt() * 1000).toLong()
        mTimer = object : CountDownTimer(seconds, 1000) {
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

            manager.updateFocus(mQuestions[mCurrentQuestion].idWeb, true, this::onFocusSaves, this::onSavingError)

            displayFollowAndFocus()
        }

        question_dontcare.setOnClickListener {
            mHadChange = true

            question_dontcare.isEnabled = false
            question_dontcare.setImageDrawable(ContextCompat.getDrawable(this@QuestionsActivity, R.drawable.ic_cached_black))
            question_dontcare.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorGrey))

            manager.updateFocus(mQuestions[mCurrentQuestion].idWeb, false, this::onFocusSaves, this::onSavingError)

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

    private fun onSavingError() {
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

        val good = mQuestions[mCurrentQuestion].follow?.good ?: 0
        val wrong = mQuestions[mCurrentQuestion].follow?.wrong ?: 0

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

            when {
                good > wrong -> {
                    question_good_img.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorAccent))
                    question_wrong_img.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorGrey))
                }
                good < wrong -> {
                    question_good_img.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorGrey))
                    question_wrong_img.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorAccent))
                }
                else -> {
                    question_good_img.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorGrey))
                    question_wrong_img.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorGrey))
                }
            }
        }
    }

    private fun initAnswerCardDisplay() {
        for (i in 0..3) {
            mQuestionCardView[i].setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.cardview_light_background))
        }
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
        for (i in 0..3) {
            checkOneBox(mQuestionCardView[i], false)
        }
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
        if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestions.size) {
            mQuestions[mCurrentQuestion].answers.let {
                (0 until it.count())
                        .filter { i -> it[i].good }
                        .forEach { i ->
                            mQuestionCardView[i].background = if (it[i].good)
                                ContextCompat.getDrawable(applicationContext, R.drawable.answer_right)
                            else
                                ContextCompat.getDrawable(applicationContext, R.drawable.answer_wrong)
                        }
            }
        }
    }
}
