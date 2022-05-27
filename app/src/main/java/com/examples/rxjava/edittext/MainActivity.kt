package com.examples.rxjava.edittext

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import com.examples.rxjava.edittext.databinding.ActivityMainBinding
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.run {
            /**
             * Only allow user to input numbers from 0 to 1 [Integer], otherwise it will throw an error.
             * There isn't any error handler, because this is just a sample app.
             */
            etBinary.observeInput().subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {}
                override fun onNext(t: String) {
                    // store calculation data
                    val decResult = t.fold(0L) { acc, element ->
                        (acc * 2) + element.digitToInt(2)
                    }
                    val octResult = decResult.toString(8)
                    val hexResult = decResult.toString(16).uppercase()

                    // and display the results to another edit text which corresponds with their radix representation
                    etDecimal.setText(decResult.toString())
                    etOctal.setText(octResult)
                    etHexadecimal.setText(hexResult)
                }

                override fun onError(e: Throwable) {
                    Log.d("Binary Input", e.message.toString())
                }
            })


            // You could to disable the inspection, to make it easier to read and
            /**
             * When I add some logic to another [EditText] (for example lets add some logic to [Decimal] edit text)
             * and add some number/digit to [Binary] edit text, then the input just display
             * only the first number. If we try to add another number/digit to the [Binary] edit text,
             * the number/digit will not appeared.
             *
             * This can happen because, when we set the result from operation above (which is [Binary] operation)
             * to another [EditText], in this example are [Decimal].
             * Then the [Decimal] input will begin to observe its input too, because there is new data
             * coming from the [Binary] operation.
             *
             * The [Decimal] edit text will do the calculations, and set the results to another [EditText],
             * which is [Binary], [Octal] and [Hexadecimal]. [Binary] input notify there is a new data
             * coming from [Decimal] input and will begin its calculation too.
             *
             * This condition is like a infinite loops.
             * I hope my explanation above can help.
             */
            etDecimal.observeInput().subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {}
                override fun onNext(t: String) {
                    // store calculation data
                    val binResult = t.toLong().toString(2)
                    val octResult = t.toLong().toString(8)
                    val hexResult = t.toLong().toString(16).uppercase()

                    // and display the results to another edit text which corresponds with their radix representation
                    etBinary.setText(binResult)
                    etOctal.setText(octResult)
                    etHexadecimal.setText(hexResult)
                }

                override fun onError(e: Throwable) {
                    Log.d("Decimal Input", e.message.toString())
                }
            })
        } ?: throw Error("Something went wrong, please relaunch the app.")
    }

    private fun <T: EditText> T.observeInput() =
        RxTextView.textChanges(this)
            .skipInitialValue()
            .observeOn(AndroidSchedulers.mainThread())
            .map(CharSequence::toString)
            .publish()
            .refCount()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}