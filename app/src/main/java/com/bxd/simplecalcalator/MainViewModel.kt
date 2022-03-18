package com.bxd.simplecalcalator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round

class MainViewModel : ViewModel() {

    private val ADD_OPERATOR = "+"
    private val SUB_OPERATOR = "–"
    private val MULTI_OPERATOR = "×"
    private val DIV_OPERATOR = "÷"
    private val NEGATIVE_SIGN = "-"
    private val DOT_SIGN = "."
    private val MAX_VAL = 999999999999
    private val MIN_VAL = 0.0000000001
    private val MAX_REUSULT_LENGTH = 14

    private val _firstArg = MutableLiveData<String>()
    val firstArg: LiveData<String>
        get() = _firstArg

    private val _secondArg = MutableLiveData<String>()
    val secondArg: LiveData<String>
        get() = _secondArg

    private val _operator = MutableLiveData<String>()
    val operator: LiveData<String>
        get() = _operator

    private val _result = MutableLiveData<String>()
    val result: LiveData<String>
        get() = _result

    private val regex = Regex("^\\-?\\d+(\\.\\d+)?$")

    private val _isShowErrorToast = MutableLiveData<Boolean>()
    val isShowErrorToast: LiveData<Boolean>
        get() = _isShowErrorToast
    var lastValue = ""

    init {
        clear()
        _isShowErrorToast.value = false
    }

    fun onClickNumber(value: String) {
        if (!_result.value.isNullOrEmpty()) {
            clear()
        }
        if (!canEnterSecondArg()) {
            if (_firstArg.value?.length ?: 0 < 12) {
                _firstArg.value = firstArg.value + value;
            } else {
                showErrorToast()
            }
        } else {
            if (_secondArg.value?.length ?: 0 < 12) {
                _secondArg.value = _secondArg.value + value;
            } else {
                showErrorToast()
            }
        }
    }

    fun onClickOperator(value: String) {
        if (!_result.value.isNullOrEmpty()) {
            _firstArg.value = _result.value?.substring(2)
            _secondArg.value = ""
            _result.value = ""
        }
        if (isValidNumber(_firstArg.value ?: "")) {
            _operator.value = value
        }
    }

    fun onClickEqual() {
        if (
            isValidNumber(_firstArg.value ?: "")
            && isValidNumber(_secondArg.value ?: "")
            && !_operator.value.isNullOrEmpty()
        ) {
            if (calculateResult().isNotEmpty()) {
                _result.value = "= " + calculateResult()
                lastValue = calculateResult()
            }
        }
    }

    fun onClickNegative() {

        if (!_result.value.isNullOrEmpty()) {
            clear()
        }

        if (!canEnterSecondArg()) {
            if (!_firstArg.value!!.contains(NEGATIVE_SIGN)) {
                _firstArg.value = NEGATIVE_SIGN + _firstArg.value
            } else {
                _firstArg.value = _firstArg.value?.substring(1)
            }
        } else {
            if (!_secondArg.value!!.contains(NEGATIVE_SIGN)) {
                _secondArg.value = NEGATIVE_SIGN + _secondArg.value
            } else {
                _secondArg.value = _secondArg.value?.substring(1)
            }
        }
    }

    fun onClickClear() {
        clear()
    }

    fun onClickDot() {

        if (!_result.value.isNullOrEmpty()) {
            clear()
        }

        if (!canEnterSecondArg()) {
            if (_firstArg.value.isNullOrEmpty()) {
                _firstArg.value = "0."
            }
            if (!_firstArg.value!!.contains(DOT_SIGN)) {
                _firstArg.value = _firstArg.value + DOT_SIGN
            }
        } else {
            if (_secondArg.value.isNullOrEmpty()) {
                _secondArg.value = "0."
            }
            if (!_secondArg.value!!.contains(DOT_SIGN)) {
                _secondArg.value = _secondArg.value + DOT_SIGN
            }
        }
    }

    fun onClickDelete() {
        _result.value = ""
        if (!_secondArg.value.isNullOrEmpty()) {
            val len = _secondArg.value?.length ?: 0
            _secondArg.value = _secondArg.value?.substring(0, len - 1)
            return
        }
        if (!_operator.value.isNullOrEmpty()) {
            _operator.value = ""
            return
        }
        if (!_firstArg.value.isNullOrEmpty()) {
            val len = _firstArg.value?.length ?: 0
            _firstArg.value = _firstArg.value?.substring(0, len - 1)
        }
    }

    fun onClickAns() {
        if (!_result.value.isNullOrEmpty()) {
            clear()
        }
        if (!canEnterSecondArg()) {
            if (_firstArg.value?.length ?: 0 < 12) {
                _firstArg.value = lastValue;
            } else {
                showErrorToast()
            }
        } else {
            if (_secondArg.value?.length ?: 0 < 12) {
                _secondArg.value = lastValue;
            } else {
                showErrorToast()
            }
        }
    }

    private fun showErrorToast() {
        _isShowErrorToast.value = true
    }

    fun resetErrorToast() {
        _isShowErrorToast.value = false
    }

    private fun canEnterSecondArg(): Boolean {
        if (_operator.value.isNullOrEmpty()) {
            return false
        }
        return true
    }

    private fun isValidNumber(n: String): Boolean {
        if (regex.matches(n)) {
            return true
        }
        return false
    }

    private fun calculateResult(): String {
        val firstNum = _firstArg.value!!.toDouble()
        val secondNum = _secondArg.value!!.toDouble()
        val op = _operator.value
        var res: Double = 0.0
        when (op) {
            ADD_OPERATOR -> res = firstNum + secondNum
            SUB_OPERATOR -> res = firstNum - secondNum
            MULTI_OPERATOR -> res = firstNum * secondNum
            DIV_OPERATOR -> res = firstNum / secondNum
        }
        if (res != 0.0 && (abs(res) > MAX_VAL || abs(res) < MIN_VAL)) {
            showErrorToast()
            return ""
        }
        if (isInt(res)) {
            return res.toLong().toBigDecimal().toPlainString()
        }
        return formatDoubleResult(res)
    }

    private fun formatDoubleResult(res: Double): String {
        val intLen = res.toLong().toBigDecimal().toPlainString().length
        val precision = 10.0.pow(MAX_REUSULT_LENGTH - intLen - 1)
        val roundedRes = round(res * precision) / precision
        return roundedRes.toBigDecimal().toPlainString()
    }

    private fun isInt(n: Double): Boolean {
        val intVal = n.toLong()
        val diff = abs(n - intVal)
        if (diff > 0) {
            return false
        }
        return true
    }

    private fun clear() {
        _firstArg.value = ""
        _secondArg.value = ""
        _operator.value = ""
        _result.value = ""
    }

}