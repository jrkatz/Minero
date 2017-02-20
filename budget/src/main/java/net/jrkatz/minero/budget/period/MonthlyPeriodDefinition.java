/*
 *     Minero is a minimal budget application
 *     Copyright (C) 2017 Jacob Katz
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.jrkatz.minero.budget.period;

import android.os.Parcel;

import org.joda.time.LocalDate;

/**
 * @Author jrkatz
 * @Date 2/19/2017.
 */
public class MonthlyPeriodDefinition extends PeriodDefinition {
    private final int mDayStart;

    public MonthlyPeriodDefinition(final int dayStart) {
        mDayStart = dayStart;
    }

    protected MonthlyPeriodDefinition(Parcel in) {
        mDayStart = in.readInt();
    }

    private LocalDate startForMonth(final int year, final int month) {
        LocalDate firstOfMonth = new LocalDate(year, month, 1);
        return firstOfMonth.withDayOfMonth(
                Math.min(firstOfMonth.dayOfMonth().getMaximumValue(), mDayStart));
    }

    private Period periodForMonth(final int year, final int month) {
        LocalDate start = startForMonth(year, month);
        LocalDate nextMonth = start.plusMonths(1);
        LocalDate end = startForMonth(nextMonth.getYear(), nextMonth.getMonthOfYear());
        return new Period(start, end);
    }

    @Override
    public Period periodForDate(final LocalDate date) {
        return periodForMonth(date.getYear(), date.getMonthOfYear());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mDayStart);
    }

    public static final Creator<MonthlyPeriodDefinition> CREATOR = new Creator<MonthlyPeriodDefinition>() {
        @Override
        public MonthlyPeriodDefinition createFromParcel(Parcel in) {
            return new MonthlyPeriodDefinition(in);
        }

        @Override
        public MonthlyPeriodDefinition[] newArray(int size) {
            return new MonthlyPeriodDefinition[size];
        }
    };
}
