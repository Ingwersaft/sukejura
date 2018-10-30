package io.kesselring.kron.pattern

sealed class MonthsOfYear {
    object Every : MonthsOfYear()
    object Jan : MonthsOfYear()
    object Feb : MonthsOfYear()
    object Mar : MonthsOfYear()
    object Apr : MonthsOfYear()
    object May : MonthsOfYear()
    object Jun : MonthsOfYear()
    object Jul : MonthsOfYear()
    object Aug : MonthsOfYear()
    object Sep : MonthsOfYear()
    object Oct : MonthsOfYear()
    object Nov : MonthsOfYear()
    object Dec : MonthsOfYear()
}

fun Set<MonthsOfYear>.isActive(monthValue: Int): Boolean {
    val anyMisses = map {
        when (it) {
            MonthsOfYear.Every -> true
            MonthsOfYear.Jan -> monthValue == 1
            MonthsOfYear.Feb -> monthValue == 2
            MonthsOfYear.Mar -> monthValue == 3
            MonthsOfYear.Apr -> monthValue == 4
            MonthsOfYear.May -> monthValue == 5
            MonthsOfYear.Jun -> monthValue == 6
            MonthsOfYear.Jul -> monthValue == 7
            MonthsOfYear.Aug -> monthValue == 8
            MonthsOfYear.Sep -> monthValue == 9
            MonthsOfYear.Oct -> monthValue == 10
            MonthsOfYear.Nov -> monthValue == 11
            MonthsOfYear.Dec -> monthValue == 12
        }
    }.any { !it }
    return anyMisses.not()
}