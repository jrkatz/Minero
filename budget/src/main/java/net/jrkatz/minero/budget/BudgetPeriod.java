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

package net.jrkatz.minero.budget;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import net.jrkatz.minero.budget.period.Period;

import java.util.ArrayList;

/**
 * @Author jrkatz
 * @Date 2/19/2017.
 */

public class BudgetPeriod implements Parcelable {
    private final Budget mBudget;
    private final Period mPeriod;
    private final long mRemaining;
    private final ImmutableList<SpendEvent> mSpendEvents;

    public BudgetPeriod(
            Budget budget,
            Period period,
            long remaining,
            ImmutableList<SpendEvent> spendEvents
    ) {
        this.mBudget = budget;
        this.mPeriod = period;
        this.mRemaining = remaining;
        this.mSpendEvents = spendEvents;
    }

    protected BudgetPeriod(Parcel in) {
        mBudget = in.readParcelable(Budget.class.getClassLoader());
        mPeriod = in.readParcelable(Period.class.getClassLoader());
        mRemaining = in.readLong();
        final ArrayList<SpendEvent> spendEvents = new ArrayList<>();
        in.readTypedList(spendEvents, SpendEvent.CREATOR);
        mSpendEvents = ImmutableList.copyOf(spendEvents);
    }

    public static final Creator<BudgetPeriod> CREATOR = new Creator<BudgetPeriod>() {
        @Override
        public BudgetPeriod createFromParcel(Parcel in) {
            return new BudgetPeriod(in);
        }

        @Override
        public BudgetPeriod[] newArray(int size) {
            return new BudgetPeriod[size];
        }
    };

    public long getRemaining() {
        return mRemaining;
    }

    public Period getPeriod() {
        return mPeriod;
    }

    public ImmutableCollection<SpendEvent> getSpendEvents() {
        return mSpendEvents;
    }

    public Budget getBudget() {
        return mBudget;
    }

    public BudgetPeriod spend(final SpendEvent spendEvent) {
        return new BudgetPeriod(
                mBudget,
                mPeriod,
                mRemaining - spendEvent.getAmount(),
                ImmutableList.<SpendEvent>builder().addAll(mSpendEvents).add(spendEvent).build()
        );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mBudget, flags);
        dest.writeParcelable(mPeriod, flags);
        dest.writeLong(mRemaining);
        dest.writeTypedList(mSpendEvents);
    }
}