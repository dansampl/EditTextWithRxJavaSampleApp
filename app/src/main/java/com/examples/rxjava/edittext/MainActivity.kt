package com.examples.rxjava.edittext

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import com.examples.rxjava.edittext.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.*

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.run {
            etBinary.observeInput().subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {}
                override fun onNext(t: String) {
                    // store calculation data
                    try {
                        Log.d("XYZ-etBinary", t);
                        val decResult = t.fold(0L) { acc, element ->
                            (acc * 2) + element.digitToInt(2)
                        }
                        val octResult = decResult.toString(8)
                        val hexResult = decResult.toString(16).uppercase()

                        // and display the results to another edit text which corresponds with their radix representation
                        etDecimal.setText(decResult.toString(), true)
                        etOctal.setText(octResult, true)
                        etHexadecimal.setText(hexResult, true)
                    } catch (e: Throwable) {
                        Log.e("XYZ-etBinary", e.message.toString())
                    }
                }

                override fun onError(e: Throwable) {
                    Log.d("Binary Input", e.message.toString())
                }
            })

            etDecimal.observeInput().subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {}
                override fun onNext(t: String) {
                    // store calculation data
                    try {
                        Log.d("XYZ-etDecimal", t);
                        val binResult = t.toLong().toString(2)
                        val octResult = t.toLong().toString(8)
                        val hexResult = t.toLong().toString(16).uppercase()

                        // and display the results to another edit text which corresponds with their radix representation
                        etBinary.setText(binResult, true)
                        etOctal.setText(octResult, true)
                        etHexadecimal.setText(hexResult, true)
                    } catch (e: Throwable) {
                        Log.e("XYZ-etDecimal", e.message.toString())
                    }
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
            .map(CharSequence::toString)
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .publish()
            .refCount()

    private fun TextInputEditText.setText(text: CharSequence, onlyIfChanged: Boolean) {
        if (onlyIfChanged) {
            if (Objects.equals(this.text.toString(), text.toString())) {
                return;
            }
        }
        this.setText(text);
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
